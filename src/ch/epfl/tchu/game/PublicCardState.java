package ch.epfl.tchu.game;


import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;

public class PublicCardState{
	
	private final List<Card> faceUpCards;
	private final int deckSize;
	private final int discardsSize;
	
	public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
		
		Preconditions.checkArgument(faceUpCards.size() != Constants.FACE_UP_CARDS_COUNT & deckSize <0 & discardsSize<0);
		this.faceUpCards = faceUpCards;
		this.deckSize = deckSize;
		this.discardsSize = discardsSize;
		
	}
	
	public int totalSize() {
		
		return faceUpCards.size() + deckSize + discardsSize; 
	}
	
	public List<Card> faceUpCards(){
		
		return faceUpCards;
	}
	
	public Card faceUpCard(int slot) {

		Objects.checkIndex(slot,Constants.FACE_UP_CARDS_COUNT );
		return faceUpCards.get(slot);
	}		
		
	public int deckSize() {
		
		return deckSize;
	}
	
	public boolean isDeckEmpty() {
		
		return deckSize == 0;
	}
	
	public int discardsSize() {
		
		return discardsSize;
	 }

}
