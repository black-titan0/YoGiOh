package models.cards;

import com.google.gson.annotations.SerializedName;
import controller.ActionJsonParser;
import controller.Duel;
import controller.EventHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.regex.Matcher;

public class Spell extends Card implements Cloneable {
    transient Boolean isActive = false;
    Boolean isActionable;
    @SerializedName("Status")
    String status;
    @SerializedName("Property")
    SpellProperty property;
    @SerializedName("Action")
    String action;
    @SerializedName("Trigger")
    String trigger;
    private transient ArrayList<String> activationTimeActions = new ArrayList<>();
    private transient ArrayList<String> deathTimeActions = new ArrayList<>();

    public Spell(String name) {
        super(name);
        isActive = false;
    }

    public SpellProperty getProperty() {
        return property;
    }

    public void setActionable(Boolean actionable) {
        isActionable = actionable;
    }

    public Boolean getActionable() {
        if (isActionable == null)
            return true;
        return isActionable;
    }

    public void initializeSpellEffects() {
        isActive = false;
        if (horcruxOf == null)
            horcruxOf = new ArrayList<>();
        if (action == null)
            return;
        String[] actions = action.split("->");
        for (String action : actions) {
            String[] actionInformation = action.split(":");
            switch (actionInformation[0]) {
                case "activation-time":
                    if (activationTimeActions == null)
                        activationTimeActions = new ArrayList<>();
                    activationTimeActions.add(actionInformation[1]);
                    break;
                case "death-time":
                    if (deathTimeActions == null)
                        deathTimeActions = new ArrayList<>();
                    deathTimeActions.add(actionInformation[1]);
                    break;
            }
        }
    }

    public void die() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (deathTimeActions != null) {
            for (String action : deathTimeActions) {
                Matcher actionMatcher = getActionMatcher(action);
                if (actionMatcher.group("condition").equals("") || ActionJsonParser.getInstance().checkConditionList(actionMatcher.group("condition"), this))
                    ActionJsonParser.getInstance().doActionList(actionMatcher.group("action"), this, "death-time");
            }
            isActive = false;
        }
        for (Card card : horcruxOf)
            Duel.getCurrentDuel().changeDeck(card , "GY");
    }

    public void activate() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        currentDeck = currentDeck.getOwner().getBoard().getSpellZone();
        if (activationTimeActions == null)
            return;
        for (String action : activationTimeActions) {
            Matcher actionMatcher = getActionMatcher(action);
            if (actionMatcher.group("condition").equals("") || ActionJsonParser.getInstance().checkConditionList(actionMatcher.group("condition"), this))
                ActionJsonParser.getInstance().doActionList(actionMatcher.group("action"), this, "activation-time");
        }
        isActive = true;
        EventHandler.assignWaitingEffect(trigger , this);
    }

    protected boolean isLike() {
        return true;
    }

    public Boolean getActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\n" +
                "Spell" + "\n" +
                "Type: " + property + "\n" +
                "Status" + status + "\n" +
                "Description: " + description + "\n";
    }

    @Override
    public Spell clone() throws CloneNotSupportedException {
        Spell spell = (Spell) super.clone();
        spell.action = this.action;
        spell.trigger = this.trigger;
        spell.initializeSpellEffects();
        return spell;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
