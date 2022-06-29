package menu.mainmenu.control;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import menu.mainmenu.model.MainMenuModel;
import menu.mainmenu.model.MainMenuModelImpl;
import menu.mainmenu.view.MainMenuView;
import menu.mainmenu.view.MainMenuViewImpl;
import utils.graphics.StageManager;

/**
 * Implementation of {@link MainMenuController}.
 */
public class MainMenuControllerImpl<S> implements MainMenuController<S> {

	private MainMenuView<S> menuView;
	private MainMenuModel menuModel;
	private StageManager<S> stageManager;
	@FXML private Button exitButton;
	@FXML private Button createGameButton;

	public MainMenuControllerImpl(final StageManager<S> s) {
		super();
		this.stageManager = s;
		this.menuModel = new MainMenuModelImpl();
		this.menuView = new MainMenuViewImpl<>(s);
	}
	
	@Override
	public void start() {
        this.menuView.createMainMenu();
	}
	
	@Override
	public void exitGame() {
		this.menuModel.exit();
	}
	
	@Override
	public void createGame() {
		this.menuModel.setMenuView(this.menuView);
		this.menuModel.gameCreationMenu();
	}

}
