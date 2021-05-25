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
     * @param ownId       The identifier of the player
     * @param playerNames The names of the different players
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * Communicates information to the player
     *
     * @param info The information
     */
    void receiveInfo(String info);

    /**
     * Informs the player when the Game State has changed
     *
     * @param newState The public game's state
     * @param ownState The player's state
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Informs the player of the initial tickets drawn
     *
     * @param tickets The initial tickets
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Asks the player what initial tickets they want to keep
     *
     * @return the initial tickets the player chose to keep
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * Asks the player what action they choose to do for their next turn
     *
     * @return the turn kind the player chose
     */
    TurnKind nextTurn();

    /**
     * Informs the player of the drawn tickets and asks them to choose which ones they
     * want to keep
     *
     * @param options The additional tickets the player has drawn
     * @return the tickets the player chose to keep
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * States the card slot the player chose to draw a card from
     *
     * @return a value between 0 and 4 if they draw a face-up card
     * or -1 if the card is drawn from the deck
     */
    int drawSlot();

    /**
     * States the route the player has claimed
     *
     * @return the claimed route
     */
    Route claimedRoute();

    /**
     * States cards a player has used to try to claim a route
     *
     * @return the cards initially used to claim a route
     */
    SortedBag<Card> initialClaimCards();

    /**
     * States the additional claim cards choice of the player to claim the tunnel
     *
     * @param options The additional tickets the player could use to claim a tunnel
     * @return the additional claim cards the player chooses to claim the tunnel
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);
}
