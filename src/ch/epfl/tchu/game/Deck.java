package ch.epfl.tchu.game;

import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

public final class Deck<C extends Comparable<C>> {

	private int size;

	<C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {

	}

	public int size() {
		return size;
	}

	boolean isEmpty() {

		Preconditions.checkArgument(size() != 0);
	}

	C topCard() {

	}

	Deck<C> withoutTopCard() {

	}

	SortedBag<C> topCards(int count) {

	}

	Deck<C> withoutTopCards(int count) {

	}

}
