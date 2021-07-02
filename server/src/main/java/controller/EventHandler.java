package controller;

import models.Player;
import models.cards.Card;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventHandler {
    private final static HashMap<Card, String>
            monsterSummonEvent = new HashMap<>(),
            spellTrapActivationEvent = new HashMap<>(),
            opponentSpellTrapActivationEvent = new HashMap<>(),
            drawPhaseEvent = new HashMap<>(),
            standbyPhaseEvent = new HashMap<>(),
            endPhaseEvent = new HashMap<>(),
            opponentMonsterSummonEvent = new HashMap<>(),
            spellActivationEvent = new HashMap<>(),
            opponentSpellActivationEvent = new HashMap<>(),
            trapActivationEvent = new HashMap<>(),
            opponentTrapActivationEvent = new HashMap<>(),
            monsterAttackEvent = new HashMap<>(),
            monsterDeathEvent = new HashMap<>(),
            opponentMonsterAttackEvent = new HashMap<>();
    private static Card trigger;
    private static Player player;

    public static Card getTrigger() {
        return trigger;
    }

    public static void assignWaitingEffect(String waitingEffectString, Card client) {
        if (waitingEffectString == null)
            return;
        String[] waiters = waitingEffectString.split("\\|");
        for (String waiter : waiters) {
            Matcher matcher = Pattern.compile("wait-for\\{\\{(?<event>.+?)}}then-do\\{\\{(?<action>.+?)}}").matcher(waiter);
            matcher.find();
            waitFor(matcher.group("event"), matcher.group("action"), client);
        }

    }

    public static void triggerTrapActivation(Card trigger) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        EventHandler.trigger = trigger;
        triggerEvents(trapActivationEvent, false, "trap-activation-trigger");
    }

    public static void triggerSpellActivation(Card trigger) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        EventHandler.trigger = trigger;
        triggerEvents(spellActivationEvent, false, "spell-activation-trigger");
    }

    public static void triggerMonsterSummon(Card trigger) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        EventHandler.trigger = trigger;
        triggerEvents(monsterSummonEvent, false, "monster-summon-trigger");
    }
    public static void triggerMonsterDeath(Card trigger) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        EventHandler.trigger = trigger;
        triggerEvents(monsterDeathEvent, false, "monster-death-trigger");
    }

    public static void triggerMonsterAttack(Card trigger) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        EventHandler.trigger = trigger;
        triggerEvents(monsterAttackEvent, false, "monster-attack-trigger");
    }

    public static void triggerOpponentTrapActivation(Card trigger) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        EventHandler.trigger = trigger;
        triggerEvents(opponentTrapActivationEvent, true, "opponent-trap-activation-trigger");
    }

    public static void triggerSpellTrapActivation(Card trigger) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        EventHandler.trigger = trigger;
        triggerEvents(spellTrapActivationEvent, false, "spell-trap-activation-trigger");
    }

    public static void triggerOpponentSpellTrapActivation(Card trigger) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        EventHandler.trigger = trigger;
        triggerEvents(opponentSpellTrapActivationEvent, true, "opponent-spell-trap-activation-trigger");
    }

    public static void triggerDrawPhase(Player client) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        player = client;
        triggerEvents(drawPhaseEvent, true, "draw-phase-trigger");
    }
    public static void triggerEndPhase(Player client) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        player = client;
        triggerEvents(endPhaseEvent, true, "end-phase-trigger");
    }

    public static void triggerStandbyPhase(Card trigger) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        EventHandler.trigger = trigger;
        triggerEvents(standbyPhaseEvent, true, "standby-phase-trigger");
    }

    public static void triggerOpponentSpellActivation(Card trigger) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        EventHandler.trigger = trigger;
        triggerEvents(opponentSpellActivationEvent, true, "spell-activation-trigger");
    }

    public static void triggerOpponentMonsterSummon(Card trigger) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        EventHandler.trigger = trigger;
        triggerEvents(opponentMonsterSummonEvent, true, "spell-activation-trigger");
    }

    public static void triggerOpponentMonsterAttack(Card trigger) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        EventHandler.trigger = trigger;
        triggerEvents(opponentMonsterAttackEvent, true, "spell-activation-trigger");
    }

    private static void waitForSpellActivation(Card waitingCard, String action) {
        spellActivationEvent.put(waitingCard, action);
    }

    private static void waitForOpponentSpellActivation(Card waitingCard, String action) {
        opponentSpellActivationEvent.put(waitingCard, action);
    }

    private static void waitForMonsterSummon(Card waitingCard, String action) {
        monsterSummonEvent.put(waitingCard, action);
    }

    private static void waitForOpponentMonsterSummon(Card waitingCard, String action) {
        opponentMonsterSummonEvent.put(waitingCard, action);
    }

    private static void waitForTrapActivation(Card waitingCard, String action) {
        trapActivationEvent.put(waitingCard, action);
    }

    private static void waitForOpponentTrapActivation(Card waitingCard, String action) {
        opponentTrapActivationEvent.put(waitingCard, action);
    }

    private static void waitForMonsterAttack(Card waitingCard, String action) {
        monsterAttackEvent.put(waitingCard, action);
    }
    private static void waitForMonsterDeath(Card waitingCard, String action) {
        monsterDeathEvent.put(waitingCard, action);
    }

    private static void waitForOpponentMonsterAttack(Card waitingCard, String action) {
        opponentMonsterAttackEvent.put(waitingCard, action);
    }

    private static void waitForSpellTrapActivation(Card waitingCard, String action) {
        spellTrapActivationEvent.put(waitingCard, action);
    }

    private static void waitForOpponentSpellTrapActivation(Card waitingCard, String action) {
        opponentMonsterAttackEvent.put(waitingCard, action);
    }

    private static void waitForDrawPhase(Card waitingCard, String action) {
        drawPhaseEvent.put(waitingCard, action);
    }
    private static void waitForEndPhase(Card waitingCard, String action) {
        endPhaseEvent.put(waitingCard, action);
    }

    private static void waitForStandbyPhase(Card waitingCard, String action) {
        standbyPhaseEvent.put(waitingCard, action);
    }

    private static void triggerEvents(HashMap<Card, String> event, boolean justFromOpponent, String eventName) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (trigger != null)
            player = trigger.getCurrentDeck().getOwner();
        for (Card card : event.keySet()) {
            if (justFromOpponent) {
                Player waitingCardOwner = card.getCurrentDeck().getOwner();
                if (waitingCardOwner == player)
                    continue;
            }
            String action = event.get(card);
            Matcher actionMatcher = getActionMatcher(action);
            if (actionMatcher.group("condition").equals("") || ActionJsonParser.getInstance().checkConditionList(actionMatcher.group("condition"), card)) {
                ActionJsonParser.getInstance().doActionList(actionMatcher.group("action"), card, eventName);
                event.remove(card);
            }
        }
        trigger = null;
    }

    public static Matcher getActionMatcher(String action) {
        Matcher matcher = Pattern.compile("\\*(?<condition>.*)\\*+(?<action>.+)").matcher(action);
        matcher.find();
        return matcher;
    }

    private static void waitFor(String event, String action, Card card) {
        switch (event) {
            case "TA":
                waitForTrapActivation(card, action);
                break;
            case "MA":
                waitForMonsterAttack(card, action);
                break;
            case "MS":
                waitForMonsterSummon(card, action);
                break;
            case "SA":
                waitForSpellActivation(card, action);
                break;
            case "STA":
                waitForSpellTrapActivation(card, action);
                break;
            case "OTA":
                waitForOpponentTrapActivation(card, action);
                break;
            case "OMA":
                waitForOpponentMonsterAttack(card, action);
                break;
            case "OMS":
                waitForOpponentMonsterSummon(card, action);
                break;
            case "OSA":
                waitForOpponentSpellActivation(card, action);
                break;
            case "ODP":
                waitForDrawPhase(card, action);
                break;
            case "EP":
                waitForEndPhase(card, action);
            case "OSP":
                waitForStandbyPhase(card, action);
                break;
            case "MD":
                waitForMonsterDeath(card , action);
                break;


        }
    }
}
