package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A trip between two stations that is worth a number of points
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class Trip {
    private final Station from;
    private final Station to;
    private final int points;

    /**
     * Constructs a trip from a station to another with a given value
     *
     * @param from the departure station
     * @param to the arrival station
     * @param points the value of the trip
     *
     * @throws IllegalArgumentException if points isn't strictly positive
     * @throws NullPointerException if from or to are null
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);

        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Returns a list of all possible trips from two lists
     * of departure and arrival stations and their points value
     *
     * @param from the departure stations
     * @param to the arrival stations
     * @param points the trips value
     *
     * @return a list of all possible trips
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        List<Trip> tripPossibilities = new ArrayList<>();

        for (Station departure : from) {
            for (Station arrival : to) {
                tripPossibilities.add(new Trip(departure, arrival, points));
            }
        }

        return tripPossibilities;
    }

    /**
     * Returns the departure station
     *
     * @return the departure station
     */
    public Station from() { return from; }

    /**
     * Returns the arrival station
     *
     * @return the arrival station
     */
    public Station to() { return to; }

    /**
     * Returns the trip value
     *
     * @return the trip value
     */
    public int points() { return points; }

    /**
     * Returns the number of points won or lost by the player depending
     * on whether or not the two trip stations are connected in their network
     *
     * @param connectivity the station connectivity of the player network
     *
     * @return positive points if both stations are connected, negative otherwise
     */
    public int points(StationConnectivity connectivity) { return connectivity.connected(from, to) ? points : -points; }
}
