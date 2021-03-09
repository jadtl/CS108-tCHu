package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;

class CardStateTest {
  @Test
  void ofWorksWithTrivialParameters() {
    Deck<Card> deck = Deck.of(SortedBag.of(3, Card.BLACK, 5, Card.RED), new Random());
    CardState cardState = CardState.of(deck);

    assertEquals(deck.withoutTopCards(5).topCard(), cardState.topDeckCard());
  }
}
