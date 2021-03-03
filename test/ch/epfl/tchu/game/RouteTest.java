package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RouteTest {
    @Test
    void constructorFailsOnIncorrectLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("0", new Station(0, "Lausanne"), new Station(1, "Zürich"),
                    -1, Route.Level.UNDERGROUND, Color.RED); });
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("0", new Station(0, "Lausanne"), new Station(1, "Zürich"),
                    Constants.MAX_ROUTE_LENGTH + 1, Route.Level.UNDERGROUND, Color.RED); });
    }

    @Test
    void possibleClaimCardsWorksWithColoredUndergroundRoutes() {
        Route route = new Route("0", new Station(0, "Lausanne"), new Station(1, "Zürich"),
                3, Route.Level.UNDERGROUND, Color.RED);

        assertEquals(List.of(Card.RED, Card.RED, Card.RED), route.possibleClaimCards().get(0).toList());
        assertEquals(List.of(Card.RED, Card.RED, Card.LOCOMOTIVE), route.possibleClaimCards().get(1).toList());
        assertEquals(List.of(Card.RED, Card.LOCOMOTIVE, Card.LOCOMOTIVE), route.possibleClaimCards().get(2).toList());
        assertEquals(List.of(Card.LOCOMOTIVE, Card.LOCOMOTIVE, Card.LOCOMOTIVE), route.possibleClaimCards().get(3).toList());
        assertEquals(4, route.possibleClaimCards().size());
    }

    @Test
    void possibleClaimCardsWorksWithNeuterUndergroundRoutes() {
        Route route = new Route("0", new Station(0, "Lausanne"), new Station(1, "Zürich"),
                2, Route.Level.UNDERGROUND, null);

        assertEquals(List.of(Card.BLACK, Card.BLACK), route.possibleClaimCards().get(0).toList());
        assertEquals(List.of(Card.RED, Card.RED), route.possibleClaimCards().get(6).toList());
        assertEquals(List.of(Card.VIOLET, Card.LOCOMOTIVE), route.possibleClaimCards().get(9).toList());
        assertEquals(List.of(Card.BLUE, Card.LOCOMOTIVE), route.possibleClaimCards().get(10).toList());
        assertEquals(17, route.possibleClaimCards().size());
    }

    @Test
    void possibleClaimCardsWorksWithColoredOvergroundRoutes() {
        Route route = new Route("0", new Station(0, "Lausanne"), new Station(1, "Zürich"),
                2, Route.Level.OVERGROUND, Color.RED);

        assertEquals(List.of(Card.RED, Card.RED), route.possibleClaimCards().get(0).toList());
        assertEquals(1, route.possibleClaimCards().size());
    }

    @Test
    void possibleClaimCardsWorksWithNeuterOvergroundRoutes() {
        Route route = new Route("0", new Station(0, "Lausanne"), new Station(1, "Zürich"),
                2, Route.Level.OVERGROUND, null);

        assertEquals(List.of(Card.RED, Card.RED), route.possibleClaimCards().get(6).toList());
        assertEquals(8, route.possibleClaimCards().size());
    }
}