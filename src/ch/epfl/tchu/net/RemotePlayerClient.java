package ch.epfl.tchu.net;

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

import static java.nio.charset.StandardCharsets.US_ASCII;

import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;

public class RemotePlayerClient {
  private Player player;
  private String host;
  private int port;
  private String lastMessage;

  public RemotePlayerClient(Player player, String host, int port) {
    this.player = player;
    this.host = host;
    this.port = port;
    this.lastMessage = "";
  }

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
          break;
        case CHOOSE_ADDITIONAL_CARDS:
          break;
        case CHOOSE_INITIAL_TICKETS:
          break;
        case CHOOSE_TICKETS:
          break;
        case DRAW_SLOT:
          writer.write(Serdes.INTEGER.serialize(player.drawSlot()));
          writer.write('\n');
          writer.flush();
          break;
        case INIT_PLAYERS:
          PlayerId ownId = Serdes.PLAYER_ID.deserialize(arguments.get(0));
          List<String> names = Serdes.STRING_LIST.deserialize(arguments.get(1));
          Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, names.get(0), PlayerId.PLAYER_2, names.get(1));
          player.initPlayers(ownId, playerNames);
          break;
        case NEXT_TURN:
          writer.write(Serdes.TURN_KIND.serialize(player.nextTurn()));
          writer.write('\n');
          writer.flush();
          break;
        case RECEIVE_INFO:
          break;
        case ROUTE:
          break;
        case SET_INITIAL_TICKETS:
          break;
        case UPDATE_STATE:
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
}
