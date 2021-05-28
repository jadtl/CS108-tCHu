package ch.epfl.tchu.game;

import java.util.List;

import ch.epfl.tchu.Preconditions;

/**
 * The public part of the player state
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public class PublicPlayerState {
    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;
    private final int carCount;
    private final int claimPoints;

    /**
     * A public player state with its ticket and card counts as well as its claimed routes
     *
     * @param ticketCount The number of {@link Ticket} the player has
     * @param cardCount   The number of {@link Card} the player has
     * @param routes      The {@link List} of {@link Route} the player claimed
     * @throws IllegalArgumentException If {@code ticketCount} or {@code cardCount} are strictly negative
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);

        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);
        this.carCount = Constants.INITIAL_CAR_COUNT - routes.stream().map(Route::length).reduce(0, Integer::sum);
        this.claimPoints = routes.stream().map(Route::claimPoints).reduce(0, Integer::sum);
    }

    /**
     * The number of tickets the player has
     *
     * @return The number of {@link Ticket} {@link PublicPlayerState#ticketCount} the player has
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * The number of cards the player has
     *
     * @return The number of {@link Card} {@link PublicPlayerState#cardCount} the player has
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * The routes the player owns
     *
     * @return the {@link List} of {@link Route} {@link PublicPlayerState#routes} the player owns
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * The number of cars the player has
     *
     * @return The number of cars {@link PublicPlayerState#carCount} the player has
     */
    public int carCount() {
        return carCount;
    }

    /**
     * The number of construction points claimed by the player
     *
     * @return The number of construction points {@link PublicPlayerState#claimPoints} claimed by the player
     */
    public int claimPoints() {
        return claimPoints;
    }
}
