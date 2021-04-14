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
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class PlayerState extends PublicPlayerState {
	private final SortedBag<Ticket> tickets;
	private final SortedBag<Card> cards;
	private final int ticketPoints;

	/**
	 * Constructs the player state with the given tickets, cards and routes
	 * 
	 * @param tickets
	 *        The player's tickets
	 * 
	 * @param cards
	 * 				The player's cards
	 * 
	 * @param routes
	 * 				The player's claimed routes
	 */
	public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
		super(tickets.size(), cards.size(), routes);

		int highestId = routes.isEmpty() ? 0 : routes.stream().flatMap((Route route) -> (List.of(route.station1().id(), route.station2().id()).stream()))
		.collect(Collectors.toSet()).stream().max(Comparator.comparing(Integer::valueOf)).get();
		StationPartition.Builder stationPartitionBuilder = new StationPartition
		.Builder(highestId + 1);
		routes.stream().forEach(route -> stationPartitionBuilder.connect(route.station1(), route.station2()));
		StationPartition stationPartition = stationPartitionBuilder.build();

		this.tickets = SortedBag.of(tickets);
		this.cards = SortedBag.of(cards);
		this.ticketPoints = tickets.stream().map(ticket -> ticket.points(stationPartition)).reduce(0, Integer::sum);
	}

	/**
	 * Returns the initial state of a player to who the given initial
	 * cards have been given
	 * 
	 * @param initialCards
	 * 				The player's initial cards
	 * 
	 * @return the initial state of a player to who the given initial
	 * cards have been given
	 * 
	 * @throws IllegalArgumentException 
	 *         If the number of initial cards is incorrect
	 */
	public static PlayerState initial(SortedBag<Card> initialCards) {
		Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);

		return new PlayerState(new SortedBag.Builder<Ticket>().build(), SortedBag.of(initialCards), new ArrayList<Route>());
	}

	/**
	 * Returns the tickets of the player
	 * 
	 * @return the tickets of the player
	 */
	public SortedBag<Ticket> tickets() { return tickets; }

	/**
	 * Returns a state identical to the one applied to, except that 
	 * the given tickets were added
	 * 
	 * @param newTickets
	 *        The tickets to add to the player state
	 * 
	 * @return a state identical to the one applied to, except that
	 * the given tickets were added
	 */
	public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
		return new PlayerState(new SortedBag.Builder<Ticket>().add(tickets())
		.add(newTickets).build(), cards(), routes());
	}

	/**
	 * Returns the car cards of the player
	 * 
	 * @return the car cards of the player
	 */
	public SortedBag<Card> cards() { return cards; }

	/**
	 * Returns a state identical to the one applied to, except that
	 * the given card was added
	 * 
	 * @param card
	 * 				The card to add to the player state
	 * 
	 * @return a state identical to the one applied to, except that
	 * the given card was added
	 */
	public PlayerState withAddedCard(Card card) {
		return new PlayerState(tickets(), new SortedBag.Builder<Card>()
		.add(cards()).add(card).build(), routes());
	}

	/**
	 * Returns a state identical to the one applied to, except that
	 * the given cards were added
	 * 
	 * @param additionalCards
	 * 				The cards to add to the player state
	 * 
	 * @return a state identical to the one applied to, except that
	 * the given cards were added
	 */
	public PlayerState withAddedCards(SortedBag<Card> additionalCards) { 
		return new PlayerState(tickets(), new SortedBag.Builder<Card>()
		.add(cards()).add(additionalCards).build(), routes());
	}

	/**
	 * Returns the ability of the player to claim the given route
	 * 
	 * @param route
	 * 			  The selected route
	 * 
	 * @return true iff. the player has the needed cars and cards
	 */
	public boolean canClaimRoute(Route route) {
		if (carCount() < route.length()) return false;

		boolean hasClaimCards = false;
		for(SortedBag<Card> claimCards : possibleClaimCards(route))
			if(cards().contains(claimCards)) hasClaimCards = true;
		
		return hasClaimCards;
	}

	/**
	 * Returns the set of cards that the player could use to
	 * claim the given route
	 * 
	 * @param route
	 * 				The selected route
	 * 
	 * @return thes sets of cards that the player could use to
	 * claim the given route
	 * 
	 * @throws IllegalArgumentException 
	 * 				 If the player doesn't have enough cars
	 */
	public List<SortedBag<Card>> possibleClaimCards(Route route) {
		Preconditions.checkArgument(carCount() >= route.length());

		return route.possibleClaimCards().stream().filter((SortedBag<Card> cards) 
		-> this.cards().contains(cards)).collect(Collectors.toList());
	}

	/**
	 * Returns all the sets of cards that a player could use to claim a tunnel,
	 * sorted in increasing number of locomotive cards 
	 * 
	 * @param additionalCardsCount
	 *        The number of cards the player needs in addition to 
	 *        the initial ones in order to claim the tunnel
	 * 
	 * @param initialCards
	 * 				The initial cards the player used to attempt claiming
	 *        the tunnel
	 * 
	 * @param drawnCards
	 *        The cards that were drawn to decide of the additional
	 *        cards count
	 * 
	 * @return all the sets of cards that a player could use to claim a tunnel,
	 * sorted in increasing number of locomotive cards
	 * 
	 * @throws IllegalArgumentException
	 * 				 If additionalCardsCount is incorrect, if initialCards is empty or
	 *         if drawnCards is of incorrect size
	 */
	public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, 
	SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
		Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
		Preconditions.checkArgument(!initialCards.isEmpty() && initialCards.toSet().size() <= 2); //TODO create 2 as a constant
		Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

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
	 * Returns a state identical to the one applied to, except that the player
	 * claimed the given route using the given cards
	 * 
	 * @param route
	 *        The claimed route
	 * 
	 * @param claimCards
	 *        The cards used to claim the route
	 * 
	 * @return a state identical to the one applied to, except that the player
	 * claimed the given route using the given cards
	 */
	public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
		List<Route> updatedRoutes = new ArrayList<>(routes());
		updatedRoutes.add(route);

		return new PlayerState(tickets(), new SortedBag.Builder<Card>().add(cards().difference(claimCards)).build(), updatedRoutes);
	}

	/**
	 * Returns the number of points, eventually negative, earned by the player
	 * from their tickets
	 * 
	 * @return the number of points, eventually negative, earned by the player
	 * from their tickets
	 */
	public int ticketPoints() { return ticketPoints; }

	/**
	 * Returns the number of points the player earned from tickets and
	 * claiming routes
	 * 
	 * @return the number of points the player earned from tickets and
	 * claiming routes
	 */
	public int finalPoints() { return claimPoints() + ticketPoints(); }
}
