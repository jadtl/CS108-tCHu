package ch.epfl.tchu.game;

import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;

/**
 * A overground or underground route that links 2 neighbor cities
 * with a length and a color
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public class PublicCardState {
	private final List<Card> faceUpCards;
	private final int deckSize;
	private final int discardsSize;
	
	/**
	 * 
	 * @param faceUpCards
	 * @param deckSize
	 * @param discardsSize
	 */
	public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
		Preconditions.checkArgument(faceUpCards.size() != Constants.FACE_UP_CARDS_COUNT & deckSize <0 & discardsSize<0);

		this.faceUpCards = faceUpCards;
		this.deckSize = deckSize;
		this.discardsSize = discardsSize;
	}
	
	/**
	 * 
	 * @return
	 */
	public int totalSize() { return faceUpCards.size() + deckSize + discardsSize; }
	
	/**
	 * 
	 * @return
	 */
	public List<Card> faceUpCards() { return faceUpCards; }
	
	/**
	 * 
	 * @param slot
	 * 
	 * @return
	 */
	public Card faceUpCard(int slot) {
		Objects.checkIndex(slot,Constants.FACE_UP_CARDS_COUNT );
		
		return faceUpCards.get(slot);
	}		
	
	/**
	 * Returns the size of the deck
	 * 
	 * @return the size of the deck
	 */
	public int deckSize() { return deckSize; }
	
	/**
	 * Returns true if the deck is empty, false otherwise
	 * 
	 * @return true iff. the deck is empty
	 */
	public boolean isDeckEmpty() { return deckSize == 0; }
	
	/**
	 * Returns the size of the discarded cards pile
	 * 
	 * @return the size of the discarded cards pile
	 */
	public int discardsSize() { return discardsSize; }

}
