package controller.menus;

import controller.ErrorChecker;
import models.Database;
import models.Player;
import models.cards.Card;
import serverConection.Output;

import java.util.Arrays;
import java.util.Comparator;

public class ShoppingMenuController {
    private ShoppingMenuController() {
    }

    private static ShoppingMenuController instance;

    public static ShoppingMenuController getInstance() {
        if (instance == null)
            instance = new ShoppingMenuController();
        return instance;
    }

    public void buyCard(Player player, String cardName) {
        Card card = Database.getInstance().getCardByName(cardName);
        if (card == null) {
            Output.getInstance().showMessage("there is no card with this name");
            return;
        }
        if (ErrorChecker.doseNotHaveEnoughMoney(player, card.getPrice())) {
            Output.getInstance().showMessage("not enough money");
            return;
        }

        player.addCardToAllPlayerCard(card);
        player.setMoney(player.getMoney() - card.getPrice());
        Output.getInstance().showMessage("Card purchased");
    }

    public void showAllCard() {
        Card[] sortedCards = Database.allCards.toArray(new Card[0]);
        Arrays.sort(sortedCards, Comparator.comparing(Card::getName));
        for (Card card : sortedCards) {
            System.out.println(card);
        }
    }

    public void showMoney(Player player) {
        Output.getInstance().showMessage("money: " + player.getMoney());
    }

    public void increaseMoney(Player player, int amount) {
        player.setMoney(player.getMoney() + amount);
    }


}
