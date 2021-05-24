package ch.epfl.tchu.net;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.ArrayList;
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

/**
 * The proxy of a distant player
 * 
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public class RemotePlayerProxy implements Player {
  private Socket socket;

  /**
   * Constructs a proxy with a socket
   * 
   * @param socket
   *        A socket to communicate to the client
   */
  public RemotePlayerProxy(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
    List<String> names = new ArrayList<String>();
    PlayerId.ALL.forEach(p -> names.add(playerNames.get(p)));
    send(MessageId.INIT_PLAYERS, List.of(Serdes.PLAYER_ID.serialize(ownId),
      Serdes.STRING_LIST.serialize(names)));
  }

  @Override
  public void receiveInfo(String info) {
	  send(MessageId.RECEIVE_INFO, List.of(Serdes.STRING.serialize(info)));
  }

  @Override
  public void updateState(PublicGameState newState, PlayerState ownState) {
	  send(MessageId.UPDATE_STATE, List.of(Serdes.PUBLIC_GAME_STATE.serialize(newState), Serdes.PLAYER_STATE.serialize(ownState)));
  }

  @Override
  public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
	  send(MessageId.SET_INITIAL_TICKETS, List.of(Serdes.TICKET_SORTED_BAG.serialize(tickets)));
  }

  @Override
  public SortedBag<Ticket> chooseInitialTickets() {
   send(MessageId.CHOOSE_INITIAL_TICKETS, List.of());
   
   return Serdes.TICKET_SORTED_BAG.deserialize(receive());
  }

  @Override
  public TurnKind nextTurn() {
    send(MessageId.NEXT_TURN, List.of());
    
    return Serdes.TURN_KIND.deserialize(receive());
  }

  @Override
  public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
	  send(MessageId.CHOOSE_TICKETS, List.of(Serdes.TICKET_SORTED_BAG.serialize(options)));

    return Serdes.TICKET_SORTED_BAG.deserialize(receive());
  }

  @Override
  public int drawSlot() {
    send(MessageId.DRAW_SLOT, List.of());

    return Serdes.INTEGER.deserialize(receive());
  }

  @Override
  public Route claimedRoute() {
    send(MessageId.ROUTE, List.of());
    
    return Serdes.ROUTE.deserialize(receive());
  }

  @Override
  public SortedBag<Card> initialClaimCards() {
    send(MessageId.CARDS, List.of());
    
    return Serdes.CARD_SORTED_BAG.deserialize(receive());
  }

  @Override
  public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
	  send(MessageId.CHOOSE_ADDITIONAL_CARDS, List.of(Serdes.CARD_SORTED_BAG_LIST.serialize(options)));
	  
    return Serdes.CARD_SORTED_BAG.deserialize(receive());
  }

  /**
   * Sends a serialized message to the client using its socket
   * 
   * @param messageId
   *        The identifier of the server's message
   * 
   * @param serializedArgs
   *        A list of serialized arguments to send to the client
   */
  private void send(MessageId messageId, List<String> serializedArgs) {
    List<String> arguments = new ArrayList<String>();
    arguments.add(messageId.name());
    arguments.addAll(serializedArgs);
    try {
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
      String message = String.join(" ", arguments);
      writer.write(message);
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
