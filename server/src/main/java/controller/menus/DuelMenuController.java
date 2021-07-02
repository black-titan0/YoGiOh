package controller.menus;

import controller.AI;
import controller.Duel;
import controller.ErrorChecker;
import models.Database;
import models.Player;
import models.cards.Card;
import serverConection.GameInputs;
import serverConection.Output;

import java.lang.reflect.InvocationTargetException;

public class DuelMenuController {
    private static DuelMenuController instance;
    public static boolean debugBool = false;
    private DuelMenuController() {
    }

    public static DuelMenuController getInstance() {
        if (instance == null)
            instance = new DuelMenuController();
        return instance;
    }

    public void startGame(String firstUsername, String secondUsername, String round, boolean isAI)
            throws CloneNotSupportedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Player firstPlayer = Database.getInstance().getPlayerByUsername(firstUsername);
        Player secondPlayer = Database.getInstance().getPlayerByUsername(secondUsername);

        if (!ErrorChecker.doesUsernameExist(secondUsername)) {
            Output.getInstance().showMessage("there are no player with this username");
            return;
        }
        if (firstPlayer.getActiveDeck() == null) {
            Output.getInstance().showMessage(firstUsername + " has no active deck");
            return;
        }
        if (secondPlayer.getActiveDeck() == null) {
            Output.getInstance().showMessage(secondUsername + " has no active deck");
            return;
        }
        if (!ErrorChecker.isDeckAllowed(firstPlayer.getActiveDeck())) {
            Output.getInstance().showMessage(firstUsername + "'s deck is invalid");
            return;
        }
        if (!ErrorChecker.isDeckAllowed(secondPlayer.getActiveDeck())) {
            Output.getInstance().showMessage(secondUsername + "'s deck is invalid");
            return;
        }
        if (!round.equals("1") && !round.equals("3")) {
            Output.getInstance().showMessage("number of rounds is not supported");
            return;
        }

        int numberOfRound = Integer.parseInt(round);
        int numberOfWinPlayer1 = 0, numberOfWinPlayer2 = 0;

        for (int i = 1; i <= numberOfRound; i++) {
            Output.getInstance().showMessage("game on");

            if (isAI) runSinglePlayer(firstPlayer, secondPlayer);
            if (!isAI) runMultiplePlayer(firstPlayer, secondPlayer);

            Player winner = Duel.getCurrentDuel().getWinner();
            if (winner == null)
                return;
            if (Duel.getCurrentDuel().getWinner().getUsername().equals(firstPlayer.getUsername()))
                numberOfWinPlayer1++;
            if (Duel.getCurrentDuel().getWinner().getUsername().equals(secondPlayer.getUsername()))
                numberOfWinPlayer2++;

            if ((numberOfWinPlayer1 == 2 && numberOfWinPlayer2 == 0) ||
                    (numberOfWinPlayer1 == 0 && numberOfWinPlayer2 == 2)) break;

            if (i != numberOfRound) changeDeck(firstPlayer, secondPlayer, isAI);

        }

        if (numberOfWinPlayer1 > numberOfWinPlayer2)
            Output.getInstance().showMessage(firstUsername + "won the whole match with score: " +
                    firstPlayer.getScore() + "-" + secondPlayer.getScore());
        else
            Output.getInstance().showMessage(secondUsername + "won the whole match with score: " +
                    secondPlayer.getScore() + "-" + firstPlayer.getScore());

    }

    private void runMultiplePlayer(Player firstPlayer, Player secondPlayer)
            throws InvocationTargetException, CloneNotSupportedException, NoSuchMethodException, IllegalAccessException {
        Duel duel;
        GameInputs.getInstance().setOnlineDuel(duel = new Duel(firstPlayer, secondPlayer));

        while (!duel.isGameOver(debugBool))
            GameInputs.getInstance().runGamePlay();

    }

    private void runSinglePlayer(Player firstPlayer, Player secondPlayer)
            throws InvocationTargetException, CloneNotSupportedException, NoSuchMethodException, IllegalAccessException {
        Duel duel;
        GameInputs.getInstance().setOnlineDuel(duel = new Duel(firstPlayer, secondPlayer));
        AI aiPlayer = AI.getInstance();
        aiPlayer.setOnlineDuel(duel);
        aiPlayer.setSinglePlayer(firstPlayer);
        aiPlayer.setAiPlayer(secondPlayer);
        while (!duel.isGameOver(false)) {
            if (duel.getOnlinePlayer().getUsername().equals(firstPlayer.getUsername()))
                GameInputs.getInstance().runGamePlay();
            if (duel.getOnlinePlayer().getUsername().equals(secondPlayer.getUsername()))
                aiPlayer.action();
        }

    }

    private void changeDeck(Player firstPlayer, Player secondPlayer, boolean isAI) {
        Duel.getCurrentDuel().setOnlinePlayer(firstPlayer);
        Output.getInstance().showMessage("Transferred cards between sideDeck and mainDeck for " + firstPlayer.getUsername() + ":");
        GameInputs.getInstance().runChangeHand();
        Output.getInstance().showMessage("end changing!");

        Duel.getCurrentDuel().setOnlinePlayer(secondPlayer);
        if (!isAI) {
            Output.getInstance().showMessage("change cards between sideDeck and mainDeck for " + secondPlayer.getUsername() + ":");
            GameInputs.getInstance().runChangeHand();
            Output.getInstance().showMessage("end changing!");
        }

    }

    public void showMainDeck(Player player) {
        for (Card card : player.getBoard().getDeckZoneMainCards())
            Output.getInstance().showMessage(card.toString());

    }

    public void showSideDeck(Player player) {
        for (Card card : player.getBoard().getDeckZoneSideCards())
            Output.getInstance().showMessage(card.toString());

    }

    public void swapCard(Player player, String cardName1, String cardName2) {
        Card card1 = player.getBoard().getDeckZone().getCardByNameInMainDeck(cardName1);
        Card card2 = player.getBoard().getDeckZone().getCardByNameInSideDeck(cardName2);

        if (card1 == null || card2 == null) {
            System.out.println("card with this name dose not exist!");
            return;
        }
        player.getBoard().getDeckZone().removeCard(card1, true);
        player.getBoard().getDeckZone().removeCard(card2, false);

        player.getBoard().getDeckZone().mainCards.add(card2);
        player.getBoard().getDeckZone().sideCards.add(card1);

        System.out.println("card Transferred successfully.");

    }

}
