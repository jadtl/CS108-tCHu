package ch.epfl.tchu.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerProxy implements Player {
  private Socket socket;

  public RemotePlayerProxy(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

    send(MessageId.INIT_PLAYERS, List.of(Serdes.PLAYER_ID.serialize(ownId), 
      Serdes.STRING_LIST.serialize(new ArrayList<String>(playerNames.values()))));
  }

  @Override
  public void receiveInfo(String info) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void updateState(PublicGameState newState, PlayerState ownState) {
    
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
    send(MessageId.NEXT_TURN, List.of());
    
    return Serdes.TURN_KIND.deserialize(receive());
  }

  @Override
  public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int drawSlot() {
    send(MessageId.DRAW_SLOT, List.of());
    
    return Serdes.INTEGER.deserialize(receive());
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

  private void send(MessageId messageId, List<String> serializedArgs) {
    List<String> arguments = new ArrayList<String>();
    arguments.add(messageId.name());
    arguments.addAll(serializedArgs);
    try {
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
      
      writer.write(String.join(" ", arguments));
      writer.write('\n');
      writer.flush();

    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }

  private String receive() {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));

      return reader.readLine();

    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }
}
