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
	private final Deck<Card> deck;
	private final SortedBag<Card> discardedCards;

	/**
	 * Constructs a card state from face-up cards, a deck and discards
	 * 
	 * @param faceUpCards
	 * @param deck
	 * @param discardedCards
	 */
	private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discardedCards) {
		super(faceUpCards, deck.size(), discardedCards.size());

		this.deck = deck;
		this.discardedCards = discardedCards;
	}

	/**
	 * Returns a new card state from a deck
	 * 
	 * @param deck a deck of cards
	 * 
	 * @return a new card state from a deck
	 * 
	 * @throws IllegalArgumentException if the deck size is below the required face-up cards size
	 */
	public static CardState of(Deck<Card> deck) {
		Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

		return new CardState(deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList(),
				deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT), new SortedBag.Builder<Card>().build());
	}

	/**
	 * Returns a card state for which the chosen face-up card is replaced by the top deck card and discarded
	 * 
	 * @param slot the index of the drawn face-up card
	 * 
	 * @return a card state for which the chosen face-up card is replaced by the top deck card and discarded
	 * 
	 * @throws IndexOutOfBounds if slot is not within faceUpCards range
	 */
	CardState withDrawnFaceUpCard(int slot) {
		Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);

		SortedBag<Card> updatedDiscardedCards = new SortedBag.Builder<Card>().add(discardedCards)
				.add(faceUpCards().get(slot)).build();
		List<Card> faceUpCards = List.copyOf(faceUpCards());
		faceUpCards.set(slot, topDeckCard());

		return new CardState(faceUpCards, deck.withoutTopCard(), updatedDiscardedCards);
	}

	/**
	 * Returns the card on top of the deck
	 * 
	 * @return the card on top of the deck
	 */
	public Card topDeckCard() { return deck.topCard(); }

	/**
	 * Returns a card state for which the deck top card has been removed
	 * 
	 * @return a card state for which the deck top card has been removed
	 */
	public CardState withoutTopDeckCard() { return new CardState(faceUpCards(), deck.withoutTopCard(), discardedCards); }

	/**
	 * Returns a card state for which the empty deck has been replaced by the discards, shuffled
	 * 
	 * @param rng a random number generator
	 * 
	 * @return a card state for which the empty deck has been replaced by the discards, shuffled
	 * 
	 * @throws IllegalArgumentException if the deck is not empty
	 */
	public CardState withDeckRecreatedFromDiscards(Random rng) {
		Preconditions.checkArgument(isDeckEmpty());
		
		return new CardState(faceUpCards(), Deck.of(discardedCards, rng), new SortedBag.Builder<Card>().build());
	}

	/**
	 * Returns the card state for which additionalDiscards has been added to its discards
	 * 
	 * @param additionalDiscards the cards to add to the discards
	 * 
	 * @return the card state for which additionalDiscards has been added to its discards
	 */
	public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) { 
		return new CardState(faceUpCards(), deck, new SortedBag.Builder<Card>()
		.add(discardedCards).add(additionalDiscards).build());
	}
}
