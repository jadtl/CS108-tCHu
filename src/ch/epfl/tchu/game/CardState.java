package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * A overground or underground route that links 2 neighbor cities
 * with a length and a color
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 * @see ch.epfl.tchu.game.PublicCardState
 */
public final class CardState extends PublicCardState {
    private final Deck<Card> deck;
    private final SortedBag<Card> discardedCards;

    /**
     * Constructs a card state from face-up cards, a deck and discards
     *
     * @param faceUpCards    The visible cards on the board
     * @param deck           A pile of shuffled cards
     * @param discardedCards A pile of discarded cards
     */
    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discardedCards) {
        super(faceUpCards, deck.size(), discardedCards.size());

        this.deck = deck;
        this.discardedCards = discardedCards;
    }

    /**
     * Returns a new card state from a deck
     *
     * @param deck A deck of cards
     * @return a new card state from a deck
     * @throws IllegalArgumentException If the deck size is below the required face-up cards size
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

        List<Card> faceUpCards = new ArrayList<>();
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            faceUpCards.add(deck.withoutTopCards(i).topCard());
        }

        return new CardState(faceUpCards,
                deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT), new SortedBag.Builder<Card>().build());
    }

    /**
     * Returns a card state for which the chosen face-up card is replaced by the top deck card and discarded
     *
     * @param slot The index of the drawn face-up card
     * @return a card state for which the chosen face-up card is replaced by the top deck card and discarded
     * @throws IndexOutOfBoundsException If slot is not within faceUpCards range
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);

        List<Card> faceUpCards = new ArrayList<>(faceUpCards());
        faceUpCards.set(slot, topDeckCard());

        return new CardState(faceUpCards, deck.withoutTopCard(), discardedCards);
    }

    /**
     * Returns the card on top of the deck
     *
     * @return the card on top of the deck
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());

        return deck.topCard();
    }

    /**
     * Returns a card state for which the deck top card has been removed
     *
     * @return a card state for which the deck top card has been removed
     */
    public CardState withoutTopDeckCard() {
        return new CardState(faceUpCards(), deck.withoutTopCard(), discardedCards);
    }

    /**
     * Returns a card state for which the empty deck has been replaced by the discards, shuffled
     *
     * @param rng A random number generator
     * @return a card state for which the empty deck has been replaced by the discards, shuffled
     * @throws IllegalArgumentException If the deck is not empty
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(isDeckEmpty());

        return new CardState(faceUpCards(), Deck.of(discardedCards, rng), new SortedBag.Builder<Card>().build());
    }

    /**
     * Returns the card state for which additionalDiscards has been added to its discards
     *
     * @param additionalDiscards The cards to add to the discards
     * @return the card state for which additionalDiscards has been added to its discards
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(faceUpCards(), deck, new SortedBag.Builder<Card>()
                .add(discardedCards).add(additionalDiscards).build());
    }
}
