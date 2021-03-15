package ch.epfl.tchu.game;

import java.util.Arrays;
import java.util.List;

/**
 * The identity of the player
 * 
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public enum PlayerId {
  PLAYER_1,
  PLAYER_2;

  public static final List<PlayerId> ALL = Arrays.asList(PlayerId.values());
  public static final int COUNT = ALL.size();

  /**
   * Returns the identity of the player who follows the one applied to
   * 
   * @return the identity of the player who follows the one applied to
   */
  public PlayerId next() {
    return ALL.get((this.ordinal() + 1) % COUNT);
  }
}