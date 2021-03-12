package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

public final class PlayerState extends PublicPlayerState {
	private final SortedBag<Ticket> tickets;
	private final SortedBag<Card> cards;

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
	 * @return
	 */
	public SortedBag<Ticket> tickets() { return tickets; }

	public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
		return new PlayerState(new SortedBag.Builder<Ticket>().add(tickets())
		.add(newTickets).build(), cards(), routes());
	}

	/**
	 * 
	 * @return
	 */
	public SortedBag<Card> cards() {

		return cards;
	}

	/**
	 * 
	 * @param card
	 * 
	 * @return
	 */
	public PlayerState withAddedCard(Card card) {

		return new PlayerState(tickets(), new SortedBag.Builder<Card>().add(cards()).add(card).build(), routes());
	}

	/**
	 * 
	 * @param additionalCards
	 * 
	 * @return
	 */
	public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
		
		return new PlayerState(tickets(), new SortedBag.Builder<Card>().add(cards()).add(additionalCards).build(), routes());
	}

	/**
	 * 
	 * @param route
	 * 
	 * @return
	 */
	public boolean canClaimRoute(Route route) {

		boolean hasClaimCards = false;
		for(SortedBag<Card> claimCards : possibleClaimCards(route)) {
			if(cards().contains(claimCards)) hasClaimCards = true;
			
		}
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
	public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards,
			SortedBag<Card> drawnCards) {

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

	}

	/**
	 * 
	 * @return
	 */
	public int ticketPoints() {

	}

	/**
	 * 
	 * @return
	 */
	public int finalPoints() {

	}
}
