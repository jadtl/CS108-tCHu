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

  // TODO canClaimRoute test
  // TODO possibleClaimCards test
  // TODO possibleAdditionalCards test
  @Test
  void possibleAdditionalCards() {
    SortedBag<Card> cards = SortedBag.of(4, Card.RED, 5, Card.LOCOMOTIVE);

    PlayerState playerState = new PlayerState(new SortedBag.Builder<Ticket>().build(), cards, new ArrayList<Route>());

    
  }
  // TODO withClaimedRoute test
}
