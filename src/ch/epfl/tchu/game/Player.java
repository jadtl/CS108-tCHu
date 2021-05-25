package ch.epfl.tchu.game;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ch.epfl.tchu.SortedBag;

/**
 * The player of a tCHu game
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public interface Player {
    /**
     * The different kind of actions the player can do each turn
     */
    enum TurnKind {
        DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE;

        public static final List<TurnKind> ALL = Arrays.asList(TurnKind.values());
    }

    /**
     * Tells the player their identifier and the names of the other players
     *
     * @param ownId       The {@link PlayerId} of the player
     * @param playerNames The names of the different players
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * Communicates information to the player
     *
     * @param info The information to communicate
     */
    void receiveInfo(String info);

    /**
     * Informs the player when the game state has changed
     *
     * @param newState The updated {@link PublicGameState}
     * @param ownState The updated {@link PlayerState} of the concerned player
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Informs the player of the initial tickets drawn
     *
     * @param tickets The initial {@link SortedBag} of {@link Ticket}
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Asks the player what initial tickets they want to keep
     *
     * @return The initial {@link SortedBag} of {@link Ticket} the player chose to keep
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * Asks the player what action they choose to do for their next turn
     *
     * @return The {@link TurnKind} the player chose
     */
    TurnKind nextTurn();

    /**
     * Informs the player of the drawn tickets and asks them to choose which ones they want to keep
     *
     * @param options The additional {@link SortedBag} of {@link Ticket} the player has drawn
     * @return The {@link SortedBag} of {@link Ticket} the player chose to keep
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * States the card slot the player chose to draw a card from
     *
     * @return Between 0 and 4 for a face-up card or -1 for the top deck card
     */
    int drawSlot();

    /**
     * States the route the player has claimed
     *
     * @return The claimed {@link Route}
     */
    Route claimedRoute();

    /**
     * States cards a player has used to try to claim a route
     *
     * @return The {@link SortedBag} of {@link Card} initially used to claim a {@link Route}
     */
    SortedBag<Card> initialClaimCards();

    /**
     * States the additional claim cards choice of the player to claim the tunnel
     *
     * @param options The {@link List} of {@link SortedBag} of additional {@link Card} the player can choose to claim the tunnel
     * @return The additional claim cards the player chooses to claim the tunnel
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);
}
