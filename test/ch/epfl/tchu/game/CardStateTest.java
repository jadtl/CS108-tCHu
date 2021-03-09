package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

class CardStateTest {
  @Test
  void ofWorksWithTrivialParameters() {
    Deck<Card> deck = Deck.of(SortedBag.of(3, Card.BLACK, 5, Card.RED), new Random());
    CardState cardState = CardState.of(deck);

    assertEquals(deck.withoutTopCards(5).topCard(), cardState.topDeckCard());
    assertEquals(deck.size() - 5, cardState.deckSize());
    assertEquals(0, cardState.discardsSize());
  }

  @Test
  void withoutTopDeckCardWorks() {
    Deck<Card> deck = Deck.of(SortedBag.of(3, Card.BLACK, 5, Card.RED), new Random());
    CardState cardState = CardState.of(deck);

    assertEquals(deck.withoutTopCards(6).topCard(), cardState.withoutTopDeckCard().topDeckCard());
    assertEquals(deck.size() - 6, cardState.withoutTopDeckCard().deckSize());
  }

  @Test
  void withDeckRecreatedFromDiscardsWorks() {
    Deck<Card> deck = Deck.of(SortedBag.of(3, Card.BLACK, 3, Card.RED), new Random());
    CardState cardState = CardState.of(deck);
    cardState = cardState.withDrawnFaceUpCard(0);
    cardState = cardState.withDeckRecreatedFromDiscards(new Random());

    assertEquals(1, cardState.deckSize());
    assertEquals(deck.topCard(), cardState.topDeckCard());
  }
}
