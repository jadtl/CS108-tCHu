package ch.epfl.tchu.game;

import java.util.Arrays;
import java.util.List;

public enum Card
{
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

    // same list as ALL, except without LOCOMOTIVE
    public static final List<Card> CARS = ALL.subList(0, ALL.size() - 1);
    
    /**
     * returns type of wagon 
     * corresponding to color
     */
    
    public static Card of(Color color)
    {
        for (Card card : CARS)
        {
            // return current card if equal to given color
            if (card.toString().equals(color.toString()))
                return card;
        }
        return null;
    }
    
    /**
     * @param color
     */

    private Color color;

    public Color color()
    {
        return color;
    }
    
    /**
     * Returns the card color 
     * for a wagon and null for
     * a locomotive (have no color)
     */


     Card()
    {
        color = this.toString().equals("LOCOMOTIVE") ? null : Color.valueOf(this.toString());
    }
}

