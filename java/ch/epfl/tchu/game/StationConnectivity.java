package ch.epfl.tchu.game;

/**
 * method establishes if 2 stations are connected
 */

public interface StationConnectivity {

	public abstract boolean connected(Station s1, Station s2);

}

