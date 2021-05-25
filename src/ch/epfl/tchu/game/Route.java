
package ch.epfl.tchu.game;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * A route that links two neighbor cities
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 * @see Station
 * @see Color
 */
public final class Route {
    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;
    private final List<SortedBag<Card>> possibleClaimCards;

    /**
     * The two levels a route can be on
     */
    public enum Level {UNDERGROUND, OVERGROUND}

    /**
     * A route with its identifier, stations, length, level and colors
     *
     * @param id       The {@link String} identifier
     * @param station1 The first {@link Station}
     * @param station2 The second {@link Station}
     * @param length   The route length
     * @param level    The route {@link Level}
     * @param color    The route {@link Color}
     * @throws IllegalArgumentException If both stations are equal or if the length is incorrect
     * @throws NullPointerException     If the identifier, the stations or the level are null
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
        this.possibleClaimCards = computePossibleClaimCards();
    }

    /**
     * The identifier of the route
     *
     * @return The {@link String} identifier of the {@link Route}
     */
    public String id() {
        return id;
    }

    /**
     * The first station of the route
     *
     * @return The first {@link Station} {@link Route#station1} of the {@link Route}
     */
    public Station station1() {
        return station1;
    }

    /**
     * The second station of the route
     *
     * @return The second {@link Station} {@link Route#station2} of the {@link Route}
     */
    public Station station2() {
        return station2;
    }

    /**
     * The length of the route
     *
     * @return The length {@link Route#length} of the {@link Route}
     */
    public int length() {
        return length;
    }

    /**
     * The level of the route
     *
     * @return The {@link Level} of the {@link Route}
     */
    public Level level() {
        return level;
    }

    /**
     * The color of the route
     *
     * @return The {@link Color} of the {@link Route}
     */
    public Color color() {
        return color;
    }

    /**
     * A list composed of the two route stations
     *
     * @return A {@link List} composed of the two {@link Station} of the {@link Route}
     */
    public List<Station> stations() {
        return List.of(station1(), station2());
    }

    /**
     * The station opposite to a given one
     *
     * @param station The {@link Station}
     * @return The {@link Station} opposite to {@code station}
     * @throws IllegalArgumentException If the {@code station} doesn't belong to {@link Route#stations()}
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(stations().contains(station));

        return station.equals(station1) ? station2 : station1;
    }

    /**
     * The list of possible sets of cards a player can use to claim the route
     *
     * @return The {@link List} of possible {@link SortedBag} of {@link Card} a player can use to claim the route
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        return possibleClaimCards;
    }

    /**
     * The additional number of cards the player must have to claim the route
     *
     * @param claimCards The {@link SortedBag} of {@link Card} that the player attempts to claim the route with
     * @param drawnCards The {@link SortedBag} of {@link Card} that were drawn to decide of the additional cards count
     * @return The additional number of cards the player must have to claim the {@link Route}
     * @throws IllegalArgumentException If the {@link Route} is not {@link Level#UNDERGROUND} or if {@code drawnCards} are not
     *                                  of size {@link Constants#ADDITIONAL_TUNNEL_CARDS}
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(level() == Level.UNDERGROUND && drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        // Note: claimCards.get(0).color() could cause problems if SortedBag is changed
        return (int) drawnCards.stream().filter((Card card) -> Objects.isNull(card.color()) ||
                card.color().equals(claimCards.get(0).color())).count();
    }

    /**
     * The claim points for the route
     *
     * @return The claim points depending on {@link Route#length()}
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length());
    }

    private List<SortedBag<Card>> computePossibleClaimCards() {
        // Using a linked hash set to keep the order intact as elements are added and to remove duplicates
        Set<SortedBag<Card>> computedPossibleClaimCards = new LinkedHashSet<SortedBag<Card>>();

        for (int i = 0; i <= (level().equals(Level.UNDERGROUND) ? length() : 0); i++) {
            List<Color> usableColors = (Objects.isNull(color())) ? Color.ALL : List.of(color);
            for (Color color : usableColors) {
                SortedBag<Card> claimCards = SortedBag.of(length() - i, Card.of(color), i, Card.LOCOMOTIVE);
                computedPossibleClaimCards.add(claimCards);
            }
        }

        return List.copyOf(computedPossibleClaimCards);
    }
}
