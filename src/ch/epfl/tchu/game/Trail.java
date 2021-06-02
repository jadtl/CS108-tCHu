package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A trail between two stations in the network
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public final class Trail {
    private final Station station1;
    private final Station station2;
    private final List<Route> routes;
    private final int length;

    private Trail(Station station1, Station station2, List<Route> routes, int length) {
        this.station1 = station1;
        this.station2 = station2;
        this.routes = routes;
        this.length = length;
    }

    /**
     * The longest path in the network composed of the given routes
     *
     * @param routes The {@link List} of {@link Route} that the player controls
     * @return A trail with the maximum length from {@code routes}
     */
    public static Trail longest(List<Route> routes) {
        // Filling the list with starting trails from routes
        List<Trail> toExtendTrails = routes.stream()
                .flatMap(r -> Stream.of(new Trail(r.station1(), r.station2(), List.of(r), r.length()), new Trail(r.station2(), r.station1(), List.of(r), r.length())))
                .collect(Collectors.toList());

        // A list that stores trails that cannot be extended further
        List<Trail> deadEndTrails = new ArrayList<>();

        // A list that stores trails that just got extended
        List<Trail> updatedTrails = new ArrayList<>(toExtendTrails);

        // While at least one trail has been extended, search for further extensions
        while (!updatedTrails.isEmpty()) {
            // The trails that last got updated now must be extended if possible
            toExtendTrails = List.copyOf(updatedTrails);

            // The list of updated trails is cleared
            updatedTrails.clear();

            // Iterating through the trails to be extended
            toExtendTrails.forEach(t -> {
                // Removing from the routes list already used routes as well as the ones that
                // do not contain the current trail's second station
                List<Route> eligibleRoutes = routes.stream()
                        .filter(r -> !t.routes.contains(r) && r.stations().contains(t.station2()))
                        .collect(Collectors.toList());

                if (eligibleRoutes.isEmpty())
                    // If no eligible route exist, the current trail is a dead end
                    deadEndTrails.add(t);
                else
                    // For every eligible route, a new longer trail is added to the updates
                    eligibleRoutes.forEach(r -> {
                        List<Route> updatedRoutes = new ArrayList<>(t.routes);
                        updatedRoutes.add(r);
                        updatedTrails.add(new Trail(t.station1(), r.stationOpposite(t.station2()), updatedRoutes, computeTrailLength(updatedRoutes)));
                    });
            });
        }

        // Figuring the longest trail among the found dead ends
        return deadEndTrails.stream()
                .max(Comparator.comparingInt(Trail::length))
                .orElse(new Trail(null, null, List.of(), 0));
    }

    private static int computeTrailLength(List<Route> routes) {
        return routes.stream()
                .map(Route::length)
                .reduce(0, Integer::sum);
    }

    /**
     * The length of the trail
     *
     * @return The length {@link Trail#length} of the {@link Trail}
     */
    public int length() {
        return length;
    }

    /**
     * The first station of the trail
     *
     * @return The first station {@link Trail#station1} of the {@link Trail}
     */
    public Station station1() {
        return station1;
    }

    /**
     * The second station of the trail
     *
     * @return The second station {@link Trail#station2} of the {@link Trail}
     */
    public Station station2() {
        return station2;
    }

    /**
     * The routes of the trail
     *
     * @return The routes of the trail
     */
    public List<Route> routes() { return List.copyOf(routes); }

    /**
     * The string representation of the trail
     *
     * @return The string representation of the trail
     */
    @Override
    public String toString() {
        return (station1() + " - " + station2() + " (" + length() + ")");
    }
}
