package ch.epfl.tchu.game;

/**
 * An interface for the station connectivity logic
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public interface StationConnectivity {
    /**
     * Returns the connection status of the two
     * given stations in the player's network
     *
     * @param s1 The departure station
     * @param s2 The arrival station
     * @return true if the two stations are connected
     */
    boolean connected(Station s1, Station s2);

}

