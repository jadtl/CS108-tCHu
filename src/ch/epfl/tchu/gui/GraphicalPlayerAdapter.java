// TODO: Graphical player adapter Javadoc
package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

public class GraphicalPlayerAdapter implements Player {

  @Override
  public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void receiveInfo(String info) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void updateState(PublicGameState newState, PlayerState ownState) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
    // TODO Auto-generated method stub
    
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
    // TODO Auto-generated method stub
    return null;
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