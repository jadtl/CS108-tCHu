package ch.epfl.tchu.game;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.Random;

public final class GameState extends PublicGameState {
  private final Deck<Ticket> tickets;
  private final Map<PlayerId, PlayerState> playerState;
  private final CardState cardState;

  private GameState(Deck<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, 
  Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
    super(tickets.size(), new PublicCardState(cardState.faceUpCards(), cardState.deckSize(), 
    cardState.discardsSize()), currentPlayerId, makePublic(playerState), lastPlayer);
    
    this.tickets = tickets;
    this.playerState = playerState;
    this.cardState = cardState;
  }
  
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
  public PlayerState playerState(PlayerId playerId) {
    return playerState.get(playerId);
  }

  @Override
  public PlayerState currentPlayerState() {
    return playerState.get(currentPlayerId());
  }

  public SortedBag<Ticket> topTickets(int count) {
    return tickets.topCards(count);
  }

  public GameState withoutTopTickets(int count) {
    Preconditions.checkArgument(0 < count && count <= tickets.size());

    return new GameState(tickets.withoutTopCards(count), cardState, currentPlayerId(), playerState, lastPlayer());
  }

  public Card topCard() {
    Preconditions.checkArgument(!cardState.isDeckEmpty());

    return cardState.topDeckCard();
  }

  public GameState withoutTopCard() {
    Preconditions.checkArgument(!cardState.isDeckEmpty());

    return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), playerState, lastPlayer());
  }

  public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
    return new GameState(tickets, cardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), playerState, lastPlayer());
  }

  public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
    return cardState.isDeckEmpty() ? new GameState(tickets, cardState.withDeckRecreatedFromDiscards(rng), currentPlayerId(), playerState, lastPlayer()) : this;
  }
  
  public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
    Preconditions.checkArgument(playerState(playerId).ticketCount() == 0);

    Map<PlayerId, PlayerState> updatedPlayerState = Map.copyOf(playerState);
    updatedPlayerState.put(playerId, updatedPlayerState.get(playerId).withAddedTickets(chosenTickets));

    return new GameState(tickets, cardState, currentPlayerId(), updatedPlayerState, lastPlayer());
  }
  
  public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
    Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

    Map<PlayerId, PlayerState> updatedPlayerState = Map.copyOf(playerState);
    updatedPlayerState.put(currentPlayerId(), updatedPlayerState.get(currentPlayerId()).withAddedTickets(chosenTickets));

    return new GameState(tickets.withoutTopCards(Constants.IN_GAME_TICKETS_COUNT), cardState, currentPlayerId(), playerState, lastPlayer());
  }
  
  public GameState withDrawnFaceUpCard(int slot) {
    Preconditions.checkArgument(canDrawCards());

    Card drawnCard = cardState.faceUpCard(slot);
    CardState updatedCardState = cardState.withDrawnFaceUpCard(slot);

    Map<PlayerId, PlayerState> updatedPlayerState = Map.copyOf(playerState);
    updatedPlayerState.put(currentPlayerId(), updatedPlayerState.get(currentPlayerId()).withAddedCard(drawnCard));

    return new GameState(tickets, updatedCardState, currentPlayerId(), updatedPlayerState, lastPlayer());
  }
  
  public GameState withBlindlyDrawnCard() {
    Preconditions.checkArgument(canDrawCards());

    Card drawnCard = cardState.topDeckCard();
    CardState updatedCardState = cardState.withoutTopDeckCard();

    Map<PlayerId, PlayerState> updatedPlayerState = Map.copyOf(playerState);
    updatedPlayerState.put(currentPlayerId(), updatedPlayerState.get(currentPlayerId()).withAddedCard(drawnCard));

    return new GameState(tickets, updatedCardState, currentPlayerId(), updatedPlayerState, lastPlayer());
  }
  
  public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
    CardState updatedCardState = cardState.withMoreDiscardedCards(cards);

    Map<PlayerId, PlayerState> updatedPlayerState = Map.copyOf(playerState);
    updatedPlayerState.put(currentPlayerId(), updatedPlayerState.get(currentPlayerId()).withClaimedRoute(route, cards));

    return new GameState(tickets, updatedCardState, currentPlayerId(), updatedPlayerState, lastPlayer());
  }
  
  public boolean lastTurnBegins() {
    return Objects.isNull(lastPlayer()) && playerState(currentPlayerId()).carCount() <= 2;
  }

  public GameState forNextTurn() {
    return new GameState(tickets, cardState, currentPlayerId().next(), playerState, lastTurnBegins() ? currentPlayerId() : lastPlayer());
  }

  private static Map<PlayerId, PublicPlayerState> makePublic(Map<PlayerId, PlayerState> globalPlayerState) {
    Map<PlayerId, PublicPlayerState> publicPlayerState = new EnumMap<>(PlayerId.class);
    globalPlayerState.forEach((PlayerId playerId, PlayerState playerState) -> {
      publicPlayerState.put(playerId, new PublicPlayerState(playerState.ticketCount(), playerState.cardCount(), playerState.routes()));
    });
    return publicPlayerState;
  }
}
