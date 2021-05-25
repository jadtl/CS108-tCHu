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
    }

    /**
     * A ticket from a single trip
     *
     * @param from The departure {@link Station}
     * @param to The arrival {@link Station}
     * @param points The {@link Trip} value
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }

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
                .max(Comparator.comparing(Integer::valueOf))
                .get();
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

    // TODO: Make it hold in 5 lines
    private static String computeText(List<Trip> trips) {
        Map<String, Integer> map = new HashMap<>();

        // Print the beginning of the text
        StringBuilder text = new StringBuilder(trips.get(0).from().name() + " - ");

        // Removing duplicate arrival stations
        Collection<String> arrivalNoDuplicates = trips.stream()
                .map(t -> t.to().name())
                .collect(Collectors.toCollection(TreeSet::new));

        // Storing the points for each unique arrival station in a map
        for (String arrivalStation : arrivalNoDuplicates) {
            trips.stream()
                    .filter(t -> t.to().name().equals(arrivalStation))
                    .forEach(t -> map.put(arrivalStation, t.points()));
        }

        // Print the remaining of the text depending on whether the ticket holds a unique trip or more
        if (arrivalNoDuplicates.size() == 1) {
            text.append(String.format("%s (%s)", arrivalNoDuplicates.toArray()[0], trips.get(0).points()));
        }
        else {
            text.append("{");
            for (String arrivalStation : arrivalNoDuplicates) {
                text.append(String.format("%s (%s), ", arrivalStation, map.get(arrivalStation)));
            }
            text = new StringBuilder(text.substring(0, text.length() - 2) + "}");
        }

        return text.toString();
    }
}
