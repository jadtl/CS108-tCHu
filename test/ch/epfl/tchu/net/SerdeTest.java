package ch.epfl.tchu.net;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PublicCardState;
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
    List<String> toSerialize = List.of("Imagine", "je", "fonctionne", "");
    assertEquals("SW1hZ2luZQ==,amU=,Zm9uY3Rpb25uZQ==,", Serdes.STRING_LIST.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.STRING_LIST.deserialize(Serdes.STRING_LIST.serialize(toSerialize)));
  }

  @Test
  void cardListSerdeWorks() {
    List<Card> toSerialize = List.of(Card.RED, Card.BLUE, Card.BLACK);
    assertEquals("6,2,0", Serdes.CARD_LIST.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.CARD_LIST.deserialize(Serdes.CARD_LIST.serialize(toSerialize)));
  }

  @Test
  void routeListSerdeWorks() {
    List<Route> toSerialize = List.of(ChMap.routes().get(14), ChMap.routes().get(7), ChMap.routes().get(17));
    assertEquals("14,7,17", Serdes.ROUTE_LIST.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.ROUTE_LIST.deserialize(Serdes.ROUTE_LIST.serialize(toSerialize)));
  }

  @Test
  void cardSortedBagSerdeWorks() {
    SortedBag<Card> toSerialize = SortedBag.of(2, Card.RED, 3, Card.LOCOMOTIVE);
    assertEquals("6,6,8,8,8", Serdes.CARD_SORTED_BAG.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.CARD_SORTED_BAG.deserialize(Serdes.CARD_SORTED_BAG.serialize(toSerialize)));
  }

  @Test
  void ticketSortedBagSerdeWorks() {
    SortedBag<Ticket> toSerialize = SortedBag.of(4, ChMap.tickets().get(7), 7, ChMap.tickets().get(2));
    assertEquals("2,2,2,2,2,2,2,7,7,7,7", Serdes.TICKET_SORTED_BAG.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.TICKET_SORTED_BAG.deserialize(Serdes.TICKET_SORTED_BAG.serialize(toSerialize)));
  }

  @Test
  void cardSortedBagListSerdeWorks() {
    List<SortedBag<Card>> toSerialize = List.of(SortedBag.of(2, Card.LOCOMOTIVE, 1, Card.BLACK), SortedBag.of(3, Card.GREEN), SortedBag.of());
    assertEquals("0,8,8;3,3,3;", Serdes.CARD_SORTED_BAG_LIST.serialize(toSerialize));
    assertEquals(toSerialize, Serdes.CARD_SORTED_BAG_LIST.deserialize(Serdes.CARD_SORTED_BAG_LIST.serialize(toSerialize)));
  }

  @Test
  void publicCardStateSerdeWorks() {
    List<Card> faceUpCards = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED);
    PublicCardState publicCardState = new PublicCardState(faceUpCards, 30, 31);
    assertEquals("6,7,2,0,6;30;31", Serdes.PUBLIC_CARD_STATE.serialize(publicCardState));
    assertEquals(publicCardState, Serdes.PUBLIC_CARD_STATE.deserialize(Serdes.PUBLIC_CARD_STATE.serialize(publicCardState)));
  }
}