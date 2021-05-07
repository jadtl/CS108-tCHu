package ch.epfl.tchu.game;

import java.util.List;

/**
 * An enumeration of the eight colors
 * used in the game for wagons and roads
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public enum Color {
    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    public static final List<Color> ALL = List.of(Color.values());
    public static final int COUNT = ALL.size();
}

