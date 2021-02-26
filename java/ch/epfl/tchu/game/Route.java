
package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Objects;

/**
 * Road that links 2 neighbor cities
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */

public final class Route {

	private final String id;
	private final Station station1;
	private final Station station2;
	private final int length;
	private final Level level;
	private final Color color;
	private final List<Station> stations;
	List<SortedBag<Card>> possibleClaimCards;

	public enum Level {

		UNDERGROUND, OVERGROUND
	}

	public Route(String id, Station station1, Station station2, int length, Level level, Color color) {

		Preconditions.checkArgument(length > Constants.MAX_ROUTE_LENGTH || station1.id() == station2.id());

		stations = new ArrayList<>();
		stations.addAll(List.of(station1, station2));

		this.length = length;
		this.color = color;
		this.station1 = Objects.requireNonNull(station1);
		this.station2 = Objects.requireNonNull(station2);
		this.id = Objects.requireNonNull(id);
		this.level = Objects.requireNonNull(level);

	}

	public String id() {

		return id;
	}

	public Station station1() {

		return station1;
	}

	public Station station2() {

		return station2;
	}

	public int length() {

		return length;
	}

	public Level level() {

		return level;
	}

	public Color color() {

		return color;
	}

	public List<Station> stations() {

		return stations;

	}

	public Station stationOpposite(Station station) {

		Preconditions.checkArgument(station.id() == station1.id() || station.id() == station2.id());

		return station.id() == station1.id() ? station2 : station1;

	}

	public List<SortedBag<Card>> possibleClaimCards() {

		possibleClaimCards = new ArrayList<>();

		SortedBag.Builder<Card> builder = new SortedBag.Builder<>();

		if (color() == null) {

			for (Color color : Color.values()) {
				for (int i = 0; i < length(); i++) {

					builder.add(Card.of(color));
				}

				possibleClaimCards.add(builder.build());
				builder = new SortedBag.Builder<>();
			}

		}

		else {

			for (int i = 0; i < length(); i++) {

				builder.add(Card.of(color()));
			}
			possibleClaimCards.add(builder.build());
			builder = new SortedBag.Builder<>();
		}

		if (level() == Level.UNDERGROUND) {

			for (int i = 0; i < length(); i++) {
				builder.add(Card.of(null));
			}
			possibleClaimCards.add(builder.build());

		}

		return possibleClaimCards;
	}

	public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {

		Preconditions.checkArgument(level() == Level.UNDERGROUND && drawnCards.size() == 3);
		int result = 0;

		for (Card card : drawnCards) {
			if (card.color() == null || card.color() == claimCards.get(0).color())
				++result;

		}
		return result;
	}
	
	public int claimPoints() {
		
		return Constants.ROUTE_CLAIM_POINTS.get(length());
	}

}
