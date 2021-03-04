package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrailTest {
    @Test
    void longestWorksWithTrivialRoutesList() {
        List<Route> routes = new ArrayList<>();

        Station LAU = new Station(0, "Lausanne");
        Station GVA = new Station(1, "Genève");
        Station ZUR = new Station(2, "Zürich");
        Station FRI = new Station(3, "Fribourg");
        Station SIO = new Station(4, "Sion");
        Station NEU = new Station(5, "Neuchâtel");

        routes.add(new Route("0", LAU, ZUR, 1, Route.Level.OVERGROUND, Color.BLACK ));
        routes.add(new Route("1", GVA, SIO, 4, Route.Level.OVERGROUND, Color.BLUE));
        routes.add(new Route("3", ZUR, NEU, 1, Route.Level.OVERGROUND, Color.RED));
        routes.add(new Route("4", NEU, FRI, 1, Route.Level.OVERGROUND, Color.RED));

        assertEquals("Genève - Sion (4)", Trail.longest(routes).toString());
    }

    @Test
    void longestWorksWithEmptyRouteList() {
        List<Route> routes = new ArrayList<>();

        assertEquals(null, Trail.longest(routes).station1());
        assertEquals(null, Trail.longest(routes).station2());
        assertEquals(0, Trail.longest(routes).length());
    }
}