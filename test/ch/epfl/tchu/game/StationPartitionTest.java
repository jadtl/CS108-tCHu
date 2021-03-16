package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StationPartitionTest {
  @Test
  void stationPartitionBuilderConstructorFailsWithNegativeStationCount() {
    assertThrows(IllegalArgumentException.class, () -> { new StationPartition.Builder(-1); });
  }

  @Test
  void stationPartitionBuilderConstructorGeneratesDefaultStationConnectivity() {
    StationPartition stationPartition = new StationPartition.Builder(3).build();
    assertTrue(stationPartition.connected(new Station(0, ""), new Station(0, "")));
    assertTrue(stationPartition.connected(new Station(1, ""), new Station(1, "")));
    assertTrue(stationPartition.connected(new Station(2, ""), new Station(2, "")));
    assertFalse(stationPartition.connected(new Station(0, ""), new Station(1, "")));
    assertFalse(stationPartition.connected(new Station(1, ""), new Station(2, "")));
  }

  @Test
  void stationPartitionBuilderConnectConnectsStations() {
    StationPartition stationPartition = new StationPartition.Builder(3)
    .connect(new Station(0, ""), new Station(1, "")).build();
    assertTrue(stationPartition.connected(new Station(0, ""), new Station(1, "")));
  }

  @Test
  void stationPartitionBuilderBuildGeneratesFlatPartition() {
    StationPartition stationPartition = new StationPartition.Builder(3)
    .connect(new Station(0, ""), new Station(1, ""))
    .connect(new Station(0, ""), new Station(2, "")).build();
    assertTrue(stationPartition.connected(new Station(1, ""), new Station(2, "")));
    stationPartition = new StationPartition.Builder(3)
    .connect(new Station(0, ""), new Station(1, ""))
    .connect(new Station(1, ""), new Station(2, "")).build();
    assertTrue(stationPartition.connected(new Station(1, ""), new Station(2, "")));
  }
}
