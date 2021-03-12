package ch.epfl.tchu.game;

import java.util.Arrays;
import java.util.List;

/**
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
   * 
   * @return
   */
  public PlayerId next() {
    return this == PLAYER_1 ? PLAYER_2 : PLAYER_1;
  }
}