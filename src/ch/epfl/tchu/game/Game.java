package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

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

		/*
		 * Start of the game
		 */
		// Initialiazing every player
		PlayerId.ALL.stream().forEach((PlayerId playerId) -> { 
			players.get(playerId).initPlayers(playerId, playerNames); 
		});

		// Generating the initial game state
		GameState gameState = GameState.initial(tickets, rng);
		// A map that links a player's identifier to their info class instance
		Map<PlayerId, Info> playerInfos = new HashMap<>();
		for (PlayerId playerId : PlayerId.ALL) {
			playerInfos.put(playerId, new Info(playerNames.get(playerId)));
		}
		sendInfo(players,playerInfos.get(gameState.currentPlayerId()).willPlayFirst());

		// Drawing initial tickets and asking the player which they want to keep
		for (int i = 0; i < PlayerId.COUNT; i++) {
			SortedBag<Ticket> initialTickets = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT * i).topTickets(Constants.INITIAL_TICKETS_COUNT);
			players.get(PlayerId.ALL.get(i)).setInitialTicketChoice(initialTickets);
			updatePlayerStates(players, gameState);
			gameState = gameState.withInitiallyChosenTickets(PlayerId.ALL.get(i), players.get(PlayerId.ALL.get(i)).chooseInitialTickets());
		}
		// Informing every player of the number of initially drawn tickets only once everyone has drawn
		for (PlayerId playerId : PlayerId.ALL) {
			sendInfo(players, playerInfos.get(playerId).keptTickets(gameState.currentPlayerState().ticketCount()));
		}
		// Updating the game state after having drawn the initial tickets
		gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT * PlayerId.COUNT);
		
		/*
		 * Middle of the game
		 */
		while (Objects.isNull(gameState.lastPlayer()) || gameState.currentPlayerId() != gameState.lastPlayer()) {
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
					for (int i = 0; i < 2; i++) {
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
								sendInfo(players, playerInfos.get(gameState.currentPlayerId()).didNotClaimRoute(claimedRoute));
							}
						}
					}

					else if (claimedRoute.level().equals(Level.OVERGROUND)) {
						gameState = gameState.withClaimedRoute(claimedRoute, initialClaimCards);
						// Informing every player that the current player claimed the route
						sendInfo(players, playerInfos.get(gameState.currentPlayerId()).claimedRoute(claimedRoute, initialClaimCards));
					}
					break;
			}
			/*
			System.out.println("Discards size: " + gameState.cardState().discardsSize());
			System.out.println(playerNames.get(gameState.currentPlayerId()) + " car count: " + gameState.currentPlayerState().carCount());
			System.out.println(playerNames.get(gameState.currentPlayerId()) + " cards: " + gameState.currentPlayerState().cards());
			*/
			gameState = gameState.forNextTurn();
			if (gameState.lastTurnBegins()) sendInfo(players, playerInfos.get(gameState.currentPlayerId()).lastTurnBegins(gameState.currentPlayerState().carCount()));
		}
	  
		/*
		 * End of the game
		 */
		updatePlayerStates(players, gameState);
		Map<PlayerId, Integer> playersScores = new HashMap<PlayerId, Integer>();
		Map<PlayerId, Trail> playersLongestTrail = new HashMap<PlayerId, Trail>();
		Integer longestTrailLength = 0;

		for (PlayerId playerId : PlayerId.ALL) {
			playersScores.put(playerId, gameState.playerState(playerId).finalPoints());
			Trail currentPlayerLongestTrail = Trail.longest(gameState.playerState(playerId).routes());
			playersLongestTrail.put(playerId, currentPlayerLongestTrail);
			longestTrailLength = Math.max(longestTrailLength, currentPlayerLongestTrail.length());
		}

		List<PlayerId> bonusEarningPlayers = new ArrayList<PlayerId>();
		for (PlayerId playerId : PlayerId.ALL) {
			if (playersLongestTrail.get(playerId).length() == longestTrailLength)
				bonusEarningPlayers.add(playerId); 
		}

		for (PlayerId playerId : bonusEarningPlayers) {
			Integer newScore = playersScores.get(playerId) + Constants.LONGEST_TRAIL_BONUS_POINTS;
			playersScores.replace(playerId, newScore);
			sendInfo(players, playerInfos.get(playerId).getsLongestTrailBonus(playersLongestTrail.get(playerId)));
		}
		
		Integer highestScore = 0;
		for (PlayerId playerId : PlayerId.ALL)
			highestScore = Math.max(playersScores.get(playerId), highestScore);

		List<PlayerId> winners = new ArrayList<PlayerId>();
		for (PlayerId playerId : PlayerId.ALL)
			if (playersScores.get(playerId) == highestScore)
				winners.add(playerId);

		// Note: This code only works for 2 players
		if (winners.size() == 1)	sendInfo(players, playerInfos.get(winners.get(0)).won(playersScores.get(winners.get(0)), playersScores.get(winners.get(0).next())));	
		else sendInfo(players, Info.draw(new ArrayList<String>(playerNames.values()), highestScore));
	}

	private static void sendInfo(Map<PlayerId, Player> players, String info) {
		for (PlayerId playerId : PlayerId.ALL)
			players.get(playerId).receiveInfo(info);
	}

	private static void updatePlayerStates(Map<PlayerId, Player> players, GameState gameState) {
		for (PlayerId playerId : PlayerId.ALL)
			players.get(playerId).updateState(gameState, gameState.playerState(playerId));
	}
}
