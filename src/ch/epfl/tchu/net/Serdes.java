package ch.epfl.tchu.net;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Base64;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicCardState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.PublicPlayerState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * 
 */
public class Serdes {
  /**
   * Class is not instanciable
   */
  private Serdes() {}

  public static final Serde<Integer> INTEGER = Serde.of(
    i -> Integer.toString(i), 
    Integer::parseInt
  );

  public static final Serde<String> STRING = Serde.of(
    i -> new String(Base64.getEncoder().encode(((String)i).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8),
    i -> new String(Base64.getDecoder().decode(i), StandardCharsets.UTF_8)
  );

  public static final Serde<PlayerId> PLAYERID = Serde.oneOf(PlayerId.ALL);

  public static final Serde<Player.TurnKind> TURNKIND = Serde.oneOf(Player.TurnKind.ALL);

  public static final Serde<Card> CARD = Serde.oneOf(Card.ALL);

  public static final Serde<Route> ROUTE = Serde.oneOf(ChMap.routes());

  public static final Serde<Ticket> TICKET = Serde.oneOf(ChMap.tickets());

  public static final Serde<List<String>> STRING_LIST = Serde.listOf(Serdes.STRING, ",");

  public static final Serde<List<Card>> CARD_LIST = Serde.listOf(Serdes.CARD, ",");

  public static final Serde<List<Route>> ROUTE_LIST = Serde.listOf(Serdes.ROUTE, ",");

  public static final Serde<SortedBag<Card>> CARD_SORTED_BAG = Serde.bagOf(Serdes.CARD, ",");

  public static final Serde<SortedBag<Ticket>> TICKET_SORTED_BAG = Serde.bagOf(Serdes.TICKET, ",");

  public static final Serde<List<SortedBag<Card>>> CARD_SORTED_BAG_LIST = Serde.listOf(Serdes.CARD_SORTED_BAG, ";");

  public static final Serde<PublicCardState> PUBLIC_CARD_STATE = Serde.of(
  , 
  );

  public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE = Serde.of(
  , 
  );

  public static final Serde<PlayerState> PLAYER_STATE = Serde.of(
  , 
  );

  public static final Serde<PublicGameState> PUBLIC_GAME_STATE = Serde.of(
  , 
  );
}
