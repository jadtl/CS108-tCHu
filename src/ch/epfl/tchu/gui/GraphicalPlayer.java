// TODO: Graphical player Javadoc
package ch.epfl.tchu.gui;

import static javafx.collections.FXCollections.observableArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.stage.WindowEvent;

public class GraphicalPlayer {
  private ObservableGameState gameState;
  private final ObservableList<Text> gameInfos;
  private final Stage mainWindow;

  private ObjectProperty<DrawTicketsHandler> drawTickets;   
  private ObjectProperty<DrawCardHandler> drawCard;
  private ObjectProperty<ClaimRouteHandler> claimRoute;

  public GraphicalPlayer(PlayerId player, Map<PlayerId, String> playerNames) {
    claimRoute = new SimpleObjectProperty<ClaimRouteHandler>(null);
    drawTickets = new SimpleObjectProperty<DrawTicketsHandler>(null);
    drawCard = new SimpleObjectProperty<DrawCardHandler>(null);

    gameState = new ObservableGameState(player);
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
    mainWindow.setTitle(String.join(" \u2014 ", List.of("tCHu", playerNames.get(player))));
    mainWindow.show();
  }

  public void setState(PublicGameState newState, PlayerState ownState) {
    assert Platform.isFxApplicationThread();
    gameState.setState(newState, ownState);
  }

  // FIXME: Weird display at 5 messages
  public void receiveInfo(String info) {
    assert Platform.isFxApplicationThread();

    gameInfos.add(new Text(info + '\n'));
    if (gameInfos.size() >= 5)
      gameInfos.remove(0);
  }

  public void startTurn(DrawTicketsHandler drawTicketsHandler, DrawCardHandler drawCardHandler, ClaimRouteHandler claimRouteHandler) {
    assert Platform.isFxApplicationThread();
    
    if (gameState.canDrawTicketsProperty().get()) drawTickets.set(new DrawTicketsHandler() {
      @Override
      public void onDrawTickets() {
        drawTicketsHandler.onDrawTickets();
        drawTickets.set(null);
        claimRoute.set(null);
        drawCard.set(null);
      }
    });
    if (gameState.canDrawCardsProperty().get()) drawCard.set(new DrawCardHandler(){
      @Override
      public void onDrawCard(int slot) {
        drawCardHandler.onDrawCard(slot);
        drawTickets.set(null);
        claimRoute.set(null);
        drawCard(this);
      }
    });
    claimRoute.set(new ClaimRouteHandler(){
      @Override
      public void onClaimRoute(Route route, SortedBag<Card> claimCards) {
        claimRouteHandler.onClaimRoute(route, claimCards);
        drawTickets.set(null);
        claimRoute.set(null);
        drawCard.set(null);
      }
    });
  }

  public void drawCard(DrawCardHandler drawCardHandler) {
    assert Platform.isFxApplicationThread();

    drawCard.set(new DrawCardHandler() {
      @Override
      public void onDrawCard(int slot) {
        drawCardHandler.onDrawCard(slot);
        drawCard.set(null);
      }
    });
  }

  public void chooseTickets(SortedBag<Ticket> ticketOptions, ChooseTicketsHandler chooseTicketsHandler) {
    assert Platform.isFxApplicationThread();

    Stage window = new Stage();
    ListView<Ticket> items = listView(ticketOptions.toList());
    Button button = new Button(StringsFr.CHOOSE);
    button.disableProperty().bind(Bindings.size(items.getSelectionModel().getSelectedItems()).lessThan(ticketOptions.size() - 2));
    button.setOnAction(e -> {
        window.hide();
        chooseTickets(ticketOptions, chooseTicketsHandler);
      }
    );

    showModalWindow(window, items, button, StringsFr.TICKETS_CHOICE, String.format(StringsFr.CHOOSE_TICKETS, List.of(Constants.IN_GAME_TICKETS_COUNT, StringsFr.plural(Constants.IN_GAME_TICKETS_COUNT))));
  }

  public void chooseClaimCards(List<SortedBag<Card>> cardOptions, ChooseCardsHandler chooseCardsHandler) {
    assert Platform.isFxApplicationThread();

    Stage window = new Stage();
    EventHandler<ActionEvent> buttonActionHandler = e -> {
        window.hide();
        chooseClaimCards(cardOptions, chooseCardsHandler);
    };

    showChooseCardsWindow(window, cardOptions, buttonActionHandler, StringsFr.CHOOSE_ADDITIONAL_CARDS);
  }

  public void chooseAdditionalCards(List<SortedBag<Card>> cardOptions, ChooseCardsHandler chooseCardsHandler) {
    assert Platform.isFxApplicationThread();

    Stage window = new Stage();
    EventHandler<ActionEvent> buttonActionHandler = e -> {
        window.hide();
        chooseAdditionalCards(cardOptions, chooseCardsHandler);
    };

    showChooseCardsWindow(window, cardOptions, buttonActionHandler, StringsFr.CHOOSE_ADDITIONAL_CARDS);
  }

  private void showChooseCardsWindow(Stage window, List<SortedBag<Card>> options, EventHandler<ActionEvent> buttonActionH, String prompt) {
    ListView<SortedBag<Card>> items = bagCardlistView(options);

    Button button = new Button(StringsFr.CHOOSE);
    button.disableProperty().bind(Bindings.size(items.getSelectionModel().getSelectedItems()).isEqualTo(0));
    button.setOnAction(buttonActionH);

    showModalWindow(window, items, button, StringsFr.CARDS_CHOICE, prompt);
  }

  private <E> ListView<E> listView(List<E> items) {
    ObservableList<E> list = observableArrayList(items);
    ListView<E> listView = new ListView<E>(list);
    if (Constants.IN_GAME_TICKETS_COUNT > 1) listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    return listView;
  }

  private ListView<SortedBag<Card>> bagCardlistView(List<SortedBag<Card>> items) {
    ListView<SortedBag<Card>> listView = listView(items);
    listView.setCellFactory(v -> new TextFieldListCell<SortedBag<Card>>(new CardBagStringConverter()));

    return listView;
  }

  private <E> void showModalWindow(Stage window, ListView<E> items, Button button, String title, String prompt) {
    Text text = new Text(prompt);
    TextFlow textFlow = new TextFlow(text);
    VBox box = new VBox();
    box.getChildren().addAll(List.of(textFlow, items, button));

    Scene scene = new Scene(box);
    scene.getStylesheets().add("chooser.css");

    window.initStyle(StageStyle.UTILITY);
    window.initOwner(mainWindow);
    window.initModality(Modality.WINDOW_MODAL);
    window.setOnCloseRequest(e -> {
        e.consume();
      }
    );
    
    window.show();
  }

  private class CardBagStringConverter extends javafx.util.StringConverter<SortedBag<Card>> {
    @Override
    public SortedBag<Card> fromString(String string) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString(SortedBag<Card> cards) {
      StringBuilder stringBuilder = new StringBuilder();
      List<String> words = new ArrayList<String>();

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
