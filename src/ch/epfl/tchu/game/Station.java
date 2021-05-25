package ch.epfl.tchu.game;

import java.util.Objects;

import ch.epfl.tchu.Preconditions;

/**
 * A station with a name and a unique identifier number
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public final class Station {
    private final int id;
    private final String name;

    /**
     * Constructs a station with the given identifier number and name
     *
     * @param id   The station identifier
     * @param name The station name
     * @throws IllegalArgumentException if the identifier is strictly negative
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);

        this.id = id;
        this.name = Objects.requireNonNull(name);
    }

    /**
     * Returns the station unique identifier number
     *
     * @return the station unique identifier number
     */
    public int id() {
        return id;
    }

    /**
     * Returns the station name
     *
     * @return the station name
     */
    public String name() {
        return name;
    }

    /**
     * Returns the station name
     *
     * @return the station name
     */
    @Override
    public String toString() {
        return name;
    }
}

