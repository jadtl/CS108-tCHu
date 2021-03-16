package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * The flat partition of stations
 * 
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class StationPartition implements StationConnectivity {
  private final int[] stationConnectivity;

  /**
   * Constructs a station partition with its connectivity
   * 
   * @param stationConnectivity
   *        The connectivity of the stations for the player's network
   */
  private StationPartition(int[] stationConnectivity) { this.stationConnectivity = stationConnectivity; }

  @Override
  public boolean connected(Station s1, Station s2) {
    if (s1.id() >= stationConnectivity.length || s1.id() >= stationConnectivity.length) {
      return s1.id() == s2.id();
    } else {
      return stationConnectivity[s1.id()] == stationConnectivity[s2.id()];
    }
  }

  /**
   * The builder of a station partition
   */
  public static final class Builder {
    private int[] stationConnectivity;

    /**
     * Constructs a station partition builder with a set of stations 
     * 
     * @param stationCount
     *        The number of stations in the set
     * 
     * @throws IllegalArgumentException
     *         If stationCount is strictly negative
     */
    public Builder(int stationCount) {
      Preconditions.checkArgument(stationCount >= 0);
      
      this.stationConnectivity = new int[stationCount];
      for (int i = 0; i < stationConnectivity.length; i++)
        stationConnectivity[i] = i;
    }

    /**
     * Returns an identical builder except that its two given stations are connected
     * 
     * @param s1
     *        The first station
     * 
     * @param s2
     *        The second station
     * 
     * @return an identical builder except that its two given stations are connected
     */
    public Builder connect(Station s1, Station s2) {
      stationConnectivity[representative(s2.id())] = representative(s1.id());
      
      return this;
    }

    /**
     * Returns the flat partition of the stations corresponding to the builder's
     * in-progress deep station partition
     * 
     * @return the flat partition of the stations corresponding to the builder's
     * in-progress deep station partition
     */
    public StationPartition build() {
      for (int i = 0; i < stationConnectivity.length; i++)
        stationConnectivity[i] = representative(i);

      return new StationPartition(stationConnectivity);
    }

    /**
     * Returns the representative of the given station
     * 
     * @param stationId
     *        The station
     * 
     * @return the representative of the given station
     */
    private int representative(int stationId) {
      int currentRepresentative = stationConnectivity[stationId];
      while (currentRepresentative != stationConnectivity[currentRepresentative])
        currentRepresentative = stationConnectivity[currentRepresentative];
        
      return currentRepresentative;
    }
  }
}
