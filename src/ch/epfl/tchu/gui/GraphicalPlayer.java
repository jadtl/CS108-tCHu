// TODO: Graphical player Javadoc
package ch.epfl.tchu.gui;

import static javafx.collections.FXCollections.observableArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ChooseTicketsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

/**
 * The graphical interface of a tCHu player
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public final class GraphicalPlayer {
    private final ObservableGameState gameState;
    private final ObservableList<Text> gameInfos;
    private final Stage mainWindow;

    private final ObjectProperty<DrawTicketsHandler> drawTickets;
    private final ObjectProperty<DrawCardHandler> drawCard;
    private final ObjectProperty<ClaimRouteHandler> claimRoute;

    /**
     * @param ownId
     * @param playerNames
     */
    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> playerNames) {
        claimRoute = new SimpleObjectProperty<>();
        drawTickets = new SimpleObjectProperty<>();
        drawCard = new SimpleObjectProperty<>();

        gameState = new ObservableGameState(ownId);
        gameInfos = observableArrayList();

        Node mapView = MapViewCreator
                .createMapView(gameState, claimRoute, this::chooseClaimCards);
        Node cardsView = DecksViewCreator
                .createCardsView(gameState, drawTickets, drawCard);
        Node handView = DecksViewCreator
                .createHandView(gameState);
        Node infoView = InfoViewCreator
                .createInfoView(PlayerId.PLAYER_1, playerNames, gameState, gameInfos);

        BorderPane pane = new BorderPane(mapView, null, cardsView, handView, infoView);
        Scene scene = new Scene(pane);
        mainWindow = new Stage();
        mainWindow.setScene(scene);
        mainWindow.setTitle(String.join(" \u2014 ", List.of("tCHu", playerNames.get(ownId))));
        mainWindow.show();
    }

    /**
     * @param newState
     * @param ownState
     */
    public void setState(PublicGameState newState, PlayerState ownState) {
        assert Platform.isFxApplicationThread();
        gameState.setState(newState, ownState);
    }

    /**
     * @param info
     */
    public void receiveInfo(String info) {
        assert Platform.isFxApplicationThread();

        gameInfos.add(new Text(info + '\n'));
        if (gameInfos.size() >= 5)
            gameInfos.remove(0);
    }

    /**
     * @param drawTicketsHandler
     * @param drawCardHandler
     * @param claimRouteHandler
     */
    public void startTurn(DrawTicketsHandler drawTicketsHandler, DrawCardHandler drawCardHandler, ClaimRouteHandler claimRouteHandler) {
        assert Platform.isFxApplicationThread();

        if (gameState.canDrawTicketsProperty().get()) drawTickets.set(() -> {
            drawTicketsHandler.onDrawTickets();
            drawTickets.set(null);
            claimRoute.set(null);
            drawCard.set(null);
        });
        if (gameState.canDrawCardsProperty().get()) drawCard.set(s -> {
            drawCardHandler.onDrawCard(s);
            drawTickets.set(null);
            claimRoute.set(null);
            drawCard(drawCardHandler);
        });
        claimRoute.set((r, c) -> {
            claimRouteHandler.onClaimRoute(r, c);
            drawTickets.set(null);
            claimRoute.set(null);
            drawCard.set(null);
        });
    }

    /**
     * @param drawCardHandler
     */
    public void drawCard(DrawCardHandler drawCardHandler) {
        assert Platform.isFxApplicationThread();

        drawCard.set(s -> {
            drawCardHandler.onDrawCard(s);
            drawCard.set(null);
        });
    }

    /**
     * @param ticketOptions
     * @param chooseTicketsHandler
     */
    public void chooseTickets(SortedBag<Ticket> ticketOptions, ChooseTicketsHandler chooseTicketsHandler) {
        assert Platform.isFxApplicationThread();

        Stage window = new Stage();
        ObservableList<Ticket> items = observableArrayList(ticketOptions.toList());
        ListView<Ticket> listView = new ListView<>(items);
        if (Constants.IN_GAME_TICKETS_COUNT > 1) listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button button = new Button(StringsFr.CHOOSE);
        button.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).lessThan(ticketOptions.size() - Constants.DISCARDABLE_TICKETS_COUNT));
        button.setOnAction(e -> {
                    window.hide();
                    chooseTicketsHandler.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems()));
                }
        );

        Text text = new Text(String.format(StringsFr.CHOOSE_TICKETS, Constants.IN_GAME_TICKETS_COUNT, StringsFr.plural(Constants.IN_GAME_TICKETS_COUNT)));
        TextFlow textFlow = new TextFlow(text);
        VBox box = new VBox(3, textFlow, listView, button);

        Scene scene = new Scene(box);
        scene.getStylesheets().add("chooser.css");

        window.initStyle(StageStyle.UTILITY);
        window.initOwner(mainWindow);
        window.initModality(Modality.WINDOW_MODAL);
        window.setOnCloseRequest(Event::consume);
        window.setTitle(StringsFr.TICKETS_CHOICE);
        window.setScene(scene);

        window.show();
    }

    /**
     * @param cardOptions
     * @param chooseCardsHandler
     */
    public void chooseClaimCards(List<SortedBag<Card>> cardOptions, ChooseCardsHandler chooseCardsHandler) {
        assert Platform.isFxApplicationThread();

        Stage window = new Stage();

        ObservableList<SortedBag<Card>> list = observableArrayList(cardOptions);
        ListView<SortedBag<Card>> listView = new ListView<>(list);
        listView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button button = new Button(StringsFr.CHOOSE);
        button.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).isEqualTo(0));
        EventHandler<ActionEvent> buttonActionHandler = e -> {
            window.hide();
            chooseCardsHandler.onChooseCards(listView.getSelectionModel().getSelectedItem());
        };
        button.setOnAction(buttonActionHandler);

        Text text = new Text(StringsFr.CHOOSE_CARDS);
        TextFlow textFlow = new TextFlow(text);
        VBox box = new VBox();
        box.getChildren().addAll(List.of(textFlow, listView, button));

        Scene scene = new Scene(box);
        scene.getStylesheets().add("chooser.css");

        window.initStyle(StageStyle.UTILITY);
        window.initOwner(mainWindow);
        window.initModality(Modality.WINDOW_MODAL);
        window.setOnCloseRequest(Event::consume);
        window.setTitle(StringsFr.CARDS_CHOICE);
        window.setScene(scene);

        window.show();
    }

    /**
     * @param cardOptions
     * @param chooseCardsHandler
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> cardOptions, ChooseCardsHandler chooseCardsHandler) {
        assert Platform.isFxApplicationThread();

        Stage window = new Stage();

        ObservableList<SortedBag<Card>> list = observableArrayList(cardOptions);
        ListView<SortedBag<Card>> listView = new ListView<>(list);
        listView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button button = new Button(StringsFr.CHOOSE);
        EventHandler<ActionEvent> buttonActionHandler = e -> {
            window.hide();
            SortedBag<Card> choice = listView.getSelectionModel().getSelectedItem();
            chooseCardsHandler.onChooseCards(Objects.isNull(choice) ? SortedBag.of() : choice);
        };
        button.setOnAction(buttonActionHandler);

        Text text = new Text(StringsFr.CHOOSE_ADDITIONAL_CARDS);
        TextFlow textFlow = new TextFlow(text);
        VBox box = new VBox();
        box.getChildren().addAll(List.of(textFlow, listView, button));

        Scene scene = new Scene(box);
        scene.getStylesheets().add("chooser.css");

        window.initStyle(StageStyle.UTILITY);
        window.initOwner(mainWindow);
        window.initModality(Modality.WINDOW_MODAL);
        window.setOnCloseRequest(Event::consume);
        window.setTitle(StringsFr.CARDS_CHOICE);
        window.setScene(scene);

        window.show();
    }

    private static class CardBagStringConverter extends StringConverter<SortedBag<Card>> {
        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString(SortedBag<Card> cards) {
            StringBuilder stringBuilder = new StringBuilder();
            List<String> words = new ArrayList<>();

            for (Card card : Card.values()) {
                if (cards.contains(card))
                    words.add(stringBuilder.append(cards.countOf(card))
                            .append(" ").append(Info.cardName(card, cards.countOf(card))).toString());
                stringBuilder = new StringBuilder();
            }

            if (words.size() == 1)
                return words.get(0);
            else
                return stringBuilder.append(String.join(", ", words.subList(0, words.size() - 1)))
                        .append(StringsFr.AND_SEPARATOR).append(words.get(words.size() - 1)).toString();
        }
    }
}
