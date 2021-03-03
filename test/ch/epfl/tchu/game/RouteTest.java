package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
    void possibleClaimCardsWorksWithTrivialRoutes() {
        Route route1 = new Route("0", new Station(0, "Lausanne"), new Station(1, "Zürich"),
                2, Route.Level.UNDERGROUND, Color.RED);

        assertEquals(new ArrayList<>(List.of(Card.RED, Card.RED)), route1.possibleClaimCards().get(0).toList());
        assertEquals(new ArrayList<>(List.of(Card.RED, Card.LOCOMOTIVE)), route1.possibleClaimCards().get(1).toList());
        assertEquals(new ArrayList<>(List.of(Card.LOCOMOTIVE, Card.LOCOMOTIVE)), route1.possibleClaimCards().get(2).toList());
        assertEquals(3, route1.possibleClaimCards.size());

    }
}