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
     * A station with the given identifier number and name
     *
     * @param id   The station's identifier
     * @param name The station's name
     * @throws IllegalArgumentException If the {@code id} is strictly negative
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);

        this.id = id;
        this.name = Objects.requireNonNull(name);
    }

    /**
     * The station unique identifier number
     *
     * @return The station's unique identifier number {@code id}
     */
    public int id() {
        return id;
    }

    /**
     * The station name
     *
     * @return The station's name {@code name}
     */
    public String name() {
        return name;
    }

    /**
     * The station name
     *
     * @return The station's name {@code name}
     */
    @Override
    public String toString() {
        return name;
    }
}

