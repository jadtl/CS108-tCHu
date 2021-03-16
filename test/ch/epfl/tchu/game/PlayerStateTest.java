package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class PlayerStateTest {
  @Test
  void playerStateConstructorWorksOnKnownExample() {
    var s1 = new Station(1, "Yverdon");
    var s2 = new Station(2, "Fribourg");
    var s3 = new Station(3, "Neuchâtel");
    var s4 = new Station(4, "Berne");
    var s5 = new Station(5, "Lucerne");
    var s6 = new Station(6, "Soleure");

    var t1 = new Ticket(s1, s3, 13);
    var t2 = new Ticket(s2, s4, 2);

    var routes = List.of(
            new Route("A", s3, s1, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("B", s3, s6, 4, Route.Level.OVERGROUND, Color.GREEN),
            new Route("C", s4, s3, 2, Route.Level.OVERGROUND, Color.RED),
            new Route("D", s4, s6, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("E", s4, s5, 4, Route.Level.OVERGROUND, null),
            new Route("F", s4, s1, 1, Route.Level.OVERGROUND, Color.ORANGE));
    
    PlayerState playerState = new PlayerState(SortedBag.of(2, t2, 3, t1), SortedBag.of(3, Card.ORANGE), routes);
    
    assertEquals(35, playerState.ticketPoints());

    routes = List.of(
            new Route("A", s3, s1, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("B", s3, s6, 4, Route.Level.OVERGROUND, Color.GREEN),
            new Route("C", s4, s3, 2, Route.Level.OVERGROUND, Color.RED),
            new Route("D", s4, s6, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("E", s4, s5, 4, Route.Level.OVERGROUND, null),
            new Route("F", s4, s2, 1, Route.Level.OVERGROUND, Color.ORANGE));

    playerState = new PlayerState(SortedBag.of(2, t2, 3, t1), SortedBag.of(3, Card.ORANGE), routes);

    assertEquals(43, playerState.ticketPoints()); 
  }

  @Test
  void playerStateInitialFailsOnIncorrectInitialCardsSize() {
    assertThrows(IllegalArgumentException.class, () -> { PlayerState.initial(SortedBag.of(Constants.INITIAL_CARDS_COUNT + 1, Card.BLACK)); });
  }

  @Test
  void playerStateWithAddedTicketsWorks() {
    var s1 = new Station(1, "Yverdon");
    var s2 = new Station(2, "Fribourg");
    var s3 = new Station(3, "Neuchâtel");
    var s4 = new Station(4, "Berne");

    var t1 = new Ticket(s1, s3, 13);
    var t2 = new Ticket(s2, s4, 2);

    assertEquals(3, PlayerState.initial(SortedBag.of(Constants.INITIAL_CARDS_COUNT, Card.RED)).withAddedTickets(SortedBag.of(2, t1, 1, t2)).ticketCount());
  }

  @Test
  void playerStateWithAddedCardsWorks() {  
    PlayerState playerState = PlayerState.initial(SortedBag.of(Constants.INITIAL_CARDS_COUNT, Card.BLACK));

    playerState = playerState.withAddedCard(Card.GREEN);
    assertEquals(Constants.INITIAL_CARDS_COUNT + 1, playerState.cardCount());
    assertEquals(SortedBag.of(4, Card.BLACK, 1, Card.GREEN), playerState.cards());

    playerState = playerState.withAddedCards(SortedBag.of(2, Card.BLACK, 3, Card.LOCOMOTIVE));
    assertEquals(Constants.INITIAL_CARDS_COUNT + 6, playerState.cardCount());
    assertEquals("{6×BLACK, GREEN, 3×LOCOMOTIVE}", playerState.cards().toString());
  }

  @Test
  void playerStateWorksWithEmptyRoutes() {
    PlayerState playerState = PlayerState.initial(SortedBag.of(Constants.INITIAL_CARDS_COUNT, Card.LOCOMOTIVE));

    assertEquals(0, playerState.claimPoints());
    assertEquals(0, playerState.ticketPoints());
    assertEquals(Constants.INITIAL_CAR_COUNT, playerState.carCount());
    assertTrue(playerState.tickets().isEmpty());
  }

  @Test
  void playerStateCanClaimRouteWorksWithKnownExamples() {
    var s1 = new Station(1, "Yverdon");
    var s2 = new Station(2, "Fribourg");
    var s3 = new Station(3, "Neuchâtel");
    var s4 = new Station(4, "Berne");
    var s5 = new Station(5, "Lucerne");
    var s6 = new Station(6, "Soleure");

    var t1 = new Ticket(s1, s3, 13);
    var t2 = new Ticket(s2, s4, 2);

    var r1 = new Route("A", s3, s1, 2, Route.Level.OVERGROUND, Color.BLACK);
    var r2 = new Route("A", s3, s1, 2, Route.Level.UNDERGROUND, Color.BLACK);

    var routes = List.of(
            new Route("B", s3, s6, 6, Route.Level.OVERGROUND, Color.GREEN),
            new Route("C", s4, s3, 6, Route.Level.OVERGROUND, Color.RED),
            new Route("D", s4, s6, 6, Route.Level.OVERGROUND, Color.BLACK),
            new Route("E", s4, s5, 6, Route.Level.OVERGROUND, null),
            new Route("F", s4, s1, 5, Route.Level.OVERGROUND, Color.ORANGE),
            new Route("G", s4, s5, 3, Route.Level.OVERGROUND, null),
            new Route("H", s4, s1, 5, Route.Level.OVERGROUND, Color.ORANGE));
    
    var playerState1 = new PlayerState(SortedBag.of(2, t2, 3, t1), SortedBag.of(3, Card.BLACK), routes);
    assertTrue(playerState1.canClaimRoute(r1));

    playerState1 = new PlayerState(SortedBag.of(2, t2, 3, t1), SortedBag.of(1, Card.BLACK), routes);
    assertFalse(playerState1.canClaimRoute(r1));

    playerState1 = new PlayerState(SortedBag.of(2, t2, 3, t1), SortedBag.of(2, Card.LOCOMOTIVE), routes);
    assertFalse(playerState1.canClaimRoute(r1));
    assertTrue(playerState1.canClaimRoute(r2));

    playerState1 = new PlayerState(SortedBag.of(2, t2, 3, t1), SortedBag.of(1, Card.LOCOMOTIVE, 1, Card.BLACK), routes);
    assertFalse(playerState1.canClaimRoute(r1));
    assertTrue(playerState1.canClaimRoute(r2));

    playerState1 = new PlayerState(SortedBag.of(2, t2, 3, t1), SortedBag.of(), routes);
    assertFalse(playerState1.canClaimRoute(r1));

    routes = List.of(
            new Route("B", s3, s6, 6, Route.Level.OVERGROUND, Color.GREEN),
            new Route("C", s4, s3, 6, Route.Level.OVERGROUND, Color.RED),
            new Route("D", s4, s6, 6, Route.Level.OVERGROUND, Color.BLACK),
            new Route("E", s4, s5, 6, Route.Level.OVERGROUND, null),
            new Route("F", s4, s1, 5, Route.Level.OVERGROUND, Color.ORANGE),
            new Route("G", s4, s5, 5, Route.Level.OVERGROUND, null),
            new Route("H", s4, s1, 5, Route.Level.OVERGROUND, Color.ORANGE));
    var playerState2 = new PlayerState(SortedBag.of(2, t2, 3, t1), SortedBag.of(1, Card.LOCOMOTIVE, 1, Card.BLACK), routes);
    assertThrows(IllegalArgumentException.class, () -> { playerState2.canClaimRoute(r2); });
  }
  @Test
  void playerStatePossibleClaimCardsWorksWithKnownExamples() {
    var s1 = new Station(1, "Yverdon");
    var s2 = new Station(2, "Fribourg");
    var s3 = new Station(3, "Neuchâtel");
    var s4 = new Station(4, "Berne");
    var s5 = new Station(5, "Lucerne");
    var s6 = new Station(6, "Soleure");

    var t1 = new Ticket(s1, s3, 13);
    var t2 = new Ticket(s2, s4, 2);

    var r1 = new Route("A", s3, s1, 2, Route.Level.OVERGROUND, Color.BLACK);
    var r2 = new Route("A", s3, s1, 2, Route.Level.UNDERGROUND, Color.BLACK);

    var routes = List.of(
            new Route("B", s3, s6, 6, Route.Level.OVERGROUND, Color.GREEN),
            new Route("C", s4, s3, 6, Route.Level.OVERGROUND, Color.RED),
            new Route("D", s4, s6, 6, Route.Level.OVERGROUND, Color.BLACK),
            new Route("E", s4, s5, 6, Route.Level.OVERGROUND, null),
            new Route("F", s4, s1, 5, Route.Level.OVERGROUND, Color.ORANGE),
            new Route("G", s4, s5, 3, Route.Level.OVERGROUND, null),
            new Route("H", s4, s1, 5, Route.Level.OVERGROUND, Color.ORANGE));
    var playerState1 = new PlayerState(SortedBag.of(2, t2, 3, t1), SortedBag.of(), routes);
    assertTrue(playerState1.possibleClaimCards(r1).isEmpty());
    playerState1 = new PlayerState(SortedBag.of(2, t2, 3, t1), SortedBag.of(1, Card.BLACK, 2, Card.LOCOMOTIVE), routes);
    System.out.println(playerState1.possibleClaimCards(r2));
  }
  
  @Test
  void playerStatePossibleAdditionalCardsWorksWithKnownExamples() {
    SortedBag<Card> cards = new SortedBag.Builder<Card>().add(SortedBag.of(7, Card.RED, 5, Card.LOCOMOTIVE)).add(4, Card.GREEN).build();

    PlayerState playerState = new PlayerState(new SortedBag.Builder<Ticket>().build(), cards, new ArrayList<Route>());
    assertEquals("[{3×RED}, {2×RED, LOCOMOTIVE}, {RED, 2×LOCOMOTIVE}, {3×LOCOMOTIVE}]", playerState.possibleAdditionalCards(3, SortedBag.of(3, Card.RED), SortedBag.of(2, Card.RED, 1, Card.BLACK)).toString());
  }

  @Test
  void withClaimedRoute() {
    var s1 = new Station(1, "Yverdon");
    var s2 = new Station(2, "Fribourg");
    var s3 = new Station(3, "Neuchâtel");
    var s4 = new Station(4, "Berne");
    var s5 = new Station(5, "Lucerne");
    var s6 = new Station(6, "Soleure");

    var t1 = new Ticket(s1, s3, 13);
    var t2 = new Ticket(s2, s4, 2);

    var r1 = new Route("A", s3, s1, 2, Route.Level.OVERGROUND, Color.BLACK);

    var routes = List.of(
            new Route("B", s3, s6, 6, Route.Level.OVERGROUND, Color.GREEN),
            new Route("C", s4, s3, 6, Route.Level.OVERGROUND, Color.RED),
            new Route("D", s4, s6, 6, Route.Level.OVERGROUND, Color.BLACK),
            new Route("E", s4, s5, 6, Route.Level.OVERGROUND, null),
            new Route("F", s4, s1, 5, Route.Level.OVERGROUND, Color.ORANGE),
            new Route("G", s4, s5, 3, Route.Level.OVERGROUND, null),
            new Route("H", s4, s1, 5, Route.Level.OVERGROUND, Color.ORANGE));
    var playerState1 = new PlayerState(SortedBag.of(2, t2, 3, t1), SortedBag.of(3, Card.BLACK), routes);

    playerState1 = playerState1.withClaimedRoute(r1, SortedBag.of(2, Card.BLACK));
    assertEquals(1, playerState1.cardCount());
    assertEquals(1, playerState1.carCount());
  }
}
