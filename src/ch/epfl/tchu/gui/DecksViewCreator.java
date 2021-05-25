package ch.epfl.tchu.gui;

import static ch.epfl.tchu.game.Card.LOCOMOTIVE;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;

/**
 * A creator for the hand view and the cards view
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
class DecksViewCreator {
    /**
     * The class is not instantiable
     */
    private DecksViewCreator() {
    }

    /**
     * @param gameState The current player's observable game state
     * @return The hand view displaying the information from the given game state
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
     * @param gameState        The current player's observable game state
     * @param drawTicketsHandlerProperty The property of the tickets drawing handler
     * @param drawCardHandlerProperty    The property of the card drawing handler
     * @return The cards view displaying the information from the given game state and properties
     */
    public static VBox createCardsView(ObservableGameState gameState,
                                       ObjectProperty<DrawTicketsHandler> drawTicketsHandlerProperty,
                                       ObjectProperty<DrawCardHandler> drawCardHandlerProperty) {
        Button ticketsDeck = new Button("Billets");
        ticketsDeck.setGraphic(createButtonGauge(gameState.remainingTicketsPercentageProperty()));
        ticketsDeck.getStyleClass().add("gauged");
        ticketsDeck.disableProperty().bind(drawTicketsHandlerProperty.isNull());
        ticketsDeck.setOnMouseClicked(e -> drawTicketsHandlerProperty.get().onDrawTickets());

        Button cardsDeck = new Button("Cartes");
        cardsDeck.setGraphic(createButtonGauge(gameState.remainingCardsPercentageProperty()));
        cardsDeck.getStyleClass().add("gauged");
        cardsDeck.disableProperty().bind(drawCardHandlerProperty.isNull());
        cardsDeck.setOnMouseClicked((new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                drawCardHandlerProperty.get().onDrawCard(Constants.DECK_SLOT);
            }
        }
        ));

        VBox cardsView = new VBox();
        cardsView.getChildren().add(ticketsDeck);
        cardsView.getChildren().addAll(createFaceUpCardsView(gameState, drawCardHandlerProperty));
        cardsView.getChildren().add(cardsDeck);
        cardsView.setId("card-pane");
        cardsView.getStylesheets().addAll(List.of("decks.css", "colors.css"));

        return cardsView;
    }

    private static List<Node> createHandViewCards(ObservableGameState gameState) {
        List<Node> cards = new ArrayList<Node>();
        for (Card card : Card.ALL) {
            ReadOnlyIntegerProperty count = gameState.ownedCardsProperty(card);
            Text counter = new Text();
            counter.textProperty().bind(Bindings.convert(count));
            counter.visibleProperty().bind(Bindings.greaterThan(count, 1));

            StackPane cardAndCount = new StackPane();
            cardAndCount.visibleProperty().bind(Bindings.greaterThan(count, 0));
            cardAndCount.getChildren().addAll(createCardGeometry());
            cardAndCount.getChildren().add(counter);
            cardAndCount.getStyleClass().addAll(List.of("card", card.equals(LOCOMOTIVE) ? "NEUTRAL" : card.toString()));

            cards.add(cardAndCount);
        }

        return cards;
    }

    private static List<Node> createFaceUpCardsView(ObservableGameState gameState, ObjectProperty<DrawCardHandler> drawCardHandlerProperty) {
        List<Node> faceUpCards = new ArrayList<Node>();
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card card = gameState.faceUpCard(slot).get();
            StackPane faceUpCard = new StackPane();
            faceUpCard.getChildren().addAll(createCardGeometry());
            faceUpCard.getStyleClass().addAll(List.of("card", cardStyleClass(card)));
            faceUpCard.disableProperty().bind(drawCardHandlerProperty.isNull());
            faceUpCard.setOnMouseClicked(e -> drawCardHandlerProperty.get().onDrawCard(slot));
            gameState.faceUpCard(slot).addListener((o, oV, nV) -> {
                faceUpCard.getStyleClass().remove(cardStyleClass(oV));
                faceUpCard.getStyleClass().add(cardStyleClass(nV));
            });

            faceUpCards.add(faceUpCard);
        }

        return faceUpCards;
    }

    private static String cardStyleClass(Card card) {
        return card.equals(LOCOMOTIVE) ? "NEUTRAL" : card.toString();
    }

    private static List<Node> createCardGeometry() {
        Rectangle outside = new Rectangle(60, 90);
        outside.getStyleClass().add("outside");
        Rectangle inside = new Rectangle(40, 70);
        inside.getStyleClass().addAll(List.of("filled", "inside"));
        Rectangle train = new Rectangle(40, 70);
        train.getStyleClass().add("train-image");

        return List.of(outside, inside, train);
    }

    private static Group createButtonGauge(ReadOnlyIntegerProperty gauge) {
        Rectangle background = new Rectangle(50, 5);
        background.getStyleClass().add("background");
        Rectangle foreground = new Rectangle(5, 5);
        foreground.getStyleClass().add("foreground");
        foreground.widthProperty().bind(gauge.multiply(50).divide(100));

        return new Group(List.of(background, foreground));
    }
}
