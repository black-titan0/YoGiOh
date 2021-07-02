package controller;

import models.Deck;
import models.Player;
import models.cards.*;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionJsonParser {

    private Matcher actionMatcher;
    private Duel currentDuel;

    private final HashMap<String, String> actionsRegexes = new HashMap<>();

    {
        actionsRegexes.put("collect<(?<deckList>.*)>\\[-(?<class>\\w*)-(?<attributeList>.*)]", "collectCards");
        actionsRegexes.put("increase-attack-power\\{(?<amount>-?.+)}", "increaseAttackPower");
        actionsRegexes.put("increase-life-point\\{(?<amount>-?.+)}", "increaseLifePoint");
        actionsRegexes.put("increase-opponent-life-point\\{(?<amount>-?.+)}", "increaseOpponentLifePoint");
        actionsRegexes.put("increase-defence-power\\{(?<amount>-?.+)}", "increaseDefencePower");
        actionsRegexes.put("draw\\{(?<howMany>.+)}", "drawCard");
        actionsRegexes.put("cancel\\{(?<eventName>.+)}", "cancel");
        actionsRegexes.put("kill-offender", "killOffender");
        actionsRegexes.put("kill", "kill");
        actionsRegexes.put("die", "die");
        actionsRegexes.put("set-attack-power\\{(?<amount>.+)}", "set-attack-power");
        actionsRegexes.put("cancel-attack", "cancel-attack");
        actionsRegexes.put("consume-effect", "consumeEffect");
        actionsRegexes.put("select\\{(?<howMany>.+)}", "selectCardsByUserChoice");
        actionsRegexes.put("send-to\\{(?<deckName>.+)}", "sendCardsToDeck");
        actionsRegexes.put("get-in\\{(?<deckName>.+)}", "getCardsToDeck");
        actionsRegexes.put("make-horcrux", "makeHorcrux");
        actionsRegexes.put("be-horcrux", "beHorcrux");
        actionsRegexes.put("skip-phase", "skipPhase");
        actionsRegexes.put("negate-attack", "negateAttack");
        actionsRegexes.put("negate-activation", "negateActivation");
        actionsRegexes.put("negate-summon", "negateSummon");
        actionsRegexes.put("get-collected\\{(?<eventName>.+)}", "getCollected");

    }

    private static ActionJsonParser actionJsonParserInstance;

    public static ActionJsonParser getInstance() {
        if (actionJsonParserInstance == null)
            return (actionJsonParserInstance = new ActionJsonParser());
        return actionJsonParserInstance;
    }

    public void doActionList(String actionsString, Card clientCard, String event) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ActionExecutor actionExecutor = new ActionExecutor(event + ((Object) clientCard).toString(), clientCard , actionsString);
        doActionExecutor(actionExecutor , actionsString);
    }

    public void doActionExecutor(ActionExecutor actionExecutor , String actionsString) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String[] actions = actionsString.split(";");
        for (String action : actions) {
            String actionMethodName = getActionMethodName(action);
            actionExecutor.execute(actionMethodName, actionMatcher);
        }
    }

    public boolean checkConditionList(String conditionListString, Card clientCard) {
        if (conditionListString.length() == 0)
            return true;
        boolean shouldBeInverted = false;
        ConditionChecker conditionChecker = new ConditionChecker();
        if (conditionListString.charAt(0) == '!') {
            shouldBeInverted = true;
            conditionListString = conditionListString.replaceFirst("!", "");
        }
        String[] conditions = conditionListString.split("&");
        boolean result = true;
        for (String condition : conditions) {
            result &= conditionChecker.check(condition, clientCard);
        }
        if (shouldBeInverted)
            return !result;
        else return result;
    }

    private String getActionMethodName(String action) {

        for (String regex : actionsRegexes.keySet()) {
            actionMatcher = Pattern.compile(regex).matcher(action);
            if (actionMatcher.matches())
                return actionsRegexes.get(regex);
        }
        return "none";

    }

    public Card getDesiredCard(String[] attributeListStrings, String ofClass) {

        Card card;
        switch (ofClass) {
            case "Monster":
                card = new Monster("desired" + ofClass);
                break;
            case "Trap":
                card = new Trap("desired" + ofClass);
                break;
            case "Spell":
                card = new Spell("desired" + ofClass);
                break;
            case "Any":
                card = new Card("desiredCard");
                break;
            default:
                return null;
        }

        return card;
    }


    public ArrayList<Deck> getDecksByTheirName(String[] deckLists , Player client) {
        ArrayList<Deck> decks = new ArrayList<>();
        for (String deckName : deckLists) {
            switch (deckName) {
                case "MZ":
                    decks.add(client.getBoard().getMonsterZone());
                    break;
                case "OMZ":
                    decks.add(currentDuel.getOpponent(client).getBoard().getMonsterZone());
                    break;
                case "SZ":
                    decks.add(client.getBoard().getSpellZone());
                    break;
                case "OSZ":
                    decks.add(currentDuel.getOpponent(client).getBoard().getSpellZone());
                    break;
                case "GY":
                    decks.add(client.getBoard().getGraveyardZone());
                    break;
                case "OGY":
                    decks.add(currentDuel.getOpponent(client).getBoard().getGraveyardZone());
                    break;
                case "H":
                    decks.add(client.getBoard().getHand());
                    break;
                case "OH":
                    decks.add(currentDuel.getOpponent(client).getBoard().getHand());
                    break;
                case "D":
                    decks.add(client.getBoard().getDeckZone());
                    break;
                case "OD":
                    decks.add(currentDuel.getOpponent(client).getBoard().getDeckZone());
                    break;
                case "F":
                    Deck singleCardDeck = new Deck("F", null);
                    singleCardDeck.getMainCards().add(currentDuel.getOnlinePlayer().getBoard().getFieldZone());
                    decks.add(singleCardDeck);
                    break;
                case "OF":
                    singleCardDeck = new Deck("OF", null);
                    singleCardDeck.getMainCards().add(currentDuel.getOfflinePlayer().getBoard().getFieldZone());
                    decks.add(singleCardDeck);
                    break;
                case "TR":
                    singleCardDeck = new Deck("TR", null);
                    singleCardDeck.getMainCards().add(EventHandler.getTrigger());
                    decks.add(singleCardDeck);
                    break;
                case "AT":
                    singleCardDeck = new Deck("AT", null);
                    singleCardDeck.getMainCards().add(currentDuel.getAttackingMonster());
                    decks.add(singleCardDeck);
                    break;
            }
        }
        return decks;
    }

    public void setDuel(Duel duel) {
        currentDuel = duel;
    }
}
