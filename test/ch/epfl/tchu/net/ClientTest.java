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
    private SortedBag<Ticket> initialTickets;

    @Override
    public void initPlayers(PlayerId ownId,
			    Map<PlayerId, String> names) {
      System.out.printf("ownId: %s\n", ownId);
      System.out.printf("playerNames: %s\n", names);
    }

    @Override
    public void receiveInfo(String info) {
      System.out.println(info);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
      System.out.println("New state: " + Serdes.PUBLIC_GAME_STATE.serialize(newState));
      System.out.println("Own state: " + Serdes.PLAYER_STATE.serialize(ownState));
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
      this.initialTickets = tickets;
      System.out.println(tickets);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
      return initialTickets.subsetsOfSize(new Random().nextInt(initialTickets.size())).iterator().next();
    }

    @Override
    public TurnKind nextTurn() {
      return TurnKind.values()[new Random().nextInt(3)];
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
      return options.subsetsOfSize(new Random().nextInt(options.size())).iterator().next();
    }

    @Override
    public int drawSlot() {
      return new Random().nextInt(6) - 1;
    }

    @Override
    public Route claimedRoute() {
      return null;
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
      return null;
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
      return null;
    }
    
  }
}
