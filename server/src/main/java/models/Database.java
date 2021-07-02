package models;

import controller.CSVReader;
import controller.FileWorker;
import models.cards.Card;
import models.cards.Monster;
import models.cards.Spell;
import models.cards.Trap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Database {

    public static ArrayList<Player> allPlayers = new ArrayList<>();
    public static ArrayList<Card> allCards = new ArrayList<>();
    public static ArrayList<Monster> allMonsters = new ArrayList<>();
    public static ArrayList<Spell> allSpells = new ArrayList<>();
    public static ArrayList<Trap> allTraps = new ArrayList<>();
    public static ArrayList<Deck> allDecks = new ArrayList<>();

    private final String usersDateBase = "./src/main/resources/Database/Users/";
    private final String monsterDateBase = "./src/main/resources/Database/card-information/monsters/";
    private final String spellDateBase = "./src/main/resources/Database/card-information/spells/";
    private final String trapDateBase = "./src/main/resources/Database/card-information/traps/";
    private final String decksDateBase = "./src/main/resources/Database/Decks/";
    private final String spellCsvFilePath = "./src/main/resources/Database/CSVs/spells.csv";
    private final String trapCsvFilePath = "./src/main/resources/Database/CSVs/traps.csv";
    private final String monsterCsvFilePath = "./src/main/resources/Database/CSVs/monsters.csv";


    private Database() {
    }

    private static Database instance;

    public static Database getInstance() {
        if (instance == null)
            instance = new Database();
        return instance;
    }

    public static void removeDeck(String name) {
        allDecks.removeIf(deck -> deck.getName().equals(name));
    }

    public Player getPlayerByUsername(String username) {
        for (Player player : allPlayers) {
            if (player.getUsername().equals(username))
                return player;
        }
        return null;

    }

    public Player getPlayerByNickname(String nickname) {
        for (Player player : allPlayers) {
            if (player.getNickname().equals(nickname))
                return player;
        }
        return null;

    }

    public Card getCardByName(String name) {
        for (Card card : allCards) {
            if (card.getName().equals(name))
                return card;
        }
        return null;

    }

    public Deck getDeckByName(String name) {
        for (Deck deck : allDecks) {
            if (deck.getName().equals(name))
                return deck;
        }
        return null;

    }

    public Monster getMonsterByName(String name) {
        for (Monster monster : allMonsters) {
            if (monster.getName().equals(name))
                return monster;
        }
        return null;

    }

    public Spell getSpellByName(String name) {
        for (Spell spell : allSpells) {
            if (spell.getName().equals(name))
                return spell;
        }
        return null;

    }

    public Trap getTrapByName(String name) {
        for (Trap trap : allTraps) {
            if (trap.getName().equals(name))
                return trap;
        }
        return null;

    }

    public void loadPlayers() {
        File file = new File(usersDateBase);
        File[] files = file.listFiles();
        for (File filePointer : files) {
            allPlayers.add(FileWorker.getInstance().readPlayerJSON(filePointer.toString()));
        }

    }

    public void loadMonsters() {
        File file = new File(monsterDateBase);
        if (!file.exists()){
            file.mkdir();
        }
        File[] files = file.listFiles();
        assert files != null;
        if (files.length < 2) {
            new CSVReader(monsterCsvFilePath , monsterDateBase);
        }
        files = file.listFiles();
        for (File filePointer : files) {
            Monster monster = FileWorker.getInstance().readMonsterJSON(filePointer.toString());
            allMonsters.add(monster);
            allCards.add(monster);
        }

    }
    public void loadTraps() {
        File file = new File(trapDateBase);
        if (!file.exists()){
            file.mkdir();
        }
        File[] files = file.listFiles();
        assert files != null;
        if (files.length < 2) {
            new CSVReader(trapCsvFilePath, trapDateBase);
        }
        files = file.listFiles();
        for (File filePointer : files) {
            Trap trap = FileWorker.getInstance().readTrapJSON(filePointer.toString());
            allTraps.add(trap);
            allCards.add(trap);
        }

    }

    public void loadSpells() {
        File file = new File(spellDateBase);
        if (!file.exists()){
            file.mkdir();
        }
        File[] files = file.listFiles();
        assert files != null;
        if (files.length < 2) {
            new CSVReader(spellCsvFilePath, spellDateBase);
        }
        files = file.listFiles();
        for (File filePointer : files) {
            Spell spell = FileWorker.getInstance().readSpellJSON(filePointer.toString());
            allSpells.add(spell);
            allCards.add(spell);
        }

    }

    public void loadDecks() {
        File file = new File(decksDateBase);
        try {
            file.createNewFile();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        File[] files = file.listFiles();
        for (File filePointer : files) {
            Deck deck = FileWorker.getInstance().readDeckJSON(filePointer.toString());
            allDecks.add(deck);
            deck.updateOwnerDecks();
        }

    }

    public void setPlayers() {
        for (Player player : allPlayers)
            FileWorker.getInstance().writeUserJSON(player);

    }

    public void setDecks() {
        for (Deck deck : allDecks)
            FileWorker.getInstance().writeDeckJSON(deck);
    }

    public void loadingDatabase() {
        loadPlayers();
        loadMonsters();
        loadSpells();
        loadTraps();
        loadDecks();

    }

    public void updatingDatabase() {
        setPlayers();
        setDecks();
    }

}
