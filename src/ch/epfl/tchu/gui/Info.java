package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
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
   * @param playerName the name of the player
   */
  public Info(String playerName) {
    this.playerName = playerName;
  }

  /**
   * Returns the French card name in singular or plural depending on count
   * 
   * @param card the card referred to
   * @param count the number of cards referred to
   * 
   * @return card name in singular iff. the absolute value of count is 1
   */
  public static String cardName(Card card, int count) {
    List<String> cardsStrings = List.of(StringsFr.BLACK_CARD, StringsFr.BLUE_CARD,
    StringsFr.GREEN_CARD, StringsFr.ORANGE_CARD, StringsFr.RED_CARD, StringsFr.VIOLET_CARD,
    StringsFr.WHITE_CARD, StringsFr.YELLOW_CARD, StringsFr.LOCOMOTIVE_CARD);
    
    return new StringBuilder()
    .append(cardsStrings.get(card.ordinal()))
    .append(StringsFr.plural(count)).toString();
  }

  /**
   * 
   * 
   * @param playerNames
   * @param count
   * 
   * @return 
   */
  public static String draw(List<String> playerNames, int count) {
    return String.format(StringsFr.DRAW, String.join
    (StringsFr.AND_SEPARATOR, playerNames), String.valueOf(count));
  }

  /**
   * 
   * 
   * @return
   */
  public String willPlayFirst() {
    return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
  }

  /**
   * 
   * 
   * @param count
   * 
   * @return
   */
  public String keptTickets(int count) {
    return String.format(StringsFr.DREW_TICKETS, playerName, 
    String.valueOf(count), StringsFr.plural(count));
  }

  /**
   * 
   * 
   * @return
   */
  public String canPlay() {
    return String.format(StringsFr.CAN_PLAY, playerName);
  }

  /**
   * 
   * 
   * @param count
   * 
   * @return
   */
  public String drewTickets(int count) {
    return String.format(StringsFr.DREW_TICKETS, playerName, 
    String.valueOf(count));
  }

  /**
   * 
   * 
   * @return
   */
  public String drewBlindCard() {
    return String.format(StringsFr.DREW_BLIND_CARD, playerName);
  }

  /**
   * 
   * 
   * @param card
   * 
   * @return
   */
  public String drewVisibleCard(Card card) {
    return String.format(StringsFr.DREW_VISIBLE_CARD, 
    playerName, cardName(card, 1));
  }

  /**
   * 
   * 
   * @param route
   * @param cards
   * 
   * @return
   */
  public String claimedRoute(Route route, SortedBag<Card> cards) {
    return String.format(StringsFr.CLAIMED_ROUTE, playerName, 
    route.toString(), cardsEnumeration(cards));
  }

  /**
   * 
   * 
   * @param route
   * @param initialCards
   * 
   * @return
   */
  public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
    return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, 
    route.toString(), cardsEnumeration(initialCards));
  }

  /**
   * 
   * 
   * @param drawnCards
   * @param additionnalCost
   * 
   * @return
   */
  public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionnalCost) {
    return new StringBuilder().append(String.format(StringsFr.ADDITIONAL_CARDS_ARE, 
    cardsEnumeration(drawnCards))).append(additionnalCost == 0 ? 
    StringsFr.NO_ADDITIONAL_COST : StringsFr.SOME_ADDITIONAL_COST).toString();
  }

  /**
   * 
   * 
   * @param route
   * 
   * @return
   */
  public String didNotClaimRoute(Route route) {
    return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, route.toString());
  }

  /**
   * 
   * 
   * @param carCount
   * 
   * @return
   */
  public String lastTurnBegins(int carCount) {
    return String.format(StringsFr.LAST_TURN_BEGINS, playerName, 
    String.valueOf(carCount), StringsFr.plural(carCount));
  }

  /**
   * 
   * 
   * @param longestTrail
   * 
   * @return
   */
  public String getsLongestTrailBonus(Trail longestTrail) {
    return String.format(StringsFr.GETS_BONUS, playerName, longestTrail.toString());
  }

  /**
   * 
   * 
   * @param points
   * @param loserPoints
   * 
   * @return
   */
  public String won(int points, int loserPoints) {
    return String.format(StringsFr.WINS, playerName, String.valueOf(points), 
    StringsFr.plural(points), String.valueOf(loserPoints), StringsFr.plural(loserPoints));
  }

  private String cardsEnumeration(SortedBag<Card> cards) {
      StringBuilder stringBuilder = new StringBuilder();
      List<String> words = new ArrayList<String>();

      for (Card card : cards) {
        words.add(stringBuilder.append(cards.countOf(card))
        .append(" ").append(cardName(card, cards.countOf(card))).toString());
        stringBuilder = new StringBuilder();
      }
      
      return stringBuilder.append(String.join(", ", words.subList(0, words.size() - 1)))
      .append(StringsFr.AND_SEPARATOR).append(words.get(words.size() -1)).toString();
  }
}
