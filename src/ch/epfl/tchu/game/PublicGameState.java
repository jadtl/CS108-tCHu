package ch.epfl.tchu.game;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.epfl.tchu.Preconditions;

/**
 * The public part of a tCHu game state
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public class PublicGameState {
  private final int ticketsCount;
  private final PublicCardState cardState;
  private final PlayerId currentPlayerId;
  private final Map<PlayerId, PublicPlayerState> playerState;
  private final PlayerId lastPlayer;

  /**
   * Constructs the public game state of a tCHu game
   * 
   * @param ticketsCount
   *        The deck of tickets size
   * 
   * @param cardState
   *        The public cards state
   * 
   * @param currentPlayerId
   *        The current player
   * 
   * @param playerState
   *        The public players state
   * 
   * @param lastPlayer
   *        The identifier of the last player
   * 
   * @throws IllegalArgumentException
   *         If ticketsCount is negative or the numbers of players is incorrect
   */
  public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
    Preconditions.checkArgument(ticketsCount >= 0 && playerState.size() == 2);

    this.ticketsCount = ticketsCount;
    this.cardState = Objects.requireNonNull(cardState);
    this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
    this.playerState = playerState;
    this.lastPlayer = lastPlayer;
  }

  /**
   * Returns the number of tickets on the board
   * 
   * @return the number of tickets on the board
   */
  public int ticketsCount() { return ticketsCount; }

  /**
   * Returns the ability to draw tickets
   * 
   * @return true iff. ticketsCount is non-zero
   */
  public boolean canDrawTickets() { return ticketsCount != 0; }

  /**
   * Returns the card state of the game
   * 
   * @return the card state of the game
   */
  public PublicCardState cardState() { return cardState; }

  /**
   * Returns the ability to draw cards
   * 
   * @return true iff. the deck of cards and the discards have 5 or more cards
   */
  public boolean canDrawCards() { return cardState.deckSize() + cardState.discardsSize() >= 5; }

  /**
   * Returns the identifier of the current player
   * 
   * @return the identifier of the current player
   */
  public PlayerId currentPlayerId() { return currentPlayerId; }

  /**
   * Returns the player state of the given player
   * 
   * @param playerId
   *        The player
   * 
   * @return the player state of the given player
   */
  public PublicPlayerState playerState(PlayerId playerId) { return playerState.get(playerId); }

  /**
   * Returns the player state of the current player
   * 
   * @return the player state of the current player
   */
  public PublicPlayerState currentPlayerState() { return playerState.get(currentPlayerId); }

  /**
   * Returns the claimed routes of the current player
   * 
   * @return the claimed routes of the current player
   */
  public List<Route> claimedRoutes() { return Stream.concat(playerState(PlayerId.PLAYER_1).routes().stream(), 
    playerState(PlayerId.PLAYER_2).routes().stream()).collect(Collectors.toList()); }

  /**
   * Returns the last player identifier
   * 
   * @return the last player identifier
   */
  public PlayerId lastPlayer() { return lastPlayer; }
}
