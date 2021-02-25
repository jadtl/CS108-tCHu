package ch.epfl.tchu.game;

import java.util.Arrays;
import java.util.List;

/**
 * An enumeration of the different types
 * of game cards, i.e. eight wagon cards
 * types and the locomotive card type.
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public enum Card {
    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE,
    LOCOMOTIVE;

    public static final List<Card> ALL = Arrays.asList(Card.values());
    public static final int COUNT = ALL.size();
    public static final List<Card> CARS = ALL.subList(0, ALL.size() - 1);

    private Color color;

    /**
     * Returns a wagon card type according to the given color
     *
     * @param color the color of the wanted wagon
     */
    public static Card of(Color color) {
        for (Card card : CARS) {
            if (card.toString().equals(color.toString()))
                return card;
        }
        return null;
    }

    /**
     * Returns the card color
     *
     * @return a color for a wagon and null for a locomotive
     */
    public Color color() {
        return color;
    }

    /**
     * Constructs a card with the given color or null for a locomotive card
     */
    Card() {
        color = this.toString().equals("LOCOMOTIVE") ? null : Color.valueOf(this.toString());
    }
}

