package menu.mainmenu.control;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import menu.mainmenu.model.MainMenuModel;
import menu.mainmenu.model.MainMenuModelImpl;
import menu.mainmenu.view.MainMenuView;
import menu.mainmenu.view.MainMenuViewImpl;

/**
 * Implementation of {@link MainMenuController}.
 */
public class MainMenuControllerImpl implements MainMenuController {

	private MainMenuView menuView;
	private MainMenuModel menuModel;
	@FXML private Button exitButton;
	@FXML private Button createGameButton;
	
	public MainMenuControllerImpl() {
		super();
		this.menuView = new MainMenuViewImpl();
		this.menuModel = new MainMenuModelImpl();
	}
	
	@Override
	public void start() {
        this.menuView.run(null);
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
