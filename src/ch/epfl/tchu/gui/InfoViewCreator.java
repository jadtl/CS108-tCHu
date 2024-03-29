package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A creator for the info view
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
class InfoViewCreator {
    /**
     * The class is not instantiable
     */
    private InfoViewCreator() {
    }

    /**
     * The info view of the graphical interface
     *
     * @param playerNames The {@link Map} of the players' names
     * @param gameState   The {@link ObservableGameState} of the concerned player
     * @param gameInfos   An {@link ObservableList} of infos related to the ongoing game
     * @return The {@link javafx.scene.layout.VBox} info view of the game
     */
    public static VBox createInfoView(Map<PlayerId, String> playerNames, ObservableGameState gameState, ObservableList<Text> gameInfos) {
        VBox playerStats = new VBox();
        playerStats.setId("player-stats");

        List<TextFlow> playersInfo = new LinkedList<>();
        PlayerId.ALL.forEach(p -> {
            Circle circle = new Circle(5);
            circle.getStyleClass().add("filled");

            Text text = new Text();
            text.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(p), gameState.ticketCountProperty(p),
                    gameState.cardCountProperty(p), gameState.carCountProperty(p), gameState.claimPointsProperty(p)));

            TextFlow playerInfo = new TextFlow();
            playerInfo.getStyleClass().add(p.name());
            playerInfo.getChildren().addAll(List.of(circle, text));

            if (p.equals(gameState.ownIdProperty().get()))
                playersInfo.add(0, playerInfo);
            else
                playersInfo.add(playerInfo);
        });

        playersInfo.forEach(t -> playerStats.getChildren().add(t));

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
