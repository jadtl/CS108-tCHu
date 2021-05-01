package ch.epfl.tchu.net;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Ticket;

/**
 * 
 */
public class RemotePlayerClient {
  private Player player;
  private String host;
  private int port;
  private String lastMessage;

  /**
   * 
   * @param player
   * @param host
   * @param port
   */
  public RemotePlayerClient(Player player, String host, int port) {
    this.player = player;
    this.host = host;
    this.port = port;
    this.lastMessage = "";
  }

  /**
   * 
   */
  public void run() {
    try {
      Socket socket = new Socket(host, port);
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));

      while (!Objects.isNull(lastMessage)) {
        lastMessage = reader.readLine();
        if (Objects.isNull(lastMessage)) {
          socket.close();
          return;
        }
        MessageId messageId = MessageId.valueOf(Arrays.asList(lastMessage.split(Pattern.quote(" "), -1)).stream()
        .collect(Collectors.toList()).get(0));
        List<String> arguments = Arrays.asList(lastMessage.split(Pattern.quote(" "), -1)).stream()
        .collect(Collectors.toList());
        arguments.remove(0);

        switch(messageId) {
        case CARDS:
        	writer.write(Serdes.CARD_SORTED_BAG.serialize(player.initialClaimCards()));
        	endMessage(writer);
          break;
        case CHOOSE_ADDITIONAL_CARDS:
        	List<SortedBag<Card>> optionCards = Serdes.CARD_SORTED_BAG_LIST.deserialize(arguments.get(0));
        	writer.write(Serdes.CARD_SORTED_BAG.serialize(player.chooseAdditionalCards(optionCards)));
        	endMessage(writer);
          break;
        case CHOOSE_INITIAL_TICKETS:
        writer.write(Serdes.TICKET_SORTED_BAG.serialize(player.chooseInitialTickets()));
        endMessage(writer);
          break;
        case CHOOSE_TICKETS:
        SortedBag<Ticket> optionTickets = Serdes.TICKET_SORTED_BAG.deserialize(arguments.get(0));
         writer.write(Serdes.TICKET_SORTED_BAG.serialize(player.chooseTickets(optionTickets)));
         endMessage(writer);
          break;
        case DRAW_SLOT:
          writer.write(Serdes.INTEGER.serialize(player.drawSlot()));
          endMessage(writer);
          break;
        case INIT_PLAYERS:
          PlayerId ownId = Serdes.PLAYER_ID.deserialize(arguments.get(0));
          List<String> names = Serdes.STRING_LIST.deserialize(arguments.get(1));
          Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, names.get(0), PlayerId.PLAYER_2, names.get(1));
          player.initPlayers(ownId, playerNames);
          break;
        case NEXT_TURN:
          writer.write(Serdes.TURN_KIND.serialize(player.nextTurn()));
          endMessage(writer);
          break;
        case RECEIVE_INFO:
        	player.receiveInfo(Serdes.STRING.deserialize(arguments.get(0)));
          break;
        case ROUTE:
          writer.write(Serdes.ROUTE.serialize(player.claimedRoute()));
          endMessage(writer);
          break;
        case SET_INITIAL_TICKETS:      	
        	player.setInitialTicketChoice(Serdes.TICKET_SORTED_BAG.deserialize(arguments.get(0))); 
          break;
        case UPDATE_STATE:
         PublicGameState newState = Serdes.PUBLIC_GAME_STATE.deserialize(arguments.get(0));
         PlayerState ownState = Serdes.PLAYER_STATE.deserialize(arguments.get(0));
         player.updateState(newState, ownState);
          break;
        default:
          break; 
        }
      }
      socket.close();
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }

  /**
   * 
   * @param writer
   */
  private void endMessage(BufferedWriter writer) {
    try {
      writer.write('\n');
      writer.flush();
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }
}