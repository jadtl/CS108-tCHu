package ch.epfl.tchu.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Test;

import ch.epfl.tchu.SortedBag;
import java.util.Random;

public class GameStateTest {
	
	@Test
	public void GameStateInitialTest() {
		Station LAU = new Station(0, "Lausanne");
		Station EPF = new Station(1, "EPFL");
		GameState gameState = GameState.initial(SortedBag.of(5, new Ticket(List.of(new Trip(LAU, EPF, 10)))), new Random());

		assertEquals(5, gameState.ticketsCount());
		assertEquals(Constants.ALL_CARDS.size() - 2 * Constants.INITIAL_CARDS_COUNT, gameState.cardState().totalSize());
		assertEquals(Constants.INITIAL_CARDS_COUNT, gameState.playerState(PlayerId.PLAYER_1).cardCount());
		assertEquals(Constants.INITIAL_CARDS_COUNT, gameState.playerState(PlayerId.PLAYER_2).cardCount());
	}
}
