
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
     *
     */
    public enum Level { UNDERGROUND, OVERGROUND }

    /**
     * Constructs a route
     *
     * @param id
     * @param station1
     * @param station2
     * @param length
     * @param level
     * @param color
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
     *
     * @return
     */
    public String id() { return id; }

    /**
     *
     * @return
     */
    public Station station1() { return station1; }

    /**
     *
     * @return
     */
    public Station station2() { return station2; }

    /**
     *
     * @return
     */
    public int length() { return length; }

    /**
     *
     * @return
     */
    public Level level() { return level; }

    /**
     *
     * @return
     */
    public Color color() { return color; }

    /**
     *
     * @return
     */
    public List<Station> stations() { return List.of(station1(), station2()); }

    /**
     *
     * @param station
     * @return
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station.equals(station1) || station.equals(station2));

        return station.equals(station1) ? station2 : station1;
    }

    /**
     *
     * @return
     */
    public List<SortedBag<Card>> possibleClaimCards() { return possibleClaimCards; }

    /**
     *
     * @param claimCards
     * @param drawnCards
     * @return
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
     *
     * @return
     */
    public int claimPoints() { return Constants.ROUTE_CLAIM_POINTS.get(length()); }

    /**
     * Computes the possible claim cards for the routed
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

        this.possibleClaimCards = possibleClaimCards;
    }
}
