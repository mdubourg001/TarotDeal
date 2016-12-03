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
import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.util.Pair;

public class View implements Observer {
    public static double DISTRIBUTION_GROUP_ROTATE = -30; //CAMERA ROTATION MIN -30 MAX 0
    private static double DISTRIBUTION_GROUP_SHIFT_Y = 15*DISTRIBUTION_GROUP_ROTATE;
    public static double DISTRIBUTION_GROUP_SHIFT_Z = 0; //DEZOOM MIN 0 MAX 400

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

    private HashMap<String, CardView> cardViews = new HashMap<String, CardView>();
    
    private Ground ground = new Ground();

    private MenuView menuView = new MenuView(this);
    private SettingsView settingsView = new SettingsView(this);

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

        distributionArea.setTranslateZ(3);

        for(ActionButton b : actionButtons.values()){
            b.setOnMouseClicked(mouseEvent -> controller.chooseAction(b.getAction()));
        }

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.BACK_SPACE)
                    nouvelleDonne();
                    displayMenu();
            }
        });
        menuView.display(menuGroup, root);
    }

    public Controller getController() {
        return this.controller;
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
        menuView.display(menuGroup, root);
    }

    public void displaySettings() {
        settingsView.display(settingsGroup, root);
    }

    public EventHandler<ActionEvent> doNexTActionEvent() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                controller.doNextAction();
            }
        };
    }

    private void updateRotateAndZoom() {
        DISTRIBUTION_GROUP_ROTATE = settingsView.getRotationValue();
        DISTRIBUTION_GROUP_SHIFT_Z = settingsView.getZoomValue();
        DISTRIBUTION_GROUP_SHIFT_Y = 15*DISTRIBUTION_GROUP_ROTATE;

        distributionGroup.setRotate(DISTRIBUTION_GROUP_ROTATE);
        distributionGroup.setTranslateY(DISTRIBUTION_GROUP_SHIFT_Y);
        distributionGroup.setTranslateZ(DISTRIBUTION_GROUP_SHIFT_Z);
    }

    private void initGameView() {
        updateRotateAndZoom();
        distributionGroup.getChildren().add(distributionArea);
        distributionGroup.getChildren().add(ground.getView());
    }

    public void displayView(){
        updateRotateAndZoom();
        ground.resize();
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
                JeuManager.moveCardFromJeu(this, cardViews.get(((Pair<TarotAction, CardModel>) arg1).getValue().getName()),
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
                revertDeck(model.getChienCards(), Model.CHIEN_SIZE, GapManager.doGap(this, model, controller, cardViews));
                break;
            case CARD_ADDED_GAP:
            	moveCard(cardViews.get(((Pair<TarotAction, CardModel>) arg1).getValue().getName()),
                        ((Pair<TarotAction, CardModel>) arg1).getValue(), 0.0, null);
            	break;
            case GAP_DONE:
                GapManager.updateGapDone(model, controller, cardViews);
                break;
            case DISTRIBUTION_DONE:
            	addDinosaurs();
            	break;
        }
    }

    public void updateDeckMixed() {
        for (CardModel card : model.getJeu()) {
            cardViews.put(card.getName(), new CardView(card));
            distributionGroup.getChildren().add(cardViews.get(card.getName()).getView());
        }
        LightManager.addLights(distributionGroup);
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

    private static final double CUT_TIME = 0.2;

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

    private static final double TIME_BETWEEN_DISTRIBUTIONS = 0.2; //TODO Remettre � 0.2
    public void update3CardsDistributed(Pair<Boolean, CardModel[]> arg) {
    	JeuManager.moveCardFromJeu(this, cardViews.get(arg.getValue()[0].getName()), arg.getValue()[0], null);
    	JeuManager.moveCardFromJeu(this, cardViews.get(arg.getValue()[1].getName()), arg.getValue()[1], null);
    	JeuManager.moveCardFromJeu(this, cardViews.get(arg.getValue()[2].getName()), arg.getValue()[2], null);
    	
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
    
    public void moveCard(CardView cardView, CardModel card, EventHandler<ActionEvent> onFinished) {
        moveCard(cardView, card.getX(), card.getY(), card.getZ(), onFinished);
    }
    public void moveCard(CardView cardView, CardModel card, Double a, EventHandler<ActionEvent> onFinished) {
        moveCard(cardView, card.getX(), card.getY(), card.getZ(), a, onFinished);
    }
    public void moveCard(CardView cardView, Double x, Double y, Double z, EventHandler<ActionEvent> onFinished) {
        moveCard(cardView, x, y, z, cardView.getView().getRotate(), onFinished);
    }
    public void moveCard(CardView cardView, Double x, Double y, Double z, Double a, EventHandler<ActionEvent> onFinished) {
        Double time = calculTime(new double[]{calculDelta(cardView.getView().getTranslateX(), x),
        		calculDelta(cardView.getView().getTranslateY(), y),
        		calculDelta(cardView.getView().getTranslateZ(), z)});

        moveMeshView(time, cardView.getView(), x, y, z, a, onFinished);
    }
    public void moveCard(double time, CardView cardView, Double x, Double y, Double z, EventHandler<ActionEvent> onFinished) {
    	moveCard(time, cardView, x, y, z, cardView.getView().getRotate(), onFinished);
    }
    public void moveCard(double time, CardView cardView, Double x, Double y, Double z, Double a, EventHandler<ActionEvent> onFinished) {
        moveMeshView(time, cardView.getView(), x, y, z, a, onFinished);
    }
    public void moveMeshView(double time, MeshView view, Double x, Double y, Double z, Double a, EventHandler<ActionEvent> onFinished) {
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

    public void updateActionChosen(PlayerAction action) {
    	unrealElementsGroup.getChildren().clear();

        if (action == PlayerAction.PASSE) {
            nouvelleDonne();
        } else if (action == PlayerAction.PRISE || action == PlayerAction.GARDE) {
        	controller.doNextAction();
        } else {
            controller.skipGap();
        }
    }

    private void nouvelleDonne() {
        distributionGroup.getChildren().clear();
        unrealElementsGroup.getChildren().clear();

        distributionGroup.getChildren().add(distributionArea);
        distributionGroup.getChildren().add(ground.getView());
        addCardsZones();

        controller.restart();
    }
    
    private EventHandler<ActionEvent> nouvelleDonneEvent(){
    	return new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				nouvelleDonne();
			}
    	};
    }

    private void addDinosaurs(){
    	for(CardModel card : model.getMyCards()){
    		if(cardViews.get(card.getName()).getDinosaurType() != null){
    			distributionGroup.getChildren().add((new Dinosaur3D(card, cardViews.get(card.getName()).getDinosaurType()).getView()));
    		}
    	}
    }
}
