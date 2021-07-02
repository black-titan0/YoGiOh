package serverConection;

import controller.Duel;
import controller.menus.DuelMenuController;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GameInputs {

    private static GameInputs instance;
    private final String[] gamePlayRegexes = {
            "^select (--monster|-m) (?<address>\\d+)$",
            "^select (--monster|-m) (?<address>\\d+) (--opponent|-o)$",
            "^select (--opponent|-o) (--monster|-m) (?<address>\\d+)$",
            "^select (--spell|-s) (?<address>\\d+)$",
            "^select (--spell|-s) (?<address>\\d+) (--opponent|-o)$",
            "^select (--opponent|-o) (--spell|-s) (?<address>\\d+)$",
            "^select (--field|-f) (?<address>\\d+)$",
            "^select (--field|-f) (?<address>\\d+) (--opponent|-o)$",
            "^select (--opponent|-o) (--field|-f) (?<address>\\d+)$",
            "^select (--hand|-h) (?<address>\\d+)$",
            "^select -d$",
            "^next phase$",
            "^summon$",
            "^set$",
            "^set (--position|-p) (?<mode>attack|defence)$",
            "^flip-summon",
            "^attack (?<address>\\d+)$",
            "^attack direct$",
            "^activate effect$",
            "^show graveyard$",
            "^card show (--selected|-s)$",
            "^increase --LP (?<amount>\\d+)$",
            "^duel set-winner (?<nickname>\\w+)$",
            "show board",
            "show turn"
    };

    private final String[] changeDeckRegexes = {
            "^show main cards$",
            "^show side cards$",
            "^change (?<card1>.+) with (?<card2>.+)$",
            "^end$"
    };

    Matcher commandMatcher;
    String command = "a";

    private Duel onlineDuel;

    private GameInputs() {
    }

    public static GameInputs getInstance() {
        if (instance == null)
            instance = new GameInputs();
        return instance;
    }

    public Duel getOnlineDuel() {
        return onlineDuel;
    }

    public void setOnlineDuel(Duel onlineDuel) {
        this.onlineDuel = onlineDuel;
    }


    public void runGamePlay() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        command = ConsoleBasedMenus.scanner.nextLine().replaceAll("\\s+", " ");
        if (command.equals("end debugging")){
            DuelMenuController.debugBool = true;
            return;
        }
        int whichCommand;
        for (whichCommand = 0; whichCommand < gamePlayRegexes.length; whichCommand++) {
            commandMatcher = findMatcher(command, gamePlayRegexes[whichCommand]);
            if (commandMatcher.find()) {
                executeGamePlayCommands(commandMatcher, whichCommand);
                break;
            } else if (whichCommand == gamePlayRegexes.length - 1)
                Output.getInstance().showMessage("invalid command");
        }

    }

    private void executeGamePlayCommands(Matcher commandMatcher, int whichCommand) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        switch (whichCommand) {
            case 0:
                onlineDuel.select(commandMatcher.group("address"), true, "m");
                break;
            case 1:
            case 2:
                onlineDuel.select(commandMatcher.group("address"), false, "m");
                break;
            case 3:
                onlineDuel.select(commandMatcher.group("address"), true, "s");
                break;
            case 4:
            case 5:
                onlineDuel.select(commandMatcher.group("address"), false, "s");
                break;
            case 6:
                onlineDuel.select(commandMatcher.group("address"), true, "f");
                break;
            case 7:
            case 8:
                onlineDuel.select(commandMatcher.group("address"), false, "f");
                break;
            case 9:
                onlineDuel.select(commandMatcher.group("address"), true, "h");
                break;
            case 10:
                onlineDuel.deSelect();
                break;
            case 11:
                onlineDuel.changePhase();
                break;
            case 12:
                onlineDuel.summon();
                break;
            case 13:
                onlineDuel.setMonster();
                onlineDuel.setSpellAndTrap();
                break;
            case 14:
                onlineDuel.setPosition(commandMatcher.group("mode"));
                break;
            case 15:
                onlineDuel.flipSummon();
                break;
            case 16:
                onlineDuel.attack(commandMatcher.group("address"));
                break;
            case 17:
                onlineDuel.attackDirect();
                break;
            case 18:
                onlineDuel.activateSpellCard();
                break;
            case 19:
                onlineDuel.showGraveyard();
                break;
            case 20:
                onlineDuel.showCard();
                break;
            case 21:
                onlineDuel.increaseLP(Integer.parseInt(commandMatcher.group("amount")));
                break;
            case 22:
                onlineDuel.cheatForWinGame(commandMatcher.group("nickname"));
            case 23:
                onlineDuel.showBoard();
            case 24:
                onlineDuel.showTurn();
        }
    }


    public void runChangeHand() {
        String command = "";
        while (!command.equals("end")) {
            command = ConsoleBasedMenus.scanner.nextLine().replaceAll("\\s+", " ");

            commandMatcher = findMatcher(command, changeDeckRegexes[0]);
            if (commandMatcher.find())
                DuelMenuController.getInstance().showMainDeck(onlineDuel.getOnlinePlayer());

            commandMatcher = findMatcher(command, changeDeckRegexes[1]);
            if (commandMatcher.find())
                DuelMenuController.getInstance().showSideDeck(onlineDuel.getOnlinePlayer());

            commandMatcher = findMatcher(command, changeDeckRegexes[2]);
            if (commandMatcher.find())
                DuelMenuController.getInstance().swapCard(onlineDuel.getOnlinePlayer(),
                        commandMatcher.group("card1"), commandMatcher.group("card2"));

            commandMatcher = findMatcher(command, changeDeckRegexes[3]);
            if (commandMatcher.find())
                break;

        }

    }


    public String getAddressForTribute() {
        String address;
        Output.getInstance().showMessage("enter Address For tribute: ");
        address = ConsoleBasedMenus.scanner.nextLine();
        return address;
    }

    public String getAddressForDeleteCard() {
        String address;
        Output.getInstance().showMessage("enter Address For delete card: ");
        address = ConsoleBasedMenus.scanner.nextLine();
        return address;
    }

    public boolean yesOrNoQuestion() {
        while (true) {
            String command = ConsoleBasedMenus.scanner.nextLine().replaceAll("\\s+", "");
            if (command.equals("yes")) return true;
            else if (command.equals("no")) return false;
        }
    }

    public boolean backQ() {
        Output.getInstance().showMessage("\ntype \"back\" to return ");
        while (true) {
            String command = ConsoleBasedMenus.scanner.nextLine().replaceAll("\\s+", " ");
            if (command.equals("back")) return true;
        }
    }


    private Matcher findMatcher(String input, String regex) {

        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }

}
