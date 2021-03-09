package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PublicCardStateTest {
  @Test
  void constructorWorksWithTrivialParameters() {
    List<Card> faceUpCards = List.of(Card.RED, Card.RED, Card.RED, Card.RED, Card.RED);
    int deckSize = 10;
    int discardsSize = 0;

    assertDoesNotThrow(() -> { new PublicCardState(faceUpCards, deckSize, discardsSize); });
  }

  @Test
  void totalSizeWorks() {
    List<Card> faceUpCards = List.of(Card.RED, Card.RED, Card.RED, Card.RED, Card.RED);
    int deckSize = 10;
    int discardsSize = 0;
    PublicCardState publicCardState = new PublicCardState(faceUpCards, deckSize, discardsSize);

    assertEquals(faceUpCards.size() + deckSize + 0, publicCardState.totalSize());
  }

  @Test

  void faceUpCardWorks() {
    List<Card> faceUpCards = List.of(Card.RED, Card.WHITE, Card.BLACK, Card.LOCOMOTIVE, Card.VIOLET);
    int deckSize = 10;
    int discardsSize = 0;
    PublicCardState publicCardState = new PublicCardState(faceUpCards, deckSize, discardsSize);

    assertEquals(Card.RED, publicCardState.faceUpCard(0));
    assertEquals(Card.WHITE, publicCardState.faceUpCard(1));
    assertEquals(Card.BLACK, publicCardState.faceUpCard(2));
    assertEquals(Card.LOCOMOTIVE, publicCardState.faceUpCard(3));
    assertEquals(Card.VIOLET, publicCardState.faceUpCard(4));
  }
}
