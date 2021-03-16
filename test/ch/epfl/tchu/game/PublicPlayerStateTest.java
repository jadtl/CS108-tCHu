package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class PublicPlayerStateTest {
  @Test
  void publicPlayerStateConstructorFailsOnIncorrectCounts() {
    assertThrows(IllegalArgumentException.class, 
    () -> { new PublicPlayerState(-1, 0, new ArrayList<Route>()); });
    assertThrows(IllegalArgumentException.class, 
    () -> { new PublicPlayerState(0, -1, new ArrayList<Route>()); });
  }

  @Test
  void publicPlayerStateCarCountIsCorrect() {
    var s1 = new Station(1, "Yverdon");
    var s2 = new Station(2, "Fribourg");
    var s3 = new Station(3, "Neuchâtel");
    var s4 = new Station(4, "Berne");
    var s5 = new Station(5, "Lucerne");
    var s6 = new Station(6, "Soleure");

    var routes = List.of(
            new Route("A", s3, s1, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("B", s3, s6, 4, Route.Level.OVERGROUND, Color.GREEN),
            new Route("C", s4, s3, 2, Route.Level.OVERGROUND, Color.RED),
            new Route("D", s4, s6, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("E", s4, s5, 4, Route.Level.OVERGROUND, null),
            new Route("F", s4, s2, 1, Route.Level.OVERGROUND, Color.ORANGE));
    
    assertEquals(25, new PublicPlayerState(0, 0, routes).carCount());        
  }

  @Test
  void publicPlayerStateClaimPointsIsCorrect() {
    var s1 = new Station(1, "Yverdon");
    var s2 = new Station(2, "Fribourg");
    var s3 = new Station(3, "Neuchâtel");
    var s4 = new Station(4, "Berne");
    var s5 = new Station(5, "Lucerne");
    var s6 = new Station(6, "Soleure");

    var routes = List.of(
            new Route("A", s3, s1, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("B", s3, s6, 4, Route.Level.OVERGROUND, Color.GREEN),
            new Route("C", s4, s3, 2, Route.Level.OVERGROUND, Color.RED),
            new Route("D", s4, s6, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("E", s4, s5, 4, Route.Level.OVERGROUND, null),
            new Route("F", s4, s2, 1, Route.Level.OVERGROUND, Color.ORANGE));
    
    assertEquals(21, new PublicPlayerState(0, 0, routes).claimPoints());
  }
}