package ch.epfl.tchu.game;

import java.util.Arrays;

import ch.epfl.tchu.Preconditions;

/**
 * The flat partition of stations
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 * @see ch.epfl.tchu.game.StationConnectivity
 */
public final class StationPartition implements StationConnectivity {
    private final int[] stationConnectivity;

    /**
     * A station partition with its connectivity
     *
     * @param stationConnectivity The connectivity of the stations for the player's network
     */
    private StationPartition(int[] stationConnectivity) {
        this.stationConnectivity = Arrays.copyOf(stationConnectivity, stationConnectivity.length);
    }

    @Override
    public boolean connected(Station s1, Station s2) {
        if (s1.id() >= stationConnectivity.length || s2.id() >= stationConnectivity.length)
            return s1.id() == s2.id();
        else
            return stationConnectivity[s1.id()] == stationConnectivity[s2.id()];
    }

    /**
     * The builder of a station partition
     */
    public static final class Builder {
        private final int[] stationConnectivity;

        /**
         * A station partition builder with a set of stations
         *
         * @param stationCount The number of stations in the set
         * @throws IllegalArgumentException If {@code stationCount} is strictly negative
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);

            this.stationConnectivity = new int[stationCount];
            for (int i = 0; i < stationConnectivity.length; i++)
                stationConnectivity[i] = i;
        }

        /**
         * The same builder except that its two given stations are connected
         *
         * @param s1 The first {@link Station}
         * @param s2 The second {@link Station}
         * @return The same builder except that its two given stations are connected
         */
        public Builder connect(Station s1, Station s2) {
            stationConnectivity[representative(s2.id())] = representative(s1.id());

            return this;
        }

        /**
         * The flat partition of the stations corresponding to the builder's deep station partition
         *
         * @return The {@link StationPartition} of the stations corresponding to the builder's deep station partition
         */
        public StationPartition build() {
            for (int i = 0; i < stationConnectivity.length; i++)
                stationConnectivity[i] = representative(i);

            return new StationPartition(stationConnectivity);
        }

        /**
         * The representative of the given station
         *
         * @param stationId The {@link Station}
         * @return the representative of {@code stationId}
         */
        private int representative(int stationId) {
            int currentRepresentative = stationConnectivity[stationId];
            while (currentRepresentative != stationConnectivity[currentRepresentative])
                currentRepresentative = stationConnectivity[currentRepresentative];

            return currentRepresentative;
        }
    }
}
