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
     * A deck composed of shuffled elements from cards
     *
     * @param cards A {@link SortedBag} of cards
     * @param rng   A {@link Random} number generator
     * @param <C>   The type of the cards
     * @return a {@link Deck} composed of shuffled elements from cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> shuffledCards = cards.toList();
        Collections.shuffle(shuffledCards, rng);

        return new Deck<>(shuffledCards);
    }

    /**
     * A {@link Deck} from a {@link List} of cards
     *
     * @param cards A {@link List} of cards
     */
    private Deck(List<C> cards) {
        this.cards = cards;
    }

    /**
     * The size of the deck
     *
     * @return the {@link List#size()} of the {@link Deck}
     */
    public int size() {
        return cards.size();
    }

    /**
     * The emptiness state of the deck
     *
     * @return true iff. {@link Deck#size()} is zero
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * The top element of the deck
     *
     * @return the first element of {@link Deck}'s {@link Deck#cards}
     * @throws IllegalArgumentException If the {@link Deck} {@link Deck#isEmpty()}
     */
    public C topCard() {
        Preconditions.checkArgument(!isEmpty());

        return cards.get(0);
    }

    /**
     * The same deck without the top card
     *
     * @return The same {@link Deck} without the first element of {@link Deck#cards}
     * @throws IllegalArgumentException If the {@link Deck} {@link Deck#isEmpty()}
     */
    public Deck<C> withoutTopCard() {
        return withoutTopCards(1);
    }

    /**
     * A sorted bag with a given number of top cards
     *
     * @param count The number of top cards
     * @return A {@link SortedBag} with {@code count} top cards
     * @throws IllegalArgumentException If {@code count} is not between {@code 0} and {@link Deck#size()}
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(0 <= count && count <= size());

        SortedBag.Builder<C> builder = new SortedBag.Builder<>();
        cards.subList(0, count).forEach(builder::add);

        return builder.build();
    }

    /**
     * The same deck without a number of top cards
     *
     * @param count The number of top cards to remove from the {@link Deck}
     * @return The same {@link Deck} without {@code count} top cards
     * @throws IllegalArgumentException If {@code count} is not between {@code 0} and {@link Deck#size()}
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(0 <= count && count <= size());

        return new Deck<>(cards.subList(count, size()));
    }
}
