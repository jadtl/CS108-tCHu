package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that generates texts describing various events in the game
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public final class Info {
    private final String playerName;

    /**
     * A log generator linked to the player's name
     *
     * @param playerName The name of the player
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * The name of the card depending on its count
     *
     * @param card  The {@link Card} referred to
     * @param count The number of {@link Card}
     * @return The {@link Card} name in singular iff. the absolute value of {@code count} is {@code 1}
     */
    public static String cardName(Card card, int count) {
        StringBuilder builder = new StringBuilder();
        switch (card) {
            case BLACK:
                builder.append(StringsFr.BLACK_CARD);
                break;
            case BLUE:
                builder.append(StringsFr.BLUE_CARD);
                break;
            case GREEN:
                builder.append(StringsFr.GREEN_CARD);
                break;
            case LOCOMOTIVE:
                builder.append(StringsFr.LOCOMOTIVE_CARD);
                break;
            case ORANGE:
                builder.append(StringsFr.ORANGE_CARD);
                break;
            case RED:
                builder.append(StringsFr.RED_CARD);
                break;
            case VIOLET:
                builder.append(StringsFr.VIOLET_CARD);
                break;
            case WHITE:
                builder.append(StringsFr.WHITE_CARD);
                break;
            case YELLOW:
                builder.append(StringsFr.YELLOW_CARD);
                break;
            default:
                break;
        }

        return builder.append(StringsFr.plural(count)).toString();
    }

    /**
     * A message that announces the game ended in a draw with the given points
     *
     * @param playerNames The names of the players
     * @param count       The score they ended up with
     * @return A message that announces the game ended in a draw with {@code count} points
     */
    public static String draw(List<String> playerNames, int count) {
        return String.format(StringsFr.DRAW, String.join(StringsFr.AND_SEPARATOR, playerNames), count);
    }

    /**
     * A message that announces the player will play first
     *
     * @return a message that announces the player will play first
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * A message announcing that the player kept the given number of tickets
     *
     * @param count The number of {@link ch.epfl.tchu.game.Ticket} kept by the player
     * @return A message announcing that the player kept {@code count} {@link ch.epfl.tchu.game.Ticket}
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * A message announcing that the player can play
     *
     * @return A message announcing that the player can play
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * A message announcing that the player drew the given number of tickets
     *
     * @param count The number of tickets drawn by the player
     * @return A message announcing that the player drew {@code count} {@link ch.epfl.tchu.game.Ticket}
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * A message announcing that the player drew a card from the top of the deck
     *
     * @return A message announcing that the player drew {@link Deck#topCard()}
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * A message announcing that the player drew the given face-up card
     *
     * @param card The face-up card the player drew
     * @return A message announcing that the player drew {@code card}
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * A message announcing that the player has claimed the route using the given cards
     *
     * @param route The {@link Route} the player claimed
     * @param cards The {@link SortedBag} of {@link Card} the player claimed {@code route} with
     * @return A message announcing that the players claimed {@code route} using {@code cards}
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, routeText(route.station1(), route.station2()),
                cardsEnumeration(cards));
    }

    /**
     * A message announcing that the player attempts to claim a given tunnel using the given cards
     *
     * @param route The {@link Route} tunnel the player attempts to claim
     * @param initialCards The {@link SortedBag} of {@link Card} the player attempts to claim {@code route}
     * @return A message announcing that the player attempts to claim {@code route} using {@code initialCards}
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, routeText(route.station1(), route.station2()),
                cardsEnumeration(initialCards));
    }

    /**
     * A message announcing that the player drew the additional cards and their induced additional cost
     *
     * @param drawnCards The additional {@link SortedBag} of {@link Card} the player drew
     * @param additionalCost The additional cost associated to the {@code drawnCards}
     * @return A message announcing that the player drew the additional cards and their induced additional cost
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        return String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardsEnumeration(drawnCards)) + String.format(additionalCost == 0 ?
                StringsFr.NO_ADDITIONAL_COST : StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost));
    }

    /**
     * A message announcing that the player didn't or couldn't claim the route
     *
     * @param route The {@link Route} that the player did not claim
     * @return A message announcing that the player didn't or couldn't claim {@code route}
     */
    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, routeText(route.station1(), route.station2()));
    }

    /**
     * A message announcing that the last turn starts, specifying the last player's car count
     *
     * @param carCount The number of cars left for the player
     * @return A message announcing that the last turn starts with {@code carCount} cars left for the player
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName,
                carCount, StringsFr.plural(carCount));
    }

    /**
     * A message announcing the bonus points won from the player's longest trail
     *
     * @param longestTrail The longest {@link Trail} of the player's network
     * @return A message announcing the bonus points won from the player's longest {@link Trail}
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, playerName, routeText(longestTrail.station1(), longestTrail.station2()));
    }

    /**
     * A message announcing that the player won the game, breaking the points down
     *
     * @param points      The points won by the player
     * @param loserPoints The points won by the player's opponent
     * @return A message announcing that the player won the game with {@code points} and the other lost with {@code loserPoints} points
     */
    public String won(int points, int loserPoints) {
        return String.format(StringsFr.WINS, playerName, points,
                StringsFr.plural(points), loserPoints, StringsFr.plural(loserPoints));
    }

    /**
     * A string enumeration of the given cards
     *
     * @param cards The {@link List} of {@link Card} to enumerate
     * @return A {@link String} enumeration of the given {@link List} of {@link Card}
     */
    private String cardsEnumeration(SortedBag<Card> cards) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> words = new ArrayList<>();

        for (Card card : Card.values()) {
            if (cards.contains(card))
                words.add(stringBuilder.append(cards.countOf(card))
                        .append(" ").append(cardName(card, cards.countOf(card))).toString());
            stringBuilder = new StringBuilder();
        }

        if (words.size() == 1)
            return words.get(0);
        else
            return stringBuilder.append(String.join(", ", words.subList(0, words.size() - 1)))
                    .append(StringsFr.AND_SEPARATOR).append(words.get(words.size() - 1)).toString();
    }

    private String routeText(Station station1, Station station2) {
        return station1.toString() + StringsFr.EN_DASH_SEPARATOR + station2.toString();
    }
}
