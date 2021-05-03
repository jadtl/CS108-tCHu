package ch.epfl.tchu.gui;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * 
 */
class DecksViewCreator {
  /**
   * The class is non-instanciable
   */
  private DecksViewCreator() {}
  
  /**
   * 
   * 
   * @param observableGameState
   * 
   * @return
   */
  public static HBox createHandView(ObservableGameState gameState) {
    HBox cards = new HBox();
    cards.getChildren().addAll(createHandViewCards(gameState));
    cards.setId("hand-pane");

    ListView<Ticket> tickets = new ListView<Ticket>(gameState.ownedTicketsProperty().get());
    tickets.setId("tickets");

    HBox handView = new HBox(tickets);
    handView.getChildren().add(cards);
    handView.getStylesheets().addAll(List.of("decks.css", "colors.css"));
    
    return handView;
  }

  /**
   * 
   * 
   * @param observableGameState
   * 
   * @param drawTicketsHandlerProperty
   * 
   * @param drawCardHandlerProperty
   * 
   * @return
   */
  public static VBox createCardsView(ObservableGameState observableGameState, 
    ObjectProperty<DrawTicketsHandler> drawTicketsHandlerProperty,
    ObjectProperty<DrawCardHandler> drawCardHandlerProperty) {
      return null;
  }

  private static List<Node> createHandViewCards(ObservableGameState gameState) {
    List<Node> cards = new ArrayList<Node>();
    for (Card card : Card.ALL) {
      Rectangle outside = new Rectangle(60, 90);
      outside.getStyleClass().add("outside");
      Rectangle inside = new Rectangle(40, 70);
      inside.getStyleClass().addAll(List.of("filled", "inside"));
      Rectangle train = new Rectangle(40, 70);
      train.getStyleClass().add("train-image");

      Text count = new Text(String.valueOf(gameState.ownedCardsProperty(card).get()));
      
      StackPane cardAndCount = new StackPane(count);
      cardAndCount.getChildren().addAll(List.of(outside, inside, train));
      cardAndCount.getStyleClass().addAll(List.of("card", card.equals(Card.LOCOMOTIVE) ? "NEUTRAL" : card.toString()));

      cards.add(cardAndCount);
    }

    return cards;
  }
}
