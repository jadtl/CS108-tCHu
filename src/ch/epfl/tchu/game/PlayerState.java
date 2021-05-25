package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * The complete state of the player
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 * @see ch.epfl.tchu.game.PublicPlayerState
 */
public final class PlayerState extends PublicPlayerState {
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final int ticketPoints;

    /**
     * A player state with the given tickets, cards and routes
     *
     * @param tickets The player's {@link SortedBag} of {@link Ticket}
     * @param cards   The player's {@link SortedBag} of {@link Card}
     * @param routes  The player's claimed {@link List} of {@link Route}
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);

        int highestId = routes.isEmpty() ? 0 : routes.stream().flatMap((Route route) -> (List.of(route.station1().id(), route.station2().id()).stream()))
                .collect(Collectors.toSet()).stream().max(Comparator.comparing(Integer::valueOf)).get();
        StationPartition.Builder stationPartitionBuilder = new StationPartition
                .Builder(highestId + 1);
        routes.forEach(route -> stationPartitionBuilder.connect(route.station1(), route.station2()));
        StationPartition stationPartition = stationPartitionBuilder.build();

        this.tickets = tickets;
        this.cards = cards;
        this.ticketPoints = tickets.stream().map(ticket -> ticket.points(stationPartition)).reduce(0, Integer::sum);
    }

    /**
     * The initial state of a player to who the given initial cards have been given
     *
     * @param initialCards The player's initial {@link SortedBag} of {@link Card}
     * @return The initial {@link PlayerState} of a player to who {@code initialCards} have been given
     * @throws IllegalArgumentException If {@code initialCards}'s count is not {@link Constants#INITIAL_CARDS_COUNT}
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);

        return new PlayerState(new SortedBag.Builder<Ticket>().build(), initialCards, new ArrayList<Route>());
    }

    /**
     * The tickets of the player
     *
     * @return The {@link PlayerState#tickets} of the player
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    /**
     * The same player state, except that the given tickets were added
     *
     * @param newTickets The {@link SortedBag} of {@link Ticket} to add to the {@link PlayerState}
     * @return The same {@link PlayerState}, except that {@code newTickets} were added
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(new SortedBag.Builder<Ticket>().add(tickets())
                .add(newTickets).build(), cards(), routes());
    }

    /**
     * The car cards of the player
     *
     * @return The car {@link SortedBag} of {@link Card} of the player
     */
    public SortedBag<Card> cards() {
        return cards;
    }

    /**
     * The same player state, except that the given card was added
     *
     * @param card The {@link Card} to add to the {@link PlayerState}
     * @return The same {@link PlayerState}, except that {@code card} was added
     */
    public PlayerState withAddedCard(Card card) {
        return new PlayerState(tickets(), new SortedBag.Builder<Card>()
                .add(cards()).add(card).build(), routes());
    }

    /**
     * The ability of the player to claim the given route
     *
     * @param route The selected {@link Route}
     * @return true iff. the player has the needed cars and cards to claim {@code route}
     */
    public boolean canClaimRoute(Route route) {
        return carCount() >= route.length() && !possibleClaimCards(route).isEmpty();
    }

    /**
     * The list of cards that the player could use to claim the given route
     *
     * @param route The selected {@link Route}
     * @return The {@link List} of {@link SortedBag} of {@link Card} that the player could use to claim the given route
     * @throws IllegalArgumentException If the player's {@link PlayerState#carCount()} is less than {@code route}'s length
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(carCount() >= route.length());

        return route.possibleClaimCards().stream().filter((SortedBag<Card> cards)
                -> this.cards().contains(cards)).collect(Collectors.toList());
    }

    /**
     * A list of sets of cards that a player could use to claim a tunnel, sorted in increasing number of locomotive cards
     *
     * @param additionalCardsCount The number of {@link Card} the player needs in addition to {@code initialCards} to claim the tunnel
     * @param initialCards         The initial {@link SortedBag} of {@link Card} the player used to attempt claiming the tunnel
     * @return A list of sets of cards that a player could use to claim a tunnel in increasing number of {@link Card#LOCOMOTIVE}
     * @throws IllegalArgumentException If {@code additionalCardsCount} is incorrect of if {@code initialCards} is empty
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards) {
        Preconditions.checkArgument(1 <= additionalCardsCount && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(!initialCards.isEmpty() && initialCards.toSet().size() <= 2);

        SortedBag<Card> remainingCards = cards().difference(initialCards);
        if (remainingCards.isEmpty()) return new ArrayList<SortedBag<Card>>();

        SortedBag<Card> filteredCards = SortedBag.of(remainingCards.stream()
                .filter((Card card) -> card.equals(Card.LOCOMOTIVE) || card.equals(initialCards.get(0)))
                .collect(Collectors.toList()));
        if (filteredCards.size() < additionalCardsCount) return new ArrayList<SortedBag<Card>>();

        return filteredCards.subsetsOfSize(additionalCardsCount).stream()
                .sorted(Comparator.comparingInt(additionalCards -> additionalCards.countOf(Card.LOCOMOTIVE))).collect(Collectors.toList());
    }

    /**
     * The same player state, except that the player has claimed the given route using the given cards
     *
     * @param route      The claimed {@link Route}
     * @param claimCards The {@link SortedBag} of {@link Card} used to claim the route
     * @return The same {@link PlayerState}, except that the player claimed {@code route} using {@code claimCards}
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> updatedRoutes = new ArrayList<>(routes());
        updatedRoutes.add(route);

        return new PlayerState(tickets(), new SortedBag.Builder<Card>().add(cards().difference(claimCards)).build(), updatedRoutes);
    }

    /**
     * The number of points, eventually negative, earned by the player from their tickets
     *
     * @return The number of points, eventually negative, earned by the player from their tickets
     */
    public int ticketPoints() {
        return ticketPoints;
    }

    /**
     * The number of points the player earned from tickets and claiming routes
     *
     * @return The sum of {@link PlayerState#claimPoints()} and {@link PlayerState#ticketPoints()}
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}
