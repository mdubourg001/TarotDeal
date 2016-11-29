package Tarot.ViewPack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import Tarot.*;
import Tarot.ModelPack.CardModel;
import Tarot.ModelPack.Model;
import Tarot.ModelPack.Player;
import Tarot.ModelPack.PlayerAction;
import Tarot.ModelPack.TarotAction;
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
import javafx.scene.control.Label;
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
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Pair;

public class View implements Observer {
    private static final double DISTRIBUTION_GROUP_ROTATE = -30; //CAMERA ROTATION MIN -30 MAX 0
    private static final double DISTRIBUTION_GROUP_SHIFT_Y = 15*DISTRIBUTION_GROUP_ROTATE;
    private static final double DISTRIBUTION_GROUP_SHIFT_Z = 0; //DEZOOM MIN 0 MAX 400

    private static final double DISTRIBUTION_AREA_SHIFT = Model.SCREEN_W/2;

    private Controller controller;
    private Model model;

    private Group root = new Group();
    private Group menuGroup = new Group();
    private Group settingsGroup = new Group();
    private Group distributionGroup = new Group();
    private Group unrealElementsGroup = new Group();
    private Scene scene = new Scene(root, Model.SCREEN_W, Model.SCREEN_H, true, SceneAntialiasing.DISABLED);
    private PerspectiveCamera camera = new PerspectiveCamera();

    private Map<String, CardView> cardViews = new HashMap<String, CardView>();
    
    private MeshView ground = createGround();
    
    private ImageView menuBackground = new ImageView("file:./res/menu_background.jpg");
    private ImageView menuTitle = new ImageView("file:./res/title.png");
    private ImageView settingsBackground = new ImageView("file:./res/settings_background.png");

    private Map<String, ImageButton> imageButtons = new HashMap<String, ImageButton>() {{
        put("play", new ImageButton("file:./res/play.png", Model.SCREEN_W / 2 - ImageButton.BUTTON_W / 2, Model.SCREEN_W / 2));
        put("settings", new ImageButton("file:./res/settings.png", Model.SCREEN_W / 2 - (ImageButton.BUTTON_W + Model.SCREEN_W / 19.2) / 2, Model.SCREEN_H / 2 + Model.SCREEN_H / 20));
        put("quit", new ImageButton("file:./res/quit.png",  Model.SCREEN_W / 2 - (ImageButton.BUTTON_W + Model.SCREEN_W / 19.2) / 2, Model.SCREEN_H / 2 + Model.SCREEN_H / 3.6));
    }};

    private ImageView cardsBackImage = new ImageView("file:./res/cardsback.png");
    private ImageView soundImage = new ImageView("file:./res/sound.png");

    private Rectangle distributionArea = new Rectangle(-DISTRIBUTION_AREA_SHIFT, -DISTRIBUTION_AREA_SHIFT, Model.SCREEN_W+2*DISTRIBUTION_AREA_SHIFT, Model.SCREEN_H + 2*DISTRIBUTION_AREA_SHIFT);

    private Map<String, ActionButton> actionButtons = new HashMap<String, ActionButton>(){{
        put("passe", new ActionButton("Passe",
                ActionButton.BUTTON_X_START, ActionButton.BUTTON_Y, -300,
                PlayerAction.PASSE));
        put("prise", new ActionButton("Prise",
                ActionButton.BUTTON_X_START + (ActionButton.BUTTON_W + ActionButton.BUTTON_X_DIFF), ActionButton.BUTTON_Y, -300,
                PlayerAction.PRISE));
        put("garde", new ActionButton("Garde",
                ActionButton.BUTTON_X_START + 2 * (ActionButton.BUTTON_W + ActionButton.BUTTON_X_DIFF), ActionButton.BUTTON_Y, -300,
                PlayerAction.GARDE));
        put("gardeSans", new ActionButton("Garde\nsans chien",
                ActionButton.BUTTON_X_START + 3 * (ActionButton.BUTTON_W + ActionButton.BUTTON_X_DIFF), ActionButton.BUTTON_Y, -300,
                PlayerAction.GARDE_SANS));
        put("gardeContre", new ActionButton("Garde contre\nle chien",
                ActionButton.BUTTON_X_START + 4 * (ActionButton.BUTTON_W + ActionButton.BUTTON_X_DIFF), ActionButton.BUTTON_Y, -300,
                PlayerAction.GARDE_CONTRE));
    }};
    
    public View(Controller controller) {
        this.controller = controller;
        this.model = controller.getModel();
        
        root.getChildren().add(menuGroup);
        initMenuBackground();
        initMenuButtons();

        initSettingsBackground();
        initSettingsButtons();

        initGameView();

        distributionGroup.setDepthTest(DepthTest.ENABLE);

        if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
            throw new RuntimeException("SCENE3D not supported");
        }
        
        scene.setFill(Color.BLACK);
        scene.setCamera(camera);
        camera.setRotationAxis(new Point3D(1, 0, 0));
        distributionGroup.setRotationAxis(new Point3D(1, 0, 0));
        addCardsZones();

        ground.setScaleX(1 + DISTRIBUTION_GROUP_SHIFT_Z/1330 - DISTRIBUTION_GROUP_ROTATE/100);
        ground.setScaleY(1 + DISTRIBUTION_GROUP_SHIFT_Z/1330 - DISTRIBUTION_GROUP_ROTATE/100);

        distributionArea.setTranslateZ(3);

        for(ActionButton b : actionButtons.values()){
            b.setOnMouseClicked(mouseEvent -> controller.chooseAction(b.getAction()));
        }
    }
    
    private void addCardsZones(){
    	CardsZone[] zones = new CardsZone[]{
    			new CardsZone("Chien", Model.CHIEN_X-5, Model.CHIEN_Y-5, Model.CHIEN_W+10, Model.CHIEN_H+10),
    			new CardsZone("Your Cards", Model.MY_CARDS_X-5, Model.MY_CARDS_Y-5, Model.MY_CARDS_W+10, Model.MY_CARDS_H+10),
    			new CardsZone("Gap", Model.GAP_X-5, Model.GAP_Y-5, Model.GAP_W+10, Model.GAP_H+10)
    					};
    	for(CardsZone zone : zones){
    		distributionGroup.getChildren().add(zone.getZone());
    		distributionGroup.getChildren().add(zone.getLab());
    	}
    }

    private MeshView createGround(){
        TriangleMesh mesh = new TriangleMesh();

        mesh.getPoints().addAll(
                0, 0, 2.1f,
                (float)Model.SCREEN_W, 0, 2.1f,
                0, (float)Model.SCREEN_H, 2.1f,
                (float)Model.SCREEN_W, (float)Model.SCREEN_H, 2.1f
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
        root.getChildren().clear();
        root.getChildren().add(menuGroup);
    }

    public void displaySettings() {
        root.getChildren().clear();
        root.getChildren().add(settingsGroup);
    }

    public EventHandler<ActionEvent> doNexTActionEvent() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                controller.doNextAction();
            }
        };
    }
    private void initMenuBackground() {
        menuGroup.getChildren().add(menuBackground);
        menuGroup.getChildren().add(menuTitle);

        double ratioBackImage = Model.SCREEN_H / menuBackground.getImage().getHeight();
        menuBackground.setScaleY(ratioBackImage);
        menuBackground.setScaleX(ratioBackImage);

        menuBackground.setX(Model.SCREEN_W / 2 - menuBackground.getImage().getWidth() / 2);
        menuBackground.setY(Model.SCREEN_H / 2 - menuBackground.getImage().getHeight() / 2);

        menuTitle.setTranslateX(Model.SCREEN_W / 2 - menuTitle.getImage().getWidth() / 2);
        menuTitle.setTranslateY(Model.SCREEN_H / 10);
    }

    private void initSettingsBackground() {
        settingsGroup.getChildren().add(settingsBackground);
        settingsGroup.getChildren().add(cardsBackImage);
        settingsGroup.getChildren().add(soundImage);

        cardsBackImage.setFitWidth(Model.SCREEN_W / 4);
        cardsBackImage.setFitHeight(Model.SCREEN_H / 4);
        cardsBackImage.setTranslateX(Model.SCREEN_W / 10);
        cardsBackImage.setTranslateY(Model.SCREEN_H/10);

        soundImage.setFitWidth(Model.SCREEN_W / 3.5);
        soundImage.setFitHeight(Model.SCREEN_H / 4);
        soundImage.setTranslateX(Model.SCREEN_W / 10);
        soundImage.setTranslateY(Model.SCREEN_H/1.8);

    }

    private void initMenuButtons() {

        imageButtons.get("play").setImageSize(Model.SCREEN_W / 5, Model.SCREEN_H / 5);
        imageButtons.get("play").setPosition(Model.SCREEN_W / 2 - ImageButton.BUTTON_W / 2.5,
                Model.SCREEN_H / 4 + ImageButton.BUTTON_H / 2);

        imageButtons.get("settings").setImageSize(Model.SCREEN_W / 4, Model.SCREEN_H / 4);
        imageButtons.get("settings").setPosition(Model.SCREEN_W / 2 - ImageButton.BUTTON_W / 2,
                Model.SCREEN_H / 2 + ImageButton.BUTTON_H / 3);

        imageButtons.get("quit").setImageSize(Model.SCREEN_W / 5, Model.SCREEN_H / 5);
        imageButtons.get("quit").setPosition(Model.SCREEN_W / 2 - ImageButton.BUTTON_W / 2,
                Model.SCREEN_H / 2 + ImageButton.BUTTON_H * 1.7);

        imageButtons.get("play").setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                displayView();
                controller.doNextAction();
            }
        });
        imageButtons.get("play").setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { imageButtons.get("play").inflate(30); }
        });
        imageButtons.get("play").setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { imageButtons.get("play").deflate(-30); }
        });
        imageButtons.get("settings").setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { displaySettings(); }
        });
        imageButtons.get("settings").setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { imageButtons.get("settings").inflate(-30); }
        });
        imageButtons.get("settings").setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { imageButtons.get("settings").deflate(30); }
        });
        imageButtons.get("quit").setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { System.exit(0); }
        });
        imageButtons.get("quit").setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { imageButtons.get("quit").inflate(-85); }
        });
        imageButtons.get("quit").setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                imageButtons.get("quit").deflate(85);
            }
        });

        menuGroup.getChildren().add(imageButtons.get("play"));
        menuGroup.getChildren().add(imageButtons.get("settings"));
        menuGroup.getChildren().add(imageButtons.get("quit"));
        menuGroup.getChildren().add(imageButtons.get("play").getImage());
        menuGroup.getChildren().add(imageButtons.get("settings").getImage());
        menuGroup.getChildren().add(imageButtons.get("quit").getImage());
    }

    private void initSettingsButtons() {

    }

    private void initGameView() { //TODO
        distributionGroup.setRotate(DISTRIBUTION_GROUP_ROTATE);
        distributionGroup.setTranslateY(DISTRIBUTION_GROUP_SHIFT_Y);
        distributionGroup.setTranslateZ(DISTRIBUTION_GROUP_SHIFT_Z);
        distributionGroup.getChildren().add(distributionArea);
        distributionGroup.getChildren().add(ground);
    }

    private void displayView(){
        root.getChildren().clear();
        root.getChildren().add(distributionGroup);
        root.getChildren().add(unrealElementsGroup);
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
                revertDeck(model.getMyCards(), Player.NB_CARDS, doNexTActionEvent());
                break;
            case PLAYER_ORGANIZED:
                updatePlayerCardsOrganized();
                break;
            case PETIT_SEC_DETECTED:
                updatePetitSec(((Pair<TarotAction, Pair<Boolean, Integer>>) arg1).getValue());
                break;
            case ACTION_CHOSEN:
                updateActionChosen(((Pair<TarotAction, PlayerAction>) arg1).getValue());
                break;
            case CHIEN_REVERTED:
                revertDeck(model.getChienCards(), Model.CHIEN_SIZE, null);
                break;
            case CARD_ADDED_GAP:
            	moveCard(cardViews.get(((Pair<TarotAction, CardModel>) arg1).getValue().getName()),
                        ((Pair<TarotAction, CardModel>) arg1).getValue(), 0.0, null);
            	break;
            case GAP_DONE:
                updateGapDone();
                break;
            case DISTRIBUTION_DONE:
            	System.out.println("Distribution Done"); //TODO
            	break;
        }
    }

    public void updateDeckMixed() {
        for (CardModel card : model.getJeu()) {
            cardViews.put(card.getName(), new CardView(card));
            distributionGroup.getChildren().add(cardViews.get(card.getName()).getView());
        }
        addLights();
        controller.doNextAction();
    }
    
    public static final double MOON_LIGHT_Z = -1500;
    public static final double LAMPS_HOOK_Z = -600;
    public static final double LAMP_Z = -350;
    public static final double LAMP_SHINING_Z = -300;
    private static final double LAMPS_SPEED = 1;
    
    private void addLights(){
        PointLight moonLight = new EnvironmentLight(Color.DARKBLUE, new Point3D(0, 0, MOON_LIGHT_Z));
        PointLight lampLight = new LampLight(Color.LIGHTBLUE, 
        		new Point3D(Model.SCREEN_W/2, Model.SCREEN_H/2, LAMP_Z), LAMPS_HOOK_Z, LAMPS_SPEED);
        PointLight lampLightShining = new LampLight(Color.WHITE, 
        		new Point3D(Model.SCREEN_W/2, Model.SCREEN_H/2, LAMP_SHINING_Z), LAMPS_HOOK_Z, LAMPS_SPEED);

        distributionGroup.getChildren().addAll(moonLight, lampLight, lampLightShining);
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

    private static final double CUT_TIME = 0.4;

    private void moveCutDeck(double xShiftValue, boolean trueZ, Integer indexCut, EventHandler<ActionEvent> onFinished) {
        CardModel card;
        double xShift;
        double z;
        for (int i = 0; i < model.getJeu().size(); i++) {
            card = model.getJeu().get(i);
            if (i < Model.NB_CARDS - indexCut) {
                xShift = -xShiftValue;
            } else {
                xShift = xShiftValue;
            }

            if (trueZ) {
                z = card.getZ();
            } else {
                z = cardViews.get(card.getName()).getView().getTranslateZ();
            }

            if (i != model.getJeu().size() - 1) {
                moveCard(CUT_TIME, cardViews.get(card.getName()), card.getX() + xShift, null, z, null);
            } else {
            	moveCard(CUT_TIME, cardViews.get(card.getName()), card.getX() + xShift, null, z, onFinished);
            }
        }
    }

    private static final double TIME_BETWEEN_DISTRIBUTIONS = 0.1; //TODO Remettre � 0.3
    public void update3CardsDistributed(Pair<Boolean, CardModel[]> arg) {
    	moveCardFromDeck(cardViews.get(arg.getValue()[0].getName()), arg.getValue()[0], null);
        moveCardFromDeck(cardViews.get(arg.getValue()[1].getName()), arg.getValue()[1], null);
        moveCardFromDeck(cardViews.get(arg.getValue()[2].getName()), arg.getValue()[2], null);
    	
        waiter(TIME_BETWEEN_DISTRIBUTIONS, continueCardDistribution(arg.getKey()));
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
        controller.doNextAction();
    }

    private void moveCard(CardView cardView, CardModel card, EventHandler<ActionEvent> onFinished) {
        moveCard(cardView, card.getX(), card.getY(), card.getZ(), onFinished);
    }
    private void moveCard(CardView cardView, CardModel card, Double a, EventHandler<ActionEvent> onFinished) {
        moveCard(cardView, card.getX(), card.getY(), card.getZ(), a, onFinished);
    }
    private void moveCard(CardView cardView, Double x, Double y, Double z, EventHandler<ActionEvent> onFinished) {
        moveCard(cardView, x, y, z, cardView.getView().getRotate(), onFinished);
    }
    private void moveCard(CardView cardView, Double x, Double y, Double z, Double a, EventHandler<ActionEvent> onFinished) {
        Double time = calculTime(new double[]{calculDelta(cardView.getView().getTranslateX(), x),
        		calculDelta(cardView.getView().getTranslateY(), y),
        		calculDelta(cardView.getView().getTranslateZ(), z)});

        moveMeshView(time, cardView.getView(), x, y, z, a, onFinished);
    }
    private void moveCard(double time, CardView cardView, Double x, Double y, Double z, EventHandler<ActionEvent> onFinished) {
    	moveCard(time, cardView, x, y, z, cardView.getView().getRotate(), onFinished);
    }
    private void moveCard(double time, CardView cardView, Double x, Double y, Double z, Double a, EventHandler<ActionEvent> onFinished) {
        moveMeshView(time, cardView.getView(), x, y, z, a, onFinished);
    }
    private void moveMeshView(double time, MeshView view, Double x, Double y, Double z, Double a, EventHandler<ActionEvent> onFinished) {
        Timeline animationMoveCard = new Timeline();

        KeyValue kVMoveX = new KeyValue(view.translateXProperty(), unNull(x, view.getTranslateX()));
        KeyValue kVMoveY = new KeyValue(view.translateYProperty(), unNull(y, view.getTranslateY()));
        KeyValue kVMoveZ = new KeyValue(view.translateZProperty(), unNull(z, view.getTranslateZ()));
        KeyValue kVRotate = new KeyValue(view.rotateProperty(), unNull(a, view.getRotate()));

        Duration duration = Duration.seconds(time);

        KeyFrame keyFrame = new KeyFrame(duration, onFinished, kVMoveX, kVMoveY, kVMoveZ, kVRotate);
        animationMoveCard.getKeyFrames().add(keyFrame);

        animationMoveCard.play();
    }
    
    private double calculDelta(double a, Double b){
    	if(b == null){
    		return 0;
    	}
    	else{
    		return Math.abs(a - b);
    	}
    }

    private double unNull(Double value, double sub){
        if(value == null)
            return sub;
        return  value;
    }

    public static final double TIME_DIVIDER = 1000; // TODO REMETTRE A 1000

    private double calculTime(double[] deltas) {
        double time = 0;
        for (double d : deltas) {
            time += d;
        }
        time /= TIME_DIVIDER;
        return time;
    }

    /*Attend que les cartes soit totalement sortient du cercle forme par la diagonale du deck (jeu)
    avant de descendre (en z). Evite que les cartes passe au travers du deck.*/
    private void moveCardFromDeck(CardView cardView, CardModel card, EventHandler<ActionEvent> onFinished2) {
        Point2D firstDest = calculFirstDest(cardView, card);

        EventHandler<ActionEvent> onFinished1 = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                moveCard(cardView, card, onFinished2);
            }
        };
        moveCard(cardView, firstDest.getX(), firstDest.getY(), cardView.getView().getTranslateZ(), onFinished1);
    }

    private Point2D calculFirstDest(CardView cardView, CardModel card) {
        Point2D viewP = new Point2D(cardView.getView().getTranslateX(), cardView.getView().getTranslateY());
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
    
    private final static double REVERT_CARD_WAIT_COEF = 0.05; //TODO Remetre � 0.1
    public void revertDeck(ArrayList<CardModel> deck, int size, EventHandler<ActionEvent> onFinished) {
        int i = 1;
        for (CardModel card : deck) {
            waiter(REVERT_CARD_DURATION * REVERT_CARD_WAIT_COEF * i, new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    if (deck.indexOf(card) != size - 1)
                        revertCard(cardViews.get(card.getName()), null);
                    else {
                        revertCard(cardViews.get(card.getName()), onFinished);
                    }
                }
            });
            i++;
        }
    }

    private final static double REVERT_CARD_DURATION = 1; // TODO REMETTRE A 2
    private final static double REVERT_CARD_Z = -300;

    private void revertCard(CardView cardView, EventHandler<ActionEvent> onFinished) {
        EventHandler<ActionEvent> continueAnimation = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {

                moveMeshView(REVERT_CARD_DURATION/2, cardView.getView(), null, null,  1.0, 360.0, onFinished);
            }
        };
        moveMeshView(REVERT_CARD_DURATION/2, cardView.getView(), null, null, REVERT_CARD_Z, 270.0, continueAnimation);
    }

    private final static double ORGANIZE_CARD_TIME = 0.5;
    public void updatePlayerCardsOrganized() {
        CardModel card;
        for (int i = 0; i < model.getMyCards().size(); i++) {
            card = model.getMyCards().get(i);
            cardViews.get(card.getName()).getView().setTranslateZ(card.getZ());
            moveCard(cardViews.get(card.getName()), card, null);
        }
        waiter(ORGANIZE_CARD_TIME, doNexTActionEvent());
    }

    public void updatePetitSec(Pair<Boolean, Integer> petitSec) {
        if (!petitSec.getKey()) {
        	drawActionsButtons();
        } else {
        	unrealElementsGroup.getChildren().add(createPetitSecLabel(petitSec.getValue()));
            waiter(3, nouvelleDonneEvent());
        }
    }
    
    private Label createPetitSecLabel(Integer player){
    	Label l = new Label("Petit Sec Player " + player);
    	l.setTextFill(Color.WHITE);
    	l.setFont(new Font(Model.SCREEN_W/12));
    	l.setTranslateX(Model.SCREEN_W/7.5);
    	l.setTranslateY(Model.SCREEN_H/3);
    	l.setTranslateZ(-300);
    	l.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(50), null)));
    	return l;
    }
    
    private void drawActionsButtons(){
    	for(Button b : actionButtons.values()){
    		unrealElementsGroup.getChildren().add(b);
    	}
    }

    public static final double ECART_ZONE_X = Model.GAP_X - 40;
    public static final double ECART_ZONE_Y = Model.GAP_Y - 40;
    public static final double ECART_ZONE_W = 2 * CardModel.CARD_W + Model.DIST_CARD_X_SHIFT + 80;
    public static final double ECART_ZONE_H = 3 * (CardModel.CARD_H + Model.DIST_CARD_Y_SHIFT) + 80;

    public void updateActionChosen(PlayerAction action) {
    	unrealElementsGroup.getChildren().clear();

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
            cardViews.get(card.getName()).getView().setOnMousePressed(selectCardEvent(cardViews.get(card.getName())));
            cardViews.get(card.getName()).getView().setOnMouseDragged(followMouseEvent(cardViews.get(card.getName())));
            cardViews.get(card.getName()).getView().setOnMouseReleased(tryAddCardToGapEvent(cardViews.get(card.getName()), card));
        }
    }

    private EventHandler<MouseEvent> selectCardEvent(CardView view) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	if(view.canBeSelected()){
                    selectCardEvent(view, event);
            	}
            }
        };
    }

    private void selectCardEvent(CardView view, MouseEvent event){
        selectedCardXSave = (int) view.getView().getTranslateX();
        selectedCardYSave = (int) view.getView().getTranslateY();
        riseCard(view, event);
        viewSelected = view;
        view.canBeSelected(false);
    }

    private void riseCard(CardView view, MouseEvent event){
        view.getView().setRotationAxis(Rotate.X_AXIS);
        view.getView().setRotate(-DISTRIBUTION_GROUP_ROTATE);
        view.getView().setTranslateZ(-1 + CardModel.CARD_H * DISTRIBUTION_GROUP_ROTATE/90);
        followMouse(view, event);
    }

    private EventHandler<MouseEvent> followMouseEvent(CardView view) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	if(viewSelected == view){
                    followMouse(view, event);
            	}
            }
        };
    }

    private void followMouse(CardView view, MouseEvent event){
    	/*Formule inexacte qui essaye de garder la carte au niveau de la souris, lors du glisser deposer,
    	en prenant en compte l'inclinaison de la camera et le dezoom. Ci ces 2 valeurs sont a 0 le centre
    	de la carte est toujours au niveau de la souris.*/
        view.getView().setTranslateX((event.getSceneX() - CardModel.CARD_W / 2)
        		+ 0.0004*(event.getSceneX()-Model.SCREEN_W/2)*DISTRIBUTION_GROUP_SHIFT_Z
        		+ 0.002*(event.getSceneX()-Model.SCREEN_W/2)*(-DISTRIBUTION_GROUP_ROTATE)*(1-event.getSceneY()/(0.2*Model.SCREEN_H)));
        view.getView().setTranslateY((event.getSceneY() - CardModel.CARD_H / 2)
        		+ 0.0004*(event.getSceneY()-Model.SCREEN_H/2)*DISTRIBUTION_GROUP_SHIFT_Z
        		+ 0.004*event.getSceneY()*(-DISTRIBUTION_GROUP_ROTATE));
    }

    private EventHandler<MouseEvent> tryAddCardToGapEvent(CardView view, CardModel card){
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tryAddCardToGap(view, card);
            }
        };
    }

    private void tryAddCardToGap(CardView view, CardModel card){
        if(viewSelected == view){
        	viewSelected = null;
            if (model.ungapableCards().contains(card.getName()) || cardViewInEcart(cardViews.get(card.getName()))) {
                moveCard(cardViews.get(card.getName()), selectedCardXSave, selectedCardYSave, card.getZ(), 0.0, canSelectView(view));
            }
            else{
                controller.addCardToGap(card);
                removeListeners(cardViews.get(card.getName()));
            }
        }
    }

    private EventHandler<ActionEvent> canSelectView(CardView view){
    	return new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				view.canBeSelected(true);
			}
    	};
    }

    private boolean cardViewInEcart(CardView view) {
        return view.getView().getTranslateX() + CardModel.CARD_W / 2 < ECART_ZONE_X
                || view.getView().getTranslateX() + CardModel.CARD_W / 2 > ECART_ZONE_X + ECART_ZONE_W
                || view.getView().getTranslateY() + CardModel.CARD_H / 2 < ECART_ZONE_Y
                || view.getView().getTranslateY() + CardModel.CARD_H / 2 > ECART_ZONE_Y + ECART_ZONE_H;
    }

    private void removeListeners(CardView cardView) {
        cardView.getView().setOnMousePressed(null);
        cardView.getView().setOnMouseDragged(null);
        cardView.getView().setOnMouseReleased(null);
    }

    private void nouvelleDonne() {
        distributionGroup.getChildren().clear();
        unrealElementsGroup.getChildren().clear();

        distributionGroup.getChildren().add(distributionArea);
        distributionGroup.getChildren().add(ground);

        controller.nouvelleDonne();
    }
    
    private EventHandler<ActionEvent> nouvelleDonneEvent(){
    	return new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				nouvelleDonne();
			}
    	};
    }
}
