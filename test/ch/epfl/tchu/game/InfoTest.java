package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.gui.Info;
import ch.epfl.tchu.gui.StringsFr;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class InfoTest {
  @Test
  void cardNameWorks() {
    assertEquals("noires", Info.cardName(Card.BLACK, 2));
  }

  @Test
  void drawWorks() {
    String playerName = "Elon Musk";
    String opponentName = "Jeff Bezos";

    assertEquals("\nElon Musk et Jeff Bezos sont ex æqo avec 10 points !\n", Info.draw(List.of(playerName, opponentName), 10));
  }
}
