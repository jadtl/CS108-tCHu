// TODO: Server main Javadoc
package ch.epfl.tchu.gui;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import static ch.epfl.tchu.game.PlayerId.*;

import ch.epfl.tchu.SortedBag;

/**
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public class ServerMain extends Application {

    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        List<String> arguments = this.getParameters().getRaw();
        int port = 5108;
        Map<PlayerId, String> names = new HashMap<>(Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles"));

        if (arguments.size() >= 2) {
            names.replace(PLAYER_1, arguments.get(0));
            names.replace(PLAYER_2, arguments.get(1));
        }

        try (ServerSocket s0 = new ServerSocket(port)) {
            Socket s = s0.accept();
            Map<PlayerId, Player> players =
                    Map.of(PLAYER_1, new GraphicalPlayerAdapter(),
                            PLAYER_2, new RemotePlayerProxy(s));
            new Thread(() -> Game.play(players, names, SortedBag.of(ChMap.tickets()), new Random())).start();

        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}