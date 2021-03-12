package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

public final class PlayerState extends PublicPlayerState {

	private final SortedBag<Ticket> tickets;
	private final SortedBag<Card> cards;

	public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {

		super(tickets.size(), cards.size(), routes);
		this.tickets = tickets;
		this.cards = cards;
	}

	public static PlayerState initial(SortedBag<Card> initialCards) {

		Preconditions.checkArgument(initialCards.size() == 4);
		return new PlayerState(new SortedBag.Builder<Ticket>().build(), initialCards, new ArrayList<Route>());

	}

	public SortedBag<Ticket> tickets() {

		return tickets;
	}

	public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {

		return new PlayerState(new SortedBag.Builder<Ticket>().add(tickets()).add(newTickets).build(), cards(),
				routes());

	}

	public SortedBag<Card> cards() {

		return cards;
	}

	public PlayerState withAddedCard(Card card) {

		return new PlayerState(tickets(), new SortedBag.Builder<Card>().add(cards()).add(card).build(), routes());
	}

	public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
		
		return new PlayerState(tickets(), new SortedBag.Builder<Card>().add(cards()).add(additionalCards).build(), routes());
	}

	public boolean canClaimRoute(Route route) {

		boolean hasClaimCards = false;
		for(SortedBag<Card> claimCards : possibleClaimCards(route)) {
			if(cards().contains(claimCards)) hasClaimCards = true;
			
		}
		return (cardCount()>= route.length() && hasClaimCards);
		
	}

	public List<SortedBag<Card>> possibleClaimCards(Route route) {

	}

	public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards,
			SortedBag<Card> drawnCards) {

	}

	public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {

	}

	public int ticketPoints() {

	}

	public int finalPoints() {

	}

}
