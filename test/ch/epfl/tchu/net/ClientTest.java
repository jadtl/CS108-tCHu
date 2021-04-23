package ch.epfl.tchu.net;

import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

public final class ClientTest {
  public static void main(String[] args) {
    System.out.println("Starting client!");
    RemotePlayerClient playerClient =
      new RemotePlayerClient(new TestPlayer(),
			     "localhost",
			     5108);
    playerClient.run();
    System.out.println("Client done!");
  }

  private final static class TestPlayer implements Player {
    @Override
    public void initPlayers(PlayerId ownId,
			    Map<PlayerId, String> names) {
      System.out.printf("ownId: %s\n", ownId);
      System.out.printf("playerNames: %s\n", names);
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
      return TurnKind.values()[new Random().nextInt(3)];
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public int drawSlot() {
      return new Random().nextInt(6) - 1;
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
}
