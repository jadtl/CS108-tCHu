package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

class InfoViewCreator {
  /**
   * The class is non-instanciable
   */
  private InfoViewCreator() {}

  public static VBox createInfoView(PlayerId player, Map<PlayerId, String> playerNames, ObservableGameState gameState, ObservableList<Text> gameInfos) {
    VBox playerStats = new VBox();
    playerStats.setId("player-stats");
    PlayerId.ALL.forEach(p -> {
      Circle circle = new Circle(5);
      circle.getStyleClass().add("filled");

      Text text = new Text();
      text.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, 
      playerNames.get(p), gameState.ticketCountProperty(p), gameState.cardCountProperty(p), gameState.carCountProperty(p), gameState.claimPointsProperty(p)));


      TextFlow playerInfo = new TextFlow();
      playerInfo.getStyleClass().add(p.name());
      playerInfo.getChildren().addAll(List.of(circle, text));

      playerStats.getChildren().add(playerInfo);
    });

    Separator separator = new Separator();
    separator.orientationProperty().setValue(Orientation.HORIZONTAL);

    TextFlow messages = new TextFlow();
    messages.setId("game-info");
    Bindings.bindContent(messages.getChildren(), gameInfos);

    VBox infoView = new VBox();
    infoView.getChildren().addAll(List.of(playerStats, separator, messages));
    infoView.getStylesheets().addAll(List.of("info.css", "colors.css"));

    return infoView;
  }
}
