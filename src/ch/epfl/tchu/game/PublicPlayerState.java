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
   * as its claimed routes
   * 
   * @param ticketCount
   *        The number of tickets the player has
   * 
   * @param cardCount
   *        The number of cards the player has
   * 
   * @param routes
   *        The routes the player claimed
   * 
   * @throws IllegalArgumentException
   *         If if the number of tickets or cards are strictly negative
   */
  public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
    Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);
 
    this.ticketCount = ticketCount;
    this.cardCount = cardCount;
    this.routes = List.copyOf(routes);
    this.carCount = Constants.INITIAL_CAR_COUNT - routes.stream().map(route -> route.length()).reduce(0, Integer::sum);
    this.claimPoints = routes.stream().map(route -> route.claimPoints()).reduce(0, Integer::sum);
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
   * Returns the number of construction points claimed by the player
   * 
   * @return the number of construction points claimed by the player
   */
  public int claimPoints() { return claimPoints; }
 }
