package ch.epfl.tchu.game;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Test;

import ch.epfl.tchu.SortedBag;
import java.util.Random;

public class GameStateTest {
	@Test
	public void gameStateInitialWorks() {
		Station LAU = new Station(0, "Lausanne");
		Station EPF = new Station(1, "EPFL");
		GameState gameState = GameState.initial(SortedBag.of(5, new Ticket(List.of(new Trip(LAU, EPF, 10)))), new Random());

		assertEquals(5, gameState.ticketsCount());
		assertEquals(Constants.ALL_CARDS.size() - 2 * Constants.INITIAL_CARDS_COUNT, gameState.cardState().totalSize());
		assertEquals(Constants.INITIAL_CARDS_COUNT, gameState.playerState(PlayerId.PLAYER_1).cardCount());
		assertEquals(Constants.INITIAL_CARDS_COUNT, gameState.playerState(PlayerId.PLAYER_2).cardCount());
	}

	@Test
	public void gameStateInitiallyChosenTicketsWorks() {
		Station LAU = new Station(0, "Lausanne");
		Station EPF = new Station(1, "EPFL");
		GameState gameState = GameState.initial(SortedBag.of(5, new Ticket(List.of(new Trip(LAU, EPF, 10)))), new Random());
		gameState = gameState.withInitiallyChosenTickets(PlayerId.PLAYER_1, SortedBag.of(5, new Ticket(List.of(new Trip(LAU, EPF, 10)))));
		gameState = gameState.withInitiallyChosenTickets(PlayerId.PLAYER_2, SortedBag.of(4, new Ticket(List.of(new Trip(LAU, EPF, 10)))));
		GameState gameStateCopy = gameState;

		assertEquals(SortedBag.of(5, new Ticket(List.of(new Trip(LAU, EPF, 10)))), gameState.playerState(PlayerId.PLAYER_1).tickets());
		assertEquals(SortedBag.of(4, new Ticket(List.of(new Trip(LAU, EPF, 10)))), gameState.playerState(PlayerId.PLAYER_2).tickets());
		assertThrows(IllegalArgumentException.class, () -> { gameStateCopy.withInitiallyChosenTickets(PlayerId.PLAYER_2, SortedBag.of(5, new Ticket(List.of(new Trip(LAU, EPF, 10))))); });
	}

	@Test
	public void gameStateWithChosenAdditionalTicketsWorks() {
		Station LAU = new Station(0, "Lausanne");
		Station EPF = new Station(1, "EPFL");
		GameState gameState = GameState.initial(SortedBag.of(5, new Ticket(List.of(new Trip(LAU, EPF, 10)))), new Random());
		gameState = gameState.withInitiallyChosenTickets(gameState.currentPlayerId(), SortedBag.of(5, new Ticket(List.of(new Trip(LAU, EPF, 10)))));
		gameState = gameState.withChosenAdditionalTickets(SortedBag.of(5, new Ticket(List.of(new Trip(LAU, EPF, 10)))), SortedBag.of(4, new Ticket(List.of(new Trip(LAU, EPF, 10)))));
		GameState gameStateCopy = gameState;

		assertThrows(IllegalArgumentException.class, () -> { gameStateCopy.withChosenAdditionalTickets(SortedBag.of(4, new Ticket(List.of(new Trip(LAU, EPF, 10)))), SortedBag.of(5, new Ticket(List.of(new Trip(LAU, EPF, 10))))); });
		assertThrows(IllegalArgumentException.class, () -> { gameStateCopy.withChosenAdditionalTickets(SortedBag.of(4, new Ticket(List.of(new Trip(LAU, EPF, 10)))), SortedBag.of(5, new Ticket(List.of(new Trip(LAU, EPF, 10))))); });
		assertThrows(IllegalArgumentException.class, () -> { gameStateCopy.withChosenAdditionalTickets(SortedBag.of(6, new Ticket(List.of(new Trip(LAU, EPF, 10)))), SortedBag.of(5, new Ticket(List.of(new Trip(LAU, EPF, 10))))); });
	} 

	@Test
	public void withDrawnFaceUpCardWorks() {
		Station LAU = new Station(0, "Lausanne");
		Station EPF = new Station(1, "EPFL");
		GameState gameState = GameState.initial(SortedBag.of(5, new Ticket(List.of(new Trip(LAU, EPF, 10)))), new Random());
		Card card = gameState.topCard();
		gameState = gameState.withDrawnFaceUpCard(0);
		GameState gameStateLessCards = gameState;
		for (int i = 0; i < gameState.cardState().deckSize() - 3; i++) {
			gameStateLessCards = gameStateLessCards.withoutTopCard();
		}
		GameState gameStateCopy = gameStateLessCards;

		assertEquals(card, gameState.cardState().faceUpCard(0));
		assertEquals(Constants.INITIAL_CARDS_COUNT + 1, gameState.currentPlayerState().cardCount());
		assertThrows(IllegalArgumentException.class, () -> { gameStateCopy.withDrawnFaceUpCard(0); });
	}
}
