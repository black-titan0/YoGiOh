package controller.menus;

import controller.FileWorker;
import models.Database;
import models.cards.Card;
import models.cards.Monster;
import models.cards.Spell;
import models.cards.Trap;
import serverConection.Output;

public class ImpExpMenuController {

    private final String importDataBase = "./src/main/resources/Database/ImpExp/";


    private ImpExpMenuController() {
    }

    private static ImpExpMenuController instance;

    public static ImpExpMenuController getInstance() {
        if (instance == null)
            instance = new ImpExpMenuController();
        return instance;
    }


    public void importFromFile(String cardName) {
        String fileAddress = importDataBase + cardName + ".json";
        try {
            Monster monsterCard = FileWorker.getInstance().readMonsterJSON(fileAddress);
            if (monsterCard.getTypeCard().equals("Monster")) {
                Database.allMonsters.add(monsterCard);
                Database.allCards.add(monsterCard);
                System.out.println(1);
            }
        } catch (NullPointerException e) {
        }

        try {
            Spell spellCard = FileWorker.getInstance().readSpellJSON(fileAddress);
            if (spellCard.getTypeCard().equals("Spell")) {
                Database.allSpells.add(spellCard);
                Database.allCards.add(spellCard);
                System.out.println(2);
            }
        } catch (NullPointerException e) {
        }

        try {
            Trap trapCard = FileWorker.getInstance().readTrapJSON(fileAddress);
            if (trapCard.getTypeCard().equals("Trap")) {
                Database.allTraps.add(trapCard);
                Database.allCards.add(trapCard);
                System.out.println(3);
            }
        } catch (NullPointerException e) {
        }

        Output.getInstance().showMessage("card imported!");

    }

    public void exportToFile(String cardName) {
        String fileAddress = importDataBase + cardName + ".json";
        Card card = Database.getInstance().getCardByName(cardName);

        if (card == null) {
            Output.getInstance().showMessage("card is not exist!");
            return;
        }


        if (card.getTypeCard().equals("Monster")) {
            System.out.println(1);
            Monster monster = (Monster) card;
            FileWorker.getInstance().writeFileTo(fileAddress, monster);
        }
        if (card.getTypeCard().equals("Spell")) {
            System.out.println(2);
            Spell spell = (Spell) card;

            FileWorker.getInstance().writeFileTo(fileAddress, spell);
        }
        if (card.getTypeCard().equals("Trap")) {
            System.out.println(3);
            Trap trap = (Trap) card;

            FileWorker.getInstance().writeFileTo(fileAddress, trap);
        }

        Output.getInstance().showMessage("card exported!");

    }
}
