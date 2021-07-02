package models;

import serverConection.Output;

import java.util.ArrayList;


public class Scoreboard {

    private Scoreboard() {
    }

    private static Scoreboard instance;

    public static Scoreboard getInstance() {
        if (instance == null)
            instance = new Scoreboard();
        return instance;
    }

    public void showScoreboard() {
        int counter = 1, index = 0, previousScore = -1;
        StringBuilder output = new StringBuilder();
        ArrayList<Player> allUsers = Database.allPlayers;
        allUsers.sort(Player::compareTo);
        for (Player player : allUsers) {


            if (player.getScore() != previousScore) {
                index += counter;
                counter = 1;
            } else counter++;
            output.append(index).append(". ").append(player.getNickname()).append(": ").append(player.getScore()).append("\n");
            previousScore = player.getScore();

        }
        Output.getInstance().showMessage(output.toString());
    }

}
