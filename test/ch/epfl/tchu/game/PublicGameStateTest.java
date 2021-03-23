package ch.epfl.tchu.game;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;


public class PublicGameStateTest {

	@Test
	void PublicGameStateTestWithNullLastPlayer() {
		assertDoesNotThrow(() -> { new PublicGameState(0, new PublicCardState(SortedBag.of(5, Card.BLACK).toList(), 7, 0), PlayerId.PLAYER_1, Map.of(PlayerId.PLAYER_1, new PublicPlayerState(0, 0, List.of()), PlayerId.PLAYER_2, new PublicPlayerState(0, 0, List.of())), null);});
	}

	@Test
	void PublicGameStateCanDrawCardsTest() {
		PublicGameState gameState = new PublicGameState(0, new PublicCardState(SortedBag.of(5, Card.BLACK).toList(), 7, 0), PlayerId.PLAYER_1, Map.of(PlayerId.PLAYER_1, new PublicPlayerState(0, 0, List.of()), PlayerId.PLAYER_2, new PublicPlayerState(0, 0, List.of())), null);

		assertTrue(gameState.canDrawCards());

		gameState = new PublicGameState(0, new PublicCardState(SortedBag.of(5, Card.BLACK).toList(), 3, 2), PlayerId.PLAYER_1, Map.of(PlayerId.PLAYER_1, new PublicPlayerState(0, 0, List.of()), PlayerId.PLAYER_2, new PublicPlayerState(0, 0, List.of())), null);

		assertTrue(gameState.canDrawCards());

		gameState = new PublicGameState(0, new PublicCardState(SortedBag.of(5, Card.BLACK).toList(), 1, 2), PlayerId.PLAYER_1, Map.of(PlayerId.PLAYER_1, new PublicPlayerState(0, 0, List.of()), PlayerId.PLAYER_2, new PublicPlayerState(0, 0, List.of())), null);

		assertFalse(gameState.canDrawCards());
	}

	@Test
	void PublicGameStateClaimedRoutes() {
		PublicGameState gameState = new PublicGameState(0, new PublicCardState(SortedBag.of(5, Card.BLACK).toList(), 3, 2), PlayerId.PLAYER_1, Map.of(PlayerId.PLAYER_1, new PublicPlayerState(0, 0, List.of()), PlayerId.PLAYER_2, new PublicPlayerState(0, 0, List.of())), null);

		assertTrue(gameState.claimedRoutes().isEmpty());
	}

	@Test
	void PublicGameStateLastPlayer() {
		PublicGameState gameState = new PublicGameState(0, new PublicCardState(SortedBag.of(5, Card.BLACK).toList(), 3, 2), PlayerId.PLAYER_1, Map.of(PlayerId.PLAYER_1, new PublicPlayerState(0, 0, List.of()), PlayerId.PLAYER_2, new PublicPlayerState(0, 0, List.of())), null);

		assertTrue(Objects.isNull(gameState.lastPlayer()));
	}
}
