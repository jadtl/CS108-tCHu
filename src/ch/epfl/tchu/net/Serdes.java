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
     * The {@link Serde} applicable to an {@link Boolean}
     */
    public static final Serde<Boolean> BOOLEAN = Serde.of(
            i -> i ? "1" : "0",
            Boolean::valueOf
    );

    /**
     * The {@link Serde} applicable to an {@link Integer}
     */
    public static final Serde<Integer> INTEGER = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt
    );

    /**
     * The {@link Serde} applicable to a {@link String}
     */
    public static final Serde<String> STRING = Serde.of(
            i -> new String(Base64.getEncoder().encode(i.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8),
            i -> new String(Base64.getDecoder().decode(i), StandardCharsets.UTF_8)
    );

    /**
     * The {@link Serde} applicable to a {@link PlayerId}
     */
    public static final Serde<PlayerId> PLAYER_ID = Serde.oneOf(PlayerId.ALL);

    /**
     * The {@link Serde} applicable to a {@link ch.epfl.tchu.game.Player.TurnKind}
     */
    public static final Serde<Player.TurnKind> TURN_KIND = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * The {@link Serde} applicable to a {@link Card}
     */
    public static final Serde<Card> CARD = Serde.oneOf(Card.ALL);

    /**
     * The {@link Serde} applicable to a {@link Route}
     */
    public static final Serde<Route> ROUTE = Serde.oneOf(ChMap.routes());

    /**
     * The {@link Serde} applicable to a {@link Ticket}
     */
    public static final Serde<Ticket> TICKET = Serde.oneOf(ChMap.tickets());

    /**
     * The {@link Serde} applicable to a {@link List} of {@link String}
     */
    public static final Serde<List<String>> STRING_LIST = Serde.listOf(Serdes.STRING, ",");

    /**
     * The {@link Serde} applicable to a {@link List} of {@link Card}
     */
    public static final Serde<List<Card>> CARD_LIST = Serde.listOf(Serdes.CARD, ",");

    /**
     * The {@link Serde} applicable to a {@link List} of {@link Route}
     */
    public static final Serde<List<Route>> ROUTE_LIST = Serde.listOf(Serdes.ROUTE, ",");

    /**
     * The {@link Serde} applicable to a {@link SortedBag} of {@link Card}
     */
    public static final Serde<SortedBag<Card>> CARD_SORTED_BAG = Serde.bagOf(Serdes.CARD, ",");

    /**
     * The {@link Serde} applicable to a {@link SortedBag} of {@link Ticket}
     */
    public static final Serde<SortedBag<Ticket>> TICKET_SORTED_BAG = Serde.bagOf(Serdes.TICKET, ",");

    /**
     * The {@link Serde} applicable to a {@link List} of {@link SortedBag} of {@link Card}
     */
    public static final Serde<List<SortedBag<Card>>> CARD_SORTED_BAG_LIST = Serde.listOf(Serdes.CARD_SORTED_BAG, ";");

    /**
     * The {@link Serde} applicable to a {@link PublicCardState}
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
     * The {@link Serde} applicable to a {@link PublicPlayerState}
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
     * The {@link Serde} applicable to a {@link PlayerState}
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
     * The {@link Serde} applicable to a {@link PublicGameState}
     */
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE = Serde.of(
            i -> String.join(":",
                    List.of(INTEGER.serialize(i.ticketsCount()), PUBLIC_CARD_STATE.serialize(i.cardState()),
                            PLAYER_ID.serialize(i.currentPlayerId()),
                            PUBLIC_PLAYER_STATE.serialize(i.playerState(PlayerId.PLAYER_1)),
                            PUBLIC_PLAYER_STATE.serialize(i.playerState(PlayerId.PLAYER_2)),
                            PLAYER_ID.serialize(i.lastPlayer()),
                            BOOLEAN.serialize(i.gameEnded()))),

            i -> {
                String[] r = i.split(Pattern.quote(":"), -1);
                int ticketCount = INTEGER.deserialize(r[0]);
                PublicCardState cardState = PUBLIC_CARD_STATE.deserialize(r[1]);
                PlayerId currentPlayerId = PLAYER_ID.deserialize(r[2]);
                PublicPlayerState PPS1 = PUBLIC_PLAYER_STATE.deserialize(r[3]);
                PublicPlayerState PPS2 = PUBLIC_PLAYER_STATE.deserialize(r[4]);
                PlayerId lastPlayer = PLAYER_ID.deserialize(r[5]);
                boolean gameEnded = BOOLEAN.deserialize(r[6]);

                return new PublicGameState(ticketCount, cardState, currentPlayerId,
                        Map.of(PlayerId.PLAYER_1, PPS1, PlayerId.PLAYER_2, PPS2), lastPlayer, gameEnded);
            }
    );
}