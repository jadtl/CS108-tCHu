package ch.epfl.tchu.game;

import java.util.List;

/**
 * An enumeration of the different types
 * of game cards, i.e. eight wagon cards
 * types and the locomotive card type.
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 * @see Color
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

    public static final List<Card> ALL = List.of(Card.values());
    public static final int COUNT = ALL.size();
    public static final List<Card> CARS = ALL.subList(0, COUNT - 1);

    private final Color color;

    /**
     * Returns a wagon card type according to the given color
     *
     * @param color The {@link Color} of the wanted wagon
     */
    public static Card of(Color color) {
        switch (color) {
            case BLACK:
                return BLACK;
            case BLUE:
                return BLUE;
            case GREEN:
                return GREEN;
            case ORANGE:
                return ORANGE;
            case RED:
                return RED;
            case VIOLET:
                return VIOLET;
            case WHITE:
                return WHITE;
            case YELLOW:
                return YELLOW;
            default:
                return LOCOMOTIVE;
        }
    }

    /**
     * Returns the card's color
     *
     * @return a {@link Color} for a wagon and {@code null} for a {@link Card#LOCOMOTIVE}
     */
    public Color color() {
        return color;
    }

    /**
     * Constructs a card with the given {@link Color} or {@code null} for a {@link Card#LOCOMOTIVE}
     */
    Card() {
        color = this.toString().equals("LOCOMOTIVE") ? null : Color.valueOf(this.toString());
    }
}

