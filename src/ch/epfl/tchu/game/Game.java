package ch.epfl.tchu.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import ch.epfl.tchu.gui.Info;

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
	 */
	public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
		Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

		// Initialiazing every player
		PlayerId.ALL.stream().forEach((PlayerId playerId) -> { 
			players.get(playerId).initPlayers(playerId, playerNames); 
		});

		// Generating the initial game state
		GameState gameState = GameState.initial(tickets, rng);
		// A map that links a player's identifier to their info class instance
		Map<PlayerId, Info> playerInfos = new HashMap<>();
		PlayerId.ALL.stream().forEach((PlayerId playerId) -> {
			playerInfos.put(playerId, new Info(playerNames.get(playerId)));
			players.get(playerId).receiveInfo(playerInfos.get(gameState.currentPlayerId()).willPlayFirst());
		});

		// Drawing initial tickets and asking the player which they want to keep
		for (int i = 0; i < PlayerId.COUNT; i++) {
			SortedBag<Ticket> initialTickets = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT * i).topTickets(Constants.INITIAL_TICKETS_COUNT);
			players.get(PlayerId.ALL.get(i)).setInitialTicketChoice(initialTickets);
			SortedBag<Ticket> keptTickets = players.get(PlayerId.ALL.get(i)).chooseInitialTickets();
			for (PlayerId playerId : PlayerId.ALL) {
				players.get(playerId).receiveInfo(playerInfos.get(PlayerId.ALL.get(i)).keptTickets(keptTickets.size()));
			}
		}
		// Updating the game state after having drawn the initial tickets
		gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT * PlayerId.COUNT);
	}
}
