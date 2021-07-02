package controller;
import models.Deck;
import models.cards.Card;
import models.cards.Monster;
import models.cards.Spell;
import serverConection.GameInputs;
import serverConection.Output;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionChecker {
    private Card clientCard ;
    public boolean check(String condition , Card clientCard) {
        if (condition.equals("?")){
            Output.getInstance().showMessage("you can now activate the effect of " + clientCard.getName() +". do you want to?");
            return GameInputs.getInstance().yesOrNoQuestion();}
        Matcher conditionMatcher = getSimpleConditionMatcher(condition);
        conditionMatcher.find();
        this.clientCard = clientCard;
        int firstNumber = getVariable(conditionMatcher.group("firstNumber"));
        int secondNumber = getVariable(conditionMatcher.group("secondNumber"));

        return compare(firstNumber , secondNumber , conditionMatcher.group("sign"));
    }

    private boolean compare(int firstNumber, int secondNumber, String sign) {
        switch (sign) {
            case ">": return firstNumber > secondNumber;
            case "<": return firstNumber < secondNumber;
            case "=": return firstNumber == secondNumber;
            case ">=": return firstNumber >= secondNumber;
            case "<=": return firstNumber <= secondNumber;
            case "!=": return firstNumber != secondNumber;
        }
        return false;
    }

    private Matcher getSimpleConditionMatcher(String condition) {
        return Pattern.compile("(?<firstNumber>.+?)(?<sign>>|=|<|>=|<=|!=)(?<secondNumber>.+)").matcher(condition);
    }
    private int getVariable(String variable) {
        int value = 0;
        try {
            value = Integer.parseInt(variable);
        } catch (Exception exception) {
            switch (variable) {
                case "effected{summon-time}" :
                    ActionExecutor actionExecutor = ActionExecutor.getActionExecutorByName("summon-time" + ((Object) clientCard).toString());
                    if (actionExecutor == null)
                        break;
                    Deck collectedDeck = actionExecutor.getCollectedDeck();
                    if (collectedDeck == null)
                        break;
                   value =  collectedDeck.mainCards.size();
                    break;
                case "effected{flip-time}" :
                    actionExecutor =  ActionExecutor.getActionExecutorByName("flip-time" + ((Object) clientCard).toString());
                    if (actionExecutor == null) {
                        break;
                    }
                    collectedDeck = actionExecutor.getCollectedDeck();
                    if (collectedDeck == null)
                        break;
                    value =  collectedDeck.mainCards.size();
                    break;
                case "ATK" :
                    if (clientCard instanceof Monster)
                        return ((Monster)clientCard).getTotalAttackPower();
                    else
                        return 0;
                case "DEF":
                    if (clientCard instanceof Monster)
                        return ((Monster)clientCard).getTotalDefencePower();
                    else
                        return 0;
                case "MonsterType":
                    if (clientCard instanceof Monster)
                        return ((Monster)clientCard).getMonsterType().ordinal();
                    else
                        return 0;
                case "CardPlacement":
                        return clientCard.getCardPlacement().ordinal();
                case "MonsterMode":
                    if (clientCard instanceof Monster)
                        return ((Monster)clientCard).getMonsterMode().ordinal();
                    else
                        return 0;
                case "MonsterAttribute":
                    if (clientCard instanceof Monster)
                        return ((Monster)clientCard).getMonsterAttribute().ordinal();
                    else
                        return 0;
                case "SpellProperty":
                    if (clientCard instanceof Spell)
                        return ((Spell)clientCard).getProperty().ordinal();
                    else
                        return 0;
                case "Level":
                    if (clientCard instanceof Monster)
                        return ((Monster)clientCard).getLEVEL();
                    else
                        return 0;

            }
        }
        return value;
    }
}
