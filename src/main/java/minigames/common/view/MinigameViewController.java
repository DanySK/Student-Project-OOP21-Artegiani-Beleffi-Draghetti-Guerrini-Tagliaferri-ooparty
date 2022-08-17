package minigames.common.view;

import game.player.Player;
import utils.view.GenericViewController;

/**
 * This interface models a minigame view controller.
 */
public interface MinigameViewController extends GenericViewController {

    /**
     * This method starts the minigame next turn.
     */
    void startNextTurn();

    /**
     * This method sets the player label with the current player nickname and color.
     * 
     * @param player the current player
     */
    void setPlayerLabelText(Player player);

    /**
     * This method sets the player avatar color with the current player color.
     *
     * @param player the current player
     */
    void setPlayerAvatarColor(Player player);

}
