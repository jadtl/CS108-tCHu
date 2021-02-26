package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrailTest {
    @Test
    void longestWorksWithTrivialRoutesList() {
        List<Route> routes = new ArrayList<>();

        Station lausanne = new Station(0, "Lausanne");
        Station geneve = new Station(1, "Genève");
        Station zurich = new Station(2, "Zürich");
        Station fribourg = new Station(3, "Fribourg");
        Station sion = new Station(4, "Sion");
        Station neuchatel = new Station(5, "Neuchâtel");

        routes.add(new Route("0", lausanne, zurich, 4, Route.Level.OVERGROUND, Color.BLACK ));
        routes.add(new Route("1", geneve, sion, 3, Route.Level.OVERGROUND, Color.BLUE));
        routes.add(new Route("3", zurich, neuchatel, 5, Route.Level.OVERGROUND, Color.RED));
        routes.add(new Route("4", neuchatel, fribourg, 5, Route.Level.OVERGROUND, Color.RED));

        assertEquals("Lausanne - Fribourg (14)", Trail.longest(routes).toString());
    }

    @Test
    void longestWorksWithEmptyRouteList() {
        List<Route> routes = new ArrayList<>();
        assertEquals(null, Trail.longest(routes).station1());
        assertEquals(null, Trail.longest(routes).station2());
        assertEquals(0, Trail.longest(routes).length());
    }
}