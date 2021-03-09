package ch.epfl.tchu.game;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * A overground or underground route that links 2 neighbor cities
 * with a length and a color
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class CardState extends PublicCardState {
	private final Deck<Card> deckCards;
	private final SortedBag<Card> discardedCards;

	/**
	 * 
	 * @param faceUpCards
	 * @param deckCards
	 * @param discardedCards
	 */
	private CardState(List<Card> faceUpCards, Deck<Card> deckCards, SortedBag<Card> discardedCards) {
		super(faceUpCards, deckCards.size(), discardedCards.size());

		this.deckCards = deckCards;
		this.discardedCards = discardedCards;
	}

	/**
	 * 
	 * @param deck
	 * 
	 * @return
	 */
	public static CardState of(Deck<Card> deck) {
		Preconditions.checkArgument(deck.size() >= 5);

		return new CardState(deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList(),
				deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT), new SortedBag.Builder<Card>().build());
	}

	/**
	 * 
	 * @param slot
	 * 
	 * @return
	 */
	CardState withDrawnFaceUpCard(int slot) {
		Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);

		SortedBag<Card> updatedDiscardedCards = new SortedBag.Builder<Card>().add(discardedCards)
				.add(faceUpCards().get(slot)).build();
		List<Card> faceUpCards = List.copyOf(faceUpCards());
		faceUpCards.set(slot, topDeckCard());

		return new CardState(faceUpCards, deckCards.withoutTopCard(), updatedDiscardedCards);
	}

	/**
	 * 
	 * @return
	 */
	public Card topDeckCard() { return deckCards.topCard(); }

	/**
	 * 
	 * @return
	 */
	public CardState withoutTopDeckCard() { return new CardState(faceUpCards(), deckCards.withoutTopCard(), discardedCards); }

	/**
	 * 
	 * @param rng
	 * 
	 * @return
	 */
	public CardState withDeckRecreatedFromDiscards(Random rng) {
		Preconditions.checkArgument(deckCards.size() == 0);
		
		return new CardState(faceUpCards(), Deck.of(discardedCards, rng), new SortedBag.Builder<Card>().build());
	}

	/**
	 * 
	 * @param additionalDiscards
	 * 
	 * @return
	 */
	public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) { 
		return new CardState(faceUpCards(), deckCards, new SortedBag.Builder<Card>()
		.add(discardedCards).add(additionalDiscards).build());
	}
}
