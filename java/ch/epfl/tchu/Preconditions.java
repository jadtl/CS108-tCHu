
package ch.epfl.tchu;

/**
 * This method is created to 
 * emit error messages when 
 * certain conditions are not met
 */

public final class Preconditions
{
    private Preconditions() {}

    public static void checkArgument(boolean shouldBeTrue)
    {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }
}
