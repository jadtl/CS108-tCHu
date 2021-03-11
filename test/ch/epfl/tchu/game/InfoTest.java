package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Route.Level;
import ch.epfl.tchu.gui.Info;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class InfoTest {
  @Test
  void cardNameWorks() {
    assertEquals("noires", Info.cardName(Card.BLACK, 2));
  }

  @Test
  void drawWorks() {
    String playerName = "Elon Musk";
    String opponentName = "Jeff Bezos";

    assertEquals("\nElon Musk et Jeff Bezos sont ex æqo avec 10 points !\n", 
    Info.draw(List.of(playerName, opponentName), 10));
  }

  @Test
  void claimedRouteWorks() {
    String playerName = "Elon Musk";
    Info info = new Info(playerName);
    Route route = new Route("1", new Station(0, "Lausanne"), new Station(1, "EPFL"), 3, Level.OVERGROUND, Color.BLACK);

    assertEquals("Elon Musk a pris possession de la route Lausanne  –  EPFL au moyen de 2 violettes et 3 locomotives.\n", info.claimedRoute(route, SortedBag.of(3, Card.LOCOMOTIVE, 2, Card.VIOLET)));
  }

  @Test
  void attemptsTunnelClaimWorks() {
    String playerName = "Elon Musk";
    Info info = new Info(playerName);
    Route route = new Route("1", new Station(0, "Lausanne"), new Station(1, "EPFL"), 3, Level.UNDERGROUND, Color.BLACK);

    assertEquals("Elon Musk tente de s'emparer du tunnel Lausanne  –  EPFL au moyen de 2 violettes et 3 locomotives !\n", info.attemptsTunnelClaim(route, SortedBag.of(3, Card.LOCOMOTIVE, 2, Card.VIOLET)));
  }

  @Test
  void drewAdditionalCardsWorks() {
    String playerName = "Elon Musk";
    Info info = new Info(playerName);

    assertEquals("Les cartes supplémentaires sont 2 violettes et 3 locomotives. Elles impliquent un coût additionnel de 2 cartes.\n", info.drewAdditionalCards(SortedBag.of(3, Card.LOCOMOTIVE, 2, Card.VIOLET), 2));
    assertEquals("Les cartes supplémentaires sont 2 violettes et 3 locomotives. Elles n'impliquent aucun coût additionnel.\n", info.drewAdditionalCards(SortedBag.of(3, Card.LOCOMOTIVE, 2, Card.VIOLET), 0));
  }

  @Test
  void didNotClaimRouteWorks() {
    String playerName = "Elon Musk";
    Info info = new Info(playerName);
    Route route = new Route("1", new Station(0, "Lausanne"), new Station(1, "EPFL"), 3, Level.UNDERGROUND, Color.BLACK);

    assertEquals("Elon Musk n'a pas pu (ou voulu) s'emparer de la route Lausanne  –  EPFL.\n", info.didNotClaimRoute(route));
  }

  @Test
  void getsLongestTrailBonusWorks() {
    String playerName = "Elon Musk";
    Info info = new Info(playerName);
    Route route = new Route("1", new Station(0, "Lausanne"), new Station(1, "EPFL"), 3, Level.UNDERGROUND, Color.BLACK);
    Trail longestTrail = Trail.longest(List.of(route));

    assertEquals("\nElon Musk reçoit un bonus de 10 points pour le plus long trajet (Lausanne - EPFL (3)).\n", info.getsLongestTrailBonus(longestTrail));
  }

  @Test
  void wonWorks() {
    String playerName = "Elon Musk";
    Info info = new Info(playerName);
    int points = 10; int loserPoints = 9;
    assertEquals("\nElon Musk remporte la victoire avec 10 points, contre 9 points !\n", info.won(points, loserPoints));
  }
}
