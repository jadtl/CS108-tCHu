
package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A overground or underground route that links 2 neighbor cities
 * with a length and a color
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
    private List<SortedBag<Card>> possibleClaimCards;

    /**
     * The two levels a route can be on
     */
    public enum Level { UNDERGROUND, OVERGROUND }

    /**
     * Constructs a route with its identifier, stations, length, level and colors
     *
     * @param id 
     *        The identifier
     * 
     * @param station1 
     *        The first station
     * 
     * @param station2 
     *        The second station
     * 
     * @param length 
     *        The route length
     * 
     * @param level 
     *        The route level
     * 
     * @param color 
     *        The route color
     *
     * @throws IllegalArgumentException 
     *         If both stations are equal or if the length is incorrect
     * 
     * @throws NullPointerException 
     *         If the identifier, the stations or the level are null
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(length <= Constants.MAX_ROUTE_LENGTH
                && length >= Constants.MIN_ROUTE_LENGTH && !station1.equals(station2));

        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.length = length;
        this.level = Objects.requireNonNull(level);
        this.color = color;

        computePossibleClaimCards();
    }

    /**
     * Returns the identifier of the route
     *
     * @return the identifier of the route
     */
    public String id() { return id; }

    /**
     * Returns the first station of the route
     *
     * @return the first station of the route
     */
    public Station station1() { return station1; }

    /**
     * Returns the second station of the route
     *
     * @return the second station of the route
     */
    public Station station2() { return station2; }

    /**
     * Returns the length of the route
     *
     * @return the length of the route
     */
    public int length() { return length; }

    /**
     * Returns the level of the route
     *
     * @return the level of the route
     */
    public Level level() { return level; }

    /**
     * Returns the color of the route
     *
     * @return the color of the route
     */
    public Color color() { return color; }

    /**
     * Returns a list composed of the two route stations
     *
     * @return a list composed of the two route stations
     */
    public List<Station> stations() { return List.of(station1(), station2()); }

    /**
     * Returns the station opposite to a given one
     *
     * @param station 
     *        The given station
     *
     * @return the station opposite to the given one
     *
     * @throws IllegalArgumentException 
     *         If the given station doesn't belong to the route stations
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(stations().contains(station));

        return station.equals(station1) ? station2 : station1;
    }

    /**
     * Returns the possible sets of cards a player can use to claim the route
     *
     * @return the possible sets of cards a player can use to claim the route
     */
    public List<SortedBag<Card>> possibleClaimCards() { return possibleClaimCards; }

    /**
     * Returns the additional number of cards the player must have to claim the route
     *
     * @param claimCards 
     *        The cards that the player attempts to claim the route with
     * 
     * @param drawnCards 
     *        The cards that were drawn to decide of the additional cards count
     *
     * @return the additional number of cards the player must have to claim the route
     *
     * @throws IllegalArgumentException 
     *         If the route is not underground or if the drawn cards are not three
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(level() == Level.UNDERGROUND && drawnCards.size() == 3);

        int claimCardsCount = 0;
        for (Card card : drawnCards) {
            if (Objects.isNull(card.color()) || card.color().equals(claimCards.get(0).color()))
                ++claimCardsCount;

        }

        return claimCardsCount;
    }

    /**
     * Returns the claim points for the route
     *
     * @return the claim points depending on the length of the route
     */
    public int claimPoints() { return Constants.ROUTE_CLAIM_POINTS.get(length()); }

    /**
     * Computes the possible claim cards for the route
     */
    private void computePossibleClaimCards() {
        possibleClaimCards = new ArrayList<>();
        SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
        int claimCardsWithLocomotive = level().equals(Level.UNDERGROUND) ? length() : 0;

        for (int i = 0; i < claimCardsWithLocomotive + 1; i++) {
            if (Objects.isNull(color())) {
                if (i < length()) {
                    for (Color color : Color.values()) {
                        builder.add(length() - i, Card.of(color));
                        builder.add(i, Card.of(null));
                        possibleClaimCards.add(builder.build());
                        builder = new SortedBag.Builder<>();
                    }
                }
                else {
                    builder.add(length(), Card.of(null));
                    possibleClaimCards.add(builder.build());
                    builder = new SortedBag.Builder<>();
                }
            }
            else {
                builder.add(length() - i, Card.of(color));
                builder.add(i, Card.of(null));
                possibleClaimCards.add(builder.build());
                builder = new SortedBag.Builder<>();
            }
        }
    }
}
