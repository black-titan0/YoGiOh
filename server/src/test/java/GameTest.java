import controller.Duel;
import controller.Phases;
import controller.menus.RegisterMenuController;
import models.Database;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

public class GameTest {
    static Database database = Database.getInstance();
    static Duel onlineDuel;

    @BeforeAll
    public static void init() throws CloneNotSupportedException {
        database.loadingDatabase();
        RegisterMenuController.getInstance().login("mhdi", "p");
        onlineDuel = new Duel(database.getPlayerByUsername("mhdi"), database.getPlayerByUsername("ali"));
    }

    @Test
    public void selectTest() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        onlineDuel.select("10", true, "h");
        Assertions.assertEquals("invalid selection\n", outContent.toString());

        onlineDuel.getOnlinePlayer().getBoard().putCardInMonsterZone(database.getCardByName("Alexandrite Dragon"));
        onlineDuel.select("5", true, "m");
        Assertions.assertNotNull(onlineDuel.getOnlinePlayer().getBoard().getSelectedCard());

        onlineDuel.select("1", true, "h");
        Assertions.assertNotNull(onlineDuel.getOnlinePlayer().getBoard().getSelectedCard());

        onlineDuel.getOnlinePlayer().getBoard().setSelectedCard(null);
        onlineDuel.select("1", true, "s");
        Assertions.assertNull(onlineDuel.getOnlinePlayer().getBoard().getSelectedCard());


        onlineDuel.getOnlinePlayer().getBoard().putCardInSpellZone(database.getCardByName("Alexandrite Dragon"));
        onlineDuel.select("5", true, "s");
        Assertions.assertNotNull(onlineDuel.getOnlinePlayer().getBoard().getSelectedCard());

        onlineDuel.getOnlinePlayer().getBoard().setSelectedCard(null);
        onlineDuel.getOfflinePlayer().getBoard().putCardInSpellZone(database.getCardByName("Alexandrite Dragon"));
        onlineDuel.select("4", false, "s");
        Assertions.assertNotNull(onlineDuel.getOnlinePlayer().getBoard().getSelectedCard());


    }

    @Test
    public void summonTest() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        onlineDuel.getOfflinePlayer().getBoard().clearBoard();
        onlineDuel.getOnlinePlayer().getBoard().clearBoard();

        onlineDuel.getOnlinePlayer().getBoard().setSelectedCard(null);
        onlineDuel.summon();

        onlineDuel.select("1", true, "h");
        onlineDuel.summon();
        Assertions.assertEquals("no card is selected yet\n"
                + "card selected\n"
                + "action not allowed in this phase\n", outContent.toString());

        onlineDuel.setPhase(Phases.MAIN1);

        onlineDuel.select("1", true, "h");
        onlineDuel.summon();
        Assertions.assertEquals(onlineDuel.getOnlinePlayer().getBoard().getMonsterZoneCards().get(0).getName(), "Fireyarou");

        onlineDuel.select("1", true, "h");
        onlineDuel.summon();
        Assertions.assertNull(onlineDuel.getOnlinePlayer().getBoard().getMonsterZoneCards().get(1));
    }

    @Test
    public void setMonsterTest() {
    }

    @Test
    public void changePhaseTest() {
    }

    @Test
    public void changePositionTest() {
    }

    @Test
    public void attackTest() {
    }

    @Test
    public void tributeTest() {
    }

    @Test
    public void setSpellAndTrapTest() {
    }

}
