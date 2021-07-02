import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import serverConection.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class EffectsTest {

    @Test
    public void testMonsterRebornEffect() throws Exception {
        ArrayList<String> result = runTestNumber("1");
        Assertions.assertEquals(result.get(0) , result.get(1));
    }
    private ArrayList<String> runTestNumber(String number) throws Exception {
        String parentFile = "./src/test/resources/" + number +"/";
        String deckFile = Files.readString(Path.of(parentFile + "deck.txt")),
              inputFile = Files.readString(Path.of(parentFile + "input.txt")),
             outputFile = Files.readString(Path.of(parentFile + "output.txt"));
        Files.writeString(Path.of("./src/main/resources/Database/Decks/firstDeck.json"), deckFile);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        ByteArrayInputStream in = new ByteArrayInputStream(inputFile.getBytes());
        System.setIn(in);
        Main.main(null);
        ArrayList<String> result = new ArrayList<String>();
        result.add(outputFile.replaceAll("\\s",""));
        result.add(String.valueOf(outContent).replaceAll("\\s", ""));
        return result;
    }
}
