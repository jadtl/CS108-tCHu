package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;

/**
 * A ticket that holds a number of trips of the same departure station
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class Ticket implements Comparable<Ticket> {
    private List<Trip> trips;
    private final String text;

    /**
     * Constructs a ticket from a list of trips of the same departure station
     *
     * @param trips the list of trips
     * @throws IllegalArgumentException if the trips list is empty or
     *                      if there are more than one departure station
     */
    public Ticket(List<Trip> trips) {
        Collection<String> fromStations = new TreeSet<>();
        for (Trip trip : trips) {
            fromStations.add(trip.from().name());
        }
        Preconditions.checkArgument((trips.size() != 0) && (fromStations.size() == 1));

        this.trips = trips;
        this.text = computeText(this.trips);
    }

    /**
     * Constructs a ticket from a single trip
     *
     * @param from the departure station
     * @param to the arrival station
     * @param points the trip value
     */
    public Ticket(Station from, Station to, int points) {

        this(new ArrayList<>(List.of(new Trip(from, to, points))));
    }

    /**
     * Returns the textual representation of the trips
     *
     * @param trips a list of trips
     * @return the textual representation of the trips
     */
    private static String computeText(List<Trip> trips) {
        Map map = new HashMap();
        Collection<String> arrivalNoDuplicates = new TreeSet<>();

        // Print the beginning of the text
        String text = trips.get(0).from().name() + " - ";

        // Removing duplicate arrival stations
        for (Trip trip : trips)
            arrivalNoDuplicates.add(trip.to().name());

        // Storing the points for each unique arrival station in a map
        for (String arrivalStation : arrivalNoDuplicates) {
            for (Trip trip : trips) {
                if (trip.to().name().equals(arrivalStation))
                    map.put(arrivalStation, trip.points());
            }
        }

        // Print the remaining of the text depending on whether the ticket holds a unique trip or more
        if (arrivalNoDuplicates.size() == 1) {
            text += String.format("%s (%s)", arrivalNoDuplicates.toArray()[0], trips.get(0).points());
        }
        else {
            text += "{";
            for (String arrivalStation : arrivalNoDuplicates) {
                text += String.format("%s (%s), ", arrivalStation, map.get(arrivalStation));
            }
            text = text.substring(0, text.length() - 2) + "}";
        }

        return text;
    }

    /**
     * Returns the textual representation of the ticket
     * @return the textual representation of the ticket
     */
    public String text() {

        return text;
    }

    /**
     * Returns the points value of the ticket according the the player connectivity
     *
     * @param connectivity the station connectivity of the player network
     * @return the points value of the ticket according the the player connectivity
     */
    public int points(StationConnectivity connectivity) {
        int points = 0;
        boolean noConnectivity = true;

        for (Trip trip : trips) {
            if (connectivity.connected(trip.from(), trip.to()))
                noConnectivity = false;
        }

        if (noConnectivity) {
            for (Trip trip : trips) {
                if (points == 0) points = trip.points();
                points = Math.min(points, trip.points());
            }
        }

        else {
            for (Trip trip : trips) {
                if (connectivity.connected(trip.from(), trip.to()))
                    points = Math.max(points, trip.points());
            }
        }

        return (noConnectivity) ? -points : points;
    }

    /**
     * Returns the comparison between the applied-to ticket and the argument ticket
     * in alphabetical order of their textual representation
     *
     * @param that the ticket to compare the first to
     * @return a strictly negative integer if this is smaller than that,
     *              zero if equal and strictly positive otherwise
     */
    @Override
    public int compareTo(Ticket that) {

        return this.text().compareTo(that.text());
    }

    /**
     * Returns the textual representation of the ticket
     * @return the textual representation of the ticket
     */
    @Override
    public String toString() {

        return this.text();
    }
}
