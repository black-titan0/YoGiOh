package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Deck;
import models.Player;
import models.cards.*;

import java.io.*;

public class FileWorker {

    private final String usersDataBase = "./src/main/resources/Database/Users/";
    private final String decksDataBase = "./src/main/resources/Database/Decks/";


    private FileWorker() {
    }

    private static FileWorker instance;

    public static FileWorker getInstance() {
        if (instance == null)
            instance = new FileWorker();
        return instance;
    }


    public Player readPlayerJSON(String fileAddress) {

        try (FileReader reader = new FileReader(fileAddress)) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            BufferedReader bufferedReader = new BufferedReader(reader);

            return gson.fromJson(bufferedReader, Player.class);

        } catch (IOException e) {
            return null;
        }

    }

    public void writeUserJSON(Player player) {
        String fileAddress = usersDataBase + player.getUsername() + ".json";
        writeFileTo(fileAddress, player);

    }

    public void writeDeckJSON(Deck deck) {
        String deckName = deck.getName();
        String fileAddress = decksDataBase + deckName + ".json";
        GsonBuilder builder = new GsonBuilder().registerTypeAdapter(Player.class, Player.getPlayerSerializerForDeck()).registerTypeAdapter(Card.class, Card.getCardSerializerForDeck());
        Gson gson = builder.create();
        FileWriter writer;

        try {

            writer = new FileWriter(fileAddress);
            writer.write(gson.toJson(deck));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeFileTo(String fileAddress, Object objectToWrite) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        FileWriter writer;

        try {
            writer = new FileWriter(fileAddress);
            writer.write(gson.toJson(objectToWrite));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Monster readMonsterJSON(String fileAddress) {

        try (FileReader reader = new FileReader(fileAddress)) {
            GsonBuilder builder = new GsonBuilder().registerTypeAdapter(MonsterType.class, MonsterType.get()).registerTypeAdapter(CardType.class , CardType.get());
            Gson gson = builder.create();

            BufferedReader bufferedReader = new BufferedReader(reader);

            return gson.fromJson(bufferedReader, Monster.class);

        } catch (IOException e) {
            return null;
        }

    }

    public Spell readSpellJSON(String fileAddress) {

        try (FileReader reader = new FileReader(fileAddress)) {
            GsonBuilder builder = new GsonBuilder().registerTypeAdapter(SpellProperty.class , SpellProperty.get());
            Gson gson = builder.create();

            BufferedReader bufferedReader = new BufferedReader(reader);
            Spell spell = gson.fromJson(bufferedReader, Spell.class);
            spell.setActive(false);
            spell.setActionable(true);
            return spell;

        } catch (IOException e) {
            return null;
        }

    }

    public Trap readTrapJSON(String fileAddress) {

        try (FileReader reader = new FileReader(fileAddress)) {
            GsonBuilder builder = new GsonBuilder().registerTypeAdapter(SpellProperty.class , SpellProperty.get());
            Gson gson = builder.create();

            BufferedReader bufferedReader = new BufferedReader(reader);

            return gson.fromJson(bufferedReader, Trap.class);

        } catch (IOException e) {
            return null;
        }

    }

    public Deck readDeckJSON(String fileAddress) {

        try (FileReader reader = new FileReader(fileAddress)) {
            GsonBuilder builder = new GsonBuilder().registerTypeAdapter(Player.class, Player.getPlayerDeserializerForDeck()).registerTypeAdapter(Card.class, Card.getCardDeserializerForDeck());
            Gson gson = builder.create();

            BufferedReader bufferedReader = new BufferedReader(reader);

            return gson.fromJson(bufferedReader, Deck.class);

        } catch (IOException e) {
            return null;
        }

    }
}
