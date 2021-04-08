package ch.epfl.tchu.game;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.Test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;

public class GameTest {
  @Test
  public void gameWorks() {
    long seedPlayer1 = 30493401;
    long seedPlayer2 = 32492049;

    PlayerTest player1 = new PlayerTest(seedPlayer1, ChMap.routes());
    PlayerTest player2 = new PlayerTest(seedPlayer2, ChMap.routes());

    Game.play(Map.of(PlayerId.PLAYER_1, player1, PlayerId.PLAYER_2, player2), Map.of(PlayerId.PLAYER_1, "Sofiya", 
    PlayerId.PLAYER_2, "Jad"), SortedBag.of(ChMap.tickets()), new Random(TestRandomizer.SEED));
  }
  
  private static final class PlayerTest implements Player {
    private static final int TURN_LIMIT = 1000;

    private final Random rng;
    // Toutes les routes de la carte
    private final List<Route> allRoutes;

    private int turnCounter;
    private PlayerState ownState;
    //private PublicGameState gameState;

    // Lorsque nextTurn retourne CLAIM_ROUTE
    private Route routeToClaim;
    private SortedBag<Card> initialClaimCards;

    private PlayerId ownId;
    //private String name;

    private SortedBag<Ticket> initialTickets;

    public PlayerTest(long randomSeed, List<Route> allRoutes) {
        this.rng = new Random(randomSeed);
        this.allRoutes = List.copyOf(allRoutes);
        this.turnCounter = 0;
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        //this.gameState = newState;
        this.ownState = ownState;
    }

    @Override
    public TurnKind nextTurn() {
        turnCounter += 1;
        if (turnCounter > TURN_LIMIT)
            throw new Error("Trop de tours joués !");

        // Détermine les routes dont ce joueur peut s'emparer
        List<Route> claimableRoutes = allRoutes.stream().filter((Route route) -> ownState.canClaimRoute(route)).collect(Collectors.toList());
        if (claimableRoutes.isEmpty()) {
            return TurnKind.DRAW_CARDS;
        } else {
            int routeIndex = rng.nextInt(claimableRoutes.size());
            Route route = claimableRoutes.get(routeIndex);
            List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

            routeToClaim = route;
            initialClaimCards = cards.get(0);
            return TurnKind.CLAIM_ROUTE;
        }
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
      this.ownId = ownId;
      //this.name = playerNames.get(ownId);
    }

    @Override
    public void receiveInfo(String info) {
      if (ownId == PlayerId.PLAYER_1) System.out.println(info);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
      this.initialTickets = tickets;
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
      return initialTickets.subsetsOfSize(rng.nextInt(initialTickets.size())).stream().collect(Collectors.toList()).get(0);
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
      return options.subsetsOfSize(options.size() - 2).stream().collect(Collectors.toList()).get(0);
    }

    @Override
    public int drawSlot() {
      return new Random().nextInt(6) - 1;
    }

    @Override
    public Route claimedRoute() {
      return routeToClaim;
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
      return initialClaimCards;
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
      return options.get(0);
    }
}
}
