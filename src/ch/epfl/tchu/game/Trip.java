package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A trip between two stations that is worth a number of points
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public final class Trip {
    private final Station from;
    private final Station to;
    private final int points;

    /**
     * A trip from a station to another with a given value
     *
     * @param from   The departure {@link Station}
     * @param to     The arrival {@link Station}
     * @param points The value of the {@link Trip}
     * @throws IllegalArgumentException If {@code points} isn't strictly positive
     * @throws NullPointerException     If {@code from} or {@code to} are {@code null}
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);

        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * A list of all possible trips from two lists of departure and arrival stations and their points value
     *
     * @param from   The {@link List} of departure {@link Station}
     * @param to     The {@link List} of arrival {@link Station}
     * @param points The {@link List} of {@link Trip} value
     * @return A {@link List} of all possible {@link Trip}
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument(!from.isEmpty() && !to.isEmpty());

        List<Trip> tripPossibilities = new ArrayList<>();
        for (Station departure : from)
            for (Station arrival : to)
                tripPossibilities.add(new Trip(departure, arrival, points));

        return tripPossibilities;
    }

    /**
     * The departure station
     *
     * @return The departure {@link Station}
     */
    public Station from() {
        return from;
    }

    /**
     * The arrival station
     *
     * @return The arrival {@link Station}
     */
    public Station to() {
        return to;
    }

    /**
     * The trip value
     *
     * @return The {@link Trip} value
     */
    public int points() {
        return points;
    }

    /**
     * The number of points won or lost by the player depending on the player's network
     *
     * @param connectivity The {@link StationConnectivity} of the player network
     * @return Positive points if both stations are connected according to {@code connectivity}, negative otherwise
     */
    public int points(StationConnectivity connectivity) {
        return connectivity.connected(from, to) ? points : -points;
    }
}
