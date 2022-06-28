package menu.mainmenu.view;

import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * This interface models the main menu view of the game.
 */
public interface MainMenuView {
	
	/**
	 * This method returns the create game button.
	 * @return the button that opens the create game window
	 */
	Button getGameButton();
	
	/**
	 * This method returns the exit button.
	 * @return the button that exits the game
	 */
	Button getExitButton();
	
	/**
	 * This method sets the game creation button.
	 * @param b game creation button
	 */
	void setGameButton(Button b);

	/**
	 * This method sets the game exit button.
	 * @param b game exit button
	 */
	void setExitButton(Button b);
	
	/**
	 * This method returns the menu primary stage.
	 * @return the stage of the main menu
	 */
	Stage getStage();

	/**
	 * This method starts the javaFX gui.
	 */
	void run(String[] args);
	
}
