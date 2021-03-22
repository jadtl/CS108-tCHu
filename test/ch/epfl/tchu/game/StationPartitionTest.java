package ch.epfl.tchu.game;

import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StationPartitionTest {
    @Test
    void stationPartitionInitiallyConnectsStationsWithThemselvesOnly() {
        var stations = new ChMap().ALL_STATIONS;

        var partition = new StationPartition.Builder(stations.size())
                .build();
        for (var s1 : stations) {
            for (var s2 : stations) {
                var same = s1.equals(s2);
                assertEquals(same, partition.connected(s1, s2));
            }
        }
    }

    @Test
    void stationPartitionBuilderConnectIsIdempotent() {
        var stations = new ChMap().ALL_STATIONS;
        var s0 = stations.get(0);
        var s1 = stations.get(1);
        var partition = new StationPartition.Builder(stations.size())
                .connect(s0, s0)
                .connect(s1, s1)
                .connect(s0, s1)
                .connect(s1, s0)
                .build();

        assertTrue(partition.connected(s0, s0));
        assertTrue(partition.connected(s1, s1));
        assertTrue(partition.connected(s0, s1));
        assertTrue(partition.connected(s1, s0));
    }

    @Test
    void stationPartitionWorksOnGivenExample() {
        var stations = reducedChStations();
        var partition = new StationPartition.Builder(stations.size())
                .connect(stations.get(5), stations.get(2))  // Lausanne - Fribourg
                .connect(stations.get(0), stations.get(3))  // Berne - Interlaken
                .connect(stations.get(0), stations.get(2))  // Berne - Fribourg
                .connect(stations.get(7), stations.get(10)) // Neuchâtel - Soleure
                .connect(stations.get(10), stations.get(8)) // Soleure - Olten
                .connect(stations.get(6), stations.get(13)) // Lucerne - Zoug
                .connect(stations.get(13), stations.get(9)) // Zoug - Schwyz
                .connect(stations.get(9), stations.get(6))  // Schwyz - Lucerne
                .connect(stations.get(9), stations.get(11)) // Schwyz - Wassen
                .build();

        assertTrue(partition.connected(stations.get(5), stations.get(3)));   // Lausanne - Interlaken
        assertTrue(partition.connected(stations.get(6), stations.get(11)));  // Lucerne - Wassen
        assertTrue(partition.connected(stations.get(13), stations.get(11))); // Zoug - Wassen
        assertTrue(partition.connected(stations.get(9), stations.get(11)));  // Schwyz - Wassen

        assertFalse(partition.connected(stations.get(0), stations.get(6)));  // Berne - Lucerne
    }

    @Test
    void stationPartitionWorksOnKnownExample1() {
        var chMap = new ChMap();

        var routes = Arrays.asList(
                chMap.BRI_LOC_1, chMap.BRI_SIO_1, chMap.MAR_SIO_1, chMap.LAU_MAR_1,
                chMap.GEN_LAU_1, chMap.GEN_YVE_1, chMap.LCF_YVE_1, chMap.DEL_LCF_1,
                chMap.DEL_SOL_1, chMap.OLT_SOL_1, chMap.BAL_OLT_1, chMap.BER_LUC_1,
                chMap.SCE_WIN_1);
        var maxId = routes.stream()
                .flatMap(r -> r.stations().stream())
                .mapToInt(Station::id)
                .max()
                .orElse(0);

        Random rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            Collections.shuffle(routes, rng);
            var pb = new StationPartition.Builder(maxId + 1);
            routes.forEach(r -> pb.connect(r.station1(), r.station2()));
            var p = pb.build();

            assertTrue(p.connected(chMap.LOC, chMap.BAL));
            assertTrue(p.connected(chMap.BER, chMap.LUC));
            assertFalse(p.connected(chMap.BER, chMap.SOL));
            assertFalse(p.connected(chMap.LAU, chMap.LUC));
            assertFalse(p.connected(chMap.ZUR, chMap.KRE));
            assertTrue(p.connected(chMap.ZUR, chMap.ZUR));
        }
    }

    @Test
    void stationPartitionWorksOnKnownExample2() {
        var chMap = new ChMap();

        var routes = Arrays.asList(
                chMap.DE2_SCE_1, chMap.SCE_WIN_2, chMap.WIN_ZUR_1, chMap.ZOU_ZUR_1,
                chMap.SCZ_ZOU_1, chMap.SCZ_WAS_1, chMap.BEL_WAS_1, chMap.BEL_LUG_1,
                chMap.COI_WAS_1, chMap.COI_SAR_1, chMap.SAR_VAD_1, chMap.AT2_VAD_1,
                chMap.BRU_COI_1, chMap.BRU_IT2_1);
        var maxId = routes.stream()
                .flatMap(r -> r.stations().stream())
                .mapToInt(Station::id)
                .max()
                .orElse(0);

        Random rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            Collections.shuffle(routes, rng);
            var pb = new StationPartition.Builder(maxId + 1);
            routes.forEach(r -> pb.connect(r.station1(), r.station2()));
            var p = pb.build();

            assertTrue(p.connected(chMap.LUG, chMap.DE2));
            assertTrue(p.connected(chMap.AT2, chMap.IT2));
            assertFalse(p.connected(chMap.ZUR, chMap.AT1));
            assertFalse(p.connected(chMap.ZUR, chMap.AT1));
        }
    }

    @Test
    void stationPartitionWorksOnKnownExample3() {
        var chMap = new ChMap();

        var routes = Arrays.asList(
                chMap.DE4_KRE_1, chMap.KRE_WIN_1, chMap.WIN_ZUR_2, chMap.BAD_ZUR_1,
                chMap.ZOU_ZUR_2, chMap.LUC_ZOU_2, chMap.INT_LUC_1, chMap.BRI_INT_1,
                chMap.BER_INT_1, chMap.BER_FRI_1, chMap.BER_NEU_1, chMap.LCF_NEU_1,
                chMap.FR3_LCF_1, chMap.BER_SOL_1, chMap.BAL_DEL_1, chMap.BAL_DE1_1);
        var maxId = routes.stream()
                .flatMap(r -> r.stations().stream())
                .mapToInt(Station::id)
                .max()
                .orElse(0);

        Random rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            Collections.shuffle(routes, rng);
            var pb = new StationPartition.Builder(maxId + 1);
            routes.forEach(r -> pb.connect(r.station1(), r.station2()));
            var p = pb.build();

            assertTrue(p.connected(chMap.BRI, chMap.FR3));
            assertTrue(p.connected(chMap.DE4, chMap.FR3));
            assertTrue(p.connected(chMap.BRI, chMap.DE4));
            assertTrue(p.connected(chMap.BAD, chMap.SOL));
            assertTrue(p.connected(chMap.BAL, chMap.DE1));
            assertFalse(p.connected(chMap.BAL, chMap.SOL));
        }
    }

    private static List<Station> reducedChStations() {
        return List.of(
                new Station(0, "Berne"),
                new Station(1, "Delémont"),
                new Station(2, "Fribourg"),
                new Station(3, "Interlaken"),
                new Station(4, "La Chaux-de-Fonds"),
                new Station(5, "Lausanne"),
                new Station(6, "Lucerne"),
                new Station(7, "Neuchâtel"),
                new Station(8, "Olten"),
                new Station(9, "Schwyz"),
                new Station(10, "Soleure"),
                new Station(11, "Wassen"),
                new Station(12, "Yverdon"),
                new Station(13, "Zoug"),
                new Station(14, "Zürich"));

    }

    private static final class ChMap {
        //region Stations
        final Station BAD = new Station(0, "Baden");
        final Station BAL = new Station(1, "Bâle");
        final Station BEL = new Station(2, "Bellinzone");
        final Station BER = new Station(3, "Berne");
        final Station BRI = new Station(4, "Brigue");
        final Station BRU = new Station(5, "Brusio");
        final Station COI = new Station(6, "Coire");
        final Station DAV = new Station(7, "Davos");
        final Station DEL = new Station(8, "Delémont");
        final Station FRI = new Station(9, "Fribourg");
        final Station GEN = new Station(10, "Genève");
        final Station INT = new Station(11, "Interlaken");
        final Station KRE = new Station(12, "Kreuzlingen");
        final Station LAU = new Station(13, "Lausanne");
        final Station LCF = new Station(14, "La Chaux-de-Fonds");
        final Station LOC = new Station(15, "Locarno");
        final Station LUC = new Station(16, "Lucerne");
        final Station LUG = new Station(17, "Lugano");
        final Station MAR = new Station(18, "Martigny");
        final Station NEU = new Station(19, "Neuchâtel");
        final Station OLT = new Station(20, "Olten");
        final Station PFA = new Station(21, "Pfäffikon");
        final Station SAR = new Station(22, "Sargans");
        final Station SCE = new Station(23, "Schaffhouse");
        final Station SCZ = new Station(24, "Schwyz");
        final Station SIO = new Station(25, "Sion");
        final Station SOL = new Station(26, "Soleure");
        final Station STG = new Station(27, "Saint-Gall");
        final Station VAD = new Station(28, "Vaduz");
        final Station WAS = new Station(29, "Wassen");
        final Station WIN = new Station(30, "Winterthour");
        final Station YVE = new Station(31, "Yverdon");
        final Station ZOU = new Station(32, "Zoug");
        final Station ZUR = new Station(33, "Zürich");

        final Station DE1 = new Station(34, "Allemagne");
        final Station DE2 = new Station(35, "Allemagne");
        final Station DE3 = new Station(36, "Allemagne");
        final Station DE4 = new Station(37, "Allemagne");
        final Station DE5 = new Station(38, "Allemagne");
        final Station AT1 = new Station(39, "Autriche");
        final Station AT2 = new Station(40, "Autriche");
        final Station AT3 = new Station(41, "Autriche");
        final Station IT1 = new Station(42, "Italie");
        final Station IT2 = new Station(43, "Italie");
        final Station IT3 = new Station(44, "Italie");
        final Station IT4 = new Station(45, "Italie");
        final Station IT5 = new Station(46, "Italie");
        final Station FR1 = new Station(47, "France");
        final Station FR2 = new Station(48, "France");
        final Station FR3 = new Station(49, "France");
        final Station FR4 = new Station(50, "France");

        final List<Station> ALL_STATIONS = List.of(
                BAD, BAL, BEL, BER, BRI, BRU, COI, DAV, DEL, FRI, GEN, INT, KRE, LAU, LCF, LOC, LUC,
                LUG, MAR, NEU, OLT, PFA, SAR, SCE, SCZ, SIO, SOL, STG, VAD, WAS, WIN, YVE, ZOU, ZUR,
                DE1, DE2, DE3, DE4, DE5, AT1, AT2, AT3, IT1, IT2, IT3, IT4, IT5, FR1, FR2, FR3, FR4);
        //endregion

        final Route AT2_VAD_1 = new Route("AT2_VAD_1", AT2, VAD, 1, Route.Level.UNDERGROUND, Color.RED);
       
        final Route BAD_ZUR_1 = new Route("BAD_ZUR_1", BAD, ZUR, 1, Route.Level.OVERGROUND, Color.YELLOW);
        final Route BAL_DE1_1 = new Route("BAL_DE1_1", BAL, DE1, 1, Route.Level.UNDERGROUND, Color.BLUE);
        final Route BAL_DEL_1 = new Route("BAL_DEL_1", BAL, DEL, 2, Route.Level.UNDERGROUND, Color.YELLOW);
        final Route BAL_OLT_1 = new Route("BAL_OLT_1", BAL, OLT, 2, Route.Level.UNDERGROUND, Color.ORANGE);

        final Route BEL_LUG_1 = new Route("BEL_LUG_1", BEL, LUG, 1, Route.Level.UNDERGROUND, Color.RED);

        final Route BEL_WAS_1 = new Route("BEL_WAS_1", BEL, WAS, 4, Route.Level.UNDERGROUND, null);

        final Route BER_FRI_1 = new Route("BER_FRI_1", BER, FRI, 1, Route.Level.OVERGROUND, Color.ORANGE);

        final Route BER_INT_1 = new Route("BER_INT_1", BER, INT, 3, Route.Level.OVERGROUND, Color.BLUE);
        final Route BER_LUC_1 = new Route("BER_LUC_1", BER, LUC, 4, Route.Level.OVERGROUND, null);

        final Route BER_NEU_1 = new Route("BER_NEU_1", BER, NEU, 2, Route.Level.OVERGROUND, Color.RED);
        final Route BER_SOL_1 = new Route("BER_SOL_1", BER, SOL, 2, Route.Level.OVERGROUND, Color.BLACK);
        final Route BRI_INT_1 = new Route("BRI_INT_1", BRI, INT, 2, Route.Level.UNDERGROUND, Color.WHITE);

        final Route BRI_LOC_1 = new Route("BRI_LOC_1", BRI, LOC, 6, Route.Level.UNDERGROUND, null);
        final Route BRI_SIO_1 = new Route("BRI_SIO_1", BRI, SIO, 3, Route.Level.UNDERGROUND, Color.BLACK);

        final Route BRU_COI_1 = new Route("BRU_COI_1", BRU, COI, 5, Route.Level.UNDERGROUND, null);

        final Route BRU_IT2_1 = new Route("BRU_IT2_1", BRU, IT2, 2, Route.Level.UNDERGROUND, Color.GREEN);

        final Route COI_SAR_1 = new Route("COI_SAR_1", COI, SAR, 1, Route.Level.UNDERGROUND, Color.WHITE);
        final Route COI_WAS_1 = new Route("COI_WAS_1", COI, WAS, 5, Route.Level.UNDERGROUND, null);



        final Route DE2_SCE_1 = new Route("DE2_SCE_1", DE2, SCE, 1, Route.Level.OVERGROUND, Color.YELLOW);

        final Route DE4_KRE_1 = new Route("DE4_KRE_1", DE4, KRE, 1, Route.Level.OVERGROUND, Color.WHITE);


        final Route DEL_LCF_1 = new Route("DEL_LCF_1", DEL, LCF, 3, Route.Level.UNDERGROUND, Color.WHITE);
        final Route DEL_SOL_1 = new Route("DEL_SOL_1", DEL, SOL, 1, Route.Level.UNDERGROUND, Color.VIOLET);


        final Route FR3_LCF_1 = new Route("FR3_LCF_1", FR3, LCF, 2, Route.Level.UNDERGROUND, Color.GREEN);


        final Route GEN_LAU_1 = new Route("GEN_LAU_1", GEN, LAU, 4, Route.Level.OVERGROUND, Color.BLUE);

        final Route GEN_YVE_1 = new Route("GEN_YVE_1", GEN, YVE, 6, Route.Level.OVERGROUND, null);
        final Route INT_LUC_1 = new Route("INT_LUC_1", INT, LUC, 4, Route.Level.OVERGROUND, Color.VIOLET);




        final Route KRE_WIN_1 = new Route("KRE_WIN_1", KRE, WIN, 2, Route.Level.OVERGROUND, Color.YELLOW);
        final Route LAU_MAR_1 = new Route("LAU_MAR_1", LAU, MAR, 4, Route.Level.UNDERGROUND, Color.ORANGE);

        final Route LCF_NEU_1 = new Route("LCF_NEU_1", LCF, NEU, 1, Route.Level.UNDERGROUND, Color.ORANGE);
        final Route LCF_YVE_1 = new Route("LCF_YVE_1", LCF, YVE, 3, Route.Level.UNDERGROUND, Color.YELLOW);



        final Route LUC_ZOU_2 = new Route("LUC_ZOU_2", LUC, ZOU, 1, Route.Level.OVERGROUND, Color.YELLOW);
        final Route MAR_SIO_1 = new Route("MAR_SIO_1", MAR, SIO, 2, Route.Level.UNDERGROUND, Color.GREEN);


        final Route OLT_SOL_1 = new Route("OLT_SOL_1", OLT, SOL, 1, Route.Level.OVERGROUND, Color.BLUE);





        final Route SAR_VAD_1 = new Route("SAR_VAD_1", SAR, VAD, 1, Route.Level.UNDERGROUND, Color.ORANGE);
        final Route SCE_WIN_1 = new Route("SCE_WIN_1", SCE, WIN, 1, Route.Level.OVERGROUND, Color.BLACK);
        final Route SCE_WIN_2 = new Route("SCE_WIN_2", SCE, WIN, 1, Route.Level.OVERGROUND, Color.WHITE);

        final Route SCZ_WAS_1 = new Route("SCZ_WAS_1", SCZ, WAS, 2, Route.Level.UNDERGROUND, Color.GREEN);

        final Route SCZ_ZOU_1 = new Route("SCZ_ZOU_1", SCZ, ZOU, 1, Route.Level.OVERGROUND, Color.BLACK);




        final Route WIN_ZUR_1 = new Route("WIN_ZUR_1", WIN, ZUR, 1, Route.Level.OVERGROUND, Color.BLUE);
        final Route WIN_ZUR_2 = new Route("WIN_ZUR_2", WIN, ZUR, 1, Route.Level.OVERGROUND, Color.VIOLET);
        final Route ZOU_ZUR_1 = new Route("ZOU_ZUR_1", ZOU, ZUR, 1, Route.Level.OVERGROUND, Color.GREEN);
        final Route ZOU_ZUR_2 = new Route("ZOU_ZUR_2", ZOU, ZUR, 1, Route.Level.OVERGROUND, Color.RED);

        

        //endregion
    }
}
