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
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 * @see ch.epfl.tchu.game.PublicGameState
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
     * An initial game state with a tickets set, and a random generating number
     *
     * @param tickets The initial {@link SortedBag} of {@link Ticket} to use in the game
     * @param rng     The {@link Random} number generator
     * @return An initial {@link GameState} generated using {@code tickets} and {@code rng}
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
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    @Override
    public PlayerState currentPlayerState() {
        return playerState.get(currentPlayerId());
    }

    /**
     * A number of top tickets
     *
     * @param count The number of top tickets
     * @return A sorted bag of {@code count} top tickets
     */
    public SortedBag<Ticket> topTickets(int count) {
        return tickets.topCards(count);
    }

    /**
     * The same game state except that its tickets deck has a given number of top tickets removed
     *
     * @param count The number of top tickets to remove
     * @return The same {@link GameState} with a {@link Deck} of {@link Ticket} missing {@code count} top cards
     * @throws IllegalArgumentException If {@code count} is not between 0 and {@link GameState#tickets}' size (exclusive)
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(0 <= count && count <= tickets.size());

        return new GameState(tickets.withoutTopCards(count), cardState, currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * The top card of the game deck
     *
     * @return The top {@link Card} of the game {@link Deck}
     * @throws IllegalArgumentException If the deck is empty
     */
    public Card topCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());

        return cardState.topDeckCard();
    }

    /**
     * The same game state except that the card state had its top deck card
     *
     * @return The same {@link GameState} except that the {@link CardState} had its top {@link Deck} {@link Card} removed
     * @throws IllegalArgumentException If the {@link Deck} is empty
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());

        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * The same game state except that the given cards were added to the discards
     *
     * @param discardedCards The additional discarded cards
     * @return the same {@link GameState} except that the given cards were added to the discards
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(tickets, cardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * The same game state except that if the deck is empty, it's recreated from the discards
     *
     * @param rng The {@link Random} number generator
     * @return the same {@link GameState} except that the deck is empty, it's recreated from the discards
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return cardState.isDeckEmpty() ? new GameState(tickets, cardState.withDeckRecreatedFromDiscards(rng), currentPlayerId(), playerState, lastPlayer()) : this;
    }

    /**
     * The same game state except that the given tickets were added to the given player's hand
     *
     * @param player        The player's {@link PlayerId}
     * @param chosenTickets The {@link SortedBag} of {@link Ticket} picked by the player
     * @return An identical state except that {@code chosenTickets} were added to the {@code player}'s hand
     * @throws IllegalArgumentException if {@code player} already has tickets
     */
    public GameState withInitiallyChosenTickets(PlayerId player, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState(player).ticketCount() == 0);

        Map<PlayerId, PlayerState> updatedPlayerState = new EnumMap<PlayerId, PlayerState>(playerState);
        updatedPlayerState.put(player, playerState(player).withAddedTickets(chosenTickets));

        return new GameState(tickets, cardState, currentPlayerId(), updatedPlayerState, lastPlayer());
    }

    /**
     * The same game state except that the current player drew tickets from the top of the deck and kept the chosen tickets
     *
     * @param drawnTickets  The {@link SortedBag} of {@link Ticket} drawn by the player
     * @param chosenTickets The {@link SortedBag} of {@link Ticket} chosen by the player
     * @return The same {@link GameState} except that the {@link GameState#currentPlayerId()} drew {@code drawnTickets} and kept {@code chosenTickets}
     * @throws IllegalArgumentException If {@code drawnTickets} doesn't contain {@code chosenTickets}
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

        Map<PlayerId, PlayerState> updatedPlayerState = new EnumMap<PlayerId, PlayerState>(playerState);
        updatedPlayerState.put(currentPlayerId(), updatedPlayerState.get(currentPlayerId()).withAddedTickets(chosenTickets));

        return new GameState(tickets.withoutTopCards(Constants.IN_GAME_TICKETS_COUNT), cardState, currentPlayerId(), updatedPlayerState, lastPlayer());
    }

    /**
     * The same game state except that the selected face-up card has been drawn and placed in the current player's hand
     *
     * @param slot The slot of the face-up card to draw
     * @return The same {@link GameState} except that the face-up card at {@code slot} has been placed in {@link GameState#currentPlayerId()}'s hand
     * @throws IllegalArgumentException If cards can't be drawn
     */
    public GameState withDrawnFaceUpCard(int slot) {
        Card drawnCard = cardState.faceUpCard(slot);
        CardState updatedCardState = cardState.withDrawnFaceUpCard(slot);

        Map<PlayerId, PlayerState> updatedPlayerState = new EnumMap<PlayerId, PlayerState>(playerState);
        updatedPlayerState.put(currentPlayerId(), updatedPlayerState.get(currentPlayerId()).withAddedCard(drawnCard));

        return new GameState(tickets, updatedCardState, currentPlayerId(), updatedPlayerState, lastPlayer());
    }

    /**
     * The same game state except that the top deck card has been drawn and placed in the current player's hand
     *
     * @return The same {@link GameState} except that the top deck card and placed in the {@link GameState#currentPlayerId()}'s hand
     * @throws IllegalArgumentException If cards can't be drawn
     */
    public GameState withBlindlyDrawnCard() {
        Card drawnCard = cardState.topDeckCard();
        CardState updatedCardState = cardState.withoutTopDeckCard();

        Map<PlayerId, PlayerState> updatedPlayerState = new EnumMap<PlayerId, PlayerState>(playerState);
        updatedPlayerState.put(currentPlayerId(), updatedPlayerState.get(currentPlayerId()).withAddedCard(drawnCard));

        return new GameState(tickets, updatedCardState, currentPlayerId(), updatedPlayerState, lastPlayer());
    }

    /**
     * The same game state except that the player claimed the given route using the given cards
     *
     * @param route The claimed {@link Route}
     * @param cards The {@link SortedBag} of {@link Card} the player used to claim the route
     * @return The same {@link GameState} except that the {@link GameState#currentPlayerId()} claimed the {@code route} using {@code cards}
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        CardState updatedCardState = cardState.withMoreDiscardedCards(cards);

        Map<PlayerId, PlayerState> updatedPlayerState = new EnumMap<PlayerId, PlayerState>(playerState);
        updatedPlayerState.put(currentPlayerId(), updatedPlayerState.get(currentPlayerId()).withClaimedRoute(route, cards));

        return new GameState(tickets, updatedCardState, currentPlayerId(), updatedPlayerState, lastPlayer());
    }

    /**
     * Whether the last turn begins or not
     *
     * @return True iff. the {@link GameState#lastPlayer()}'s identifier is unknown and the {@link GameState#currentPlayerId()} has two or less cars
     */
    public boolean lastTurnBegins() {
        return Objects.isNull(lastPlayer()) && currentPlayerState().carCount() <= 2;
    }

    /**
     * The same game state, except that the current player is now the next player, if the last turn begins the current player becomes the last player
     *
     * @return The same {@link GameState} except that the {@link GameState#currentPlayerId()} is now the next player
     */
    public GameState forNextTurn() {
        return new GameState(tickets, cardState, currentPlayerId().next(), playerState, lastTurnBegins() ? currentPlayerId() : lastPlayer());
    }
}
