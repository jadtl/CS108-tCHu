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
 * The client of a remote player
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public final class RemotePlayerClient {
    private final Player player;
    private final String host;
    private final int port;
    private String lastMessage;

    /**
     * Constructs a client using a player, a hostname and a server port
     *
     * @param player The {@link Player} who needs a remote access
     * @param host   The server's hostname
     * @param port   The server's port
     */
    public RemotePlayerClient(Player player, String host, int port) {
        this.player = player;
        this.host = host;
        this.port = port;
        this.lastMessage = "";
    }

    /**
     * Waits for a message coming from the server and deserializes it, calls the corresponding
     * player's method and serializes the eventual result and sends it back
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
                MessageId messageId = MessageId.valueOf(Arrays.stream(lastMessage.split(Pattern.quote(" "), -1))
                        .collect(Collectors.toList()).get(0));
                List<String> arguments = Arrays.stream(lastMessage.split(Pattern.quote(" "), -1))
                        .collect(Collectors.toList());
                arguments.remove(0);

                switch (messageId) {
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
                        player.initPlayers(ownId, PlayerId.ALL.stream().collect(Collectors.toMap(p -> p, p -> names.get(PlayerId.ALL.indexOf(p)))));
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
                        PlayerState ownState = Serdes.PLAYER_STATE.deserialize(arguments.get(1));
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

    private void endMessage(BufferedWriter writer) {
        try {
            writer.write('\n');
            writer.flush();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}