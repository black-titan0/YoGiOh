package models.cards;

import com.google.gson.annotations.SerializedName;
import controller.ActionJsonParser;
import controller.Duel;
import controller.EventHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

public class Monster extends Card implements Cloneable {
    public transient boolean canBeUnderAttack = true;
    @SerializedName("Atk")
    int attackPower;
    @SerializedName("Def")
    int defencePower;
    @SerializedName("Monster Type")
    MonsterType monsterType;
    @SerializedName("Attribute")
    MonsterAttribute monsterAttribute;
    @SerializedName("Level")
    int LEVEL;
    @SerializedName("Action")
    private String action;
    @SerializedName("Trigger")
    private String trigger;
    private String isAttackAble;
    private transient HashMap<Card, Integer> additionalAttackPower = new HashMap<>();
    private transient HashMap<Card, Integer> additionalDefencePower = new HashMap<>();
    private transient ArrayList<String> normalSummonTimeActions = new ArrayList<>();
    private transient String specialSummonTimeActions;
    private transient String deathTimeActions;
    private transient String flipTimeActions;
    private transient String gettingRaidTimeActions;
    private transient String endOfTurnActions;
    private boolean haveBeenAttackedWithMonsterInTurn = false;


    private String condition;
    private MonsterMode monsterMode;

    public Monster(String name) {
        super(name);
    }


    public void initializeMonstersEffects() {
        if (horcruxOf == null)
            horcruxOf = new ArrayList<>();
        if (this.additionalAttackPower == null)
            this.additionalAttackPower = new HashMap<>();
        if (this.additionalDefencePower == null)
            this.additionalDefencePower = new HashMap<>();
        if (action == null)
            return;
        String[] actions = action.split("->");
        for (String action : actions) {
            String[] actionInformation = action.split(":");
            switch (actionInformation[0]) {
                case "summon-time":
                    if (normalSummonTimeActions == null)
                        normalSummonTimeActions = new ArrayList<>();
                    normalSummonTimeActions.add(actionInformation[1]);
                    break;
                case "flip-time":
                    flipTimeActions = actionInformation[1];
                    break;
                case "death-time":
                    deathTimeActions = actionInformation[1];
                    break;
                case "getting-raid":
                    gettingRaidTimeActions = actionInformation[1];
                    break;
                case "special-summon-time":
                    specialSummonTimeActions = actionInformation[1];
                    break;
                case "end-of-turn":
                    endOfTurnActions = actionInformation[1];
                    break;
            }
        }
    }

    public MonsterMode getMonsterMode() {
        return monsterMode;
    }

    public void setMonsterMode(MonsterMode monsterMode) {
        this.monsterMode = monsterMode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setAdditionalAttackPower(int additionalAttackPower, Card clientCard) {
        this.additionalAttackPower.put(clientCard, additionalAttackPower);
    }


    public void setAdditionalDefencePower(int additionalDefencePower, Card clientCard) {
        this.additionalDefencePower.put(clientCard, additionalDefencePower);
    }

    public int getTotalAttackPower() {
        int totalAttackPower = attackPower;
        for (Card client : additionalAttackPower.keySet())
            totalAttackPower += additionalAttackPower.get(client);
        return totalAttackPower;
    }

    public int getTotalDefencePower() {
        int totalDefencePower = defencePower;
        for (Card client : additionalDefencePower.keySet())
            totalDefencePower += additionalDefencePower.get(client);
        return totalDefencePower;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public int getDefencePower() {
        return defencePower;
    }

    public void setDefencePower(int defencePower) {
        this.defencePower = defencePower;
    }

    public int getLEVEL() {
        return LEVEL;
    }

    public void setLEVEL(int LEVEL) {
        this.LEVEL = LEVEL;
    }

    public boolean isHaveBeenAttackedWithMonsterInTurn() {
        return haveBeenAttackedWithMonsterInTurn;
    }

    public void setHaveBeenAttackedWithMonsterInTurn(boolean haveBeenAttackedWithMonsterInTurn) {
        this.haveBeenAttackedWithMonsterInTurn = haveBeenAttackedWithMonsterInTurn;
    }

    public MonsterType getMonsterType() {
        return monsterType;
    }

    public MonsterAttribute getMonsterAttribute() {
        return monsterAttribute;
    }

    public void setMonsterAttribute(MonsterAttribute monsterAttribute) {
        this.monsterAttribute = monsterAttribute;
    }

    @Override
    public String toString() {
        return "Name: " + super.name + '\n' +
                "Level: " + this.LEVEL + '\n' +
                "Type: " + this.monsterType + '\n' +
                "ATK: " + (this.getTotalAttackPower()) + '\n' +
                "DEF: " + (this.getTotalDefencePower()) + '\n' +
                "Description: " + super.description + '\n';
    }

    @Override
    public Monster clone() throws CloneNotSupportedException {
        Monster monster = (Monster) super.clone();
        monster.setType(this.type);
        monster.setLEVEL(this.LEVEL);
        monster.setAttackPower(this.attackPower);
        monster.setDefencePower(this.defencePower);
        monster.setAction(this.action);
        monster.trigger = this.trigger;
        monster.initializeMonstersEffects();
        return monster;
    }

    public void die() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (currentDeck == currentDeck.getOwner().getBoard().getGraveyardZone())
            return;
        currentDeck.getOwner().getBoard().putInGraveyard(this);
        this.resetAllFields(this);
        if (deathTimeActions != null) {
            Matcher actionMatcher = getActionMatcher(deathTimeActions);
            if (actionMatcher.group("condition").equals("") || ActionJsonParser.getInstance().checkConditionList(actionMatcher.group("condition"), this))
                ActionJsonParser.getInstance().doActionList(actionMatcher.group("action"), this, "death-time");
        }
        for (Card card : horcruxOf)
            Duel.getCurrentDuel().changeDeck(card, "GY");
    }

    public void flip() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (flipTimeActions == null)
            return;
        Matcher actionMatcher = getActionMatcher(flipTimeActions);
        if (actionMatcher.group("condition").equals("") || ActionJsonParser.getInstance().checkConditionList(actionMatcher.group("condition"), this))
            ActionJsonParser.getInstance().doActionList(actionMatcher.group("action"), this, "flip-time");
    }

    public void summon() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        this.currentDeck = currentDeck.getOwner().getBoard().getMonsterZone();
        if (normalSummonTimeActions == null)
            return;
        for (String action : normalSummonTimeActions) {
            Matcher actionMatcher = getActionMatcher(action);
            if (actionMatcher.group("condition").equals("") || ActionJsonParser.getInstance().checkConditionList(actionMatcher.group("condition"), this))
                ActionJsonParser.getInstance().doActionList(actionMatcher.group("action"), this, "summon-time");
        }
        EventHandler.assignWaitingEffect(trigger, this);
    }

    public void getRaid() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (gettingRaidTimeActions == null)
            return;
        Matcher actionMatcher = getActionMatcher(gettingRaidTimeActions);
        if (actionMatcher.group("condition").equals("") || ActionJsonParser.getInstance().checkConditionList(actionMatcher.group("condition"), this))
            ActionJsonParser.getInstance().doActionList(actionMatcher.group("action"), this, "getting-raid");
    }

    public void endOfTurn() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (endOfTurnActions == null)
            return;
        Matcher actionMatcher = getActionMatcher(endOfTurnActions);
        if (actionMatcher.group("condition").equals("") || ActionJsonParser.getInstance().checkConditionList(actionMatcher.group("condition"), this))
            ActionJsonParser.getInstance().doActionList(actionMatcher.group("action"), this, "end-of-turn");

    }

    public boolean isAttackAble() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (isAttackAble == null)
            return true;
        return ActionJsonParser.getInstance().checkConditionList(isAttackAble, this);
    }

    boolean isLike(String attributeList) {
        return ActionJsonParser.getInstance().checkConditionList(attributeList, this);
    }

    public void resetAllFields(Card client) {
        if (client == this) {
            additionalAttackPower = new HashMap<>();
            additionalDefencePower = new HashMap<>();
        } else {
            additionalAttackPower.remove(client);
            additionalDefencePower.remove(client);
        }
        overriddenDescription = "";
        overriddenName = "";
    }

    public void setIsAttackAble(String isAttackAble) {
        this.isAttackAble = isAttackAble;
    }
}

