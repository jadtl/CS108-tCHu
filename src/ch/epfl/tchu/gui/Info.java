package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.List;

/**
 * A class that generates texts describing the development of the game
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class Info {

  /**
   * Constructs a log generator linked to the player name
   * 
   * @param playerName the name of the player
   */
  public Info(String playerName) {
    
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

    return "";
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
    return "";
  }

  /**
   * 
   * 
   * @return
   */
  public String willPlayFirst() {
    return "";
  }

  /**
   * 
   * 
   * @param count
   * 
   * @return
   */
  public String keptTickets(int count) {
    return "";
  }

  /**
   * 
   * 
   * @return
   */
  public String canPlay() {
    return "";
  }

  /**
   * 
   * 
   * @return
   */
  public String drewTickets() {
    return "";
  }

  /**
   * 
   * 
   * @return
   */
  public String drewBlindCards() {
    return "";
  }

  /**
   * 
   * 
   * @param card
   * 
   * @return
   */
  public String drewVisibleCard(Card card) {
    return "";
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
    return "";
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
    return "";
  }

  /**
   * 
   * 
   * @param drawnCards
   * @param additionnalCost
   * 
   * @return
   */
  public String drewAdditionnalCards(SortedBag<Card> drawnCards, int additionnalCost) {
    return "";
  }

  /**
   * 
   * 
   * @param route
   * 
   * @return
   */
  public String didNotClaimRoute(Route route) {
    return "";
  }

  /**
   * 
   * 
   * @param carCount
   * 
   * @return
   */
  public String lastTurnBegins(int carCount) {
    return "";
  }

  /**
   * 
   * 
   * @param longestTrail
   * 
   * @return
   */
  public String getsLongestTrailBonus(Trail longestTrail) {
    return "";
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
    return "";
  }
}
