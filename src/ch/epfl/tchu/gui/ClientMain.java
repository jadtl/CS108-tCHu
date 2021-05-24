// TODO: Client main Javadoc
package ch.epfl.tchu.gui;

import java.util.List;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * 
 * 
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public class ClientMain extends Application{
  
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage){
      List<String> args = this.getParameters().getRaw();
      String hostName = "localhost";
      int port = 5108;

      if (args.size() >= 2) {
        hostName = args.get(0);
        port = Integer.valueOf(args.get(1));
      }

      RemotePlayerClient remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(), hostName, port);

      new Thread (() -> remotePlayerClient.run()).start();
    }
}