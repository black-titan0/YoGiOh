package controller.menus;

import controller.ErrorChecker;
import models.Database;
import models.Deck;
import models.Player;
import models.cards.Card;
import serverConection.Output;

import java.util.Objects;

public class DeckMenuController {

    private static DeckMenuController instance = null;

    private DeckMenuController() {
    }

    public static DeckMenuController getInstance() {
        return Objects.requireNonNullElseGet(instance, () -> (instance = new DeckMenuController()));
    }

    public void createDeck(String name, Player owner) {
        if (!ErrorChecker.isDeckNameUnique(name))
            return;

        new Deck(name, owner , true , true);
        Output.getInstance().showMessage("deck created successfully!");
    }

    public void deleteDeck(String name) {
        if (ErrorChecker.isDeckNameUnique(name))
            return;

        Database.removeDeck(name);
        Output.getInstance().showMessage("deck deleted successfully!");

    }


    public void setActiveDeck(String name, Player player) {
        Deck deck = null;
        boolean isPermitted = ErrorChecker.doesDeckExist(name)
                && ErrorChecker.doesDeckBelongToPlayer(deck = Database.getInstance().getDeckByName(name), player);
        if (isPermitted) {
            player.setActiveDeck(deck);

            Output.getInstance().showMessage("deck activated successfully!");
        }

    }

    public void addCardToDeck(String cardName, String deckName, Player player, boolean isMain) {
        Card card = null;
        Deck deck = null;
        boolean isPermitted = ErrorChecker.doesCardExist(cardName)
                && ErrorChecker.doesDeckExist(deckName)
                && ErrorChecker.doesDeckBelongToPlayer(deck = Database.getInstance().getDeckByName(deckName), player)
                && ((isMain) ? ErrorChecker.doesDeckHaveSpace(deck) : ErrorChecker.doesSideDeckHaveSpace(deck))
                && ErrorChecker.isNumberOfCardsInDeckLessThanFour(deck, card = Database.getInstance().getCardByName(cardName))
                && ErrorChecker.doesPlayerHaveEnoughCards(card , player);
        if (isPermitted) {
            player.getAllPlayerCard().moveCardTo(deck , card , true , isMain);
            Output.getInstance().showMessage("card added to deck successfully!");
        }
    }

    public void removeCardFromDeck(String cardName, String deckName, Player player, boolean isMain) {
        Card card;
        Deck deck = null;
        boolean isPermitted = ErrorChecker.doesCardExist(cardName)
                && ErrorChecker.doesDeckExist(deckName)
                && ErrorChecker.doesDeckBelongToPlayer(deck = Database.getInstance().getDeckByName(deckName), player);
        if (isPermitted) {
            card = Database.getInstance().getCardByName(cardName);
            deck.moveCardTo(player.getAllPlayerCard(),card, isMain , true);
            Output.getInstance().showMessage("card removed from deck successfully!");
        }
    }

    public void showAllDecks(Player player) {
        for (Deck deck : player.getAllDeck())
            Output.getInstance().showMessage(deck.toString());

    }

    public void showDeck(String name, Player player, boolean isMain) {
        if (!ErrorChecker.doesDeckExist(name))
            return;
        Deck deck = Database.getInstance().getDeckByName(name);
        if (!ErrorChecker.doesDeckBelongToPlayer(deck, player))
            return;
        Output.getInstance().showMessage(deck.toString(isMain));


    }
}
