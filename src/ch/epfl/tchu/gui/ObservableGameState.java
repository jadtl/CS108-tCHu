package ch.epfl.tchu.gui;

import static ch.epfl.tchu.game.Card.LOCOMOTIVE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The observable state of a tCHu game
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public final class ObservableGameState {
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
    private final Map<Route, BooleanProperty> routeAvailability;
    private final Map<Station, ListProperty<String>> toLinkStations;
    private final ObjectProperty<StationConnectivity> ownConnectivity;

    private final BooleanProperty canDrawTickets;
    private final BooleanProperty canDrawCards;
    private final Map<Route, ObjectProperty<List<SortedBag<Card>>>> possibleClaimCards;
    private final ObjectProperty<List<Trail>> longestTrails;

    /**
     * An observable game state using the corresponding player identifier
     *
     * @param ownId The {@link PlayerId} of the corresponding player
     */
    public ObservableGameState(PlayerId ownId) {
        this.ownId = new SimpleObjectProperty<>(ownId);

        this.remainingTicketsPercentage = new SimpleIntegerProperty();
        this.remainingCardsPercentage = new SimpleIntegerProperty();
        this.faceUpCards = new ArrayList<>();
        Constants.FACE_UP_CARD_SLOTS.forEach(s -> faceUpCards.add(new SimpleObjectProperty<>(LOCOMOTIVE)));
        this.routeOwnerships = ChMap.routes().stream()
                .collect(Collectors.toMap(r -> r, r -> new SimpleObjectProperty<>()));

        this.ticketCount = new HashMap<>();
        this.cardCount = new HashMap<>();
        this.carCount = new HashMap<>();
        this.claimPoints = new HashMap<>();
        PlayerId.ALL.forEach(p -> {
            ticketCount.put(p, new SimpleIntegerProperty());
            cardCount.put(p, new SimpleIntegerProperty());
            carCount.put(p, new SimpleIntegerProperty());
            claimPoints.put(p, new SimpleIntegerProperty());
        });

        this.ownedTickets = new SimpleObjectProperty<>(FXCollections.observableArrayList());
        this.ownedCards = Card.ALL.stream()
                .collect(Collectors.toMap(c -> c, c -> new SimpleIntegerProperty()));
        this.routeAvailability = ChMap.routes().stream()
                .collect(Collectors.toMap(r -> r, r -> new SimpleBooleanProperty()));
        this.toLinkStations = ChMap.stations().stream()
                .collect(Collectors.toMap(s -> s, s -> new SimpleListProperty<>()));
        this.ownConnectivity = new SimpleObjectProperty<>();

        this.canDrawTickets = new SimpleBooleanProperty();
        this.canDrawCards = new SimpleBooleanProperty();
        this.possibleClaimCards = ChMap.routes().stream()
                .collect(Collectors.toMap(r -> r, r -> new SimpleObjectProperty<>()));
        this.longestTrails = new SimpleObjectProperty<>();
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
        ChMap.routes().forEach(r -> PlayerId.ALL.stream()
                .filter(id -> newState.playerState(id).routes().contains(r))
                .findAny()
                .ifPresentOrElse(id -> this.routeOwnerships.get(r).set(id), () -> this.routeOwnerships.get(r).set(null)));

        PlayerId.ALL.forEach(p -> {
            ticketCount.get(p).set(newState.playerState(p).ticketCount());
            cardCount.get(p).set(newState.playerState(p).cardCount());
            carCount.get(p).set(newState.playerState(p).carCount());
            claimPoints.get(p).set(newState.playerState(p).claimPoints());
        });

        this.ownedTickets.get().setAll(ownState.tickets().toList());
        Card.ALL.forEach(c1 -> this.ownedCards.get(c1).set((int) ownState.cards().stream().filter(c1::equals).count()));
        ChMap.routes().forEach(r -> this.routeAvailability.get(r).set(ownId.get() == newState.currentPlayerId()
                && Objects.isNull(routeOwnerships.get(r).get()) && !isNeighborClaimed(r) && ownState.canClaimRoute(r)));
        ChMap.stations().forEach(s -> {
            List<Ticket> tickets = ownState.tickets().stream()
                    .filter(t -> t.points(ownState.connectivity()) < 0 && (t.fromStation().equals(s.name()) || t.toStations().contains(s.name())))
                    .collect(Collectors.toList());
            List<String> toLinkStations = new ArrayList<>();
            tickets.forEach(t -> {
                if (t.fromStation().equals(s.name()))
                    toLinkStations.addAll(t.toStations());
                else
                    toLinkStations.add(t.fromStation());
            });
            this.toLinkStations.get(s).set(FXCollections.observableArrayList(toLinkStations));
        });
        this.ownConnectivity.set(ownState.connectivity());

        this.canDrawTickets.set(newState.canDrawTickets());
        this.canDrawCards.set(newState.canDrawCards());
        ChMap.routes().forEach(r -> possibleClaimCards.get(r).set(ownState.canClaimRoute(r) ? ownState.possibleClaimCards(r) : List.of()));

        if (newState.gameEnded()) {
            int longestTrailLength = PlayerId.ALL.stream()
                    .map(p -> Trail.longest(newState.playerState(p).routes()).length())
                    .reduce(Integer.MIN_VALUE, Integer::max);
            this.longestTrails.set(PlayerId.ALL.stream()
                    .map(p -> Trail.longest(newState.playerState(p).routes()))
                    .filter(t -> t.length() == longestTrailLength)
                    .collect(Collectors.toList()));
        }
    }

    /**
     * @return The read-only property of the concerned player's identifier
     */
    public ReadOnlyObjectProperty<PlayerId> ownIdProperty() {
        return ownId;
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
     * @return The read-only property of the availability of the given route
     */
    public ReadOnlyBooleanProperty routeAvailabilityProperty(Route route) {
        return routeAvailability.get(route);
    }

    /**
     * @param station The station
     * @return The read-only property of the stations the player has to link to the given station to earn points
     */
    public ReadOnlyListProperty<String> toLinkStationsProperty(Station station) {
        return toLinkStations.get(station);
    }

    /**
     * @return The read-only property of player's connectivity
     */
    public ReadOnlyObjectProperty<StationConnectivity> ownConnectivityProperty() {
        return ownConnectivity;
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

    /**
     * @return The read-only property of the longest trails at the end of the game
     */
    public ReadOnlyObjectProperty<List<Trail>> longestTrailsProperty() {
        return longestTrails;
    }

    private boolean isNeighborClaimed(Route route) {
        Route neighbor = ChMap.routes().stream()
                .filter(r -> r.stations().containsAll(route.stations()) && !r.equals(route))
                .findAny()
                .orElse(route);
        return !Objects.isNull(routeOwnerships.get(neighbor).get());
    }
}