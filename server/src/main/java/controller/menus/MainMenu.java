package controller.menus;

import models.Player;

public class MainMenu {


    private Player playerLoggedIn;

    private MainMenu() {
    }

    private static MainMenu instance;

    public static MainMenu getInstance() {
        if (instance == null)
            instance = new MainMenu();
        return instance;
    }

    public Player getPlayerLoggedIn() {
        return playerLoggedIn;
    }

    public void setPlayerLoggedIn(Player playerLoggedIn) {
        this.playerLoggedIn = playerLoggedIn;
    }


}
