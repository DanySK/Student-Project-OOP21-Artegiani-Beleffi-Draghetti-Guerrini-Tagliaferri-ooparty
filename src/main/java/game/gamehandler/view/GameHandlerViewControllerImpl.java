package game.gamehandler.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import game.gamehandler.controller.GameHandlerController;
import game.map.CoinsGameMapSquare;
import game.map.DamageGameMapSquare;
import game.map.GameMap;
import game.map.GameMapSquare;
import game.map.PowerUpGameMapSquare;
import game.map.StarGameMapSquare;
import game.player.Player;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.util.Duration;
import utils.controller.GenericController;
import utils.enums.OrdinalNumber;
import utils.enums.PlayerTurnProgress;
import utils.enums.TurnProgress;
import utils.readers.MapLayoutReader;
import utils.view.GenericViewController;

public class GameHandlerViewControllerImpl implements GenericViewController {

    private static final int SQUARE_WIDTH = 87;
    private static final int SQUARE_HEIGHT = 74;
    private static final int MAP_WIDTH = 11;
    private static final int MAP_HEIGHT = 8;
    private static final int PLAYER_X_START = -825;
    private static final int PLAYER_Y_START = -600;
    /*
     * private static final int COIN_DIM = 25; private static final int STAR_DIM =
     * 25; private static final int POWERUP_DIM = 25; private static final int
     * DAMAGE_ICON_DIM = 25;
     */
    private static final int ICON_DIM = 25;

    private GameHandlerController controller;

    @FXML
    private Group banner;
    @FXML
    private Text bannerText;
    @FXML
    private Group avatars;
    @FXML
    private HBox rankPlayersContainer;
    @FXML
    private HBox turnOrderContainer;
    @FXML
    private GridPane mapGrid;
    @FXML
    private StackPane stackPaneContainer;
    @FXML
    private Label updatesLabel;

    private final Map<String, Group> playerToAvatar = new HashMap<String, Group>();

    @Override
    public final void setController(final GenericController controller) {
        if (controller instanceof GameHandlerController) {
            this.controller = (GameHandlerController) controller;
        } else {
            throw new IllegalArgumentException("The parameter must be an instance of GameHandlerController");
        }
    }

    public final void initialize(final List<Player> players, final GenericController controller) {
        this.setController(controller);

        List<Group> avatarsList = new ArrayList<Group>();
        this.avatars.getChildren().forEach(c -> {
            avatarsList.add((Group) c);
        });
        players.forEach(p -> {
            this.playerToAvatar.put(p.getNickname(), avatarsList.get(players.indexOf(p)));
            avatarsList.get(players.indexOf(p)).getChildren().forEach(c -> {
                if (c instanceof Circle) {
                    Circle head = (Circle) c;
                    head.setFill(p.getColor());
                }
                if (c instanceof Polygon) {
                    Polygon body = (Polygon) c;
                    body.setFill(p.getColor());
                }
            });
        });
        avatarsList.forEach(a -> {
            if (!this.playerToAvatar.values().contains(a)) {
                a.setVisible(false);
            }
        });

        this.updateTurnOrder(players);
        this.updateLeaderboard(players);

        this.initializeMap(this.controller.getGameMap());

        final List<Point2D> squarePositions = mapGrid.getChildren().stream().filter(l -> l instanceof Label)
                .map(l -> new Point2D(mapGrid.getLayoutX() + GridPane.getRowIndex(l) * SQUARE_WIDTH,
                        mapGrid.getLayoutY() + GridPane.getColumnIndex(l) * SQUARE_HEIGHT))
                .collect(Collectors.toList());

        /*
         * avatarsList.forEach(a -> { a.setLayoutX(PLAYER_X_START);
         * a.setLayoutY(PLAYER_Y_START); });
         */
        avatarsList.forEach(a -> System.out.println(a.isVisible() + ": " + a.getLayoutX() + " " + a.getLayoutY()));
    }

    @FXML
    protected final void onKeyPressed(final KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER) || ke.getCode().equals(KeyCode.SPACE)) {
            this.nextStep();
        }
        if (ke.getCode().equals(KeyCode.ESCAPE)) {
            this.controller.pauseMenu();
        }
    }

    @FXML
    protected final void onClick() {
        this.nextStep();
    }

    private void nextStep() {
        Optional<TurnProgress> progress = this.controller.nextStep();

        if (progress.isPresent()) {
            if (progress.get() == TurnProgress.SHOW_BANNER) {
                this.playerToAvatar.forEach((a, b) -> {
                    System.out.println(a + " " + b.toString());
                });
                this.showBanner("Turn " + this.controller.getTurnNumber());
                updateTurnOrder(this.controller.getTurnOrder());
            } else if (progress.get() == TurnProgress.HIDE_BANNER) {
                this.hideBanner();
            } else if (progress.get() == TurnProgress.PLAYERS_TURNS) {

                Optional<PlayerTurnProgress> playerProgress = this.controller.nextPlayerTurnStep();

                if (playerProgress.isPresent()) {
                    if (playerProgress.get() == PlayerTurnProgress.SHOW_BANNER) {
                        this.showBanner(this.controller.getCurrentPlayer().get().getNickname() + "'s turn");
                    } else if (playerProgress.get() == PlayerTurnProgress.HIDE_BANNER) {
                        this.hideBanner();
                    } else if (playerProgress.get() == PlayerTurnProgress.MOVE_PLAYER) {
                        Player currentPlayer = this.controller.getCurrentPlayer().get();
                        this.movePlayer(currentPlayer);
                        if (this.controller.getGameMap().getPlayerPosition(currentPlayer).isCoinsGameMapSquare()) {
                            this.setUpdatesLabel(currentPlayer.getNickname() + " earned "
                                    + currentPlayer.getLastEarnedCoins() + " coins!");
                        } else if (this.controller.getGameMap().getPlayerPosition(currentPlayer)
                                .isDamageGameMapSquare()) {
                            this.setUpdatesLabel(currentPlayer.getNickname() + " lost "
                                    + currentPlayer.getLastDamageTaken() + " life points!");
                            if (currentPlayer.isDead()) {
                                this.setUpdatesLabel(this.updatesLabel.getText() + " He died!");
                            }
                        } else if (this.controller.getGameMap().getPlayerPosition(currentPlayer)
                                .isStarGameMapSquare()) {
                            if (currentPlayer.getIsLastStarEarned()) {
                                this.setUpdatesLabel(currentPlayer.getNickname() + " earned a star!");
                            } else {
                                this.setUpdatesLabel(
                                        currentPlayer.getNickname() + " didn't have enough coins to buy a star!");
                            }
                        } else if (this.controller.getGameMap().getPlayerPosition(currentPlayer)
                                .isPowerUpGameMapSquare()) {
                            this.setUpdatesLabel(currentPlayer.getNickname() + " got a new powerup!");
                        }

                        this.controller.checkPlayerDeath(currentPlayer);
                        this.updateLeaderboard(this.controller.getLeaderboard());
                    }
                }
            } else if (progress.get() == TurnProgress.PLAY_MINIGAME) {
                this.setUpdatesLabel("");
            } else if (progress.get() == TurnProgress.END_OF_TURN) {
                this.updateLeaderboard(this.controller.getLeaderboard());
            }
        } else {
            // TODO game end
        }
    }

    private void setUpdatesLabel(final String text) {
        this.updatesLabel.setText(text);
    }

    private void showBanner(final String text) {
        bannerText.setText(text);
        FadeTransition fade = new FadeTransition(Duration.millis(1000), banner);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void hideBanner() {
        FadeTransition fade = new FadeTransition(Duration.millis(1000), banner);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.play();
    }

    private void movePlayer(final Player p) {
        TranslateTransition transition = new TranslateTransition();
        Group avatar = this.playerToAvatar.get(p.getNickname());
        transition.setNode(avatar);
        transition.setDuration(Duration.millis(1000));
        transition.setFromX(avatar.getTranslateX());
        transition.setFromY(avatar.getTranslateY());
        // ogni tanto dà NullPointerException
        transition.setToX(this.mapGrid.getChildren()
                .get(this.controller.getGameMap().getSquares()
                        .indexOf(this.controller.getGameMap().getPlayerPosition(p)))
                .getLayoutX());
        transition.setToY(this.mapGrid.getChildren()
                .get(this.controller.getGameMap().getSquares()
                        .indexOf(this.controller.getGameMap().getPlayerPosition(p)))
                .getLayoutY());
        transition.play();
    }

    private void updateLeaderboard(final List<Player> players) {
        rankPlayersContainer.getChildren().removeAll(rankPlayersContainer.getChildren());
        players.forEach(p -> {
            VBox box = new VBox();
            Label nicknameLabel = new Label(p.getNickname());
            Label coinsLabel = new Label("Coins: " + p.getCoinsCount());
            Label starsLabel = new Label("Stars: " + p.getStarsCount());
            Label hpLabel = new Label("Life points: " + p.getLifePoints());
            Label rankLabel = new Label(OrdinalNumber.values()[players.indexOf(p)].getTextFormat());

            box.setBorder(new Border(
                    new BorderStroke(p.getColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
            box.setPrefWidth(100);

            box.getChildren().addAll(nicknameLabel, coinsLabel, starsLabel, hpLabel, rankLabel);

            rankPlayersContainer.getChildren().add(box);
        });

        // rankPlayersContainer.setSpacing(20);
    }

    private void updateTurnOrder(final List<Player> players) {
        turnOrderContainer.getChildren().removeAll(turnOrderContainer.getChildren());
        players.forEach(p -> {
            Label l = new Label();
            l.setBackground(new Background(new BackgroundFill(p.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
            l.setPrefWidth(15);
            l.setPrefHeight(15);
            turnOrderContainer.getChildren().add(l);
        });
    }

    private void initializeMap(final GameMap map) {
        MapLayoutReader reader = new MapLayoutReader();
        var layoutType = map.getLayout();
        var layout = reader.loadMapLayout(layoutType); // list of Point2D

        map.getSquares().stream().map(s -> {
            var img = getImage(s);
            Label label = new Label();

            if (img.isPresent()) {
                ImageView view = new ImageView(img.get());
                view.setFitHeight(ICON_DIM);
                view.setPreserveRatio(true);
                label.setGraphic(view);
            } else {
                if (map.getSquares().indexOf(s) == 0) {
                    label.setText("Start");
                }
            }

            label.setId(map.getSquares().indexOf(s) + "");

            label.setPrefWidth(SQUARE_WIDTH);
            label.setPrefHeight(SQUARE_HEIGHT);
            label.setAlignment(Pos.CENTER);
            label.setBorder(new Border(
                    new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
            label.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));

            return label;
        }).forEach(l -> {
            var index = Integer.parseInt(l.getId());
            mapGrid.getChildren().add(l);
            GridPane.setRowIndex(l, (int) layout.get(index).getY());
            GridPane.setColumnIndex(l, (int) layout.get(index).getX());
            GridPane.setHalignment(l, HPos.CENTER);
            GridPane.setValignment(l, VPos.CENTER);
        });

        mapGrid.setHgap(2);
        mapGrid.setVgap(2);
    }

    private Optional<Image> getImage(final GameMapSquare s) {
        if (s.getClass().equals(CoinsGameMapSquare.class)) {
            return Optional.of(new Image("game/coin.png"));
        }
        if (s.getClass().equals(DamageGameMapSquare.class)) {
            return Optional.of(new Image("game/damage.png"));
        }
        if (s.getClass().equals(PowerUpGameMapSquare.class)) {
            return Optional.of(new Image("game/powerup.png"));
        }
        if (s.getClass().equals(StarGameMapSquare.class)) {
            return Optional.of(new Image("game/star.png"));
        }
        return Optional.empty();
    }
}
