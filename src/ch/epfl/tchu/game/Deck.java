package ch.epfl.tchu.game;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * A deck of shuffled elements
 * 
 * @param <C> the type of the deck elements
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class Deck<C extends Comparable<C>> {
	private final List<C> cards;

	/**
	 * Returns a deck composed of shuffled elements from cards
	 * 
	 * @param cards a list of elements
	 * @param rng a random number generator
	 * @param <C> the type of the elements of cards
	 * 
	 * @return a deck composed of shuffled elements from cards
	 */
	public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
		List<C> shuffledCards = cards.toList();
		Collections.shuffle(shuffledCards, rng);

		return new Deck<C>(shuffledCards);
	}

	/**
	 * Constructs a deck from a list of elements
	 * 
	 * @param cards a list of elements
	 */
	private Deck(List<C> cards) {
		this.cards = cards;
	}

	/**
	 * Returns the size of the deck
	 * 
	 * @return the size of the deck 
	 */
	public int size() {
		return cards.size();
	}

	public boolean isEmpty() { return size() != 0;}

	/**
	 * Returns the top element of the deck
	 *
	 * @return the top element of the deck
	 */
	public C topCard() {
		return cards.get(0);
	}

	/**
	 * Returns the same deck without the top card
	 * 
	 * @return the same deck without the top card
	 * 
	 * @throws IllegalArgumentException if the deck is empty
	 */
	public Deck<C> withoutTopCard() {
		Preconditions.checkArgument(isEmpty());

		return new Deck<C>(cards.subList(1, size()));
	}

	/**
	 * Returns a sorted bag with a given number of top cards
	 * 
	 * @param count the number of top cards
	 * 
	 * @return a sorted bag with count top cards
	 * 
	 * @throws IllegalArgumentException if count is not between 0 and size of the deck
	 */
	public SortedBag<C> topCards(int count) {
		Preconditions.checkArgument(count >= 0 && count <= size());

		SortedBag.Builder<C> builder = new SortedBag.Builder<C>();
		for (C element : cards.subList(0, count)) {
			builder.add(element);
		}

		return builder.build();
	}

	/**
	 * Returns the same deck without a number of top cards
	 * 
	 * @param count the number of top cards to remove from the deck
	 * 
	 * @return the same deck without count top cards
	 * 
	 * @throws IllegalArgumentException if count is not between 0 and size of the deck
	 */
	public Deck<C> withoutTopCards(int count) {
		Preconditions.checkArgument(count >= 0 && count <= size());

		return new Deck<C>(cards.subList(count, size()));
	}

}
