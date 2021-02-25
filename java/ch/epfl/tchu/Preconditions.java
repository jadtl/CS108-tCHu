package ch.epfl.tchu;

/**
 * A class that holds a checking method
 *
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public final class Preconditions {
    /**
     * Disables instance creation for the class
     */
    private Preconditions() {}

    /**
     * Checks the truth value of the given argument
     * and throws an exception if false
     *
     * @param  shouldBeTrue
     *              the argument that is checked
     * @throws IllegalArgumentException
     *              if the argument is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }
}
