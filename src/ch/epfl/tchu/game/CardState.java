package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * A card state of the game
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 * @see ch.epfl.tchu.game.PublicCardState
 */
public final class CardState extends PublicCardState {
    private final Deck<Card> deck;
    private final SortedBag<Card> discardedCards;

    /**
     * A card state from face-up cards, a deck and discards
     *
     * @param faceUpCards    The visible {@link List} of {@link Card} on the board
     * @param deck           A {@link Deck} of {@link Card} from which the player can draw
     * @param discardedCards A discarded {@link SortedBag} of {@link Card}
     */
    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discardedCards) {
        super(faceUpCards, deck.size(), discardedCards.size());

        this.deck = deck;
        this.discardedCards = discardedCards;
    }

    /**
     * A card state from a deck
     *
     * @param deck A {@link Deck} of {@link Card}
     * @return A {@link CardState} from a {@link Deck}
     * @throws IllegalArgumentException If {@code deck.size()} is below {@link Constants#FACE_UP_CARDS_COUNT}
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
     * A card state for which the chosen face-up card is replaced by the top deck card and discarded
     *
     * @param slot The index of the drawn face-up card
     * @return A {@link CardState} for which the face-up card at {@code slot} is replaced by {@link CardState#topDeckCard()} and discarded
     * @throws IndexOutOfBoundsException If {@code slot} is not within {@link CardState#faceUpCards()} range
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);

        List<Card> faceUpCards = new ArrayList<>(faceUpCards());
        faceUpCards.set(slot, topDeckCard());

        return new CardState(faceUpCards, deck.withoutTopCard(), discardedCards);
    }

    /**
     * The card on top of the deck
     *
     * @return The {@link Card} on top of {@link CardState#deck}
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());

        return deck.topCard();
    }

    /**
     * A card state for which the deck top card has been removed
     *
     * @return A {@link CardState} for which the {@link CardState#topDeckCard()} has been removed
     */
    public CardState withoutTopDeckCard() {
        return new CardState(faceUpCards(), deck.withoutTopCard(), discardedCards);
    }

    /**
     * A card state for which the empty deck has been replaced by the discards, shuffled
     *
     * @param rng A {@link Random} number generator
     * @return A {@link CardState} for which the empty deck has been replaced by the discards and shuffled
     * @throws IllegalArgumentException If the deck is not empty
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(isDeckEmpty());

        return new CardState(faceUpCards(), Deck.of(discardedCards, rng), new SortedBag.Builder<Card>().build());
    }

    /**
     * The card state for which the additional discards have been added to its discards
     *
     * @param additionalDiscards The {@link SortedBag} of {@link Card} to add to the {@link CardState#discardedCards}
     * @return The {@link CardState} for which {@code additionalDiscards} have been added to its {@link CardState#discardedCards}
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(faceUpCards(), deck, new SortedBag.Builder<Card>()
                .add(discardedCards).add(additionalDiscards).build());
    }
}
