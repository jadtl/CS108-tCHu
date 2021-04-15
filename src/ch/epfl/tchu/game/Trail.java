package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Trail between two stations in the network
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class Trail {
    private final Station station1;
    private final Station station2;
    private final List<Route> routes; 
    private final int length;

    /**
     * Constructs a trail with its two extreme stations and its length
     *
     * @param station1 
     *        The first station
     * 
     * @param station2 
     *        The second station opposite to the first
     * 
     * @param length 
     *        The length of the trail
     */
    private Trail(Station station1, Station station2, List<Route> routes) {
        this.station1 = station1;
        this.station2 = station2;
        this.routes = routes;
        this.length = routes.stream()
        .map(r -> r.length())
        .reduce(0, Integer::sum);
    }

    /**
     * The longest path in the network composed of the given routes
     *
     * @param routes 
     *        The list of routes that the player controls
     * 
     * @return a trail with the maximum length for the given routes
     */
    public static Trail longest(List<Route> routes) {
        // Filling the list with starting trails from routes
        List<Trail> toExtendTrails = routes.stream()
        .flatMap(r -> Stream.of(new Trail(r.station1(), r.station2(), List.of(r)), new Trail(r.station2(), r.station1(), List.of(r))))
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
            toExtendTrails.stream().forEach(t -> {
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
                    eligibleRoutes.stream().forEach(r -> {
                        List<Route> updatedRoutes = new ArrayList<Route>(t.routes);
                        updatedRoutes.add(r);
                        updatedTrails.add(new Trail(t.station1(), r.stationOpposite(t.station2()), updatedRoutes));
                    }) ;
            });
        }

        // Figuring the longest trail among the found dead ends
        return deadEndTrails.stream()
        .max((t1, t2) -> Integer.compare(t1.length(), t2.length()))
        .orElse(new Trail(null, null, List.of()));
    }

    /**
     * Returns the length of the trail
     * 
     * @return the length of the trail
     */
    public int length() { return length; }

    /**
     * Returns the first station of the trail
     * 
     * @return the first station of the trail
     */
    public Station station1() { return station1; }

    /**
     * Returns he second station of the trail
     * 
     * @return the second station of the trail
     */
    public Station station2() { return station2; }

    /**
     * Returns he string representation of the trail
     * 
     * @return the string representation of the trail
     */
    @Override
    public String toString() { return (station1() + " - " + station2() + " (" + length() + ")"); }
}
