package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * A container for various action handlers
 * 
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public interface ActionHandlers {
  /**
   * An interface that handles drawing tickets
   */
  @FunctionalInterface
  public interface DrawTicketsHandler {
    /**
     * Called when the player requests to draw tickets
     */
    void onDrawTickets();
  }

  /**
   * An interface that handles drawing cards
   */
  @FunctionalInterface
  public interface DrawCardHandler {
    /**
     * Called when the player requests to draw a card at the given slot
     * 
     * @param slot
     *        The card slot chosen by the player
     */
    void onDrawCard(int slot);
  }

  /**
   * An interface that handles claiming routes
   */
  @FunctionalInterface
  public interface ClaimRouteHandler {
    /**
     * Called when the player requests to claim the given route with the given cards
     * 
     * @param route
     *        The route chosen by the player
     * 
     * @param claimCards
     *        The cards chosen by the player to claim the route
     */
    void onClaimRoute(Route route, SortedBag<Card> claimCards);
  }

  /**
   * An interface that handles choosing tickets
   */
  @FunctionalInterface
  public interface ChooseTicketsHandler {
    /**
     * Called when the player chooses to keep the given tickets after tickets drawing
     * 
     * @param chosenTickets
     *        The tickets chosen by the player
     */
    void onChooseTickets(SortedBag<Ticket> chosenTickets);
  }

  /**
   * An interface that handles choosing cards
   */
  @FunctionalInterface
  public interface ChooseCardsHandler {
    /**
     * Called when the player chooses to use the given cards to claim a route,
     * if these are additional claim cards, the bag can be empty, meaning that
     * the player gave up on claiming the tunnel
     * 
     * @param chosenCards
     *        The cards chosen by the player
     */
    void onChooseCards(SortedBag<Card> chosenCards);
  }
}
