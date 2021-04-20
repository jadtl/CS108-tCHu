package ch.epfl.tchu.net;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;

import static org.junit.jupiter.api.Assertions.*;


public class SerdeTest {
  @Test
  void integerSerdeWorks() {
    Integer toSerialize = 42;
    assertEquals("42", Serdes.INTEGER.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.INTEGER.deserialize(Serdes.INTEGER.serialize(toSerialize)));
  }

  @Test
  void stringSerdeWorks() {
    String toSerialize = "Imagine je fonctionne";
    assertEquals("SW1hZ2luZSBqZSBmb25jdGlvbm5l", Serdes.STRING.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.STRING.deserialize(Serdes.STRING.serialize(toSerialize)));
  }

  @Test
  void playerIdSerdeWorks() {
    PlayerId toSerialize = PlayerId.PLAYER_2;
    assertEquals("1", Serdes.PLAYER_ID.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.PLAYER_ID.deserialize(Serdes.PLAYER_ID.serialize(toSerialize)));
  }

  @Test
  void turnKindSerdeWorks() {
    Player.TurnKind toSerialize = Player.TurnKind.CLAIM_ROUTE;
    assertEquals("2", Serdes.TURN_KIND.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.TURN_KIND.deserialize(Serdes.TURN_KIND.serialize(toSerialize)));
  }

  @Test
  void cardSerdeWorks() {
    Card toSerialize = Card.LOCOMOTIVE;
    assertEquals("8", Serdes.CARD.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.CARD.deserialize(Serdes.CARD.serialize(toSerialize)));
  }

  @Test
  void routeSerdeWorks() {
    Route toSerialize = ChMap.routes().get(7);
    assertEquals("7", Serdes.ROUTE.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.ROUTE.deserialize(Serdes.ROUTE.serialize(toSerialize)));
  }
}
