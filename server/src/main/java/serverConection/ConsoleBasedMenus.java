package serverConection;

import controller.ErrorChecker;
import controller.menus.*;
import models.Player;
import models.Scoreboard;

import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ConsoleBasedMenus {
    public static Scanner scanner = new Scanner(System.in);
    private static ConsoleBasedMenus instance;
    private final String[] registerMenusRegexes = {
            "^user create (--username|-u) (?<username>\\w+) (--nickname|-n) (?<nickname>\\w+) (--password|-p) (?<password>\\w+)$",
            "^user create (--username|-u) (?<username>\\w+) (--password|-p) (?<password>\\w+) (--nickname|-n) (?<nickname>\\w+)$",
            "^user create (--password|-p) (?<password>\\w+) (--nickname|-n) (?<nickname>\\w+) (--username|-u) (?<username>\\w+)$",
            "^user create (--password|-p) (?<password>\\w+) (--username|-u) (?<username>\\w+) (--nickname|-n) (?<nickname>\\w+)$",
            "^user create (--nickname|-n) (?<nickname>\\w+) (--password|-p) (?<password>\\w+) (--username|-u) (?<username>\\w+)$",
            "^user create (--nickname|-n) (?<nickname>\\w+) (--username|-u) (?<username>\\w+) (--password|-p) (?<password>\\w+)$",
            "^user login (--username|-u) (?<username>\\w+) (--password|-p) (?<password>\\w+)$",
            "^user login (--password|-p) (?<password>\\w+) (--username|-u) (?<username>\\w+)$",
            "^menu enter (?<name>view.Main|Deck|Duel|Profile|Scoreboard|Shop)$",
            "^menu show-current$",
            "^menu exit$"
    };

    private final String[] mainMenusRegexes = {
            "^menu enter (?<name>Duel|Deck|Scoreboard|Profile|Shop|ImpExp)$",
            "^user logout$",
            "^menu show-current$",
            "^menu exit$"
    };

    private final String[] profileMenusRegexes = {
            "^profile change (--nickname|-n) (?<nickname>\\w+)$",
            "^profile change (--password|-p) --current (?<oldPass>\\w+) --new (?<newPass>\\w+)$",
            "^profile change (--password|-p) --new (?<newPass>\\w+) --current (?<oldPass>\\w+)$",
            "^menu show-current$",
            "^menu exit$"
    };
    private final String[] deckMenuRegexes = {
            "^deck create (?<name>\\w+)$",
            "^deck delete (?<name>\\w+)$",
            "^deck set-activate (?<name>\\w+)$",
            "^deck add-card (?:--card|-c) (?<cardName>.+) (?:--deck|-d) (?<deckName>.+) (?:--side|-s)$",
            "^deck add-card (?:--card|-c) (?<cardName>.+) (?:--side|-s) (?:--deck|-d) (?<deckName>.+)$",
            "^deck add-card (?:--deck|-d) (?<deckName>.+) (?:--card|-c) (?<cardName>.+) (?:--side|-s)$",
            "^deck add-card (?:--deck|-d) (?<deckName>.+) (?:--side|-s) (?:--card|-c) (?<cardName>.+)$",
            "^deck add-card (?:--side|-s) (?:--deck|-d) (?<deckName>.+) (?:--card|-c) (?<cardName>.+)$",
            "^deck add-card (?:--side|-s) (?:--card|-c) (?<cardName>.+) (?:--deck|-d) (?<deckName>.+)$",
            "^deck add-card (?:--card|-c) (?<cardName>.+) (?:--deck|-d) (?<deckName>.+)$",
            "^deck add-card (?:--deck|-d) (?<deckName>.+) (?:--card|-c) (?<cardName>.+)$",
            "^deck rm-card (?:--card|-c) (?<cardName>.+) (?:--deck|-d) (?<deckName>.+) (?:--side|-s)$",
            "^deck rm-card (?:--card|-c) (?<cardName>.+) (?:--side|-s) (?:--deck|-d) (?<deckName>.+)$",
            "^deck rm-card (?:--deck|-d) (?<deckName>.+) (?:--card|-c) (?<cardName>.+) (?:--side|-s)$",
            "^deck rm-card (?:--deck|-d) (?<deckName>.+) (?:--side|-s) (?:--card|-c) (?<cardName>.+)$",
            "^deck rm-card (?:--side|-s) (?:--deck|-d) (?<deckName>.+) (?:--card|-c) (?<cardName>.+)$",
            "^deck rm-card (?:--side|-s) (?:--card|-c) (?<cardName>.+) (?:--deck|-d) (?<deckName>.+)$",
            "^deck rm-card (?:--card|-c) (?<cardName>.+) (?:--deck|-d) (?<deckName>.+)$",
            "^deck rm-card (?:--deck|-d) (?<deckName>.+) (?:--card|-c) (?<cardName>.+)$",
            "^deck show (?:--all|-a)$",
            "^deck show (?:--deck-name|-d) (?<deckName>.+) (?:--side|-s)$",
            "^deck show (?:--side|-s) (?:--deck-name|-d) (?<deckName>.+)$",
            "^deck show (?:--deck-name|-d) (?<deckName>.+)$",
            "^menu show-current$",
            "^menu exit$"
    };
    private final String[] shoppingMenusRegexes = {
            "^shop buy (?<cardName>.+)$",
            "^shop show --all$",
            "shop show money$",
            "^menu show-current$",
            "^menu exit$",
            "^increase (--money|-m) (?<amount>\\d+)"
    };
    private final String[] duelMenusRegexes = {
            "^duel new --second-player (?<username>\\w+) (--rounds|-r) (?<round>\\d+)$",
            "^duel new (--rounds|-r) (?<round>\\d+) --second-player (?<username>\\w+)$",
            "^duel new --single-player (--rounds|-r) (?<round>\\d+)$",
            "^duel new (--rounds|-r) (?<round>\\d+) --single-player$",
            "^menu show-current$",
            "^menu exit$"
    };

    private final String[] impExpMenusRegexes = {
            "^import card (?<name>.+)$",
            "^export card (?<name>.+)$",
            "^menu show-current$",
            "^menu exit$"
    };
    private String runningMenu = "register";

    private ConsoleBasedMenus() {
    }

    public static ConsoleBasedMenus getInstance() {
        if (instance == null)
            instance = new ConsoleBasedMenus();
        return instance;
    }


    public void runRegisterMenu() throws CloneNotSupportedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Matcher commandMatcher;
        String command;
        while (runningMenu.equals("register")) {
            command = scanner.nextLine().replaceAll("\\s+", " ");
            if (command.equals("end debugging"))
                return;
            int whichCommand;
            for (whichCommand = 0; whichCommand < registerMenusRegexes.length; whichCommand++) {
                commandMatcher = findMatcher(command, registerMenusRegexes[whichCommand]);
                if (commandMatcher.find()) {
                    executeRegisterMenuCommands(commandMatcher, whichCommand);
                    break;
                } else if (whichCommand == registerMenusRegexes.length - 1)
                    Output.getInstance().showMessage("invalid command");
            }
        }
    }

    private void executeRegisterMenuCommands(Matcher commandMatcher, int whichCommand) throws CloneNotSupportedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        switch (whichCommand) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                RegisterMenuController.getInstance().createUser(commandMatcher.group("username"),
                        commandMatcher.group("nickname"), commandMatcher.group("password"));
                break;
            case 6:
            case 7:
                RegisterMenuController.getInstance().login(commandMatcher.group("username"), commandMatcher.group("password"));
                break;
            case 8:
                if (commandMatcher.group("name").equals("view.Main")) {
                    if (!ErrorChecker.isUserLoggedIn())
                        Output.getInstance().showMessage("please login first");
                    else {
                        runningMenu = "main";
                        runMainMenu();
                    }
                } else Output.getInstance().showMessage("menu navigation is not possible");
                break;
            case 9:
                Output.getInstance().showMessage("Register Menu");
                break;
            case 10:
                runningMenu = "end";
        }
    }

    public void runMainMenu() throws CloneNotSupportedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Matcher commandMatcher;
        String command;
        while (runningMenu.equals("main")) {
            command = scanner.nextLine().replaceAll("\\s+", " ");
            int whichCommand;
            for (whichCommand = 0; whichCommand < mainMenusRegexes.length; whichCommand++) {
                commandMatcher = findMatcher(command, mainMenusRegexes[whichCommand]);
                if (commandMatcher.find()) {
                    executeMainMenuCommands(commandMatcher, whichCommand);
                    break;
                } else if (whichCommand == mainMenusRegexes.length - 1)
                    Output.getInstance().showMessage("invalid command");
            }
        }
    }

    private void executeMainMenuCommands(Matcher commandMatcher, int whichCommand) throws CloneNotSupportedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        switch (whichCommand) {
            case 0:
                String menuName = commandMatcher.group("name");
                if (menuName.equals("Duel")) {
                    runningMenu = "duel";
                    runDuelMenu();
                }
                if (menuName.equals("Deck")) {
                    runningMenu = "deck";
                    runDeckMenu();
                }
                if (menuName.equals("Scoreboard")) {
                    runningMenu = "scoreboard";
                    runScoreboard();
                }
                if (menuName.equals("Profile")) {
                    runningMenu = "profile";
                    runProfileMenu();
                }
                if (menuName.equals("Shop")) {
                    runningMenu = "shopping";
                    runShopping();
                }
                if (menuName.equals("ImpExp")) {
                    runningMenu = "ImpExp";
                    runImpExpMenu();
                }
                break;
            case 3:
            case 1:
                runningMenu = "register";
                break;
            case 2:
                Output.getInstance().showMessage("view.Main Menu");
        }
    }

    public void runDeckMenu() {
        Matcher commandMatcher;
        String command;
        while (runningMenu.equals("deck")) {
            command = scanner.nextLine().replaceAll("\\s+", " ");
            int whichCommand;
            for (whichCommand = 0; whichCommand < deckMenuRegexes.length; whichCommand++) {
                commandMatcher = findMatcher(command, deckMenuRegexes[whichCommand]);
                if (commandMatcher.find()) {
                    executeDeckMenuCommands(commandMatcher, whichCommand);
                    break;
                } else if (whichCommand == deckMenuRegexes.length - 1)
                    Output.getInstance().showMessage("invalid command");
            }

        }
    }

    private void executeDeckMenuCommands(Matcher commandMatcher, int whichCommand) {
        DeckMenuController controller = DeckMenuController.getInstance();
        Player loggedInPlayer = MainMenu.getInstance().getPlayerLoggedIn();
        switch (whichCommand) {
            case 0:
                controller.createDeck(commandMatcher.group("name"), loggedInPlayer);
                break;
            case 1:
                controller.deleteDeck(commandMatcher.group("name"));
                break;
            case 2:
                controller.setActiveDeck(commandMatcher.group("name"), loggedInPlayer);
                break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                String cardName = commandMatcher.group("cardName"),
                        deckName = commandMatcher.group("deckName");
                controller.addCardToDeck(cardName, deckName, loggedInPlayer, false);
                break;
            case 9:
            case 10:
                cardName = commandMatcher.group("cardName");
                deckName = commandMatcher.group("deckName");
                controller.addCardToDeck(cardName, deckName, loggedInPlayer, true);
                break;
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                cardName = commandMatcher.group("cardName");
                deckName = commandMatcher.group("deckName");
                controller.removeCardFromDeck(cardName, deckName, loggedInPlayer, false);
                break;
            case 17:
            case 18:
                cardName = commandMatcher.group("cardName");
                deckName = commandMatcher.group("deckName");
                controller.removeCardFromDeck(cardName, deckName, loggedInPlayer, true);
                break;
            case 19:
                controller.showAllDecks(loggedInPlayer);
                break;
            case 20:
            case 21:
                controller.showDeck(commandMatcher.group("deckName"), loggedInPlayer, false);
                break;
            case 22:
                controller.showDeck(commandMatcher.group("deckName"), loggedInPlayer, true);
                break;
            case 23:
                Output.getInstance().showMessage("Deck Menu");
                break;
            case 24:
                runningMenu = "main";
        }
    }

    public void runScoreboard() {
        String command;
        while (runningMenu.equals("scoreboard")) {
            command = scanner.nextLine().replaceAll("\\s+", " ");
            switch (command) {
                case "scoreboard show":
                    Scoreboard.getInstance().showScoreboard();
                    break;
                case "menu show-current":
                    Output.getInstance().showMessage("Scoreboard Menu");
                    break;
                case "menu exit":
                    runningMenu = "main";
                    return;
                default:
                    Output.getInstance().showMessage("invalid command");
            }
        }
    }

    public void runProfileMenu() {
        Matcher commandMatcher;
        String command;
        while (runningMenu.equals("profile")) {
            command = scanner.nextLine().replaceAll("\\s+", " ");
            int whichCommand;
            for (whichCommand = 0; whichCommand < profileMenusRegexes.length; whichCommand++) {
                commandMatcher = findMatcher(command, profileMenusRegexes[whichCommand]);
                if (commandMatcher.find()) {
                    executeProfileMenuCommands(commandMatcher, whichCommand);
                    break;
                } else if (whichCommand == profileMenusRegexes.length - 1)
                    Output.getInstance().showMessage("invalid command");
            }

        }
    }

    private void executeProfileMenuCommands(Matcher commandMatcher, int whichCommand) {
        Player playerLoggedIn = MainMenu.getInstance().getPlayerLoggedIn();
        switch (whichCommand) {
            case 0:
                ProfileMenuController.getInstance().changeNickname(playerLoggedIn, commandMatcher.group("nickname"));
                break;
            case 1:
            case 2:
                String oldPass = commandMatcher.group("oldPass");
                String newPass = commandMatcher.group("newPass");
                ProfileMenuController.getInstance().changePassword(playerLoggedIn, oldPass, newPass);
                break;

            case 3:
                Output.getInstance().showMessage("Profile Menu");
                break;

            case 4:
                runningMenu = "main";
        }
    }

    public void runShopping() {
        Matcher commandMatcher;
        String command;
        while (runningMenu.equals("shopping")) {
            command = scanner.nextLine().replaceAll("\\s+", " ");
            int whichCommand;
            for (whichCommand = 0; whichCommand < shoppingMenusRegexes.length; whichCommand++) {
                commandMatcher = findMatcher(command, shoppingMenusRegexes[whichCommand]);
                if (commandMatcher.find()) {
                    executeShoppingMenuCommands(commandMatcher, whichCommand);
                    break;
                } else if (whichCommand == shoppingMenusRegexes.length - 1)
                    Output.getInstance().showMessage("invalid command");
            }

        }
    }

    private void executeShoppingMenuCommands(Matcher commandMatcher, int whichCommand) {
        Player playerLoggedIn = MainMenu.getInstance().getPlayerLoggedIn();
        switch (whichCommand) {
            case 0:
                String cardName = commandMatcher.group("cardName");
                ShoppingMenuController.getInstance().buyCard(playerLoggedIn, cardName);
                break;
            case 1:
                ShoppingMenuController.getInstance().showAllCard();
                break;
            case 2:
                ShoppingMenuController.getInstance().showMoney(playerLoggedIn);
                break;
            case 3:
                Output.getInstance().showMessage("shopping Menu");
                break;
            case 4:
                runningMenu = "main";
                return;
            case 5:
                ShoppingMenuController.getInstance().increaseMoney(playerLoggedIn,
                        Integer.parseInt(commandMatcher.group("amount")));
        }
    }

    private void runDuelMenu() throws CloneNotSupportedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Matcher commandMatcher;
        String command;
        while (runningMenu.equals("duel")) {
            if (!scanner.hasNext())
                return;
            command = scanner.nextLine().replaceAll("\\s+", " ");
            int whichCommand;
            for (whichCommand = 0; whichCommand < duelMenusRegexes.length; whichCommand++) {
                commandMatcher = findMatcher(command, duelMenusRegexes[whichCommand]);
                if (commandMatcher.find()) {
                    executeDuelMenuCommands(commandMatcher, whichCommand);
                    break;
                } else if (whichCommand == duelMenusRegexes.length - 1)
                    Output.getInstance().showMessage("invalid command");
            }

        }
    }

    private void executeDuelMenuCommands(Matcher commandMatcher, int whichCommand) throws CloneNotSupportedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Player playerLoggedIn = MainMenu.getInstance().getPlayerLoggedIn();
        String secondUsername, round;
        switch (whichCommand) {
            case 0:
            case 1:
                secondUsername = commandMatcher.group("username");
                round = commandMatcher.group("round");
                DuelMenuController.getInstance().startGame(playerLoggedIn.getUsername(), secondUsername, round, false);
                break;
            case 2:
            case 3:
                round = commandMatcher.group("round");
                DuelMenuController.getInstance().startGame(playerLoggedIn.getUsername(), "ai", round, true);
            case 4:
                Output.getInstance().showMessage("duel Menu");
                break;
            case 5:
                runningMenu = "main";
        }
    }

    private void runImpExpMenu() throws IllegalAccessException {
        Matcher commandMatcher;
        String command;
        while (runningMenu.equals("ImpExp")) {
            command = scanner.nextLine().replaceAll("\\s+", " ");
            int whichCommand;
            for (whichCommand = 0; whichCommand < impExpMenusRegexes.length; whichCommand++) {
                commandMatcher = findMatcher(command, impExpMenusRegexes[whichCommand]);
                if (commandMatcher.find()) {
                    executeImpExpMenuCommands(commandMatcher, whichCommand);
                    break;
                } else if (whichCommand == impExpMenusRegexes.length - 1)
                    Output.getInstance().showMessage("invalid command");
            }
        }
    }

    private void executeImpExpMenuCommands(Matcher commandMatcher, int whichCommand) {
        switch (whichCommand) {
            case 0:
                ImpExpMenuController.getInstance().importFromFile(commandMatcher.group("name"));
                break;
            case 1:
                ImpExpMenuController.getInstance().exportToFile(commandMatcher.group("name"));
                break;
            case 2:
                Output.getInstance().showMessage("import/export Menu");
                break;
            case 3:
                runningMenu = "main";
        }
    }

    private Matcher findMatcher(String input, String regex) {

        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }
}
