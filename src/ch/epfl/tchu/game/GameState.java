package ch.epfl.tchu.game;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.Random;

/**
 * The tCHu game state
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class GameState extends PublicGameState {
  private final Deck<Ticket> tickets;
  private final Map<PlayerId, PlayerState> playerState;
  private final CardState cardState;

  private GameState(Deck<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, 
  Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
    super(tickets.size(), new PublicCardState(cardState.faceUpCards(), cardState.deckSize(), 
    cardState.discardsSize()), currentPlayerId, Map.copyOf(playerState), lastPlayer);
    
    this.tickets = tickets;
    this.playerState = playerState;
    this.cardState = cardState;
  }
  
  /**
   * Returns a game state with the given tickets deck and the starter card state and player state
   * in which four cards have been given to each player
   * 
   * @param tickets
   *        The initial tickets to use in the game
   * @param rng
   *        The random number generator
   * 
   * @return a game state with the given tickets deck and the starter card state and player state
   *         in which four cards have been given to each player
   */
  public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
    Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
    Deck<Card> deck = Deck.of(Constants.ALL_CARDS, rng);
    for (PlayerId playerId : PlayerId.ALL) {
      playerState.put(playerId, PlayerState.initial(deck.topCards(Constants.INITIAL_CARDS_COUNT)));
      deck = deck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
    }
    PlayerId firstPlayer = PlayerId.values()[rng.nextInt(PlayerId.COUNT)];
    return new GameState(Deck.of(tickets, rng), CardState.of(deck), firstPlayer, playerState, null);
  }

  @Override
  public PlayerState playerState(PlayerId playerId) { return playerState.get(playerId); }

  @Override
  public PlayerState currentPlayerState() { return playerState.get(currentPlayerId()); }

  /**
   * Returns a number of top tickets
   * 
   * @param count
   *        The number of top tickets
   * 
   * @return a sorted bag of count top tickets
   */
  public SortedBag<Ticket> topTickets(int count) { return tickets.topCards(count); }

  /**
   * Returns an identical game state except for its tickets deck that
   * is missing a number of top tickets
   * 
   * @param count
   *        The number of top tickets to remove
   * 
   * @return the same game state with a tickets deck missing count top cards
   * 
   * @throws IllegalArgumentException
   *         If count is incorrect
   */
  public GameState withoutTopTickets(int count) {
    Preconditions.checkArgument(0 <= count && count <= tickets.size());

    return new GameState(tickets.withoutTopCards(count), cardState, currentPlayerId(), playerState, lastPlayer());
  }

  /**
   * Returns the top card of the game deck
   * 
   * @return the top card of the game deck
   * 
   * @throws IllegalArgumentException
   *         If the deck is empty
   */
  public Card topCard() {
    Preconditions.checkArgument(!cardState.isDeckEmpty());

    return cardState.topDeckCard();
  }

  /**
   * Returns the same game state except the card state is missing its top deck card 
   * 
   * @return the same game state except the card state is missing its top deck card 
   * 
   * @throws IllegalArgumentException
   *         If the deck is empty
   */
  public GameState withoutTopCard() {
    Preconditions.checkArgument(!cardState.isDeckEmpty());

    return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), playerState, lastPlayer());
  }

  /**
   * Returns the same game state except the given cards were added to the discards
   * 
   * @param discardedCards
   *        The additional discarded cards
   * 
   * @return the same game state except the given cards were added to the discards
   */
  public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) { return new GameState(tickets, cardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), playerState, lastPlayer()); }

  /**
   * Returns the same game state except that if the deck is empty, it's recreated from the discards
   * 
   * @param rng
   *        The random number generator
   * 
   * @return the same game state except that if the deck is empty, it's recreated from the discards
   */
  public GameState withCardsDeckRecreatedIfNeeded(Random rng) { return cardState.isDeckEmpty() ? new GameState(tickets, cardState.withDeckRecreatedFromDiscards(rng), currentPlayerId(), playerState, lastPlayer()) : this; }
  
  /**
   * Returns an identical state except that the given tickets were added to the given player's hand
   * 
   * @param playerId
   *        The player
   * @param chosenTickets
   *        The tickets picked by the player
   * 
   * @return an identical state except that the given tickets were added to the given player's hand
   * 
   * @throws IllegalArgumentException if the given player already has tickets
   */
  public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
    Preconditions.checkArgument(playerState(playerId).ticketCount() == 0);

    Map<PlayerId, PlayerState> updatedPlayerState = new EnumMap<PlayerId, PlayerState>(playerState);
    updatedPlayerState.put(playerId, playerState(playerId).withAddedTickets(chosenTickets));

    return new GameState(tickets, cardState, currentPlayerId(), updatedPlayerState, lastPlayer());
  }
  
  /**
   * Returns an identical state except the current player drew tickets from the top of the deck
   * and kept the chosen tickets
   * 
   * @param drawnTickets
   *        The tickets drawn by the player
   * @param chosenTickets
   *        The tickets chosen by the player
   * 
   * @return an identical state except the current player drew tickets from the top of the deck
   * and kept the chosen tickets
   * 
   * @throws IllegalArgumentException
   *         If drawnTickets doesn't contain chosenTickets
   */
  public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
    Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

    Map<PlayerId, PlayerState> updatedPlayerState = new EnumMap<PlayerId, PlayerState>(playerState);
    updatedPlayerState.put(currentPlayerId(), updatedPlayerState.get(currentPlayerId()).withAddedTickets(chosenTickets));

    return new GameState(tickets.withoutTopCards(Constants.IN_GAME_TICKETS_COUNT), cardState, currentPlayerId(), updatedPlayerState, lastPlayer());
  }
  
  /**
   * Returns an identical state except that the selected face-up card has been drawn
   * and placed in the current player's hand
   * 
   * @param slot
   *        The slot of the face-up card to draw
   * 
   * @return an identical state except that the selected face-up card has been drawn
   * and placed in the current player's hand
   * 
   * @throws IllegalArgumentException
   *         If cards can't be drawn
   */
  public GameState withDrawnFaceUpCard(int slot) {
    Preconditions.checkArgument(canDrawCards());

    Card drawnCard = cardState.faceUpCard(slot);
    CardState updatedCardState = cardState.withDrawnFaceUpCard(slot);

    Map<PlayerId, PlayerState> updatedPlayerState = new EnumMap<PlayerId, PlayerState>(playerState);
    updatedPlayerState.put(currentPlayerId(), updatedPlayerState.get(currentPlayerId()).withAddedCard(drawnCard));

    return new GameState(tickets, updatedCardState, currentPlayerId(), updatedPlayerState, lastPlayer());
  }
  
  /**
   * Returns an identical state except that the top deck card has been drawn and placed
   * in the player's hand
   * 
   * @return an identical state except that the top deck card has been drawn and placed
   * in the player's hand
   * 
   * @throws IllegalArgumentException
   *         If cards can't be drawn
   */
  public GameState withBlindlyDrawnCard() {
    Preconditions.checkArgument(canDrawCards());

    Card drawnCard = cardState.topDeckCard();
    CardState updatedCardState = cardState.withoutTopDeckCard();

    Map<PlayerId, PlayerState> updatedPlayerState = new EnumMap<PlayerId, PlayerState>(playerState);
    updatedPlayerState.put(currentPlayerId(), updatedPlayerState.get(currentPlayerId()).withAddedCard(drawnCard));

    return new GameState(tickets, updatedCardState, currentPlayerId(), updatedPlayerState, lastPlayer());
  }
  
  /**
   * Returns an identical state except that the player claimed the given route using the given cards
   * 
   * @param route
   *        The claimed route
   * @param cards
   *        The cards the player used to claim the route
   * 
   * @return an identical state except that the player claimed the given route using the given cards
   */
  public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
    CardState updatedCardState = cardState.withMoreDiscardedCards(cards);

    Map<PlayerId, PlayerState> updatedPlayerState = new EnumMap<PlayerId, PlayerState>(playerState);
    updatedPlayerState.put(currentPlayerId(), updatedPlayerState.get(currentPlayerId()).withClaimedRoute(route, cards));

    return new GameState(tickets, updatedCardState, currentPlayerId(), updatedPlayerState, lastPlayer());
  }
  
  /**
   * Returns true if the last turn begins
   * 
   * @return true iff. the last player's identifier is unknown and the current player has two or less cars
   */
  public boolean lastTurnBegins() {
    return Objects.isNull(lastPlayer()) && currentPlayerState().carCount() <= 2;
  }

  /**
   * Returns an identical state, except that the current player is now the next player, if the last turn begins
   * the current player becomes the last player
   * 
   * @return an identical state, except that the current player is now the next player
   */
  public GameState forNextTurn() {
    return new GameState(tickets, cardState, currentPlayerId().next(), playerState, lastTurnBegins() ? currentPlayerId() : lastPlayer());
  }
}
