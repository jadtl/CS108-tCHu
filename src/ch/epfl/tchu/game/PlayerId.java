package ch.epfl.tchu.game;

import java.util.Arrays;
import java.util.List;

/**
 * The identity of the player
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2;

    public static final List<PlayerId> ALL = Arrays.asList(PlayerId.values());
    public static final int COUNT = ALL.size();

    /**
     * The identifier of the player who follows the one applied to
     *
     * @return The identifier of the player who follows the one applied to
     */
    public PlayerId next() {
        return ALL.get((this.ordinal() + 1) % COUNT);
    }
}