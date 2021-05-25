package ch.epfl.tchu.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
 * A collection of useful Serializers/Deserializers
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 * @see ch.epfl.tchu.net.Serde
 */
public class Serdes {
    /**
     * The class is not instantiable
     */
    private Serdes() {
    }

    /**
     * The serde applicable to an integer
     */
    public static final Serde<Integer> INTEGER = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt
    );

    /**
     * The serde applicable to a string
     */
    public static final Serde<String> STRING = Serde.of(
            i -> new String(Base64.getEncoder().encode(i.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8),
            i -> new String(Base64.getDecoder().decode(i), StandardCharsets.UTF_8)
    );

    /**
     * The serde applicable to a player identifier
     */
    public static final Serde<PlayerId> PLAYER_ID = Serde.oneOf(PlayerId.ALL);

    /**
     * The serde applicable to a turn kind
     */
    public static final Serde<Player.TurnKind> TURN_KIND = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * The serde applicable to a card
     */
    public static final Serde<Card> CARD = Serde.oneOf(Card.ALL);

    /**
     * The serde applicable to a route
     */
    public static final Serde<Route> ROUTE = Serde.oneOf(ChMap.routes());

    /**
     * The serde applicable to a ticket
     */
    public static final Serde<Ticket> TICKET = Serde.oneOf(ChMap.tickets());

    /**
     * The serde applicable to a string list
     */
    public static final Serde<List<String>> STRING_LIST = Serde.listOf(Serdes.STRING, ",");

    /**
     * The serde applicable to a card list
     */
    public static final Serde<List<Card>> CARD_LIST = Serde.listOf(Serdes.CARD, ",");

    /**
     * The serde applicable to a route list
     */
    public static final Serde<List<Route>> ROUTE_LIST = Serde.listOf(Serdes.ROUTE, ",");

    /**
     * The serde applicable to a sorted bag of cards
     */
    public static final Serde<SortedBag<Card>> CARD_SORTED_BAG = Serde.bagOf(Serdes.CARD, ",");

    /**
     * The serde applicable to a sorted bag of tickets
     */
    public static final Serde<SortedBag<Ticket>> TICKET_SORTED_BAG = Serde.bagOf(Serdes.TICKET, ",");

    /**
     * The serde applicable to a list of sorted bags of cards
     */
    public static final Serde<List<SortedBag<Card>>> CARD_SORTED_BAG_LIST = Serde.listOf(Serdes.CARD_SORTED_BAG, ";");

    /**
     * The serde applicable to a public card state
     */
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE = Serde.of(
            i -> String.join(";", List.of(CARD_LIST.serialize(i.faceUpCards()), INTEGER.serialize(i.deckSize()),
                    INTEGER.serialize(i.discardsSize()))),

            i -> {
                String[] s = i.split(Pattern.quote(";"), -1);
                List<Card> faceUpCards = CARD_LIST.deserialize(s[0]);
                int deckSize = INTEGER.deserialize(s[1]);
                int discardSize = INTEGER.deserialize(s[2]);

                return new PublicCardState(faceUpCards, deckSize, discardSize);
            }
    );

    /**
     * The serde applicable to a public player state
     */
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE = Serde.of(
            i -> String.join(";",
                    List.of(INTEGER.serialize(i.ticketCount()), INTEGER.serialize(i.cardCount()),
                            ROUTE_LIST.serialize(i.routes()))),

            i -> {
                String[] t = i.split(Pattern.quote(";"), -1);
                int ticketCount = INTEGER.deserialize(t[0]);
                int cardCount = INTEGER.deserialize(t[1]);
                List<Route> routes = ROUTE_LIST.deserialize(t[2]);

                return new PublicPlayerState(ticketCount, cardCount, routes);
            }
    );

    /**
     * The serde applicable to a player state
     */
    public static final Serde<PlayerState> PLAYER_STATE = Serde.of(
            i -> String.join(";",
                    List.of(TICKET_SORTED_BAG.serialize(i.tickets()),
                            CARD_SORTED_BAG.serialize(i.cards()), ROUTE_LIST.serialize(i.routes()))),

            i -> {
                String[] c = i.split(Pattern.quote(";"), -1);
                SortedBag<Ticket> tickets = TICKET_SORTED_BAG.deserialize(c[0]);
                SortedBag<Card> cards = CARD_SORTED_BAG.deserialize(c[1]);
                List<Route> routes = ROUTE_LIST.deserialize(c[2]);

                return new PlayerState(tickets, cards, routes);
            }
    );

    /**
     * The serde applicable to a public game state
     */
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE = Serde.of(
            i -> String.join(":",
                    List.of(INTEGER.serialize(i.ticketsCount()), PUBLIC_CARD_STATE.serialize(i.cardState()),
                            PLAYER_ID.serialize(i.currentPlayerId()),
                            PUBLIC_PLAYER_STATE.serialize(i.playerState(PlayerId.PLAYER_1)),
                            PUBLIC_PLAYER_STATE.serialize(i.playerState(PlayerId.PLAYER_2)),
                            PLAYER_ID.serialize(i.lastPlayer()))),

            i -> {
                String[] r = i.split(Pattern.quote(":"), -1);
                int ticketCount = INTEGER.deserialize(r[0]);
                PublicCardState cardState = PUBLIC_CARD_STATE.deserialize(r[1]);
                PlayerId currentPlayerId = PLAYER_ID.deserialize(r[2]);
                PublicPlayerState PPS1 = PUBLIC_PLAYER_STATE.deserialize(r[3]);
                PublicPlayerState PPS2 = PUBLIC_PLAYER_STATE.deserialize(r[4]);
                PlayerId lastPlayer = PLAYER_ID.deserialize(r[5]);

                return new PublicGameState(ticketCount, cardState, currentPlayerId,
                        Map.of(PlayerId.PLAYER_1, PPS1, PlayerId.PLAYER_2, PPS2), lastPlayer);
            }
    );
}