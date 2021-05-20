// TODO: Map view creator Javadoc
package ch.epfl.tchu.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Route.Level;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * 
 * 
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
class MapViewCreator {
  /**
   * The class is non-instanciable
   */
  private MapViewCreator() {}

  /**
   * 
   * @param gameState
   * 
   * @param claimRouteHandlerProperty
   * 
   * @param cardChooser
   * 
   * @return
   */
  public static Pane createMapView(ObservableGameState gameState, 
    ObjectProperty<ClaimRouteHandler> claimRouteHandlerProperty, CardChooser cardChooser) {
    List<Node> allRouteGroups = new ArrayList<Node>();
    ChMap.routes().forEach(r -> allRouteGroups.add(createRouteGroup(r, gameState, claimRouteHandlerProperty, cardChooser)));

    ImageView background = new ImageView();

    Pane mapView = new Pane(background);
    mapView.getChildren().addAll(allRouteGroups);
    mapView.getStylesheets().addAll(List.of("map.css", "colors.css"));
    
    return mapView;
  }

  private static Group createRouteGroup(Route route, ObservableGameState gameState, 
    ObjectProperty<ClaimRouteHandler> claimRouteHandlerProperty, CardChooser cardChooser) {
    List<Node> tiles = new ArrayList<Node>();
    for (int i = 1; i <= route.length(); i++) {
      Rectangle carRectangle = new Rectangle(36, 12);
      carRectangle.getStyleClass().add("filled");
      Circle carCircle1 = new Circle(12, 6, 3);
      Circle carCircle2 = new Circle(24, 6, 3);
      Group car = new Group(List.of(carRectangle, carCircle1, carCircle2));
      car.getStyleClass().add("car");

      Rectangle track = new Rectangle(36, 12);
      track.getStyleClass().add("track");
      track.getStyleClass().add("filled");

      Group tile = new Group(List.of(track, car));
      tile.setId(String.join("_", List.of(route.id(), String.valueOf(i))));
      tiles.add(tile);
    }

    Group routeGroup = new Group(tiles);
    routeGroup.setId(route.id());
    routeGroup.getStyleClass().add("route");
    if (route.level().equals(Level.UNDERGROUND)) routeGroup.getStyleClass().add("UNDERGROUND");
    if (Objects.isNull(route.color())) routeGroup.getStyleClass().add("NEUTRAL");
    else routeGroup.getStyleClass().add(route.color().toString());

    gameState.routesOwnershipProperty(route).addListener((o, oV, nV) -> {
      routeGroup.getStyleClass().add(nV.name()); 
    });

    routeGroup.disableProperty().bind(
      claimRouteHandlerProperty.isNull().or(gameState.routeClaimabilityProperty(route).not())
    );

    routeGroup.setOnMouseClicked(e -> {
        List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCardsProperty(route).get();
  
        if (possibleClaimCards.size() == 1)
          claimRouteHandlerProperty.get().onClaimRoute(route, possibleClaimCards.get(0));
        else {
          ChooseCardsHandler chooseCardsHandler = chosenCards -> claimRouteHandlerProperty.get().onClaimRoute(route, chosenCards);
          cardChooser.chooseCards(possibleClaimCards, chooseCardsHandler);
        }
      }
    );

    return routeGroup;
  }

  /**
   * 
   * 
   */
  @FunctionalInterface
  interface CardChooser {
    void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
  }
}
