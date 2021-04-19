package ch.epfl.tchu.net;

/**
 * The identifier of the message sent between the client and the server
 * 
 * @author Sofiya Malamud (313789)
 * @author Jad Tala (310821)
 */
public enum MessageId {
  INIT_PLAYERS,
  RECEIVE_INFO,
  UPDATE_STATE,
  SET_INITIAL_TICKETS,
  CHOOSE_INITIAL_TICKETS,
  NEXT_TURN,
  CHOOSE_TICKETS,
  DRAW_SLOT,
  ROUTE,
  CARDS,
  CHOOSE_ADDITIONAL_CARDS
}