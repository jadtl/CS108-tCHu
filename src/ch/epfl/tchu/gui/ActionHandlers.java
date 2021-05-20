// TODO: Action handlers Javadoc
package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * 
 * 
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public interface ActionHandlers {
  /**
   * 
   * 
   */
  @FunctionalInterface
  public interface DrawTicketsHandler {
    void onDrawTickets();
  }

  /**
   * 
   * 
   */
  @FunctionalInterface
  public interface DrawCardHandler {
    void onDrawCard(int slot);
  }

  /**
   * 
   * 
   */
  @FunctionalInterface
  public interface ClaimRouteHandler {
    void onClaimRoute(Route route, SortedBag<Card> claimCards);
  }

  /**
   * 
   * 
   */
  @FunctionalInterface
  public interface ChooseTicketsHandler {
    void onChooseTickets(SortedBag<Ticket> chosenTickets);
  }

  /**
   * 
   * 
   */
  @FunctionalInterface
  public interface ChooseCardsHandler {
    void onChooseCards(SortedBag<Card> chosenCards);
  }
}
