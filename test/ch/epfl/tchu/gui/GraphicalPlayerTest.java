package ch.epfl.tchu.gui;

import java.util.Map;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicCardState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.PublicPlayerState;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.application.Application;
import javafx.stage.Stage;

public final class GraphicalPlayerTest extends Application {
  public static void main(String[] args) { launch(args); }

  private static void setState(GraphicalPlayer player) {
    PlayerState p1State =
      new PlayerState(SortedBag.of(ChMap.tickets().subList(0, 3)),
		      SortedBag.of(1, Card.WHITE, 3, Card.RED),
		      ChMap.routes().subList(0, 3));

    PublicPlayerState p2State =
      new PublicPlayerState(0, 0, ChMap.routes().subList(3, 6));

    Map<PlayerId, PublicPlayerState> pubPlayerStates =
      Map.of(PlayerId.PLAYER_1, p1State, PlayerId.PLAYER_2, p2State);
    PublicCardState cardState =
      new PublicCardState(Card.ALL.subList(0, 5), 110 - 2 * 4 - 5, 0);
    PublicGameState publicGameState =
      new PublicGameState(36, cardState, PlayerId.PLAYER_1, pubPlayerStates, null);

    player.setState(publicGameState, p1State);
  }

  @Override
  public void start(Stage primaryStage) {
    Map<PlayerId, String> playerNames =
      Map.of(PlayerId.PLAYER_1, "Ada", PlayerId.PLAYER_2, "Charles");
    GraphicalPlayer p = new GraphicalPlayer(PlayerId.PLAYER_1, playerNames);
    setState(p);

    DrawTicketsHandler drawTicketsH =
      () -> p.receiveInfo("Je tire des billets !");
    DrawCardHandler drawCardH =
      s -> p.receiveInfo(String.format("Je tire une carte de %s !", s));
    ClaimRouteHandler claimRouteH =
      (r, cs) -> {
      String rn = r.station1() + " - " + r.station2();
      p.receiveInfo(String.format("Je m'empare de %s avec %s", rn, cs));
    };

    p.startTurn(drawTicketsH, drawCardH, claimRouteH);
  }
}
