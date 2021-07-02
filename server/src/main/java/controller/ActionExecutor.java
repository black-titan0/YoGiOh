package controller;

import models.Board;
import models.Deck;
import models.Player;
import models.cards.Card;
import models.cards.Monster;
import models.cards.Spell;
import models.cards.Trap;

import javax.print.DocFlavor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionExecutor {


    private static final ArrayList<ActionExecutor> ALL_ACTION_EXECUTORS = new ArrayList<>();
    private final String name;
    private Matcher neededInformation;
    private Deck collectedDeck;
    private Card clientsCard;
    private String actionString;

    public ActionExecutor(String name, Card clientsCard, String actionString) {
        this.actionString = actionString;
        this.clientsCard = clientsCard;
        this.name = name;
        ALL_ACTION_EXECUTORS.add(this);
    }

    public void redo() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ActionJsonParser.getInstance().doActionExecutor(this, actionString);
    }

    public static ActionExecutor getActionExecutorByName(String name) {
        for (ActionExecutor actionExecutor : ALL_ACTION_EXECUTORS) {
            if (actionExecutor.getCollectedDeck().getName().equals(name))
                return actionExecutor;
        }
        return null;
    }

    public Deck getCollectedDeck() {
        return collectedDeck;
    }

    public void execute(String methodName, Matcher matcher) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        neededInformation = matcher;
        this.getClass().getDeclaredMethod(methodName).invoke(this);
    }

    private void die() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Duel.getCurrentDuel().changeDeck(clientsCard, "GY");
    }

    private void drawCard() {
        int howMany = getNumber(neededInformation.group("howMany"));
        for (int i = 0; i < howMany; i++) {
            Duel.getCurrentDuel().getOnlinePlayer().getBoard().drawCard();
        }
    }

    public void makeHorcrux() {
        for (Card card : collectedDeck.mainCards)
            card.beACardsHorcrux(clientsCard);
    }

    public void increaseLifePoint() {
        int amount = getNumber(neededInformation.group("amount"));
        Player player = clientsCard.getCurrentDeck().getOwner();
        player.setHealth(player.getHealth() + amount);
    }

    public void increaseOpponentLifePoint() {
        int amount = getNumber(neededInformation.group("amount"));
        Player player = Duel.getCurrentDuel().getOpponent(clientsCard.getCurrentDeck().getOwner());
        player.setHealth(player.getHealth() + amount);
    }

    public void beHorcrux() {
        for (Card card : collectedDeck.mainCards)
            clientsCard.getHrocruxes().add(card);
    }

    public void cancel() {
        String[] eventNames = neededInformation.group("eventName").split("\\.");
        for (String eventName : eventNames) {
            ActionExecutor actionExecutor = ActionExecutor.getActionExecutorByName(eventName + ((Object) clientsCard).toString());
            while (actionExecutor != null) {
                for (Card card : actionExecutor.collectedDeck.mainCards) {
                    if (card instanceof Monster)
                        ((Monster) card).resetAllFields(clientsCard);
                }
                ALL_ACTION_EXECUTORS.remove(actionExecutor);
                actionExecutor = ActionExecutor.getActionExecutorByName(eventName + ((Object) clientsCard).toString());
            }
        }
        ALL_ACTION_EXECUTORS.remove(this);
    }

    public void getCollected() {
        String[] eventNames = neededInformation.group("eventName").split("\\.");
        ArrayList<Deck> deckList = new ArrayList<>();
        for (String eventName : eventNames) {
            ActionExecutor actionExecutor = ActionExecutor.getActionExecutorByName(eventName + ((Object) clientsCard).toString());
            if (actionExecutor == null)
                return;
            collectedDeck = actionExecutor.collectedDeck;
        }
        ALL_ACTION_EXECUTORS.remove(this);
    }

    private void collectCards() {
        collectedDeck = new Deck(name, clientsCard.getCurrentDeck().getOwner());
        ArrayList<Deck> deckList = ActionJsonParser.getInstance().getDecksByTheirName(neededInformation.group("deckList").split("\\."), clientsCard.getCurrentDeck().getOwner());
        getCardsFromTheirDeck(deckList, neededInformation.group("class"));
        String attributeList = neededInformation.group("attributeList");
        collectedDeck.mainCards.removeIf(card -> card == null || !card.hasAttributes(attributeList));
    }

    private void skipPhase() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Duel.setPhaseSkip(true);
    }

    private void negateAttack() {
        Duel.getCurrentDuel().negateAttack();
    }

    private void negateActivation() {
        Duel.getCurrentDuel().negateActivation();
    }

    private void negateSummon() {
        Duel.getCurrentDuel().negateSummon();
    }

    private void kill() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        for (Card card : collectedDeck.mainCards)
            if (card instanceof Monster)
                Duel.getCurrentDuel().kill(((Monster) card));
    }

    private void sendCardsToDeck() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String destination = neededInformation.group("deckName");
        for (Card card : collectedDeck.getMainCards()) {
            Duel.getCurrentDuel().changeDeck(card, destination);
        }
    }
    private void getCardsToDeck() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String destination = neededInformation.group("deckName");
        for (Card card : collectedDeck.getMainCards()) {
            Duel.getCurrentDuel().changeDeckBasedOnClientCard(clientsCard , card , destination);
        }
    }

    private void killOffender() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Duel currentDuel = Duel.getCurrentDuel();
        currentDuel.kill(currentDuel.getOnlinePlayer(), currentDuel.getAttackingMonster());
    }

    private void increaseAttackPower() {
        int amount = getNumber(neededInformation.group("amount"));
        for (Card card : collectedDeck.mainCards)
            if (card instanceof Monster)
                ((Monster) card).setAdditionalAttackPower(amount, clientsCard);
    }

    private void increaseDefencePower() {
        int amount = getNumber(neededInformation.group("amount"));
        for (Card card : collectedDeck.mainCards)
            if (card instanceof Monster)
                ((Monster) card).setAdditionalDefencePower(amount, clientsCard);
    }

    private void setAttackPower() {
        int amount = getNumber(neededInformation.group("amount"));
        for (Card card : collectedDeck.mainCards)
            if (card instanceof Monster) {
                Monster theMonster = ((Monster) card);
                theMonster.setAdditionalAttackPower(amount - theMonster.getAttackPower(), clientsCard);
            }
    }

    private void consumeEffect() {
        clientsCard.consumeEffect(collectedDeck.getName().replace(((Object) clientsCard).toString(), ""));
    }

    private void selectCardsByUserChoice() {
        int howMany = getNumber(neededInformation.group("howMany"));
        collectedDeck = Deck.getSelectionDeckFrom(collectedDeck, howMany);

    }

    private int getNumber(String input) {
        int amount = 0;
        try {
            amount = Integer.parseInt(input);
        } catch (Exception exception) {
            Matcher arithmeticMatcher = getSimpleArithmeticMatcher(input);
            arithmeticMatcher.find();
            int firstNumber = getVariable(arithmeticMatcher.group("firstNumber"));
            int secondNumber = getVariable(arithmeticMatcher.group("secondNumber"));
            amount = doArithmetic(firstNumber, secondNumber, arithmeticMatcher.group("sign"));
        }
        return amount;
    }

    private int doArithmetic(int firstNumber, int secondNumber, String sign) {
        switch (sign) {
            case "+":
                return firstNumber + secondNumber;
            case "<":
                return firstNumber - secondNumber;
            case "×":
                return firstNumber * secondNumber;
            case "÷":
                return firstNumber / secondNumber;
        }
        return 0;
    }

    private int getVariable(String variable) {
        int value = 0;
        try {
            value = Integer.parseInt(variable);
        } catch (Exception exception) {
            Player player = clientsCard.getCurrentDeck().getOwner();
            switch (variable) {
                case "GYN":
                    return player.getBoard().getGraveyardZone().getMainCards().size();
                case "OGYN":
                    return Duel.getCurrentDuel().getOpponent(player).getBoard().getGraveyardZone().getMainCards().size();
                case "collected":return collectedDeck.getMainCards().size();
                case "ATK":
                    return ((Monster) clientsCard).getTotalAttackPower();
                case "CATK":
                    return collectedDeck.getSumOfAttackPowers();

            }

        }
        return value;
    }

    private Matcher getSimpleArithmeticMatcher(String arithmetic) {
        return Pattern.compile("(?<firstNumber>.+?)(?<sign>[+\\-×÷])(?<secondNumber>.+)").matcher(arithmetic);
    }

    private void getCardsFromTheirDeck(ArrayList<Deck> decks, String ofClass) {
        ArrayList<Card> cards = collectedDeck.getMainCards();
        for (Deck deck : decks)
            cards.addAll(deck.getMainCards());
        switch (ofClass) {
            case "":
            case "Any":
                return;
            case "Monster":
                cards.removeIf(card -> !(card instanceof Monster));
                break;
            case "Spell":
                cards.removeIf(card -> card instanceof Trap);
                break;
            case "Trap":
                cards.removeIf(card -> !(card instanceof Trap));
                break;
        }

    }

}
