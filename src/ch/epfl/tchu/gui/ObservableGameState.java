package ch.epfl.tchu.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 
 */
public class ObservableGameState {
  private ObjectProperty<PlayerId> playerId;

  private IntegerProperty remainingTicketsPercentage;
  private IntegerProperty remainingCardsPercentage;
  private final List<ObjectProperty<Card>> faceUpCards;
  private final Map<Route, ObjectProperty<PlayerId>> routeOwnerships;

  private final Map<PlayerId, IntegerProperty> ticketCount;
  private final Map<PlayerId, IntegerProperty> cardCount;
  private final Map<PlayerId, IntegerProperty> carCount;
  private final Map<PlayerId, IntegerProperty> claimPoints;

  private ObjectProperty<ObservableList<Ticket>> ownedTickets;
  private final Map<Card, IntegerProperty> ownedCards;
  private final Map<Route, BooleanProperty> routeClaimability;

  private BooleanProperty canDrawTickets;
  private BooleanProperty canDrawCards;
  private final Map<Route, ObjectProperty<List<SortedBag<Card>>>> possibleClaimCards;

  /**
   * 
   * @param playerId
   */
  public ObservableGameState(PlayerId playerId) {
    this.playerId = new SimpleObjectProperty<PlayerId>(playerId);

    this.remainingTicketsPercentage = new SimpleIntegerProperty();
    this.remainingCardsPercentage = new SimpleIntegerProperty();
    this.faceUpCards = new ArrayList<ObjectProperty<Card>>();
    Constants.FACE_UP_CARD_SLOTS.forEach(s -> faceUpCards.add(new SimpleObjectProperty<Card>()));
    this.routeOwnerships = ChMap.routes().stream()
    .collect(Collectors.toMap(r -> r, r -> new SimpleObjectProperty<PlayerId>(null)));

    ticketCount = new HashMap<PlayerId, IntegerProperty>();
    cardCount = new HashMap<PlayerId, IntegerProperty>();
    carCount = new HashMap<PlayerId, IntegerProperty>();
    claimPoints = new HashMap<PlayerId, IntegerProperty>();
    PlayerId.ALL.forEach(p -> {
      ticketCount.put(p, new SimpleIntegerProperty());
      cardCount.put(p, new SimpleIntegerProperty());
      carCount.put(p, new SimpleIntegerProperty());
      claimPoints.put(p, new SimpleIntegerProperty());
    });

    this.ownedTickets = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    this.ownedCards = Card.ALL.stream()
    .collect(Collectors.toMap(c -> c, c -> new SimpleIntegerProperty()));
    this.routeClaimability = ChMap.routes().stream()
    .collect(Collectors.toMap(r -> r, r -> new SimpleBooleanProperty()));

    this.canDrawTickets = new SimpleBooleanProperty();
    this.canDrawCards = new SimpleBooleanProperty();
    this.possibleClaimCards = ChMap.routes().stream()
    .collect(Collectors.toMap(r -> r, r -> new SimpleObjectProperty<List<SortedBag<Card>>>()));
  }

  /**
   * 
   * @param newState
   * @param ownState
   */
  public void setState(PublicGameState newState, PlayerState ownState) {
    this.remainingTicketsPercentage = new SimpleIntegerProperty((int)((float)newState.ticketsCount() / (float)ChMap.tickets().size() * 100));
    this.remainingCardsPercentage = new SimpleIntegerProperty((int)((float)newState.cardState().deckSize() / (float)Constants.TOTAL_CARDS_COUNT * 100));
    Constants.FACE_UP_CARD_SLOTS.forEach(s -> this.faceUpCards.get(s).set(newState.cardState().faceUpCard(s)));
    ChMap.routes().forEach(r -> {
      PlayerId.ALL.stream()
      .filter(id -> newState.playerState(id).routes().contains(r))
      .findFirst()
      .ifPresentOrElse(id -> this.routeOwnerships.replace(r, new SimpleObjectProperty<PlayerId>(id)), () -> this.routeOwnerships.replace(r, new SimpleObjectProperty<PlayerId>(null)));
    });

    PlayerId.ALL.forEach(p -> {
      ticketCount.replace(p, new SimpleIntegerProperty(newState.playerState(p).ticketCount()));
      cardCount.replace(p, new SimpleIntegerProperty(newState.playerState(p).cardCount()));
      carCount.replace(p, new SimpleIntegerProperty(newState.playerState(p).carCount()));
      claimPoints.replace(p, new SimpleIntegerProperty(newState.playerState(p).claimPoints()));
    });

    this.ownedTickets.get().setAll(ownState.tickets().toList());
    Card.ALL.forEach(c1 -> {
      this.ownedCards.replace(c1, new SimpleIntegerProperty((int)ownState.cards().stream().filter(c2 -> c1.equals(c2)).count()));
    });
    ChMap.routes().forEach(r -> {
      this.routeClaimability.replace(r, new SimpleBooleanProperty(playerId.get() == newState.currentPlayerId()
        && Objects.isNull(routeOwnerships.get(r)) && !isNeighborClaimed(r) && ownState.canClaimRoute(r)));
    });

    this.canDrawTickets = new SimpleBooleanProperty(newState.canDrawTickets());
    this.canDrawCards = new SimpleBooleanProperty(newState.canDrawCards());
    ChMap.routes().forEach(r -> {
      possibleClaimCards.replace(r, new SimpleObjectProperty<List<SortedBag<Card>>>(ownState.possibleClaimCards(r)));
    });
  }

  /**
   * 
   * @return
   */
  public ReadOnlyIntegerProperty remainingTicketsPercentageProperty() { return remainingTicketsPercentage; }

  /**
   * 
   * @return
   */
  public ReadOnlyIntegerProperty remainingCardsPercentageProperty() { return remainingCardsPercentage; }

  /**
   * 
   * @param slot
   * @return
   */
  public ReadOnlyObjectProperty<Card> faceUpCard(int slot) { return faceUpCards.get(slot); }

  /**
   * 
   * @param route
   * @return
   */
  public ReadOnlyObjectProperty<PlayerId> routesOwnershipProperty(Route route) { return routeOwnerships.get(route); }

  /**
   * 
   * @return
   */
  public ReadOnlyIntegerProperty ticketCountProperty(PlayerId player) { return ticketCount.get(player); }

  /**
   * 
   * @return
   */
  public ReadOnlyIntegerProperty cardCountProperty(PlayerId player) { return cardCount.get(player); }

  /**
   * 
   * @return
   */
  public ReadOnlyIntegerProperty carCountProperty(PlayerId player) { return carCount.get(player); }

  /**
   * 
   * @return
   */
  public ReadOnlyIntegerProperty claimPointsProperty(PlayerId player) { return claimPoints.get(player); }

  /**
   * 
   * @return
   */
  public ReadOnlyObjectProperty<ObservableList<Ticket>> ownedTicketsProperty() { return ownedTickets; }

  /**
   * 
   * @param card
   * @return
   */
  public ReadOnlyIntegerProperty ownedCardsProperty(Card card) { return ownedCards.get(card); }

  /**
   * 
   * @param route
   * @return
   */
  public ReadOnlyBooleanProperty routeClaimabilityProperty(Route route) { return routeClaimability.get(route); }

  /**
   * 
   * @return
   */
  public ReadOnlyBooleanProperty canDrawTicketsProperty() { return canDrawTickets; }

  /**
   * 
   * @return
   */
  public ReadOnlyBooleanProperty canDrawCardsProperty() { return canDrawCards; }

  /**
   * 
   * @param route
   * @return
   */
  public ReadOnlyObjectProperty<List<SortedBag<Card>>> possibleClaimCardsProperty(Route route) { return possibleClaimCards.get(route); }

  /**
   * 
   * @param route
   * @return
   */
  private boolean isNeighborClaimed(Route route) {
    Route neighbor = ChMap.routes().stream()
    .filter(r -> r.stations().containsAll(route.stations()) && !r.equals(route))
    .findFirst()
    .orElse(route);
    
    return !Objects.isNull(routeOwnerships.get(neighbor));
  }
}