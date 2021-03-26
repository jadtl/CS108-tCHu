package ch.epfl.tchu.game;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ch.epfl.tchu.SortedBag;

/**
 * The public part of a tCHu game state
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public interface Player {

	/**
	 * The 3 possible actions a player can perform when his/her turn starts
	 */
	enum TurnKind {
		DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE;

		public static final List<TurnKind> ALL = Arrays.asList(TurnKind.values());
	}

	/**
	 * Tells the player his id and the identity of the other players
	 * 
	 * @param ownId      
	 *        Id of the player
	 * 
	 * @param playerNames 
	 *        Names of the different players
	 */
	public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

	/**
	 * Communicates information to the player
	 * 
	 * @param info 
	 *        Information given to the player
	 */

	void receiveInfo(String info);

	/**
	 * Informs the player when the Game State has changed
	 * 
	 * @param newState 
	 *        New PublicGameState
	 * 
	 * @param info     
	 *        PlayerState of the player
	 */

	void updateState(PublicGameState newState, PlayerState ownState);

	/**
	 * The tickets a player gets at the beginning of the game
	 * 
	 * @param tickets 
	 *        SortedBag of initial tickets
	 */

	void setInitialTicketChoice(SortedBag<Ticket> tickets);

	/**
	 * Asks the player to choose what tickets he/she wants to keep
	 * 
	 */

	SortedBag<Ticket> chooseInitialTickets();

	/**
	 * Asks the player at the beginning of his/her turn what action out of the Enum
	 * TurnKind he wants to perform
	 * 
	 */

	TurnKind nextTurn();

	/**
	 * Informs the player of the drawn tickets Asks him to choose which ones he
	 * wants to keep
	 * 
	 * @param options 
	 *        Additional tickets the player has drawn
	 */

	SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

	/**
	 * When a player has decided to draw wagon/locomotive cards Asks player where
	 * he/she wants to draw the card from
	 * 
	 * @return a value between 0 and 4 if f from a place containing a face up Card
	 * or Constants.DECK_SLOT if the cards are drawn from the deck
	 */

	int drawSlot();

	/**
	 * States Route the player has tried to Claim
	 */
	Route claimedRoute();

	/**
	 * States Cards a player has used to try to claim a route
	 */
	SortedBag<Card> initialClaimCards();

	/**
	 * States the additional cards a player can use to claim a Tunnel When a player
	 * has tried to claim a tunnel and additional cards are necessary If options is
	 * empty the cannot or doesn't want to use additional cards
	 * 
	 * @param options Additional tickets the player could use to claim a tunnel
	 */
	SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);
}
