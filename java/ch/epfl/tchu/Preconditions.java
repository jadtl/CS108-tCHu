package ch.epfl.tchu;

public final class Preconditions
{
    private Preconditions() {}

    public static void checkArgument(boolean shouldBeTrue)
    {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }
}
