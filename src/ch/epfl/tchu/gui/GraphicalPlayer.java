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
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ChooseTicketsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
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

public class GraphicalPlayer {
  private ObservableGameState gameState;
  private final ObservableList<Text> gameInfos;
  private final Stage mainWindow;

  private ObjectProperty<DrawTicketsHandler> drawTickets;   
  private ObjectProperty<DrawCardHandler> drawCard;
  private ObjectProperty<ClaimRouteHandler> claimRoute;
  private ObjectProperty<ChooseTicketsHandler> chooseTickets;
  private ObjectProperty<ChooseCardsHandler> chooseCards;

  public GraphicalPlayer(PlayerId player, Map<PlayerId, String> playerNames) {
    claimRoute = new SimpleObjectProperty<ClaimRouteHandler>(null);
    drawTickets = new SimpleObjectProperty<DrawTicketsHandler>(null);
    drawCard = new SimpleObjectProperty<DrawCardHandler>(null);
    chooseCards = new SimpleObjectProperty<ChooseCardsHandler>(null);

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

  public void receiveInfo(String info) {
    assert Platform.isFxApplicationThread();

    gameInfos.add(new Text(info));
    if (gameInfos.size() >= 5)
      gameInfos.remove(0);
  }

  public void startTurn(DrawTicketsHandler drawTicketsHandler, DrawCardHandler drawCardHandler, ClaimRouteHandler claimRouteHandler) {
    assert Platform.isFxApplicationThread();
    
    drawTickets = new SimpleObjectProperty<DrawTicketsHandler>(drawTicketsHandler);
    drawCard = new SimpleObjectProperty<DrawCardHandler>(drawCardHandler);
    claimRoute = new SimpleObjectProperty<ClaimRouteHandler>(claimRouteHandler);
  }

  public void drawCard(DrawCardHandler drawCardHandler) {
    assert Platform.isFxApplicationThread();

    drawTickets = new SimpleObjectProperty<DrawTicketsHandler>(null);
    drawCard = new SimpleObjectProperty<DrawCardHandler>(drawCardHandler);
    claimRoute = new SimpleObjectProperty<ClaimRouteHandler>(null);
  }

  public void chooseTickets(SortedBag<Ticket> ticketOptions, ChooseTicketsHandler chooseTicketsHandler) {
    assert Platform.isFxApplicationThread();

    ListView<Ticket> items = new ListView<Ticket>(observableArrayList(ticketOptions.toList()));
    if (Constants.IN_GAME_TICKETS_COUNT > 1) items.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    showModalWindow(listView(ticketOptions.toList()), StringsFr.TICKETS_CHOICE, String.format(StringsFr.CHOOSE_TICKETS, List.of(Constants.IN_GAME_TICKETS_COUNT, StringsFr.plural(Constants.IN_GAME_TICKETS_COUNT))));
  }

  public void chooseClaimCards(List<SortedBag<Card>> cardOptions, ChooseCardsHandler chooseCardsHandler) {
    assert Platform.isFxApplicationThread();

    showModalWindow(bagCardlistView(cardOptions), StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_CARDS);
  }

  public void chooseAdditionalCards(List<SortedBag<Card>> cardOptions, ChooseCardsHandler chooseCardsHandler) {
    assert Platform.isFxApplicationThread();

    showModalWindow(bagCardlistView(cardOptions), StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_ADDITIONAL_CARDS);
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

  private <E> void showModalWindow(ListView<E> items, String title, String prompt) {
    Text text = new Text(prompt);
    TextFlow textFlow = new TextFlow(text);
    Button button = new Button();
    VBox box = new VBox();
    box.getChildren().addAll(List.of(textFlow, items, button));

    Scene scene = new Scene(box);
    scene.getStylesheets().add("chooser.css");

    Stage selectionWindow = new Stage(StageStyle.UTILITY);
    selectionWindow.initOwner(mainWindow);
    selectionWindow.initModality(Modality.WINDOW_MODAL);
    selectionWindow.show();
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
