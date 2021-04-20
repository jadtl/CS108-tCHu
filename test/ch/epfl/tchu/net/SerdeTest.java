package ch.epfl.tchu.net;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;


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

  @Test
  void ticketSerdeWorks() {
    Ticket toSerialize = ChMap.tickets().get(9);
    assertEquals("9", Serdes.TICKET.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.TICKET.deserialize(Serdes.TICKET.serialize(toSerialize)));
  }

  @Test
  void stringListSerdeWorks() {
    List<String> toSerialize = List.of("Imagine", "je", "fonctionne");
    assertEquals("SW1hZ2luZQ==,amU=,Zm9uY3Rpb25uZQ==", Serdes.STRING_LIST.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.STRING_LIST.deserialize(Serdes.STRING_LIST.serialize(toSerialize)));
  }

  @Test
  void cardListSerdeWorks() {
    List<Card> toSerialize = List.of(Card.RED, Card.BLUE, Card.BLACK);
    assertEquals("6,2,0", Serdes.CARD_LIST.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.CARD_LIST.deserialize(Serdes.CARD_LIST.serialize(toSerialize)));
  }

  @Test
  void routeListWorks() {
    List<Route> toSerialize = List.of(ChMap.routes().get(14), ChMap.routes().get(7), ChMap.routes().get(17));
    assertEquals("14,7,17", Serdes.ROUTE_LIST.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.ROUTE_LIST.deserialize(Serdes.ROUTE_LIST.serialize(toSerialize)));
  }

  @Test
  void cardSortedBagWorks() {
    SortedBag<Card> toSerialize = SortedBag.of(2, Card.RED, 3, Card.LOCOMOTIVE);
    assertEquals("6,6,8,8,8", Serdes.CARD_SORTED_BAG.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.CARD_SORTED_BAG.deserialize(Serdes.CARD_SORTED_BAG.serialize(toSerialize)));
  }
}
