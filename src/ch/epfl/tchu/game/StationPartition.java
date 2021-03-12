package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * 
 * 
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class StationPartition implements StationConnectivity {
  private final int[] stationConnectivity;

  /**
   * 
   * 
   * @param stationConnectivity
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
   * 
   */
  public static final class Builder {
    private int[] stationConnectivity;

    /**
     * 
     * 
     * @param stationCount
     */
    public Builder(int stationCount) {
      Preconditions.checkArgument(stationCount >= 0);
      
      this.stationConnectivity = new int[stationCount];
      for (int i = 0; i < stationConnectivity.length; i++)
        stationConnectivity[i] = i;
    }

    /**
     * 
     * 
     * @param s1
     * 
     * @param s2
     * 
     * @return
     */
    public Builder connect(Station s1, Station s2) {
      stationConnectivity[s2.id()] = representative(s1.id());
      
      return this;
    }

    /**
     * 
     * 
     * @return
     */
    public StationPartition build() {
      for (int i = 0; i < stationConnectivity.length; i++) {
        int currentRepresentative = representative(i);
        while (currentRepresentative != representative(currentRepresentative)) {
          currentRepresentative = representative(currentRepresentative);
        }
        stationConnectivity[i] = currentRepresentative;
      }

      return new StationPartition(stationConnectivity);
    }

    /**
     * 
     * 
     * @param stationId
     * 
     * @return
     */
    private int representative(int stationId) {
      return stationConnectivity[stationId];
    }
  }
}
