package Tarot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

public class View implements Observer {
    public final static int CAMERA_SHIFT_Y = -300;
    public final static int CAMERA_ROTATE = 20;
    public final static int CAMERA_SHIFT_Z = 200;

    private Controller controller;
    private Model model;

    private Group group = new Group();
    private Scene scene = new Scene(group, Model.SCREEN_W, Model.SCREEN_H, true, SceneAntialiasing.DISABLED);
    private PerspectiveCamera camera = new PerspectiveCamera();

    private Map<String, CardView> cardViews = new HashMap<String, CardView>();
    
    
    private MeshView ground = createGround();
    private MeshView createGround(){
    	TriangleMesh mesh = new TriangleMesh();
    	
    	 mesh.getPoints().addAll(
                 0, 40, 2.1f,
                 Model.SCREEN_W, 40, 2.1f,
                 0, Model.SCREEN_H+40, 2.1f,
                 Model.SCREEN_W, Model.SCREEN_H+40, 2.1f
         );
    	 
    	 mesh.getTexCoords().addAll(
         		0f, 0f,
         		1f, 0f,
                0f, 1f,
                1f, 1f
         );
    	 
    	 mesh.getFaces().addAll(
                 0, 0, 3, 3, 1, 1,
                 3, 3, 0, 0, 2, 2
         );
    	
    	Image groundTexture = new Image("file:./res/ground_tarot.png");
    	PhongMaterial groundMaterial = new PhongMaterial();
    	groundMaterial.setDiffuseMap(groundTexture);
    	
    	MeshView view = new MeshView(mesh);
    	view.setMaterial(groundMaterial);
    	
    	return view;
    }
    
    private ImageView menuBackground = new ImageView("file:./res/menu_background.jpg");
    private ImageView menuTitle = new ImageView("file:./res/title.png");
    private ImageView settingsBackground = new ImageView("file:./res/settings_background.png");

    private Button playButton;
    private ImageView playImage = new ImageView("file:./res/play.png");
    private Button settingsButton;
    private ImageView settingsImage = new ImageView("file:./res/settings.png");
    private Button quitButton;
    private ImageView quitImage = new ImageView("file:./res/quit.png");
    private Font menuButtonFont = new Font("Martyric_PersonalUse.ttf", 40);

    private ImageView cardsBackImage = new ImageView("file:./res/cardsback.png");
    private ImageView soundImage = new ImageView("file:./res/sound.png");

    public View(Controller controller) {
        this.controller = controller;
        this.model = controller.getModel();

        group.setDepthTest(DepthTest.ENABLE);

        if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
            throw new RuntimeException("SCENE3D not supported");
        }
        
        scene.setFill(Color.BLACK);
        scene.setCamera(camera);
        camera.setRotationAxis(new Point3D(1, 0, 0));

        ground.setScaleX(1.2);
        ground.setScaleY(1.2);

        initActionButtons();
    }

    public Scene getScene() {
        return scene;
    }

    private void waiter(double duration, EventHandler<ActionEvent> event) {
        Timeline timeLine = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(duration), event);
        timeLine.getKeyFrames().add(keyFrame);
        timeLine.play();
    }

    public void displayMenu() {
        group.getChildren().clear();
        initMenuBackground();
        initMenuButtons();
    }

    public void displaySettings() {
        group.getChildren().clear();
        initSettingsBackground();
        initSettingsButtons();

    }

    public EventHandler<ActionEvent> doNexTActionEvent() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                controller.doNextAction();
            }
        };
    }
    private void initMenuBackground() {
        group.getChildren().add(menuBackground);
        group.getChildren().add(menuTitle);

        double ratioBackImage = Model.SCREEN_H / menuBackground.getImage().getHeight();
        menuBackground.setScaleY(ratioBackImage);
        menuBackground.setScaleX(ratioBackImage);

        menuBackground.setX(Model.SCREEN_W / 2 - menuBackground.getImage().getWidth() / 2);
        menuBackground.setY(Model.SCREEN_H / 2 - menuBackground.getImage().getHeight() / 2);

        menuTitle.setTranslateX(Model.SCREEN_W / 2 - menuTitle.getImage().getWidth() / 2);
        menuTitle.setTranslateY(Model.SCREEN_H / 10);
    }

    private void initSettingsBackground() {
        group.getChildren().add(settingsBackground);
        group.getChildren().add(cardsBackImage);
        group.getChildren().add(soundImage);

        cardsBackImage.setFitWidth(Model.SCREEN_W / 4);
        cardsBackImage.setFitHeight(Model.SCREEN_H / 4);
        cardsBackImage.setTranslateX(Model.SCREEN_W / 10);
        cardsBackImage.setTranslateY(Model.SCREEN_H/10);

        soundImage.setFitWidth(Model.SCREEN_W / 3.5);
        soundImage.setFitHeight(Model.SCREEN_H / 4);
        soundImage.setTranslateX(Model.SCREEN_W / 10);
        soundImage.setTranslateY(Model.SCREEN_H/1.8);

    }

    private Button menuButton(String name, double x, double y) {
        Button button = new Button(name);
        button.setFont(menuButtonFont);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setTextAlignment(TextAlignment.CENTER);
        button.setPrefSize(BUTTON_W + 100, BUTTON_H);
        button.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(50), null)));
        button.setTextFill(Color.WHITE);
        return button;
    }

    private void initMenuButtons() {
        playButton = menuButton("", Model.SCREEN_W / 2 - (BUTTON_W + 100) / 2, Model.SCREEN_H / 3);
        settingsButton = menuButton("", Model.SCREEN_W / 2 - (BUTTON_W + 100) / 2, Model.SCREEN_H / 2 + Model.SCREEN_H / 20);
        quitButton = menuButton("", Model.SCREEN_W / 2 - (BUTTON_W + 100) / 2, Model.SCREEN_H / 2 + Model.SCREEN_H / 3.6);

        playImage.setFitWidth(Model.SCREEN_W / 5);
        playImage.setFitHeight(Model.SCREEN_H / 5);
        playImage.setTranslateX(Model.SCREEN_W / 2 - playImage.getImage().getWidth() / 4);
        playImage.setTranslateY(Model.SCREEN_H / 4 + playImage.getImage().getHeight() / 5);

        settingsImage.setFitWidth(Model.SCREEN_W / 4);
        settingsImage.setFitHeight(Model.SCREEN_H / 4);
        settingsImage.setTranslateX(Model.SCREEN_W / 2 - settingsImage.getImage().getWidth() / 4);
        settingsImage.setTranslateY(Model.SCREEN_H / 2);

        quitImage.setFitWidth(Model.SCREEN_W / 5);
        quitImage.setFitHeight(Model.SCREEN_H / 5);
        quitImage.setTranslateX(Model.SCREEN_W / 2 - quitImage.getImage().getWidth() / 3.5);
        quitImage.setTranslateY(Model.SCREEN_H / 2 + Model.SCREEN_H / 4);

        playButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                initGameView();
            }
        });
        playButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                playImage.setTranslateZ(-400);
                playButton.setTranslateZ(-401);
                playButton.setTranslateY(30);
            }
        });
        playButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                playImage.setTranslateZ(-5);
                playButton.setTranslateZ(-6);
                playButton.setTranslateY(0);
            }
        });
        settingsButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { displaySettings(); }
        });
        settingsButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                settingsImage.setTranslateZ(-400);
                settingsButton.setTranslateZ(-401);
                settingsButton.setTranslateY(-30);
                settingsImage.setTranslateY(Model.SCREEN_H / 2 - 20);
            }
        });
        settingsButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                settingsImage.setTranslateZ(-5);
                settingsButton.setTranslateZ(-6);
                settingsButton.setTranslateY(0);
                settingsImage.setTranslateY(Model.SCREEN_H / 2);
            }
        });
        quitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { System.exit(0); }
        });
        quitButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                quitImage.setTranslateZ(-400);
                quitButton.setTranslateZ(-401);
                quitButton.setTranslateY(-85);
                quitImage.setTranslateY(Model.SCREEN_H / 2 + Model.SCREEN_H / 4 - 85);
            }
        });
       quitButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                quitImage.setTranslateZ(0);
                quitButton.setTranslateZ(0);
                quitButton.setTranslateY(0);
                quitImage.setTranslateY(Model.SCREEN_H / 2 + Model.SCREEN_H / 4);
            }
        });

        group.getChildren().add(playImage);
        group.getChildren().add(settingsImage);
        group.getChildren().add(quitImage);
        group.getChildren().add(playButton);
        group.getChildren().add(settingsButton);
        group.getChildren().add(quitButton);
    }

    private void initSettingsButtons() {

    }

    private void initGameView() {
        group.getChildren().clear();
        camera.setRotate(CAMERA_ROTATE);
        camera.setTranslateZ(CAMERA_SHIFT_Y);
        camera.setTranslateY(CAMERA_SHIFT_Z);
        group.getChildren().add(ground);
        controller.doNextAction();
    }

    @SuppressWarnings("unchecked")
	@Override
    public void update(Observable arg0, Object arg1) {
        switch (((Pair<TarotAction, Object>) arg1).getKey()) {
            case DECK_MIXED:
                updateDeckMixed();
                break;
            case DECK_CUT:
                updateDeckCut(((Pair<TarotAction, Integer>) arg1).getValue(), 1);
                break;
            case CARDS_DISTRIBUTED:
                update3CardsDistributed(((Pair<TarotAction, Pair<Boolean, CardModel[]>>) arg1).getValue());
                break;
            case CARD_MOVED_FROM_DECK:
                moveCardFromDeck(cardViews.get(((Pair<TarotAction, CardModel>) arg1).getValue().getName()),
                        ((Pair<TarotAction, CardModel>) arg1).getValue(), null);
                break;
            case CARD_MOVED:
                moveCard(cardViews.get(((Pair<TarotAction, CardModel>) arg1).getValue().getName()),
                        ((Pair<TarotAction, CardModel>) arg1).getValue(), null);
                break;
            case PLAYER_REVERTED:
                revertDeck(model.getMyCards(), Model.NB_CARDS_PLAYER);
                break;
            case PLAYER_ORGANIZED:
                updatePlayerCardsOrganized();
                break;
            case PETIT_SEC_DETECTED:
                updatePetitSec(((Pair<TarotAction, Boolean>) arg1).getValue());
                break;
            case ACTION_CHOSEN:
                updateActionChosen(((Pair<TarotAction, PlayerAction>) arg1).getValue());
                break;
            case CHIEN_REVERTED:
                revertDeck(model.getChienCards(), Model.CHIEN_SIZE);
                break;
            case CARD_ADDED_GAP:
            	moveCard(cardViews.get(((Pair<TarotAction, CardModel>) arg1).getValue().getName()),
                        ((Pair<TarotAction, CardModel>) arg1).getValue(), unselectView());
            	break;
            case GAP_DONE:
                updateGapDone();
                break;
        }
    }

    public void updateDeckMixed() {
        for (CardModel card : model.getJeu()) {
            cardViews.put(card.getName(), new CardView(card));
            group.getChildren().add(cardViews.get(card.getName()).getMeshView());
        }
        
        //TODO
        PointLight moonLight = new PointLight(Color.BLUE);
        moonLight.setTranslateX(0);
        moonLight.setTranslateY(0);
        moonLight.setTranslateZ(-1500);
        
        PointLight lampLight = new PointLight(Color.LIGHTGOLDENRODYELLOW);
        lampLight.setTranslateX(0);
        lampLight.setTranslateY(0);
        lampLight.setTranslateZ(-500);
        
        Timeline timeLine = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(5), new KeyValue(lampLight.translateXProperty(), Model.SCREEN_W+800), 
        														new KeyValue(lampLight.translateYProperty(), Model.SCREEN_H+700),
        														new KeyValue(lampLight.translateZProperty(), -500));
        timeLine.setAutoReverse(true);
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.getKeyFrames().add(keyFrame);
        timeLine.play();
        
        group.getChildren().addAll(moonLight, lampLight);
        controller.doNextAction();
    }

    public void updateDeckCut(Integer indexHalf, int iteration) {
        EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                updateDeckCut(indexHalf, iteration + 1);
            }
        };
        switch (iteration) {
            case 1:
                moveCutDeck(CardModel.CARD_W / 2 + 10, false, indexHalf, onFinished);
                break;
            case 2:
                moveCutDeck(CardModel.CARD_W / 2 + 10, true, indexHalf, onFinished);
                break;
            case 3:
                moveCutDeck(0, true, indexHalf, doNexTActionEvent());
                break;
        }
    }

    private static final double CUT_SPEED = 0.2;

    private void moveCutDeck(double xShiftValue, boolean trueZ, Integer indexHalf, EventHandler<ActionEvent> onFinished) {
        CardModel card;
        double xShift;
        double z;
        for (int i = 0; i < model.getJeu().size(); i++) {
            card = model.getJeu().get(i);
            if (i < indexHalf) {
                xShift = -xShiftValue;
            } else {
                xShift = xShiftValue;
            }

            if (trueZ) {
                z = card.getZ();
            } else {
                z = cardViews.get(card.getName()).getMeshView().getTranslateZ();
            }

            if (i != model.getJeu().size() - 1) {
                moveCard(cardViews.get(card.getName()), card.getX() + xShift, card.getY(), z, CUT_SPEED, null);
            } else {
                moveCard(cardViews.get(card.getName()), card.getX() + xShift, card.getY(), z, CUT_SPEED, onFinished);
            }
        }
    }

    public void update3CardsDistributed(Pair<Boolean, CardModel[]> arg) {
        moveCardFromDeck(cardViews.get(arg.getValue()[0].getName()), arg.getValue()[0], null);
        moveCardFromDeck(cardViews.get(arg.getValue()[1].getName()), arg.getValue()[1], null);
        moveCardFromDeck(cardViews.get(arg.getValue()[2].getName()), arg.getValue()[2], continueCardDistribution(arg.getKey()));
    }

    private EventHandler<ActionEvent> continueCardDistribution(boolean nextAction) {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                if (!nextAction) {
                    model.distributeCards();
                } else {
                    controller.doNextAction();
                }
            }
        };
    }

    public void updateGapDone() {
        for (CardModel card : model.getMyCards()) {
            removeListeners(cardViews.get(card.getName()));
        }
    }

    private void moveCard(CardView cardView, CardModel card, EventHandler<ActionEvent> onFinished) {
        moveCard(cardView, card.getX(), card.getY(), card.getZ(), onFinished);
    }

    private void moveCard(CardView cardView, CardModel card, double speed, EventHandler<ActionEvent> onFinished) {
        moveCard(cardView, card.getX(), card.getY(), card.getZ(), speed, onFinished);
    }

    private void moveCard(CardView cardView, double x, double y, double z, EventHandler<ActionEvent> onFinished) {
        moveCard(cardView, x, y, z, 1, onFinished);
    }

    private void moveCard(CardView cardView, double x, double y, double z, double speed, EventHandler<ActionEvent> onFinished) {
        double time = calculTime(new double[]{Math.abs(cardView.getMeshView().getTranslateX() - x),
                Math.abs(cardView.getMeshView().getTranslateY() - y),
                Math.abs(cardView.getMeshView().getTranslateZ() - z)}, speed);

        moveMeshView(cardView.getMeshView(), x, y, z, time, onFinished);
    }
    
    private void moveMeshView(MeshView view, double x, double y, double z, double time, EventHandler<ActionEvent> onFinished) {
        Timeline animationMoveCard = new Timeline();

        KeyValue kVMoveX = new KeyValue(view.translateXProperty(), x);
        KeyValue kVMoveY = new KeyValue(view.translateYProperty(), y);
        KeyValue kVMoveZ = new KeyValue(view.translateZProperty(), z);

        Duration duration = Duration.seconds(time);

        KeyFrame keyFrame = new KeyFrame(duration, onFinished, kVMoveX, kVMoveY, kVMoveZ);
        animationMoveCard.getKeyFrames().add(keyFrame);

        animationMoveCard.play();
    }

    public static final double TIME_MULTIPLIER = 1000; // TODO REMETTRE A 1000

    private double calculTime(double[] deltas, double speed) {
        double time = 0;
        for (double d : deltas) {
            time += d;
        }
        time /= (TIME_MULTIPLIER * speed);
        return time;
    }

    private void moveCardFromDeck(CardView cardView, CardModel card, EventHandler<ActionEvent> onFinished2) {
        Point2D firstDest = calculFirstDest(cardView, card);

        EventHandler<ActionEvent> onFinished1 = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                moveCard(cardView, card, onFinished2);
            }
        };
        moveCard(cardView, firstDest.getX(), firstDest.getY(), cardView.getMeshView().getTranslateZ(), onFinished1);
    }

    private Point2D calculFirstDest(CardView cardView, CardModel card) {
        Point2D viewP = new Point2D(cardView.getMeshView().getTranslateX(), cardView.getMeshView().getTranslateY());
        Point2D cardP = new Point2D(card.getX(), card.getY());

        if ((viewP.getX() - cardP.getX()) != 0) {
            double a = calculCoef(viewP, cardP);
            if (a != 0) {
                double b = viewP.getY() - a * viewP.getX();
                if (cardP.getX() - viewP.getX() < 0)
                    return getIntersectionsLigneCircle(a, b, viewP.getX(), viewP.getY(), CardModel.CARD_DIAG).get(0);
                else
                    return getIntersectionsLigneCircle(a, b, viewP.getX(), viewP.getY(), CardModel.CARD_DIAG).get(1);
            } else {
                if (cardP.getX() - viewP.getX() > 0) {
                    return new Point2D(viewP.getX() + CardModel.CARD_DIAG, viewP.getY());
                } else {
                    return new Point2D(viewP.getX() - CardModel.CARD_DIAG, viewP.getY());
                }
            }
        } else {
            if (cardP.getY() - viewP.getY() > 0) {
                return new Point2D(viewP.getX(), Model.DECK_Y + CardModel.CARD_DIAG);
            } else {
                return new Point2D(viewP.getX(), Model.DECK_Y - CardModel.CARD_DIAG);
            }
        }
    }

    private double calculCoef(Point2D a, Point2D b) {
        return (b.getY() - a.getY()) / (b.getX() - a.getX());
    }

    private ArrayList<Point2D> getIntersectionsLigneCircle(double a, double b, double cx, double cy, double r) {
        ArrayList<Point2D> intersections = new ArrayList<Point2D>();

        double A = 1 + a * a;
        double B = 2 * (-cx + a * b - a * cy);
        double C = cx * cx + cy * cy + b * b - 2 * b * cy - r * r;
        double delta = B * B - 4 * A * C;

        if (delta > 0) {
            double x = (-B - (float) Math.sqrt(delta)) / (2 * A);
            double y = a * x + b;
            intersections.add(new Point2D(x, y));

            x = (-B + (float) Math.sqrt(delta)) / (2 * A);
            y = a * x + b;
            intersections.add(new Point2D(x, y));
        } else if (delta == 0) {
            double x = -B / (2 * A);
            double y = a * x + b;

            intersections.add(new Point2D(x, y));
        }
        return intersections;
    }

    public void revertDeck(ArrayList<CardModel> deck, int size) {
        int i = 1;
        for (CardModel card : deck) {
            waiter(0.5 * i, new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    if (deck.indexOf(card) != size - 1)
                        revertCard(cardViews.get(card.getName()), null);
                    else {
                        revertCard(cardViews.get(card.getName()), doNexTActionEvent());
                    }
                }
            });
            i++;
        }
    }

    private final static double REVERT_CARD_DURATION = 3; // TODO REMETTRE A 3
    private final static double REVERT_CARD_Z = -300;

    private void revertCard(CardView cardView, EventHandler<ActionEvent> onFinished) {
        revertCardMove(cardView);

        Timeline revertPlayerAnimation = new Timeline();

        KeyValue kVRevert = new KeyValue(cardView.getMeshView().rotateProperty(), 360);

        KeyFrame revertPlayerKeyFrame = new KeyFrame(Duration.seconds(REVERT_CARD_DURATION), onFinished, kVRevert);

        revertPlayerAnimation.getKeyFrames().add(revertPlayerKeyFrame);

        revertPlayerAnimation.play();
    }

    private void revertCardMove(CardView cardView) {
        EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {

                moveMeshView(cardView.getMeshView(), cardView.getMeshView().getTranslateX(), cardView.getMeshView().getTranslateY(),
                        1.0, REVERT_CARD_DURATION / 2, null);
            }
        };

        moveMeshView(cardView.getMeshView(), cardView.getMeshView().getTranslateX(), cardView.getMeshView().getTranslateY(),
                REVERT_CARD_Z, REVERT_CARD_DURATION / 2, onFinished);
    }

    private final static double ORGANIZE_CARD_TIME = 0.5;

    public void updatePlayerCardsOrganized() {
        CardModel card;
        for (int i = 0; i < model.getMyCards().size(); i++) {
            card = model.getMyCards().get(i);
            moveCard(cardViews.get(card.getName()), card, null);
        }
        waiter(ORGANIZE_CARD_TIME, doNexTActionEvent());
    }

    public void updatePetitSec(Boolean petitSec) {
        if (!petitSec) {
        	drawActionButtons();
        } else {
            nouvelleDonne();
        }
    }

    private static final int BUTTON_W = 350;
    private static final int BUTTON_H = 150;
    private static final int BUTTON_X_DIFF = 10;
    public static final int BUTTON_X_START = (Model.SCREEN_W - (5 * BUTTON_W + 4 * BUTTON_X_DIFF)) / 2;
    public static final int BUTTON_Y = Model.SCREEN_H / 10;


    private Map<String, Button> actionButtons = new HashMap<String, Button>();
    private Font buttonsFont = new Font(40);

    public void initActionButtons() {
        actionButtons.put("passe", actionButton("Passe", BUTTON_X_START, BUTTON_Y, -50, PlayerAction.PASSE));
        actionButtons.put("prise", actionButton("Prise", BUTTON_X_START + (BUTTON_W + BUTTON_X_DIFF), BUTTON_Y, -50, PlayerAction.PRISE));
        actionButtons.put("garde", actionButton("Garde", BUTTON_X_START + 2 * (BUTTON_W + BUTTON_X_DIFF), BUTTON_Y, -50, PlayerAction.GARDE));
        actionButtons.put("gardeSans", actionButton("Garde\nsans chien", BUTTON_X_START + 3 * (BUTTON_W + BUTTON_X_DIFF), BUTTON_Y, -50, PlayerAction.GARDE_SANS));
        actionButtons.put("gardeContre", actionButton("Garde contre\nle chien", BUTTON_X_START + 4 * (BUTTON_W + BUTTON_X_DIFF), BUTTON_Y, -50, PlayerAction.GARDE_CONTRE));
    }

    private Button actionButton(String name, int x, int y, int z, PlayerAction action) {
        Button button = new Button(name);
        button.setFont(buttonsFont);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setTranslateZ(z);
        button.setTextAlignment(TextAlignment.CENTER);
        button.setPrefSize(BUTTON_W, BUTTON_H);
        button.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(50), null)));
        button.setTextFill(Color.WHITE);
        button.setOnMouseEntered(mouseEvent -> changeColorButton(button, true));
        button.setOnMouseExited(mouseEvent -> changeColorButton(button, false));
        button.setRotationAxis(new Point3D(1, 0, 0));
        button.setRotate(CAMERA_ROTATE);
        button.setOnMouseClicked(mouseEvent -> controller.chooseAction(action));
        return button;
    }

    private void changeColorButton(Button button, boolean toGray) {
        if (toGray) {
            button.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(50), null)));
            button.setTextFill(Color.BLACK);
        } else {
            button.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(50), null)));
            button.setTextFill(Color.WHITE);
        }
    }

    public void drawActionButtons() {
        for (Button b : actionButtons.values()) {
            group.getChildren().add(b);
        }
    }

    public static final int ECART_ZONE_X = Model.GAP_X1 - 40;
    public static final int ECART_ZONE_Y = Model.GAP_Y_START - 40;
    public static final int ECART_ZONE_W = 2 * CardModel.CARD_W + Model.DIST_CARD_X_DIFF + 80;
    public static final int ECART_ZONE_H = 3 * (CardModel.CARD_H + Model.DIST_CARD_Y_DIFF) + 80;

    public void updateActionChosen(PlayerAction action) {
        for (Button b : actionButtons.values()) {
            group.getChildren().remove(b);
        }

        if (action == PlayerAction.PASSE) {
            nouvelleDonne();
        } else if (action == PlayerAction.PRISE || action == PlayerAction.GARDE) {
        	controller.doNextAction();
            doGap();
        } else {
            controller.skipGap();
        }
    }

    private CardView viewSelected = null;
    private double selectedCardXSave = 0;
    private double selectedCardYSave = 0;

    private void doGap() {
        for (CardModel card : model.getMyCards()) {
            cardViews.get(card.getName()).getMeshView().setOnMousePressed(selectCard(cardViews.get(card.getName())));
            cardViews.get(card.getName()).getMeshView().setOnMouseDragged(followMouse(cardViews.get(card.getName())));
            cardViews.get(card.getName()).getMeshView().setOnMouseReleased(tryAddCardToGap(cardViews.get(card.getName()), card));
        }
    }

    private EventHandler<MouseEvent> selectCard(CardView view) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	if(viewSelected == null){
            		selectedCardXSave = (int) view.getMeshView().getTranslateX();
                    selectedCardYSave = (int) view.getMeshView().getTranslateY();
                    view.getMeshView().setTranslateZ(-50);
                    view.getMeshView().setTranslateX((event.getSceneX() - CardModel.CARD_W / 2) + (event.getSceneX()-Model.SCREEN_W/2)*0.15*(1-event.getSceneY()/Model.SCREEN_H));
                    view.getMeshView().setTranslateY((event.getSceneY() - CardModel.CARD_H / 2)*(1+event.getSceneY()/(5*Model.SCREEN_H)));
                    viewSelected = view;
            	}
            }
        };
    }

    private EventHandler<MouseEvent> followMouse(CardView view) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	if(viewSelected == view){
            		view.getMeshView().setTranslateZ(-50);
                    view.getMeshView().setTranslateX((event.getSceneX() - CardModel.CARD_W / 2) + (event.getSceneX()-Model.SCREEN_W/2)*0.15*(1-event.getSceneY()/Model.SCREEN_H));
                    view.getMeshView().setTranslateY((event.getSceneY() - CardModel.CARD_H / 2)*(1+event.getSceneY()/(5*Model.SCREEN_H)));
            	}
            }
        };
    }

    private EventHandler<MouseEvent> tryAddCardToGap(CardView view, CardModel card) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	if(viewSelected == view){
	            	if (model.ungapableCards().contains(card.getName()) || cardViewInEcart(cardViews.get(card.getName()))) {
	            		moveCard(cardViews.get(card.getName()), selectedCardXSave, selectedCardYSave, card.getZ(), unselectView());
	                }
	            	else{
		                controller.addCardToGap(card);
		                removeListeners(cardViews.get(card.getName()));
	                }
            	}
            }
        };
    }
    
    private EventHandler<ActionEvent> unselectView(){
    	return new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				viewSelected = null;
				
			}
    	};
    }

    private boolean cardViewInEcart(CardView view) {
        return view.getMeshView().getTranslateX() + CardModel.CARD_W / 2 < ECART_ZONE_X
                || view.getMeshView().getTranslateX() + CardModel.CARD_W / 2 > ECART_ZONE_X + ECART_ZONE_W
                || view.getMeshView().getTranslateY() + CardModel.CARD_H / 2 < ECART_ZONE_Y
                || view.getMeshView().getTranslateY() + CardModel.CARD_H / 2 > ECART_ZONE_Y + ECART_ZONE_H;
    }

    private void removeListeners(CardView cardView) {
        cardView.getMeshView().setOnMousePressed(null);
        cardView.getMeshView().setOnMouseDragged(null);
        cardView.getMeshView().setOnMouseReleased(null);
    }

    private void nouvelleDonne() {
        group.getChildren().clear();
        group.getChildren().add(ground);

        controller.nouvelleDonne();
    }
}
