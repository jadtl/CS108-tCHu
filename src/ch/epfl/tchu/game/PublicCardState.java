package ch.epfl.tchu.game;

import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;

/**
 * A public card state with face-up cards, a deck and discards
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public class PublicCardState {
    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * A public card state with its face-up cards, deck and discards
     *
     * @param faceUpCards  A {@link List} of face-up {@link Card}
     * @param deckSize     The size of the {@link Deck}
     * @param discardsSize The size of the discards
     * @throws IllegalArgumentException If {@code faceUpCards} count is not {@link Constants#FACE_UP_CARDS_COUNT},
     *                                  or if deck cards or discard cards are less than zero
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT && deckSize >= 0 && discardsSize >= 0);

        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * The list of face-up cards
     *
     * @return The {@link List} of face-up {@link Card}
     */
    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    /**
     * The face-up card of the given slot
     *
     * @param slot The index in the {@link List} of face-up {@link Card}
     * @return The face-up {@link Card} at {@code slot}
     * @throws IndexOutOfBoundsException If {@code slot} is not within {@code faceUpCards}' range
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);

        return faceUpCards.get(slot);
    }

    /**
     * The size of the deck
     *
     * @return The size {@link PublicCardState#deckSize} of the deck
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * Returns true iff. the deck is empty
     *
     * @return true iff. {@link PublicCardState#deckSize} is zero
     */
    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    /**
     * Returns the size of the discarded cards pile
     *
     * @return the size {@link PublicCardState#discardsSize} of the discards
     */
    public int discardsSize() {
        return discardsSize;
    }

}
