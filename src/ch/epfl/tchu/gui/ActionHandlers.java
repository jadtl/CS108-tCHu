package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * A container for various action handlers
 *
 * @author <a href="https://people.epfl.ch/jad.tala">Jad Tala (310821)</a>
 * @author <a href="https://people.epfl.ch/sofiya.malamud">Sofiya Malamud (313789)</a>
 */
public interface ActionHandlers {
    /**
     * An interface that handles drawing tickets
     */
    @FunctionalInterface
    interface DrawTicketsHandler {
        /**
         * Called when the player requests to draw a number {@link Ticket}
         */
        void onDrawTickets();
    }

    /**
     * An interface that handles drawing cards
     */
    @FunctionalInterface
    interface DrawCardHandler {
        /**
         * Called when the player requests to draw a card at the given slot
         *
         * @param slot The {@link Card} slot chosen by the player
         */
        void onDrawCard(int slot);
    }

    /**
     * An interface that handles claiming routes
     */
    @FunctionalInterface
    interface ClaimRouteHandler {
        /**
         * Called when the player requests to claim the given route with the given cards
         *
         * @param route      The {@link Route} chosen by the player
         * @param claimCards The {@link SortedBag} of {@link Card} chosen by the player to claim {@code route}
         */
        void onClaimRoute(Route route, SortedBag<Card> claimCards);
    }

    /**
     * An interface that handles choosing tickets
     */
    @FunctionalInterface
    interface ChooseTicketsHandler {
        /**
         * Called when the player chooses to keep the given tickets after tickets drawing
         *
         * @param chosenTickets The {@link SortedBag} of {@link Ticket} chosen by the player
         */
        void onChooseTickets(SortedBag<Ticket> chosenTickets);
    }

    /**
     * An interface that handles choosing cards
     */
    @FunctionalInterface
    interface ChooseCardsHandler {
        /**
         * Called when the player chooses to use the given cards to claim a route, if these are additional claim cards,
         * the bag can be empty, meaning that the player gave up on claiming the tunnel
         *
         * @param chosenCards The {@link SortedBag} of {@link Card} chosen by the player
         */
        void onChooseCards(SortedBag<Card> chosenCards);
    }
}
