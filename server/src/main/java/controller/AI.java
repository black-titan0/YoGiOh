package controller;


import models.Player;
import models.cards.*;
import serverConection.Output;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class AI {

    private static AI instance;
    private Duel onlineDuel;
    private Player aiPlayer;
    private Player singlePlayer;

    private AI() {
    }

    public static AI getInstance() {
        if (instance == null)
            instance = new AI();
        return instance;
    }

    public Duel getOnlineDuel() {
        return onlineDuel;
    }

    public void setOnlineDuel(Duel onlineDuel) {
        this.onlineDuel = onlineDuel;
    }

    public void action() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        switch (onlineDuel.getPhase()) {
            case DRAW:
            case END:
            case STANDBY:
                onlineDuel.changePhase();
                break;
            case MAIN1:
                setAMonster();
                setSpellsAndTraps();
                onlineDuel.changePhase();
                break;
            case BATTLE:
                attackMonster();
                handleSpell();
                onlineDuel.changePhase();
                break;
            case MAIN2:
                handleSpell();
                onlineDuel.changePhase();
                break;
        }
    }

    private void attackMonster() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (singlePlayer.getBoard().isMonsterZoneEmpty()) {
            ArrayList<Card> monsters = aiPlayer.getBoard().getMonsterZoneCards();
            for (Card monster : monsters) {
                if (monster == null) continue;
                if (((Monster) monster).getMonsterMode().equals(MonsterMode.defence)) continue;
                onlineDuel.select(String.valueOf(correctPositions(aiPlayer.getBoard().getMonsterZone().mainCards.indexOf(monster))), true, "m");
                Card selectedCard = aiPlayer.getBoard().getSelectedCard();
                singlePlayer.setHealth(singlePlayer.getHealth() - ((Monster) selectedCard).getAttackPower());
                ((Monster) selectedCard).setHaveBeenAttackedWithMonsterInTurn(true);
                Output.getInstance().showMessage("you have been received " + ((Monster) selectedCard).getAttackPower() + " battle damage" + "by" + monster.getName());
            }
        } else {
            if (findBestMonsterToAttack() == null) return;
            Monster monster = findBestMonsterToAttack();
            Monster tale = isThereWeakerCard(monster);
            assert tale != null;
            if (monster.getAttackPower() < tale.getDefencePower()) return;
            onlineDuel.select(String.valueOf(correctPositions(aiPlayer.getBoard().getMonsterZone().mainCards.indexOf(monster))), true, "m");
            int cardPosition = (singlePlayer.getBoard().getMonsterZone().mainCards.indexOf(tale));
            Card selectedCard = aiPlayer.getBoard().getSelectedCard();
            onlineDuel.runAttack(cardPosition, (Monster) selectedCard);
            ((Monster) selectedCard).setHaveBeenAttackedWithMonsterInTurn(true);
            System.out.println(monster.getName() + " attacked " + tale.getName());
        }
    }

    private void setSpellsAndTraps() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        for (Card card : getSpellsInHand()) {
            if (!aiPlayer.getBoard().isSpellZoneFull()) {
                if (!(card instanceof Spell) && !(card instanceof Trap)) return;
                if(card == null) return;
                card.setCardPlacement(CardPlacement.faceDown);
                aiPlayer.getBoard().putCardInSpellZone(card);
                aiPlayer.getBoard().removeFromHand(card);
                aiPlayer.getBoard().setSelectedCard(null);
                Output.getInstance().showMessage("Ai Set an Spell");
            }
        }
    }

    private void setAMonster() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Monster monster = findBestMonsterInHand();
        onlineDuel.select(String.valueOf(correctPositions(aiPlayer.getBoard().getHand().mainCards.indexOf(monster))), true, "h");
        Output.getInstance().showMessage("Ai Select a Card");
        if (!monster.getTypeCard().equals("ritual")) {
            Card selectedCard = aiPlayer.getBoard().getSelectedCard();
            if (monster.getDefencePower() > monster.getAttackPower() || monster.getAttackPower() < 800 || monster.getDefencePower() > 2500) {
                selectedCard.setCardPlacement(CardPlacement.faceDown);
                ((Monster) selectedCard).setMonsterMode(MonsterMode.defence);
                aiPlayer.getBoard().putCardInMonsterZone(selectedCard);
                aiPlayer.getBoard().setSummonedOrSetCardInTurn(true);
                aiPlayer.getBoard().removeFromHand(selectedCard);
                aiPlayer.getBoard().setSelectedCard(null);
                Output.getInstance().showMessage("AI set a Monster");
            } else {
                onlineDuel.summon();
            }
        }
    }

    public Monster findWeakestSacrificeInGame() {
        int power = 1000000;
        Monster monster = null;
        for (Card card : aiPlayer.getBoard().getMonsterZone().mainCards) {
            if (card instanceof Monster && ((Monster) card).getAttackPower() < power) {
                power = ((Monster) card).getAttackPower();
                monster = (Monster) card;
            }
        }
        return monster;
    }

    public Monster findBestMonsterToAttack() {
        int power = 0;
        Monster monster = null;
        for (Card card : aiPlayer.getBoard().getMonsterZoneCards()) {
            if (card == null) continue;
            if (((Monster) card).getAttackPower() > power && isThereWeakerCard((Monster) card) != null) {
                power = ((Monster) card).getAttackPower();
                monster = (Monster) card;
            }
        }
        return monster;
    }

    private Monster isThereWeakerCard(Monster monster) {
        int power = monster.getAttackPower();
        for (Card card : singlePlayer.getBoard().getMonsterZone().mainCards) {
            if (card instanceof Monster && ((Monster) card).getDefencePower() < power) {
                return (Monster) card;
            }
        }
        return null;
    }


    public Monster findBestMonsterInHand() {
        int power = 0;
        Monster monster = null;
        ArrayList<Card> deck = aiPlayer.getBoard().getHand().mainCards;
        if (deck == null) return null;
        for (Card card : deck) {
            if (card instanceof Monster && ((Monster) card).getAttackPower() > power) {
                power = ((Monster) card).getAttackPower();
                monster = (Monster) card;
            }
        }
        assert monster != null;
        System.out.println(monster.getName() + " has been chosen");
        return monster;
    }

    private void handleSpell() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Card spell = selectSpell();
        if (numberOfSpellsInTheField() == 0 && spell != null) return;
        onlineDuel.select(String.valueOf(correctPositions(aiPlayer.getBoard().getSpellZone().mainCards.indexOf(spell))),
                true, "s");
        onlineDuel.activateSpellCard();
    }

    public int numberOfSpellsInTheField() {
        int count = 0;
        for (Card card : aiPlayer.getBoard().getSpellZone().mainCards) {
            if (card != null && ((card instanceof Spell) || (card instanceof Trap))) {
                count++;
            }
        }
        return count;
    }

    public ArrayList<Card> getSpellsInHand() {
        ArrayList<Card> cards = new ArrayList<>();
        for (Card card : aiPlayer.getBoard().getHand().mainCards) {
            if (card instanceof Spell || card instanceof Trap) {
                cards.add(card);
            }
        }
        return cards;
    }

    public boolean doWeHaveTrap() {
        for (Card card : aiPlayer.getBoard().getHand().mainCards) {
            if (card instanceof Trap) return true;
        }
        return false;
    }

    public Player getAiPlayer() {
        return aiPlayer;
    }

    public void setAiPlayer(Player aiPlayer) {
        this.aiPlayer = aiPlayer;
    }

    private Card selectSpell() {
        if (aiPlayer.getBoard().getSpellZone() != null && numberOfSpellsInTheField() != 0) {
            for (Card card : aiPlayer.getBoard().getSpellZone().mainCards) {
                if (card != null) {
                    System.out.println(card.getName());
                    return card;
                }
            }
        }
        return null;
    }

    public void setSinglePlayer(Player singlePlayer) {
        this.singlePlayer = singlePlayer;
    }

    private int correctPositions(int address) {
        if (address == 0) return 5;
        if (address == 1) return 3;
        if (address == 2) return 1;
        if (address == 3) return 2;
        if (address == 4) return 4;
        return -1;
    }
}
