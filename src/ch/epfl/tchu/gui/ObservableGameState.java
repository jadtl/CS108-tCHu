package ch.epfl.tchu.gui;

import static ch.epfl.tchu.game.Card.LOCOMOTIVE;

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
 * The observable state of a tCHu game
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public class ObservableGameState {
    private final ObjectProperty<PlayerId> ownId;

    private final IntegerProperty remainingTicketsPercentage;
    private final IntegerProperty remainingCardsPercentage;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route, ObjectProperty<PlayerId>> routeOwnerships;

    private final Map<PlayerId, IntegerProperty> ticketCount;
    private final Map<PlayerId, IntegerProperty> cardCount;
    private final Map<PlayerId, IntegerProperty> carCount;
    private final Map<PlayerId, IntegerProperty> claimPoints;

    private final ObjectProperty<ObservableList<Ticket>> ownedTickets;
    private final Map<Card, IntegerProperty> ownedCards;
    private final Map<Route, BooleanProperty> routeClaimability;

    private final BooleanProperty canDrawTickets;
    private final BooleanProperty canDrawCards;
    private final Map<Route, ObjectProperty<List<SortedBag<Card>>>> possibleClaimCards;

    /**
     * An observable game state using the corresponding player identifier
     *
     * @param ownId The {@link PlayerId} of the corresponding player
     */
    public ObservableGameState(PlayerId ownId) {
        this.ownId = new SimpleObjectProperty<PlayerId>(ownId);

        this.remainingTicketsPercentage = new SimpleIntegerProperty();
        this.remainingCardsPercentage = new SimpleIntegerProperty();
        this.faceUpCards = new ArrayList<ObjectProperty<Card>>();
        Constants.FACE_UP_CARD_SLOTS.forEach(s -> faceUpCards.add(new SimpleObjectProperty<Card>(LOCOMOTIVE)));
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
     * Updates every property according to the two given states
     *
     * @param newState The updated {@link PublicGameState}
     * @param ownState The updated concerned {@link PlayerState}
     */
    public void setState(PublicGameState newState, PlayerState ownState) {
        this.remainingTicketsPercentage.set((int) ((float) newState.ticketsCount() / (float) ChMap.tickets().size() * 100));
        this.remainingCardsPercentage.set((int) ((float) newState.cardState().deckSize() / (float) Constants.TOTAL_CARDS_COUNT * 100));
        Constants.FACE_UP_CARD_SLOTS.forEach(s -> this.faceUpCards.get(s).set(newState.cardState().faceUpCard(s)));
        ChMap.routes().forEach(r -> {
            PlayerId.ALL.stream()
                    .filter(id -> newState.playerState(id).routes().contains(r))
                    .findAny()
                    .ifPresentOrElse(id -> this.routeOwnerships.get(r).set(id), () -> this.routeOwnerships.get(r).set(null));
        });

        PlayerId.ALL.forEach(p -> {
            ticketCount.get(p).set(newState.playerState(p).ticketCount());
            cardCount.get(p).set(newState.playerState(p).cardCount());
            carCount.get(p).set(newState.playerState(p).carCount());
            claimPoints.get(p).set(newState.playerState(p).claimPoints());
        });

        this.ownedTickets.get().setAll(ownState.tickets().toList());
        Card.ALL.forEach(c1 -> {
            this.ownedCards.get(c1).set((int) ownState.cards().stream().filter(c2 -> c1.equals(c2)).count());
        });
        ChMap.routes().forEach(r -> {
            this.routeClaimability.get(r).set(ownId.get() == newState.currentPlayerId()
                    && Objects.isNull(routeOwnerships.get(r).get()) && !isNeighborClaimed(r) && ownState.canClaimRoute(r));
        });

        this.canDrawTickets.set(newState.canDrawTickets());
        this.canDrawCards.set(newState.canDrawCards());
        ChMap.routes().forEach(r -> {
            // TODO: Check if it fixes the problem
            possibleClaimCards.get(r).set(ownState.canClaimRoute(r) ? ownState.possibleClaimCards(r) : List.of());
        });
    }

    /**
     * @return The read-only property of the remaining tickets percentage
     */
    public ReadOnlyIntegerProperty remainingTicketsPercentageProperty() {
        return remainingTicketsPercentage;
    }

    /**
     * @return The read-only property of the remaining cards percentage
     */
    public ReadOnlyIntegerProperty remainingCardsPercentageProperty() {
        return remainingCardsPercentage;
    }

    /**
     * @param slot The face-up card slot
     * @return The read-only property of the face-up card at the given slot
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     * @param route The route
     * @return The read-only property of the ownership of the given route
     */
    public ReadOnlyObjectProperty<PlayerId> routesOwnershipProperty(Route route) {
        return routeOwnerships.get(route);
    }

    /**
     * @param player The player
     * @return The read-only property of the given player's ticket count
     */
    public ReadOnlyIntegerProperty ticketCountProperty(PlayerId player) {
        return ticketCount.get(player);
    }

    /**
     * @param player The player
     * @return The read-only property of the given player's card count
     */
    public ReadOnlyIntegerProperty cardCountProperty(PlayerId player) {
        return cardCount.get(player);
    }

    /**
     * @param player The player
     * @return The read-only property of the given player's car count
     */
    public ReadOnlyIntegerProperty carCountProperty(PlayerId player) {
        return carCount.get(player);
    }

    /**
     * @param player The player
     * @return The read-only property of the given player's claim points
     */
    public ReadOnlyIntegerProperty claimPointsProperty(PlayerId player) {
        return claimPoints.get(player);
    }

    /**
     * @return The read-only property of the observable list of owned tickets
     */
    public ReadOnlyObjectProperty<ObservableList<Ticket>> ownedTicketsProperty() {
        return ownedTickets;
    }

    /**
     * @param card The type of player's cards
     * @return The read-only property of the number of cards of the given type in the current player's hand
     */
    public ReadOnlyIntegerProperty ownedCardsProperty(Card card) {
        return ownedCards.get(card);
    }

    /**
     * @param route The route
     * @return The read-only property of the claimability of the given route
     */
    public ReadOnlyBooleanProperty routeClaimabilityProperty(Route route) {
        return routeClaimability.get(route);
    }

    /**
     * @return The read-only property of the ability to draw tickets
     */
    public ReadOnlyBooleanProperty canDrawTicketsProperty() {
        return canDrawTickets;
    }

    /**
     * @return The read-only property of the ability to draw cards
     */
    public ReadOnlyBooleanProperty canDrawCardsProperty() {
        return canDrawCards;
    }

    /**
     * @param route The route
     * @return The read-only property of the list of possible claim cards for the given route
     */
    public ReadOnlyObjectProperty<List<SortedBag<Card>>> possibleClaimCardsProperty(Route route) {
        return possibleClaimCards.get(route);
    }

    private boolean isNeighborClaimed(Route route) {
        Route neighbor = ChMap.routes().stream()
                .filter(r -> r.stations().containsAll(route.stations()) && !r.equals(route))
                .findAny()
                .orElse(route);
        return !Objects.isNull(routeOwnerships.get(neighbor).get());
    }
}