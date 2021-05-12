package ch.epfl.tchu.game;

import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;

/**
 * A Public Card State with face-up cards, a deck and discards
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public class PublicCardState {
	private final List<Card> faceUpCards;
	private final int deckSize;
	private final int discardsSize;
	
	/**
	 * Constructs a Public Card State with its face-up cards, deck and discards
	 * 
	 * @param faceUpCards 
	 *        A list of face-up cards
	 * 
	 * @param deckSize 
	 *        The size of the deck
	 * 
	 * @param discardsSize 
	 *        The size of the discards
	 * 
	 * @throws IllegalArgumentException 
	 *         If the numbers of face-up cards, deck cards or discard cards are incorrect
	 */
	public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
		Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT && deckSize >= 0 && discardsSize >= 0);

		this.faceUpCards = List.copyOf(faceUpCards);
		this.deckSize = deckSize;
		this.discardsSize = discardsSize;
	}
	
	/**
	 * Returns the list of face-up cards
	 * 
	 * @return the list of face-up cards
	 */
	public List<Card> faceUpCards() { return faceUpCards; }
	
	/**
	 * Returns the face-up card of the given slot
	 * 
	 * @param slot 
	 *        The index in the face-up cards list
	 * 
	 * @return the face-up card of the given slot
	 * 
	 * @throws IndexOutOfBounds 
	 *         If slot is not within faceUpCards range
	 */
	public Card faceUpCard(int slot) {
		Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
		
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
