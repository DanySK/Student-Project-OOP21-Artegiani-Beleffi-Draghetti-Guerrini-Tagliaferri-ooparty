package game.gamehandler.controller;

import java.util.List;
import java.util.Optional;

import game.dice.controller.DiceController;
import game.gamehandler.model.GameHandlerModel;
import game.gamehandler.model.GameHandlerModelImpl;
import game.map.GameMap;
import game.gamehandler.view.GameHandlerViewControllerImpl;
import game.player.Player;
import menu.afterminigamemenu.controller.AfterMinigameMenuControllerImpl;
import menu.pausemenu.controller.PauseMenuControllerImpl;
import utils.controller.GenericController;
import utils.controller.GenericControllerAbstr;
import utils.graphics.controller.StageManager;
import utils.model.GenericModel;
import utils.view.GenericViewUtils;
import utils.view.GenericViewController;
import utils.enums.PlayerTurnProgress;
import utils.enums.TurnProgress;

public class GameHandlerControllerImpl<S> extends GenericControllerAbstr
        implements GenericController, GameHandlerController {

    private GameHandlerViewControllerImpl viewController;
    private GameHandlerModel model;
    private DiceController dice;

    public <S, U> GameHandlerControllerImpl(final StageManager<S> s, final DiceController diceController, final GameHandlerModel model) {
        super(s);
        this.model = model;
        this.dice = diceController;
    }

    @Override
    public final void start() {
        GenericViewUtils.createScene(this.getStageManager(), this, "game/game.fxml");
    }

    @Override
    public final void setViewController(final GenericViewController viewController) {
        if (viewController instanceof GameHandlerViewControllerImpl) {
            this.viewController = (GameHandlerViewControllerImpl) viewController;
            this.viewController.initialize(this.model.getPlayers(), this);
        } else {
            throw new IllegalArgumentException("The parameter must be an instance of GameHandlerViewControllerImpl");
        }
    }

    @Override
    public final GenericViewController getViewController() {
        return this.viewController;
    }

    @Override
    public final Optional<TurnProgress> nextStep() {
        return this.model.nextStep();
    }

    @Override
    public final Optional<PlayerTurnProgress> nextPlayerTurnStep() {
        var nextPlayerTurnStep = this.model.nextPlayerTurnStep();
        if (nextPlayerTurnStep.isPresent() && nextPlayerTurnStep.get() == PlayerTurnProgress.ROLL_DICE) {
            this.dice.start(this.getCurrentPlayer().get());
        }
        return nextPlayerTurnStep;
    }

    @Override
    public final int getTurnNumber() {
        return this.model.getTurnNumber();
    }

    @Override
    public final Optional<Player> getCurrentPlayer() {
        return this.model.getCurrentPlayer();
    }

    @Override
    public final GameMap getGameMap() {
        return this.model.getGameMap();
    }

    @Override
    public final List<Player> getPlayers() {
        return this.model.getPlayers();
    }

    @Override
    public final List<Player> getLeaderboard() {
        return this.model.getLeaderboard();
    }

    @Override
    public final List<Player> getTurnOrder() {
        return this.model.getTurnOrder();
    }

    @Override
    public final void pauseMenu() {
        PauseMenuControllerImpl pauseMenuController = new PauseMenuControllerImpl(this.getStageManager());
        pauseMenuController.createMenu();
    }

    public final void checkPlayerDeath(final Player p) {
        this.model.checkPlayerDeath(p);
    }

    @Override
    public final void showAfterMinigameMenu() {
        AfterMinigameMenuControllerImpl afterMinigameMenuControllerImpl = new AfterMinigameMenuControllerImpl(this.getStageManager());
        afterMinigameMenuControllerImpl.createMenu();
        afterMinigameMenuControllerImpl.makeLeaderboard(this.getTurnOrder());
    }

}
