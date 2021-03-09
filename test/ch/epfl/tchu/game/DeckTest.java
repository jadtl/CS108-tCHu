package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    @Test
    void topCardsWorksWithTrivialDeck() {
        SortedBag<Card> bag = SortedBag.of(2, Card.LOCOMOTIVE, 1, Card.BLUE);
        Deck<Card> deck = Deck.of(bag, new Random());

        bag = deck.topCards(2);

        assertEquals(2, bag.size());
    }
}