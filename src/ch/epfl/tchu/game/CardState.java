package ch.epfl.tchu.game;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

public final class CardState extends PublicCardState {

	private final Deck<Card> deckCards;
	private final SortedBag<Card> discardedCards;

	private CardState(List<Card> faceUpCards, Deck<Card> deckCards, SortedBag<Card> discardedCards) {

		super(faceUpCards, deckCards.size(), discardedCards.size());
		this.deckCards = deckCards;
		this.discardedCards = discardedCards;

	}

	public static CardState of(Deck<Card> deck) {

		Preconditions.checkArgument(deck.size() >= 5);

		return new CardState(deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList(),
				deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT), new SortedBag.Builder<Card>().build());

	}

	CardState withDrawnFaceUpCard(int slot) {

		Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);

		SortedBag<Card> updatedDiscardedCards = new SortedBag.Builder<Card>().add(discardedCards)
				.add(faceUpCards().get(slot)).build();
		List<Card> faceUpCards = List.copyOf(faceUpCards());
		faceUpCards.set(slot, topDeckCard());

		return new CardState(faceUpCards, deckCards.withoutTopCard(), updatedDiscardedCards);

	}

	public Card topDeckCard() {

		return deckCards.topCard();
	}

	public CardState withoutTopDeckCard() {

		return new CardState(faceUpCards(), deckCards.withoutTopCard(), discardedCards);
	}

	public CardState withDeckRecreatedFromDiscards(Random rng) {

		Preconditions.checkArgument(deckCards.size() == 0);
		return new CardState(faceUpCards(), Deck.of(discardedCards, rng), new SortedBag.Builder<Card>().build());
	}

	public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {

		return new CardState(faceUpCards(), deckCards,
				new SortedBag.Builder<Card>().add(discardedCards).add(additionalDiscards).build());

	}

}
