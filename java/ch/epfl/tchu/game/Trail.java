package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

public class Trail {

    private final List<Route> routes;
    private final int length;

    private Trail(List<Route> routes) {

        this.routes = routes;
        int length = 0;
        if (routes != null) {
            for (Route route : routes) {

                length += route.length();

            }
        }

        this.length = length;

    }

    public static final Trail longest(List<Route> routes) {

        List<List<Route>> longestRoutes = new ArrayList<>();

        for (Route route : routes) {
            List<Route> initialRoute = new ArrayList<>();
            initialRoute.add(route);
            longestRoutes.add(initialRoute);
        }
        List<List<Route>> addedRoutes = new ArrayList<>();
        do {
            addedRoutes = new ArrayList<>();
            for (List<Route> currentRoutes : longestRoutes) {

                List<Route> availableRoutes = new ArrayList<>();
                availableRoutes.addAll(routes);
                availableRoutes.removeAll(currentRoutes);

                for (Route candidateRoute : availableRoutes) {
                    if (currentRoutes.get(currentRoutes.size() - 1).station2().id() == candidateRoute.station1().id()) {
                        List<Route> newRoutes = currentRoutes;
                        newRoutes.add(candidateRoute);

                        addedRoutes.add(newRoutes);
                    }
                }
            }
            if (addedRoutes.size() != 0)
                longestRoutes = addedRoutes;
        } while(addedRoutes.size() != 0);

        return (longestRoutes.size() == 0) ? new Trail(null) : new Trail(longestRoutes.get(0));
    }

    public int length() {

        return length;
    }

    public Station station1() {

        return (length == 0) ? null : routes.get(0).station1();
    }

    public Station station2() {

        return (length == 0) ? null : routes.get(routes.size() - 1).station2();
    }

    @Override
    public String toString() {

        return (station1() + " - " + station2() + " (" + length() + ")");
    }

}
