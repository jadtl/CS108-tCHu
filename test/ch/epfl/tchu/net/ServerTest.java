package ch.epfl.tchu.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;

public final class ServerTest {
  public static void main(String[] args) throws IOException {
    System.out.println("Starting server!");
    try (ServerSocket serverSocket = new ServerSocket(5108);
	 Socket socket = serverSocket.accept()) {
      Player playerProxy = new RemotePlayerProxy(socket);
      var playerNames = Map.of(PlayerId.PLAYER_1, "Ada",
        PlayerId.PLAYER_2, "Charles");
      playerProxy.initPlayers(PlayerId.PLAYER_1, playerNames);
      System.out.println(playerProxy.drawSlot());
      System.out.println(playerProxy.nextTurn());
      System.out.println(playerProxy.chooseTickets(SortedBag.of(2, ChMap.tickets().get(3), 1, ChMap.tickets().get(2))));
    }
    System.out.println("Server done!");
  }
}
