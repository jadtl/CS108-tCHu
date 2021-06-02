package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Route.Level;
import ch.epfl.tchu.game.Station;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A creator for the map view
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
class MapViewCreator {
    /**
     * The class is not instantiable
     */
    private MapViewCreator() {
    }

    /**
     * The map view of the graphical interface
     *
     * @param gameState                 The {@link ObservableGameState} of the concerned player
     * @param claimRouteHandlerProperty The property of {@link ClaimRouteHandler}
     * @param cardChooser               The {@link CardChooser}
     * @return The map view {@link Pane} of the game
     */
    public static Pane createMapView(ObservableGameState gameState,
                                     ObjectProperty<ClaimRouteHandler> claimRouteHandlerProperty, CardChooser cardChooser) {
        List<Node> allRouteGroups = new ArrayList<>();
        ChMap.routes().forEach(r -> allRouteGroups.add(createRouteGroup(r, gameState, claimRouteHandlerProperty, cardChooser)));

        List<Node> allStationGroups = new ArrayList<>();
        ChMap.stations().forEach(s -> allStationGroups.add(createStationGroup(s, gameState)));

        ImageView background = new ImageView();

        Pane mapView = new Pane(background);
        mapView.getChildren().addAll(allRouteGroups);
        mapView.getChildren().addAll(allStationGroups);
        mapView.getStylesheets().addAll(List.of("map.css", "colors.css"));

        return mapView;
    }

    private static Group createRouteGroup(Route route, ObservableGameState gameState,
                                          ObjectProperty<ClaimRouteHandler> claimRouteHandlerProperty, CardChooser cardChooser) {
        List<Node> tiles = new ArrayList<>();
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

        gameState.routesOwnershipProperty(route).addListener((o, oV, nV) -> routeGroup.getStyleClass().add(nV.name()));

        routeGroup.disableProperty().bind(
                claimRouteHandlerProperty.isNull().or(gameState.routeAvailabilityProperty(route).not())
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

    private static Group createStationGroup(Station station, ObservableGameState gameState) {
        Circle indicator = new Circle();
        indicator.setRadius(station.id() > 33 ? 13 : 6);

        Group stationGroup = new Group(indicator);
        stationGroup.setId("S" + station.id());
        stationGroup.getStyleClass().add(station.id() > 33 ? "country-station" : "city-station");

        Tooltip ticketInfo = new Tooltip(String.valueOf(station.id()));
        ticketInfo.setShowDelay(Duration.seconds(0.25));
        Tooltip.install(stationGroup, ticketInfo);

        return stationGroup;
    }

    /**
     * A card selector interface
     */
    @FunctionalInterface
    interface CardChooser {
        /**
         * Called when the player has to choose the cards they want to use to claim a route
         *
         * @param options The cards options for the player
         * @param handler The handler associated with the choice
         */
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }
}
