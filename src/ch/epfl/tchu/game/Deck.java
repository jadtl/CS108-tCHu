package ch.epfl.tchu.game;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * A Trail between two stations in the network
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class Deck<C extends Comparable<C>> {
	private final List<C> cards;

	/**
	 *
	 * @param cards
	 * @param rng
	 * @param <C>
	 * @return
	 */
	public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {

		List<C> shuffledCards = cards.toList();
		Collections.shuffle(shuffledCards, rng);

		return new Deck(shuffledCards);
	}

	/**
	 *
	 * @param cards
	 */
	private Deck(List<C> cards) {
		this.cards = cards;
	}

	/**
	 *
	 * @return
	 */
	public int size() {
		return cards.size();
	}

	boolean isEmpty() { return size() != 0;}

	/**
	 * Returns the top element of the deck
	 *
	 * @return the top element of the deck
	 */
	C topCard() {
		return cards.get(0);
	}

	/**
	 *
	 * @return
	 */
	public Deck<C> withoutTopCard() {
		Preconditions.checkArgument(size() != 0);

		return new Deck<C>(cards.subList(1, size()));
	}

	/**
	 *
	 * @param count
	 * @return
	 */
	public SortedBag<C> topCards(int count) {
		Preconditions.checkArgument(count >= 0 && count <= size());

		SortedBag.Builder builder = new SortedBag.Builder();
		for (C element : cards.subList(0, count)) {
			builder.add(element);
		}

		return builder.build();
	}

	/**
	 *
	 * @param count
	 * @return
	 */
	public Deck<C> withoutTopCards(int count) {
		Preconditions.checkArgument(count >= 0 && count <= size());

		return new Deck<C>(cards.subList(count, size()));
	}

}
