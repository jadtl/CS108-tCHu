package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Station;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that generates texts describing the development of the game
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class Info {
  private final String playerName;

  /**
   * Constructs a log generator linked to the player name
   * 
   * @param playerName 
   *        The name of the player
   */
  public Info(String playerName) {
    this.playerName = playerName;
  }

  /**
   * Returns the French card name in singular or plural depending on count
   * 
   * @param card 
   *        The card referred to
   * 
   * @param count 
   *        The number of cards referred to
   * 
   * @return card name in singular iff. the absolute value of count is 1
   */
  public static String cardName(Card card, int count) {
    StringBuilder builder = new StringBuilder();
    switch(card) {
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
   * Returns a message that announces the game ended in a draw while winning the given points
   * 
   * @param playerNames 
   *        The names of the players in-game
   * 
   * @param count 
   *        The score they ended up with
   * 
   * @return a message that announces the game ended in a draw while winning the given points
   */
  public static String draw(List<String> playerNames, int count) {
    return String.format(StringsFr.DRAW, String.join
    (StringsFr.AND_SEPARATOR, playerNames), String.valueOf(count));
  }

  /**
   * Returns a message that announces the player will play first
   * 
   * @return a message that announcesthe player will play first
   */
  public String willPlayFirst() {
    return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
  }

  /**
   * Returns a message announcing that the player kept
   * the given number of tickets
   * 
   * @param count
   *        The number of tickets kept by the player
   * 
   * @return a message announcing that the player kept
   * the given number of tickets
   */
  public String keptTickets(int count) {
    return String.format(StringsFr.KEPT_N_TICKETS, playerName, 
    String.valueOf(count), StringsFr.plural(count));
  }

  /**
   * Returns a message announcing that the player can play
   * 
   * @return a message announcing that the player can play
   */
  public String canPlay() {
    return String.format(StringsFr.CAN_PLAY, playerName);
  }

  /**
   * Returns a message announcing that the player drew 
   * the given number of tickets
   * 
   * @param count
   *        The number of tickets drawn by the player
   * 
   * @return a message announcing that the player drew 
   * the given number of tickets
   */
  public String drewTickets(int count) {
    return String.format(StringsFr.DREW_TICKETS, playerName, 
    String.valueOf(count), StringsFr.plural(count));
  }

  /**
   * Returns a message announcing that the player drew a card
   * from the top of the deck
   * 
   * @return a message announcing that the player drew a card
   * from the top of the deck
   */
  public String drewBlindCard() {
    return String.format(StringsFr.DREW_BLIND_CARD, playerName);
  }

  /**
   * Returns a message announcing that the player drew
   * the given face-up card
   * 
   * @param card
   *        The card the player drew
   * 
   * @return a message announcing that the player drew
   * the given face-up card
   */
  public String drewVisibleCard(Card card) {
    return String.format(StringsFr.DREW_VISIBLE_CARD, 
    playerName, cardName(card, 1));
  }

  /**
   * Returns a message announcing that the players has claimed
   * the route using the given cards
   * 
   * @param route
   *        The route the player claimed
   * 
   * @param cards
   *        The cards the player claimed the route with
   * 
   * @return a message announcing that the players claimed
   * the route using the given cards
   */
  public String claimedRoute(Route route, SortedBag<Card> cards) {
    return String.format(StringsFr.CLAIMED_ROUTE, playerName, 
    routeText(route.station1(), route.station2()), cardsEnumeration(cards));
  }

  /**
   * Returns a message announcing that the player wants to control
   * the tunnel using the given cards
   * 
   * @param route
   *        The tunnel the player attemps to claim
   * 
   * @param initialCards
   *        The cards the player attemps to claim the tunnel with
   * 
   * @return a message announcing that the player wants to control
   * the tunnel using the given cards
   */
  public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
    return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, 
    routeText(route.station1(), route.station2()), cardsEnumeration(initialCards));
  }

  /**
   * Returns a message announcing that the player drew 
   * the additional cards and their induced additional cost
   * 
   * @param drawnCards
   *        The additional cards the player drew
   * 
   * @param additionnalCost
   *        The additional cost associated to the drawn cards
   * 
   * @return a message announcing that the player drew 
   * the additional cards and their induced additional cost
   */
  public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionnalCost) {
    return new StringBuilder().append(String.format(StringsFr.ADDITIONAL_CARDS_ARE, 
    cardsEnumeration(drawnCards))).append(String.format(additionnalCost == 0 ? 
    StringsFr.NO_ADDITIONAL_COST : StringsFr.SOME_ADDITIONAL_COST, additionnalCost, StringsFr.plural(additionnalCost))).toString();
  }

  /**
   * Returns a message announcing that the player didn't or 
   * couldn't claim the route
   * 
   * @param route
   *        The route that the player did not claim
   * 
   * @return a message announcing that the player didn't or 
   * couldn't claim the route
   */
  public String didNotClaimRoute(Route route) {
    return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, routeText(route.station1(), route.station2()));
  }

  /**
   * Returns a message announcing that the last turn starts
   * 
   * @param carCount
   *        The number of cars that is left for the player
   * 
   * @return a message announcing that the last turn starts
   */
  public String lastTurnBegins(int carCount) {
    return String.format(StringsFr.LAST_TURN_BEGINS, playerName, 
    String.valueOf(carCount), StringsFr.plural(carCount));
  }

  /**
   * Returns a message announcing the bonus points won 
   * from the player's longest trail
   * 
   * @param longestTrail
   *        The longest trail of the player's network
   * 
   * @return a message announcing the bonus points won 
   * from the player's longest trail
   */
  public String getsLongestTrailBonus(Trail longestTrail) {
    return String.format(StringsFr.GETS_BONUS, playerName, routeText(longestTrail.station1(), longestTrail.station2()));
  }

  /**
   * Returns a message announcing that the player won the 
   * game, breaking the points down
   * 
   * @param points
   *        The points won by the player
   * 
   * @param loserPoints
   *        The points won by the player's opponent
   * 
   * @return a message announcing that the player won the 
   * game, breaking the points down
   */
  public String won(int points, int loserPoints) {
    return String.format(StringsFr.WINS, playerName, String.valueOf(points), 
    StringsFr.plural(points), String.valueOf(loserPoints), StringsFr.plural(loserPoints));
  }

  /**
   * Returns a string enumeration of the given cards
   * 
   * @param cards 
   *        The list of cards to enumerate
   * 
   * @return a string enumeration of the given cards
   */
  private String cardsEnumeration(SortedBag<Card> cards) {
      StringBuilder stringBuilder = new StringBuilder();
      List<String> words = new ArrayList<String>();

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

  /**
   * Returns a message that describes the route
   * 
   * @param route
   *        The route to describe
   * 
   * @return a message that describes the route
   */
  private String routeText(Station station1, Station station2) {
    return station1.toString() + StringsFr.EN_DASH_SEPARATOR + station2.toString();
  }
}
