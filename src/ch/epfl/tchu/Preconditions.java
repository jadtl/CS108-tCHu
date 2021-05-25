package ch.epfl.tchu;

/**
 * A class that holds a checking method
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public final class Preconditions {
    /**
     * Disables instance creation for the class
     */
    private Preconditions() {
    }

    /**
     * Checks the truth value of the given argument
     * and throws an exception if false
     *
     * @param shouldBeTrue The argument that is checked
     * @throws IllegalArgumentException if {@code shouldBeTrue} is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }
}
