package menu.gamecreationmenu.viewcontroller;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import menu.MenuController;
import menu.gamecreationmenu.model.GameCreationMenuModelImpl;
import utils.GenericController;
import utils.GenericViewController;
import utils.NoticeUserAbstr;
import utils.GuiUtils;
import utils.enums.PlayerColor;

/**
 * Extension of {@link NoticeUserAbstr} and implementation of
 * {@link GenericViewController}.
 */
public class GameCreationMenuViewController extends NoticeUserAbstr implements GenericViewController {

    private MenuController menuController;
    @FXML
    private Spinner<Integer> numberOfPlayers;
    @FXML
    private Spinner<Integer> turnsNumber;
    @FXML
    private List<TextField> playersNicknames;
    @FXML
    private List<ComboBox<PlayerColor>> playerColors;
    @FXML
    private List<VBox> playersForms;

    /**
     * Builds a new {@link GameCreationMenuViewController}.
     */
    public GameCreationMenuViewController() {
    }

    @FXML
    private void initialize() {
        GuiUtils.fillColorsBoxes(this.playerColors);
        this.setNumberOfPlayersSpinner();
        this.setTurnsNumberSpinner();
        this.numberOfPlayers.getValueFactory().valueProperty().addListener(value -> this.showPlayersForms());
        this.showPlayersForms();
    }

    /**
     * This method returns to the main menu of the game.
     */
    @FXML
    private void returnToMainMenu() {
        this.menuController.exit();
    }

    /**
     * This method starts a new game.
     */
    @FXML
    private void startGame() {
        this.menuController.goNext();
    }

    @Override
    public final void setController(final GenericController controller) {
        this.menuController = (MenuController) controller;
    }

    /**
     * This method shows only the necessary players forms.
     */
    private void showPlayersForms() {
        this.showForms(this.numberOfPlayers.getValue());
    }

    /**
     * This method shows only the necessary players forms.
     * 
     * @param nPlayers the selected number of players
     */
    private void showForms(final Integer nPlayers) {
        for (int i = GameCreationMenuModelImpl.N_MIN_PLAYERS; i < GameCreationMenuModelImpl.N_MAX_PLAYERS; i++) {
            var form = playersForms.get(i);
            if (i >= nPlayers) {
                GuiUtils.hideForm(form);
            } else {
                GuiUtils.showForm(form);
            }
        }
    }

    /**
     * This method sets the value factory for the number of players spinner.
     */
    private void setNumberOfPlayersSpinner() {
        GuiUtils.setSpinnerControls(this.numberOfPlayers, GameCreationMenuModelImpl.N_MIN_PLAYERS,
                GameCreationMenuModelImpl.N_MAX_PLAYERS);
    }

    /**
     * This method sets the value factory for the number of turns spinner.
     */
    private void setTurnsNumberSpinner() {
        GuiUtils.setSpinnerControls(this.turnsNumber, GameCreationMenuModelImpl.N_MIN_TURNS,
                GameCreationMenuModelImpl.N_MAX_TURNS);
    }

    /**
     * This method gets the players colors values.
     * 
     * @return the list of players colors
     */
    public List<PlayerColor> getColorsValues() {
        final List<PlayerColor> valuesList = new ArrayList<>();
        this.playerColors.forEach(element -> valuesList.add(element.getValue()));
        return valuesList;
    }

    /**
     * This method gets the players nicknames.
     * 
     * @return the list of players nicknames
     */
    public List<String> getPlayersNicknames() {
        final List<String> valuesList = new ArrayList<>();
        this.playersNicknames.forEach(element -> valuesList.add(element.getText()));
        return valuesList;
    }

    /**
     * This method gets the players nicknames.
     * 
     * @return the number of turns chosen
     */
    public int getTurnsNumber() {
        return this.turnsNumber.getValue();
    }

    /**
     * This method gets the number of players.
     * 
     * @return the number of players
     */
    public int getActualNumberOfPlayers() {
        return this.numberOfPlayers.getValue();
    }

}
