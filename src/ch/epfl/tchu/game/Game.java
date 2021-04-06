package ch.epfl.tchu.game;

import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * A game of tCHu
 * 
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class Game {

	/**
	 * Makes the given players play a game of tCHu, whose names and identifiers figure in the given maps
	 * 
	 * @param players
	 * 			  A map linking a player's identifier to their entity
	 * 
	 * @param playerNames
	 * 				A map linking a player's identifier to their name
	 * 
	 * @param tickets
	 * 				The available tickets for the game
	 * 
	 * @param rng
	 * 				The random number generator
	 * 
	 * @throws IllegalArgumentException 
	 *         If one of the two maps has a size different from 2
	 * 
	 */
	public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
		Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

		PlayerId.ALL.stream().forEach((PlayerId playerId) -> { 
			players.get(playerId).initPlayers(playerId, playerNames); 
		});

		GameState gameState = new GameState();
	}
}
