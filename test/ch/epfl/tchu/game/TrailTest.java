package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrailTest {
    @Test
    void longestWorksWithTrivialRoutesList() {

        assertEquals("Lausanne - Fribourg (14)", Trail.longest(ChMap.routes()).toString());
    }

    @Test
    void longestWorksWithEmptyRouteList() {
        List<Route> routes = new ArrayList<>();

        assertEquals(null, Trail.longest(routes).station1());
        assertEquals(null, Trail.longest(routes).station2());
        assertEquals(0, Trail.longest(routes).length());
    }
}