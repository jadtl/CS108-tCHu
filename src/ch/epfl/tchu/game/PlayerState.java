package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * 
 * 
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class PlayerState extends PublicPlayerState {
	private final SortedBag<Ticket> tickets;
	private final SortedBag<Card> cards;
	private final int ticketPoints;

	/**
	 * 
	 * 
	 * @param tickets
	 * 
	 * @param cards
	 * 
	 * @param routes
	 */
	public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
		super(tickets.size(), cards.size(), routes);

		this.tickets = tickets;
		this.cards = cards;
		// TODO Determine ticketPoints
		this.ticketPoints = 0;
	}

	/**
	 * 
	 * 
	 * @param initialCards
	 * 
	 * @return
	 */
	public static PlayerState initial(SortedBag<Card> initialCards) {
		Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);

		return new PlayerState(new SortedBag.Builder<Ticket>().build(), initialCards, new ArrayList<Route>());
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public SortedBag<Ticket> tickets() { return tickets; }

	/**
	 * 
	 * 
	 * @param newTickets
	 * 
	 * @return
	 */
	public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
		return new PlayerState(new SortedBag.Builder<Ticket>().add(tickets())
		.add(newTickets).build(), cards(), routes());
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public SortedBag<Card> cards() { return cards; }

	/**
	 * 
	 * 
	 * @param card
	 * 
	 * @return
	 */
	public PlayerState withAddedCard(Card card) {
		return new PlayerState(tickets(), new SortedBag.Builder<Card>()
		.add(cards()).add(card).build(), routes());
	}

	/**
	 * 
	 * 
	 * @param additionalCards
	 * 
	 * @return
	 */
	public PlayerState withAddedCards(SortedBag<Card> additionalCards) { 
		return new PlayerState(tickets(), new SortedBag.Builder<Card>()
		.add(cards()).add(additionalCards).build(), routes());
	}

	/**
	 * 
	 * 
	 * @param route
	 * 
	 * @return
	 */
	public boolean canClaimRoute(Route route) {
		boolean hasClaimCards = false;
		for(SortedBag<Card> claimCards : possibleClaimCards(route))
			if(cards().contains(claimCards)) hasClaimCards = true;
		
		return (cardCount() >= route.length() && hasClaimCards);
	}

	/**
	 * 
	 * 
	 * @param route
	 * 
	 * @return
	 */
	public List<SortedBag<Card>> possibleClaimCards(Route route) {
		Preconditions.checkArgument(carCount() >= route.length());

		List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();
		for (SortedBag<Card> claimCards : possibleClaimCards(route)) {
			if (cards().contains(claimCards)) possibleClaimCards.add(claimCards);
		}

		return possibleClaimCards;
	}

	/**
	 * 
	 * 
	 * @param additionalCardsCount
	 * 
	 * @param initialCards
	 * 
	 * @param drawnCards
	 * 
	 * @return
	 */
	public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, 
	SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
		Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
		Preconditions.checkArgument(!initialCards.isEmpty() && drawnCards.size() == 3);
		// TODO Implement possibleAdditionalCards
		return new ArrayList<>();
	}

	/**
	 * 
	 * 
	 * @param route
	 * 
	 * @param claimCards
	 * 
	 * @return
	 */
	public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
		List<Route> updatedRoutes = new ArrayList<>(routes());
		updatedRoutes.add(route);

		return new PlayerState(tickets(), new SortedBag.Builder<Card>().add(cards().difference(claimCards)).build(), updatedRoutes);
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public int ticketPoints() { return ticketPoints; }

	/**
	 * 
	 * 
	 * @return
	 */
	public int finalPoints() { return claimPoints() + ticketPoints(); }
}
