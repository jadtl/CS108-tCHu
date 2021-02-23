package ch.epfl.tchu.game;

import java.util.Arrays;
import java.util.List;

/**
 * creation of a list of 
 * all existing colors 
 * for wagons
 */


public enum Color
{
    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    public static final List<Color> ALL = Arrays.asList(Color.values());

    public static final int COUNT = ALL.size();
}

