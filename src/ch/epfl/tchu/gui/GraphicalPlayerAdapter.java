// TODO: Graphical player adapter Javadoc
// TODO: Find abstraction for interrupted exception handling
package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;

import static javafx.application.Platform.runLater;
import static ch.epfl.tchu.game.Player.TurnKind.*;

public class GraphicalPlayerAdapter implements Player {
  private GraphicalPlayer graphicalPlayer;
  private BlockingQueue<SortedBag<Ticket>> initialTicketsChoice;
  private BlockingQueue<Integer> slot;
  private BlockingQueue<Route> route;
  private BlockingQueue<SortedBag<Card>> claimCards;

  public GraphicalPlayerAdapter() {
    initialTicketsChoice = new ArrayBlockingQueue<>(1);
    slot = new ArrayBlockingQueue<>(1);
    route = new ArrayBlockingQueue<>(1);
    claimCards = new ArrayBlockingQueue<>(1);
  }

  @Override
  public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
    runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
  }

  @Override
  public void receiveInfo(String info) {
    runLater(() -> graphicalPlayer.receiveInfo(info));
  }

  @Override
  public void updateState(PublicGameState newState, PlayerState ownState) {
    runLater(() -> graphicalPlayer.setState(newState, ownState));
  }

  @Override
  public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
    runLater(() -> graphicalPlayer.chooseTickets(tickets, c -> {
        try {
          initialTicketsChoice.put(c);
        } catch (InterruptedException e) {
          throw new Error();
        }
      })
    );
  }

  @Override
  public SortedBag<Ticket> chooseInitialTickets() {
    try {
      return initialTicketsChoice.take();
    } catch (InterruptedException e) {
      throw new Error();
    }
  }

  @Override
  public TurnKind nextTurn() {
    BlockingQueue<TurnKind> q = new ArrayBlockingQueue<>(1);

    DrawTicketsHandler drawTicketsHandler = () -> {
      try {
        q.put(DRAW_TICKETS);
      } catch (InterruptedException e1) {
        throw new Error();
      }
    };
    DrawCardHandler drawCardHandler = (s) -> {
      try {
        q.put(DRAW_CARDS);
        slot.put(s);
      } catch (InterruptedException e) {
        throw new Error();
      }
    };
    ClaimRouteHandler claimRouteHandler = (r, c) -> {
      try {
        q.put(CLAIM_ROUTE);
        route.put(r);
        claimCards.put(c);
      } catch (InterruptedException e) {
        throw new Error();
      }
    };

    runLater(() -> graphicalPlayer.startTurn(drawTicketsHandler, drawCardHandler, claimRouteHandler));

    try {
      return q.take();
    } catch (InterruptedException e) {
      throw new Error();
    }
  }

  @Override
  public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
    BlockingQueue<SortedBag<Ticket>> q = new ArrayBlockingQueue<>(1);
    runLater(() -> graphicalPlayer.chooseTickets(options, c -> {
        new Thread(() -> {
          try {
            q.put(c);
          } catch (InterruptedException e) {
            throw new Error();
          }
        }).start();
      })
    );
    try {
      return q.take();
    } catch (InterruptedException e) {
      throw new Error();
    }
  }

  @Override
  public int drawSlot() {
    try {
      return slot.take();
    } catch (InterruptedException e) {
      throw new Error();
    }
  }

  @Override
  public Route claimedRoute() {
    try {
      return route.take();
    } catch (InterruptedException e) {
      throw new Error();
    }
  }

  @Override
  public SortedBag<Card> initialClaimCards() {
    try {
      return claimCards.take();
    } catch (InterruptedException e) {
      throw new Error();
    }
  }

  @Override
  public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
    BlockingQueue<SortedBag<Card>> q = new ArrayBlockingQueue<>(1);

    runLater(() -> graphicalPlayer.chooseAdditionalCards(options, c -> {
      try {
        q.put(c);
      } catch (InterruptedException e) {
        throw new Error();
      }
    }));

    try {
      return q.take();
    } catch (InterruptedException e) {
      throw new Error();
    }
  }
}