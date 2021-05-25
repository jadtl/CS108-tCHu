package ch.epfl.tchu.game;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * A deck of shuffled elements
 *
 * @param <C> The type of the {@link Deck}'s elements
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public final class Deck<C extends Comparable<C>> {
    private final List<C> cards;

    /**
     * Returns a deck composed of shuffled elements from cards
     *
     * @param cards A {@link SortedBag} of cards
     * @param rng   A {@link Random} number generator
     * @param <C>   The type of the cards
     * @return a {@link Deck} composed of shuffled elements from cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> shuffledCards = cards.toList();
        Collections.shuffle(shuffledCards, rng);

        return new Deck<C>(shuffledCards);
    }

    /**
     * Constructs a {@link Deck} from a {@link List} of cards
     *
     * @param cards A {@link List} of cards
     */
    private Deck(List<C> cards) {
        this.cards = cards;
    }

    /**
     * Returns the size of the deck
     *
     * @return the {@link List#size()} of the {@link Deck}
     */
    public int size() {
        return cards.size();
    }

    /**
     * Returns the emptiness state of the deck
     *
     * @return true iff. {@link Deck#size()} is zero
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the top element of the deck
     *
     * @return the top element of the deck
     * @throws IllegalArgumentException If the {@link Deck} {@link Deck#isEmpty()}
     */
    public C topCard() {
        Preconditions.checkArgument(!isEmpty());

        return cards.get(0);
    }

    /**
     * Returns the same deck without the top card
     *
     * @return the same deck without the top card
     * @throws IllegalArgumentException If the deck is empty
     */
    public Deck<C> withoutTopCard() {
        return withoutTopCards(1);
    }

    /**
     * Returns a sorted bag with a given number of top cards
     *
     * @param count The number of top cards
     * @return a sorted bag with count top cards
     * @throws IllegalArgumentException If count is not between 0 and size of the deck
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(0 <= count && count <= size());

        SortedBag.Builder<C> builder = new SortedBag.Builder<C>();
        cards.subList(0, count).forEach((C element) -> builder.add(element));

        return builder.build();
    }

    /**
     * Returns the same deck without a number of top cards
     *
     * @param count The number of top cards to remove from the deck
     * @return the same deck without count top cards
     * @throws IllegalArgumentException If count is not between 0 and size of the deck
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(0 <= count && count <= size());

        return new Deck<C>(cards.subList(count, size()));
    }

}
