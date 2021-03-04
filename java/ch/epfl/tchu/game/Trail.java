package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Trail {
    private final Station station1;
    private final Station station2;
    private final int length;

    /**
     *
     * @param station1
     * @param station2
     * @param length
     */
    private Trail(Station station1, Station station2, int length) {
        this.station1 = station1;
        this.station2 = station2;
        this.length = length;
    }

    /**
     *
     * @param routes
     * @return
     */
    public static Trail longest(List<Route> routes) {
        List<List<Station>> toExtendTrails = new ArrayList<>();
        // A List that stores trails that cannot be extended further
        List<List<Station>> deadEndTrails = new ArrayList<>();
        // A Map that links a trail to its corresponding set of routes
        Map<List<Station>, List<Route>> associatedRoutes = new HashMap<>();

        // Filling the list and the map with starting elements from routes
        for (Route route : routes) {
            toExtendTrails.add(List.of(route.station1(), route.station2()));
            associatedRoutes.put(toExtendTrails.get(toExtendTrails.size() - 1), List.of(route));
            toExtendTrails.add(List.of(route.station2(), route.station1()));
            associatedRoutes.put(toExtendTrails.get(toExtendTrails.size() - 1), List.of(route));
        }

        List<List<Station>> updatedTrails = new ArrayList<>(toExtendTrails);
        // While at least one trail has been extended, search for further extensions
        while (!updatedTrails.isEmpty()) {
            toExtendTrails = new ArrayList<>(updatedTrails);
            updatedTrails.clear();
            // Looking for each trail if it can be extended
            for (List<Station> toExtendTrail : toExtendTrails) {
                // Removing from the candidate routes those which are already part of the trail
                List<Route> candidateRoutes = new ArrayList<>(routes);
                candidateRoutes.removeAll(associatedRoutes.get(toExtendTrail));
                boolean canBeExtended = false;
                // Looking for a route that can extend the trail
                for (Route candidateRoute : candidateRoutes) {
                    // A route can extend the trail iff one of its two stations is the last trail's station
                    if (candidateRoute.station1().equals(toExtendTrail.get(1))
                            || candidateRoute.station2().equals(toExtendTrail.get(1))) {
                        canBeExtended = true;
                        // The new last trail station is the opposite station of the extending route
                        updatedTrails.add(List.of(toExtendTrail.get(0), candidateRoute.stationOpposite(toExtendTrail.get(1))));
                        // Updating the map with the new trail we've discovered
                        List<Route> updatedRoutes = new ArrayList<>(associatedRoutes.get(toExtendTrail));
                        updatedRoutes.add(candidateRoute);
                        associatedRoutes.put(updatedTrails.get(updatedTrails.size() - 1), updatedRoutes);
                    }
                }
                // If the current trail can't be extend, then it's a dead end
                if (!canBeExtended) deadEndTrails.add(toExtendTrail);
            }
        }

        // Figuring the longest trail among the found dead ends
        int length = 0;
        List<Station> longestTrail = new ArrayList<>();
        if (!deadEndTrails.isEmpty()) {
            for (List<Station> trail : deadEndTrails) {
                int newLength = 0;
                for (Route route : associatedRoutes.get(trail)) {
                    newLength += route.length();
                }
                if (newLength > length) {
                    length = newLength;
                    longestTrail = trail;
                }
            }
        }

        return routes.isEmpty() ? new Trail(null, null, 0)
                : new Trail(longestTrail.get(0), longestTrail.get(1), length);
    }

    /**
     *
     * @return
     */
    public int length() { return length; }

    /**
     *
     * @return
     */
    public Station station1() { return station1; }

    /**
     *
     * @return
     */
    public Station station2() { return station2; }

    /**
     *
     * @return
     */
    @Override
    public String toString() { return (station1() + " - " + station2() + " (" + length() + ")"); }
}
