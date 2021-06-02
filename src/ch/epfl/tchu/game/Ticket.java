package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A ticket that holds a number of trips of the same departure station
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public final class Ticket implements Comparable<Ticket> {
    private final List<Trip> trips;
    private final String text;
    private final String fromStation;
    private final List<String> toStations;

    /**
     * A ticket from a list of trips of the same departure station
     *
     * @param trips The {@link List} of {@link Trip}
     * @throws IllegalArgumentException If {@code trips} {@link List#isEmpty()} or if there are more than one departure station
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(!trips.isEmpty());

        Collection<String> fromStations = trips.stream()
                .map(t -> t.from().name())
                .collect(Collectors.toCollection(TreeSet::new));

        Preconditions.checkArgument(fromStations.size() == 1);

        this.trips = List.copyOf(trips);
        this.text = computeText(this.trips);
        this.fromStation = fromStations.iterator().next();
        this.toStations = List.copyOf(trips.stream()
                .map(t -> t.to().name())
                .collect(Collectors.toCollection(TreeSet::new)));
        System.out.println(toStations);
    }

    /**
     * A ticket from a single trip
     *
     * @param from   The departure {@link Station}
     * @param to     The arrival {@link Station}
     * @param points The {@link Trip} value
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }

    /**
     * @return The departure station of the ticket
     */
    public String fromStation() { return fromStation; }

    /*
     * @return The arrival stations of the ticket
     */
    public List<String> toStations() { return List.copyOf(toStations); }

    /**
     * The textual representation of the ticket
     *
     * @return The textual representation of the ticket
     */
    public String text() {
        return text;
    }

    /**
     * The points value of the ticket according the the player connectivity
     *
     * @param connectivity The {@link StationConnectivity} of the player network
     * @return The points value of the {@link Ticket} according to {@code connectivity}
     */
    public int points(StationConnectivity connectivity) {
        return trips.stream()
                .map(t -> t.points(connectivity))
                .reduce(Integer.MIN_VALUE, Integer::max);
    }

    /**
     * The comparison between the applied-to ticket and the argument ticket in alphabetical order of their textual representation
     *
     * @param that The {@link Ticket} to compare the first to
     * @return A strictly negative integer if this is smaller than that, zero if equal and strictly positive otherwise
     */
    @Override
    public int compareTo(Ticket that) {
        return this.text().compareTo(that.text());
    }

    /**
     * The textual representation of the ticket
     *
     * @return The textual representation of the ticket
     */
    @Override
    public String toString() {
        return this.text();
    }

    private static String computeText(List<Trip> trips) {
        StringBuilder stringBuilder = new StringBuilder(trips.get(0).from().name() + " - ");
        Collection<String> arrivalStations = trips.stream()
                .map(t -> String.format("%s (%s)", t.to().name(), t.points()))
                .collect(Collectors.toCollection(TreeSet::new));
        if (trips.size() > 1)
            stringBuilder
                    .append("{")
                    .append(String.join(", ", arrivalStations))
                    .append("}");
        else
            stringBuilder.append(String.join(", ", arrivalStations));

        return stringBuilder.toString();
    }
}
