
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

	/**
	 * An enumeration of the 2 different levels used in the game
	 */

	public enum Level {

		UNDERGROUND, OVERGROUND
	}

	/**
	 * Constructs a route from the given stations, id's,levels and card colors
	 * 
	 * @param id is the id of a station
	 * @param station1 name of a station
	 * @param station2 name a station
	 * @param length length of a route
	 * @param level one of the parameters in the Level enumeration
	 * @param color color of a card
	 */

	public Route(String id, Station station1, Station station2, int length, Level level, Color color) {

		// checks that station1 differs from station2 and that the length of the route
		// is not bigger than the maximum possible length
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

	/**
	 * Returns the station unique identifier number
	 * @return the station unique identifier number
	 */

	public String id() {

		return id;
	}

	/**
	 * Returns the station name
	 * @return the station name
	 */

	public Station station1() {

		return station1;
	}

	/**
	 * Returns the station name 
	 * @return the station name
	 */

	public Station station2() {

		return station2;
	}

	/**
	 * Returns the route length 
	 * @return the route length
	 */

	public int length() {

		return length;
	}

	/**
	 * Returns the level the player is situated on
	 * @return the level the player is situated on
	 */

	public Level level() {

		return level;
	}

	/**
	 * Returns the color of a card
	 * @return the color of a card
	 */

	public Color color() {

		return color;
	}

	/**
	 * Returns a list of stations
	 * @return a list of stations
	 */

	public List<Station> stations() {

		return stations;

	}

	/**
	 * Returns a station different from the given one
	 * 
	 * @param station some train station
	 * @return station different from the given one
	 */

	public Station stationOpposite(Station station) {

		// checks that the given station is the first or second one of the route
		Preconditions.checkArgument(station.id() == station1.id() || station.id() == station2.id());

		return station.id() == station1.id() ? station2 : station1;

	}

	/**
	 * adds cards the player could use to play 
	 * adds cards of no color(locomotives)
	 * then colors in the same order they are listed in the class Color
	 * 
	 * @return list of these cards
	 */

	public List<SortedBag<Card>> possibleClaimCards() {

		possibleClaimCards = new ArrayList<>();

		SortedBag.Builder<Card> builder = new SortedBag.Builder<>();

		// first adds cards of no color
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

			// second adds cards of color
			for (int i = 0; i < length(); i++) {

				builder.add(Card.of(color()));
			}
			possibleClaimCards.add(builder.build());
			builder = new SortedBag.Builder<>();
		}

		// When situated underground only adds cards of no color
		if (level() == Level.UNDERGROUND) {

			for (int i = 0; i < length(); i++) {
				builder.add(Card.of(null));
			}
			possibleClaimCards.add(builder.build());

		}

		return possibleClaimCards;
	}

	/**
	 * computes the number the number of additional cards needed to take possession
	 * of a tunnel
	 * 
	 * @param claimCards cards initially put down by the player
	 * @param drwanCards the 3 cards taken from the top of claimCards
	 * @return number of additional cards needed to take possession of a tunnel
	 */

	public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {

		// checks we are in a tunnel and there have been 3 cards drawn from claimCards
		Preconditions.checkArgument(level() == Level.UNDERGROUND && drawnCards.size() == 3);
		int result = 0;

		// adds those to the number of cards necessary to take a tunnel
		for (Card card : drawnCards) {
			if (card.color() == null || card.color() == claimCards.get(0).color())
				++result;

		}
		return result;
	}

	/**
	 * returns the number of points a player gets when taking possession of a
	 * certain route
	 * 
	 * @return the number of points a player gets when taking possession of a certain route
	 */

	public int claimPoints() {

		return Constants.ROUTE_CLAIM_POINTS.get(length());
	}

}
