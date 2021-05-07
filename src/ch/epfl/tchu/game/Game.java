package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Player.TurnKind;
import ch.epfl.tchu.game.Route.Level;
import ch.epfl.tchu.gui.Info;

/**
 * A game of tCHu
 * 
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class Game {
	private static final int DRAW_CARDS_TIMES = 2;

	/**
	 * The class is non-instanciable
	 */
	private Game() {}

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
		Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);

		/* * * * * * * * * *
		 * The Early Game  *
		 * * * * * * * * * */

		// Initialiazing every player
		PlayerId.ALL.stream()
		.forEach(p -> players.get(p).initPlayers(p, playerNames));

		// Generating the initial game state
		GameState gameState = GameState.initial(tickets, rng);

		// Declaring a map that links a player's identifier to their info class instance
		Map<PlayerId, Info> playerInfos = PlayerId.ALL.stream()
		.collect(Collectors.toMap(p -> p, p -> new Info(playerNames.get(p))));
		
		// Informing the players which plays first
		sendInfo(players, playerInfos.get(gameState.currentPlayerId()).willPlayFirst());

		// Drawing initial tickets and asking the player which they want to keep
		// TODO: Tell TA that couldn't get given solution to work
		for (int i = 0; i < PlayerId.COUNT; i++) {
			SortedBag<Ticket> initialTickets = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT * i).topTickets(Constants.INITIAL_TICKETS_COUNT);
			players.get(PlayerId.ALL.get(i)).setInitialTicketChoice(initialTickets);
			updatePlayerStates(players, gameState);
			gameState = gameState.withInitiallyChosenTickets(PlayerId.ALL.get(i), players.get(PlayerId.ALL.get(i)).chooseInitialTickets());
		}

		// Informing every player of the number of initially drawn tickets only once everyone has drawn
		for (PlayerId playerId : PlayerId.ALL)
			sendInfo(players, playerInfos.get(playerId).keptTickets(gameState.currentPlayerState().ticketCount()));

		// Updating the game state after having drawn the initial tickets
		gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT * PlayerId.COUNT);
		
		/* * * * * * * * * *
		 * The Middle Game *
		 * * * * * * * * * */

		// TODO: Tell TA that currentPlayerInfo only accesses a map value so doesn't calculate anything
		// Keep playing until the last turn is played
		while (Objects.isNull(gameState.lastPlayer()) || gameState.currentPlayerId() != gameState.lastPlayer()) {
			// Informing that the last turn begins if the conditions are met
			if (gameState.lastTurnBegins()) sendInfo(players, playerInfos.get(gameState.currentPlayerId()).lastTurnBegins(gameState.currentPlayerState().carCount()));
			
			// TODO: Fix the fact that the first player's turn passes
			// Switching the game state to the next turn
			gameState = gameState.forNextTurn();
		
			Info currentPlayerInfo = playerInfos.get(gameState.currentPlayerId());

			// Announcing which player plays this turn
			Player currentPlayer = players.get(gameState.currentPlayerId());
			updatePlayerStates(players, gameState);
			sendInfo(players, currentPlayerInfo.canPlay());

			// Asks what the player's next move will be
			TurnKind playerMove = currentPlayer.nextTurn();
			
			// Checking the player's next move choice
			switch (playerMove) {
				// The player decides to draw tickets
				case DRAW_TICKETS:
					sendInfo(players, currentPlayerInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT));
				
					// Asking the current player which tickets they want to keep
					SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT));
					
					// Updating the game state to a state where the current player has the chosen additional tickets
					gameState = gameState.withChosenAdditionalTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT), chosenTickets);
					
					// Informing of the number of tickets drawn by the current player
					sendInfo(players, currentPlayerInfo.keptTickets(chosenTickets.size()));
					break;

				// The player decides to draw cards
				case DRAW_CARDS:
					// The player will draw a given number of cards
					for (int i = 0; i < DRAW_CARDS_TIMES; i++) {
						updatePlayerStates(players, gameState);

						// Determines which card the player wants to draw
						int drawSlot = currentPlayer.drawSlot();

						gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
						// The player draws a deck card
						if (drawSlot == Constants.DECK_SLOT) {
							// Updates the game state to a state where a top deck card has been drawn
							gameState = gameState.withBlindlyDrawnCard();

							// Informing that the current play drew a deck card
							sendInfo(players, currentPlayerInfo.drewBlindCard());
						}
						// The player draws a face-up card
						else {
							// Determines which face-up card the player drew
							Card drawnCard = gameState.cardState().faceUpCard(drawSlot);

							// Updates the game state to a state where the face-up card was drawn
							gameState = gameState.withDrawnFaceUpCard(drawSlot);

							// Informing that the current player drew the given face-up card
							sendInfo(players, currentPlayerInfo.drewVisibleCard(drawnCard));
						}
					}
					break;

				// The player decides to claim a route 
				case CLAIM_ROUTE:
					Route claimedRoute = currentPlayer.claimedRoute();
					SortedBag<Card> initialClaimCards = currentPlayer.initialClaimCards();

					// The player attempts to claim a tunnel
					if (claimedRoute.level().equals(Level.UNDERGROUND)) {
						// Informs that the current player is trying to claim a Tunnel
						sendInfo(players, currentPlayerInfo.attemptsTunnelClaim(claimedRoute, initialClaimCards));

						// Drawing the additional tunnel cards
						SortedBag.Builder<Card> additionalCardsBuilder = new SortedBag.Builder<Card>();
						for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
							gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
							additionalCardsBuilder.add(gameState.topCard());
							gameState = gameState.withoutTopCard();
						}
						SortedBag<Card> additionalCards = additionalCardsBuilder.build();

						// The additional cards are added to the pile of discards
						gameState = gameState.withMoreDiscardedCards(additionalCards);
						
						// Computing the additional claim cards that the additional tunnel cards imply
						int additionalClaimCardsCount = claimedRoute.additionalClaimCardsCount(initialClaimCards, additionalCards);

						// Informing that the current player drew additional cards to try and claim the tunnel
						sendInfo(players, currentPlayerInfo.drewAdditionalCards(additionalCards, additionalClaimCardsCount));
						
						// The player does not need to use any additional cards to claim the tunnel
						if (additionalClaimCardsCount == 0) {
							gameState = gameState
							.withClaimedRoute(claimedRoute, initialClaimCards);

							// Informing that the current player claimed the tunnel using only the initial claim cards
							sendInfo(players, currentPlayerInfo.claimedRoute(claimedRoute, initialClaimCards));
						}
						else {
							// Computing the list of possible additional claim cards the player can choose to try and claim the tunnel
							List<SortedBag<Card>> possibleAdditionalClaimCards = gameState.currentPlayerState().possibleAdditionalCards
							(additionalClaimCardsCount, initialClaimCards, additionalCardsBuilder.build());

							// The player has at least one set of additional claim cards to play
							if (possibleAdditionalClaimCards.size() > 0) {
								// The player is asked whether they want to claim the tunnel and if so with what cards
								SortedBag<Card> chosenAdditionalClaimCards = currentPlayer.chooseAdditionalCards(possibleAdditionalClaimCards);
								
								// The player cannot or doesn't want to claim the tunnel
								if (chosenAdditionalClaimCards.isEmpty()) {
									// Informing that the current player did not claim the tunnel
									sendInfo(players, currentPlayerInfo.didNotClaimRoute(claimedRoute));
								}
								else {
									// Adding the additional claim cards to the initial cards
									SortedBag<Card> finalClaimCards = initialClaimCards.union(chosenAdditionalClaimCards);
									gameState = gameState
									.withClaimedRoute(claimedRoute, finalClaimCards);
									// Informing that the current player claimed the tunnel using additional cards
									sendInfo(players, currentPlayerInfo.claimedRoute(claimedRoute, finalClaimCards));
								}
							}
							else {
								// Informing that the current player didn't claim the route
								sendInfo(players, currentPlayerInfo.didNotClaimRoute(claimedRoute));
							}
						}
					}
					// The player claims an overground route
					else if (claimedRoute.level().equals(Level.OVERGROUND)) {
						gameState = gameState.withClaimedRoute(claimedRoute, initialClaimCards);
						// Informing that the current player claimed the route
						sendInfo(players, currentPlayerInfo.claimedRoute(claimedRoute, initialClaimCards));
					}
					break;
			}
		}
	  
		/* * * * * * * * *
		 * The End Game  *
		 * * * * * * * * */

		// Letting the players know how the game effectively ended
		updatePlayerStates(players, gameState);

		// Declaring an effectively final game state to use in lambdas
		GameState gameStateFinal = gameState;

		// Declaring a map to keep track of the players' scores
		Map<PlayerId, Integer> playersScores = PlayerId.ALL.stream()
		.collect(Collectors.toMap(p -> p, p -> gameStateFinal.playerState(p).finalPoints()));

		// Computing the longest trail among the players'
		Integer longestTrailLength = PlayerId.ALL.stream()
		.map(p -> Trail.longest(gameStateFinal.playerState(p).routes()).length())
		.max(Integer::compare)
		.get();

		// Determining the players who earn the bonus for the longest trail
		List<PlayerId> bonusEarningPlayers = PlayerId.ALL.stream()
		.filter(p -> Trail.longest(gameStateFinal.playerState(p).routes()).length() == longestTrailLength)
		.collect(Collectors.toList());

		// Awarding the eligible players the bonus points and informing them
		bonusEarningPlayers.stream()
		.forEach(p -> { 
			playersScores.replace(p, playersScores.get(p) + Constants.LONGEST_TRAIL_BONUS_POINTS);
			sendInfo(players, playerInfos.get(p).getsLongestTrailBonus(Trail.longest(gameStateFinal.playerState(p).routes()))); 
		});
		
		// Getting the highest score from all players
		Integer highestScore = PlayerId.ALL.stream()
		.map((PlayerId playerId) -> playersScores.get(playerId))
		.max(Integer::compareTo).get();

		// Getting the players who got the highest score
		List<PlayerId> winners = PlayerId.ALL.stream()
		.filter((PlayerId playerId) -> playersScores.get(playerId) == highestScore)
		.collect(Collectors.toList());

		// Breaking down the game, declaring either a win for a player or a draw
		if (winners.size() == 1) sendInfo(players, playerInfos.get(winners.get(0)).won(playersScores.get(winners.get(0)), playersScores.get(winners.get(0).next())));
		else sendInfo(players, Info.draw(new ArrayList<String>(playerNames.values()), highestScore));
	}

	/**
	 * Transmits to every player a given information
	 * 
	 * @param players
	 * 				The map linking players to their identifier
	 * 
	 * @param info
	 * 				The information to transmit
	 */
	private static void sendInfo(Map<PlayerId, Player> players, String info) {
		PlayerId.ALL.forEach((PlayerId playerId) -> players.get(playerId).receiveInfo(info));
	}

	/**
	 * Updates every player's state for them to witness changes in-game
	 * 
	 * @param players
	 * 				The map linking players to their identifier
	 * 
	 * @param gameState
	 * 				The current game's state
	 */
	private static void updatePlayerStates(Map<PlayerId, Player> players, GameState gameState) {
		PlayerId.ALL.forEach((PlayerId playerId) -> players.get(playerId).updateState(gameState, gameState.playerState(playerId)));
	}
}
