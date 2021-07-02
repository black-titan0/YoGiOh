package controller.menus;

import models.Database;
import controller.ErrorChecker;
import models.Player;


public class RegisterMenuController {

    private RegisterMenuController() {
    }

    private static RegisterMenuController instance;

    public static RegisterMenuController getInstance() {
        if (instance == null)
            instance = new RegisterMenuController();
        return instance;
    }

    public String createUser(String username, String nickname, String password) {
        if (ErrorChecker.doesUsernameExist(username))
            return "Error: user with username " + username + " already exists";
        if (ErrorChecker.doesNicknameExist(nickname))
            return "Error: user with nickname " + nickname + " already exists";


        new Player(username, nickname, password);
        return "Success: user created successfully!";
    }

    public String login(String username, String password) {

        if (!ErrorChecker.doesUsernameExist(username))
            return "username and password didn't match!";

        Player player = Database.getInstance().getPlayerByUsername(username);
        if (!ErrorChecker.isPasswordCorrect(player, password))
            return "username and password didn't match!";

        MainMenuController.getInstance().setPlayerLoggedIn(player);
        return "Success: user loggedIn successfully!";
    }
}
