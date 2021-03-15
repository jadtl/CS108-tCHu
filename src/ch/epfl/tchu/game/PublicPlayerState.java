package ch.epfl.tchu.game;

import java.util.List;

import ch.epfl.tchu.Preconditions;

/**
 * The public part of the player state
 * 
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public class PublicPlayerState {
  private final int ticketCount;
  private final int cardCount;
  private final List<Route> routes;
  private final int carCount;
  private final int claimPoints;

  /**
   * Constructs a public player state with its ticket and card counts as well
   * as its owned routes
   * 
   * @param ticketCount
   *        The number of tickets the player has
   * 
   * @param cardCount
   *        The number of cards the player has
   * 
   * @param routes
   *        The routes the player owns
   * 
   * @throws IllegalArgumentException
   *         If if the number of tickets or cards are strictly negative
   */
  public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
    Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);

    int usedCarCount = 0;
    int claimPoints = 0;
    for (Route route : routes) {
      usedCarCount += route.length();
      claimPoints += route.claimPoints();
    }
 
    this.ticketCount = ticketCount;
    this.cardCount = cardCount;
    this.routes = routes;
    this.carCount = Constants.INITIAL_CAR_COUNT - usedCarCount;
    this.claimPoints = claimPoints;
  }

  /**
   * Returns the number of tickets the player has
   * 
   * @return the number of tickets the player has
   */
  public int ticketCount() { return ticketCount; }

  /**
   * Returns the number of cards the player has
   * 
   * @return the number of cards the player has
   */
  public int cardCount() { return cardCount; }

  /**
   * Returns the routes the player owns
   * 
   * @return the routes the player owns
   */
  public List<Route> routes() { return routes; }

  /**
   * Returns the number of cars the player has
   * 
   * @return the number of cars the player has
   */
  public int carCount() { return carCount; }

  /**
   * Returns the number of construction points earned by the player
   * 
   * @return the number of construction points earned by the player
   */
  public int claimPoints() { return claimPoints; }
 }
