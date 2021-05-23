// TODO: Client main Javadoc
package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;

import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;
import static ch.epfl.tchu.game.PlayerId.*;

public class ClientMain extends Application{
  
    public static void main(String[] args) {

        launch(args);
    }
    @Override

    public void start(Stage primaryStage){
        
       List<String> args = this.getParameters().getRaw();
       String hostName = "localhost";
       int port = 5108;
       if(args.size() >= 2){
         hostName = args.get(0);
         port = Integer.valueOf(args.get(1));
       }
       Map<PlayerId, String> names =
        Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
      Map<PlayerId, Player> players =
        Map.of(PLAYER_1, new GraphicalPlayerAdapter(),
           PLAYER_2, new GraphicalPlayerAdapter());
      RemotePlayerClient remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(), hostName, port);

      new Thread (() -> remotePlayerClient.run()).start();
    }
}