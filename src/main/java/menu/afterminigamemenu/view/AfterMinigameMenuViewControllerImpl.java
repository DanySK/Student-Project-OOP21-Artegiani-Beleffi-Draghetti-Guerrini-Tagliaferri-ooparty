package menu.afterminigamemenu.view;

import java.util.List;
import java.util.Random;

import game.player.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import menu.MenuController;
import utils.controller.GenericController;

public class AfterMinigameMenuViewControllerImpl implements AfterMinigameMenuViewController {

    private static final int VBOX_WIDTH = 200;
    private static final int VBOX_HEIGHT = 200;
    private static final int MAX_COINS_FROM_MINIGAME = 10;

    private MenuController menuController;
    private List<Player> players;
    @FXML
    private List<VBox> vBoxes;

    @Override
    public final void setController(final GenericController controller) throws IllegalArgumentException {
        if (controller instanceof MenuController) {
            this.menuController = (MenuController) controller;
        } else {
            throw new IllegalArgumentException("The parameter must be an instance of MenuController");
        }
    }

    @Override
    public final void makeLeaderboard(final List<Player> players) {
        for (int i = 0; i < players.size(); i++) {
            VBox v = this.vBoxes.get(i);
            v.setBorder(Border.stroke(players.get(i).getColor()));
            v.setPrefSize(VBOX_WIDTH, VBOX_HEIGHT);

            Label position = (Label) v.getChildren().get(0);
            position.setText(this.getPositionFromIndex(i));

            Label playerNickname = (Label) v.getChildren().get(1);
            playerNickname.setText(players.get(i).getNickname());

            Label coinsEarned = (Label) v.getChildren().get(2);
            coinsEarned.setText(this.earnCoins(players, i) + " coins earned");
        }
    }

    private int earnCoins(final List<Player> players, final int i) {
        Random r = new Random();
        int coins = r.nextInt(MAX_COINS_FROM_MINIGAME / (i + 1)) + (MAX_COINS_FROM_MINIGAME / (i + 2));
        players.get(i).earnCoins(coins);
        return coins;
    }

    private String getPositionFromIndex(final int i) {
        if (i == 0) {
            return "1st";
        } else if (i == 1) {
            return "2nd";
        } else if (i == 2) {
            return "3rd";
        }
        return (i + 1) + "th";
    }

    @FXML
    private void exitMenu() {
        this.menuController.exit();
    }
}
