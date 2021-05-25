package ch.epfl.tchu.game;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.epfl.tchu.Preconditions;

/**
 * The public part of a tCHu game state
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public class PublicGameState {
    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * The public game state of a tCHu game
     *
     * @param ticketsCount    The size of the {@link Deck} of {@link Ticket}
     * @param cardState       The {@link PublicCardState} of the game
     * @param currentPlayerId The {@link PlayerId} of the current player
     * @param playerState     The {@link PublicPlayerState} of the current player
     * @param lastPlayer      The {@link PlayerId} of the last player
     * @throws IllegalArgumentException If {@code ticketsCount} is negative or the number of players is incorrect
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(ticketsCount >= 0 && playerState.size() == PlayerId.COUNT);

        this.ticketsCount = ticketsCount;
        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState = Map.copyOf(playerState);
        this.lastPlayer = lastPlayer;
    }

    /**
     * The number of tickets on the board
     *
     * @return The number of tickets {@link PublicGameState#ticketsCount} on the board
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * The ability to draw tickets
     *
     * @return True iff. {@code ticketsCount} is greater than zero
     */
    public boolean canDrawTickets() {
        return ticketsCount > 0;
    }

    /**
     * The card state of the game
     *
     * @return The card state {@link PublicGameState#cardState} of the game
     */
    public PublicCardState cardState() {
        return cardState;
    }

    /**
     * The ability to draw cards
     *
     * @return True iff. the deck of cards and the discards have 5 or more cards
     */
    public boolean canDrawCards() {
        return cardState.deckSize() + cardState.discardsSize() >= 5;
    }

    /**
     * The identifier of the current player
     *
     * @return The {@link PlayerId} of the current player
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * The player state of the given player
     *
     * @param playerId The player's identifier
     * @return The {@link PublicPlayerState} of the player of identifier {@code playerId}
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * The player state of the current player
     *
     * @return The {@link PublicPlayerState} of the player of {@link PublicGameState#currentPlayerId}
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    /**
     * The claimed routes of all the players
     *
     * @return The claimed {@link List} of {@link Route} of all the players
     */
    // TODO: Check the TA's comment again
    public List<Route> claimedRoutes() {
        return playerState.values()
                .stream()
                .flatMap(pgs -> pgs.routes().stream())
                .collect(Collectors.toList());
    }

    /**
     * The last player identifier
     *
     * @return the last player's {@link PlayerId}
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}
