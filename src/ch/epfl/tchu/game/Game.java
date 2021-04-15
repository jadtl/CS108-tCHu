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
		sendInfo(players,playerInfos.get(gameState.currentPlayerId()).willPlayFirst());

		// Drawing initial tickets and asking the player which they want to keep
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

		// Keep playing until the last turn is played
		while (Objects.isNull(gameState.lastPlayer()) || gameState.currentPlayerId() != gameState.lastPlayer()) {
			if (gameState.lastTurnBegins()) sendInfo(players, playerInfos.get(gameState.currentPlayerId()).lastTurnBegins(gameState.currentPlayerState().carCount()));
			gameState = gameState.forNextTurn();

			Player currentPlayer = players.get(gameState.currentPlayerId());
			updatePlayerStates(players, gameState);
			sendInfo(players, playerInfos.get(gameState.currentPlayerId()).canPlay());
			TurnKind playerMove = currentPlayer.nextTurn();

			switch (playerMove) {
				case DRAW_TICKETS:
					sendInfo(players, playerInfos.get(gameState.currentPlayerId()).drewTickets(Constants.IN_GAME_TICKETS_COUNT));
					SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT));
					gameState = gameState.withChosenAdditionalTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT), chosenTickets);
					sendInfo(players, playerInfos.get(gameState.currentPlayerId()).keptTickets(chosenTickets.size()));
					break;

				case DRAW_CARDS:
					for (int i = 0; i < DRAW_CARDS_TIMES; i++) {
						updatePlayerStates(players, gameState);
						int drawSlot = currentPlayer.drawSlot();
						if (drawSlot == Constants.DECK_SLOT) {
							gameState = gameState
							.withCardsDeckRecreatedIfNeeded(rng)
							.withBlindlyDrawnCard();
							sendInfo(players, playerInfos.get(gameState.currentPlayerId()).drewBlindCard());
						}
						else {
							Card drawnCard = gameState.cardState().faceUpCard(drawSlot);
							gameState = gameState
							.withCardsDeckRecreatedIfNeeded(rng)
							.withDrawnFaceUpCard(drawSlot);
							sendInfo(players, playerInfos.get(gameState.currentPlayerId()).drewVisibleCard(drawnCard));
						}
					}
					break;

				case CLAIM_ROUTE:
					Route claimedRoute = currentPlayer.claimedRoute();
					SortedBag<Card> initialClaimCards = currentPlayer.initialClaimCards();

					if (claimedRoute.level().equals(Level.UNDERGROUND)) {
						sendInfo(players, playerInfos.get(gameState.currentPlayerId()).attemptsTunnelClaim(claimedRoute, initialClaimCards));
						SortedBag.Builder<Card> additionalCards = new SortedBag.Builder<Card>();
						for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
							gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
							additionalCards.add(gameState.topCard());
							gameState = gameState.withoutTopCard();
						}
						gameState = gameState.withMoreDiscardedCards(additionalCards.build());
						int additionalClaimCardsCount = claimedRoute.additionalClaimCardsCount(initialClaimCards, additionalCards.build());
						sendInfo(players, playerInfos.get(gameState.currentPlayerId()).drewAdditionalCards(additionalCards.build(), additionalClaimCardsCount));
						if (additionalClaimCardsCount == 0) {
							gameState = gameState
							.withClaimedRoute(claimedRoute, initialClaimCards);
							sendInfo(players, playerInfos.get(gameState.currentPlayerId()).claimedRoute(claimedRoute, initialClaimCards));
						}
						else {
							List<SortedBag<Card>> possibleAdditionalClaimCards = gameState.currentPlayerState().possibleAdditionalCards
							(additionalClaimCardsCount, initialClaimCards, additionalCards.build());
							if (possibleAdditionalClaimCards.size() > 0) {
								SortedBag<Card> chosenAdditionalClaimCards = currentPlayer.chooseAdditionalCards(possibleAdditionalClaimCards);
								if (chosenAdditionalClaimCards.isEmpty()) {
									sendInfo(players, playerInfos.get(gameState.currentPlayerId()).didNotClaimRoute(claimedRoute));
								}
								else {
									SortedBag<Card> finalClaimCards = new SortedBag.Builder<Card>().add(initialClaimCards).add(chosenAdditionalClaimCards).build();
									gameState = gameState
									.withClaimedRoute(claimedRoute, finalClaimCards);
									sendInfo(players, playerInfos.get(gameState.currentPlayerId()).claimedRoute(claimedRoute, finalClaimCards));
								}
							}
							else {
								// Informing that the current player didn't claim the route
								sendInfo(players, playerInfos.get(gameState.currentPlayerId()).didNotClaimRoute(claimedRoute));
							}
						}
					}

					else if (claimedRoute.level().equals(Level.OVERGROUND)) {
						gameState = gameState.withClaimedRoute(claimedRoute, initialClaimCards);
						// Informing that the current player claimed the route
						sendInfo(players, playerInfos.get(gameState.currentPlayerId()).claimedRoute(claimedRoute, initialClaimCards));
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
		PlayerId.ALL.stream().forEach((PlayerId playerId) -> players.get(playerId).receiveInfo(info));
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
		PlayerId.ALL.stream().forEach((PlayerId playerId) -> players.get(playerId).updateState(gameState, gameState.playerState(playerId)));
	}
}
