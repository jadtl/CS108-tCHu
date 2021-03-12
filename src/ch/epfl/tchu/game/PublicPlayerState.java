package ch.epfl.tchu.game;

import java.util.List;

import ch.epfl.tchu.Preconditions;

/**
 * 
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
   * 
   * 
   * @param ticketCount
   * 
   * @param cardCount
   * 
   * @param routes
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
   * 
   * 
   * @return
   */
  public int ticketCount() { return ticketCount; }

  /**
   * 
   * 
   * @return
   */
  public int cardCount() { return cardCount; }

  /**
   * 
   * 
   * @return
   */
  public List<Route> routes() { return routes; }

  /**
   * 
   * 
   * @return
   */
  public int carCount() { return carCount; }

  /**
   * 
   * 
   * @return
   */
  public int claimPoints() { return claimPoints; }
 }
