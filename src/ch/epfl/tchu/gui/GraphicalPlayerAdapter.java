// TODO: Graphical player adapter Javadoc
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

import static javafx.application.Platform.runLater;

public class GraphicalPlayerAdapter implements Player {
  private GraphicalPlayer graphicalPlayer;

  public GraphicalPlayerAdapter() {}

  @Override
  public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
    graphicalPlayer = new GraphicalPlayer(ownId, playerNames);
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
    BlockingQueue<SortedBag<Ticket>> q = new ArrayBlockingQueue<>(1);
  }

  @Override
  public SortedBag<Ticket> chooseInitialTickets() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TurnKind nextTurn() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
    BlockingQueue<SortedBag<Ticket>> q = new ArrayBlockingQueue<>(1);
    graphicalPlayer.chooseTickets(options, c -> {
      new Thread(() -> {
        try {
          q.put(c);
        } catch (InterruptedException e) {
          throw new Error();
        }
      }).start();
    });
    try {
      return q.take();
    } catch (InterruptedException e) {
      throw new Error();
    }
  }

  @Override
  public int drawSlot() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Route claimedRoute() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SortedBag<Card> initialClaimCards() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
    // TODO Auto-generated method stub
    return null;
  }
  
}