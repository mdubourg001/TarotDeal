package Tarot.ViewPack.DistributionPack;

import Tarot.Controller;
import Tarot.ModelPack.CardModel;
import Tarot.ModelPack.Model;
import Tarot.ModelPack.PlayerAction;
import Tarot.ModelPack.TarotAction;
import Tarot.ViewPack.MenuView;
import Tarot.ViewPack.SettingsView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class View implements Observer {
    private Controller controller;
    private Model model;

    private Group root = new Group();
    private Group menuGroup = new Group();
    private Group settingsGroup = new Group();
    public DistributionGroup distributionGroup = new DistributionGroup();
    
    private Group unrealElementsGroup = new Group();
    private Scene scene = new Scene(root, Model.SCREEN_W, Model.SCREEN_H, true, SceneAntialiasing.DISABLED);
    private PerspectiveCamera camera = new PerspectiveCamera();
    
    private MenuView menuView = new MenuView(this);
    private SettingsView settingsView = new SettingsView(this);

    private HashMap<String, CardView> cardViews = new HashMap<String, CardView>();
    private Map<String, ActionButton> actionButtons = new HashMap<String, ActionButton>();

    private MediaPlayer music = new MediaPlayer(new Media(new File("./res/music.wav").toURI().toString()));

    ///CONSTRUCTOR->
    public View(Controller controller) {
        root.setDepthTest(DepthTest.ENABLE);
        if (!Platform.isSupported(ConditionalFeature.SCENE3D))
            throw new RuntimeException("SCENE3D not supported");
        scene.setFill(Color.BLACK);
        scene.setCamera(camera);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.BACK_SPACE)
                    nouvelleDonne();
                    displayMenu();
            }
        });
    	
        this.controller = controller;
        this.model = controller.getModel();
        
        root.getChildren().add(menuGroup);
        menuView.display(menuGroup, root);

        initMusic();
        creatActionButtons();
    }

    private void initMusic() {
        music.setAutoPlay(true);
        music.setVolume(1.0);
        music.play();
    }

    private void creatActionButtons() {
        actionButtons.put("passe", new ActionButton("Passe",
                ActionButton.BUTTON_X_START, ActionButton.BUTTON_Y, -300,
                PlayerAction.PASSE, controller));
        actionButtons.put("prise", new ActionButton("Prise",
                ActionButton.BUTTON_X_START + (ActionButton.BUTTON_W + ActionButton.BUTTON_X_DIFF), ActionButton.BUTTON_Y, -300,
                PlayerAction.PRISE, controller));
        actionButtons.put("garde", new ActionButton("Garde",
                ActionButton.BUTTON_X_START + 2 * (ActionButton.BUTTON_W + ActionButton.BUTTON_X_DIFF), ActionButton.BUTTON_Y, -300,
                PlayerAction.GARDE, controller));
        actionButtons.put("gardeSans", new ActionButton("Garde\nsans chien",
                ActionButton.BUTTON_X_START + 3 * (ActionButton.BUTTON_W + ActionButton.BUTTON_X_DIFF), ActionButton.BUTTON_Y, -300,
                PlayerAction.GARDE_SANS, controller));
        actionButtons.put("gardeContre", new ActionButton("Garde contre\nle chien",
                ActionButton.BUTTON_X_START + 4 * (ActionButton.BUTTON_W + ActionButton.BUTTON_X_DIFF), ActionButton.BUTTON_Y, -300,
                PlayerAction.GARDE_CONTRE, controller));
    }
    
    ///<-CONSTRUCTOR GETTERS->

    public Controller getController() {
        return this.controller;
    }
    
    public Model getModel() {
        return this.model;
    }
    
    public Scene getScene() {
        return scene;
    }
    
    public DistributionGroup getDistGroup(){
    	return distributionGroup;
    }
    
    public CardView getCardView(String name){
    	return cardViews.get(name);
    }

    public void setMusicVolume(double value) {
        music.setVolume(value);
    }

    ///<-GETTERS DISPLAYS->

    public void displayMenu() {
        menuView.display(menuGroup, root);
    }

    public void displaySettings() {
        settingsView.display(settingsGroup, root);
    }
    
    public void displayDistribution(){
    	distributionGroup.updateRotateAndZoom(settingsView.getRotationValue(), settingsView.getZoomValue());
        root.getChildren().clear();
        root.getChildren().add(distributionGroup);
        root.getChildren().add(unrealElementsGroup);
        controller.startIfNeeded();
    }
    
  ///<-DISPLAYS UPDATES->
    
	@SuppressWarnings("unchecked")
	@Override
    public void update(Observable arg0, Object arg1) {
        switch (((Pair<TarotAction, Object>) arg1).getKey()) {
            case JEU_MIXED:
                updateJeuMixed();
                break;
            case JEU_CUT:
                updateJeuCut(((Pair<TarotAction, Integer>) arg1).getValue(), 1);
                break;
            case CARDS_DISTRIBUTED:
            case CARD_MOVED_TO_CHIEN:
                DistributionManager.update(this, arg0, arg1);
                break;
            case PLAYER_REVERTED:
            	updatePlayerReverted();
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
            case CARD_ADDED_GAP:
            case GAP_DONE:
                GapManager.update(this, arg0, arg1);
                break;
            case DISTRIBUTION_DONE:
            	/*Place, au dessus des 8 derniers atouts (14-21), un dinosaure 3D correspondant a l'atout.*/
            	updateDinosaurs();
            	break;
        }
    }

    public void updateJeuMixed() {
        for (int i = 0; i < model.getJeuSize(); i++) {
            cardViews.put(model.getJeu(i).getName(), new CardView(model.getJeu(i)));
            distributionGroup.getChildren().add(cardViews.get(model.getJeu(i).getName()).getView());
        }
        LightManager.addLights(distributionGroup);
        controller.doNextAction();
    }

    public void updateJeuCut(Integer indexHalf, int iteration) {
        EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                updateJeuCut(indexHalf, iteration + 1);
            }
        };
        switch (iteration) {
            case 1:
                moveCutJeu(CardModel.CARD_W / 2 + 10, false, indexHalf, onFinished);
                break;
            case 2:
                moveCutJeu(CardModel.CARD_W / 2 + 10, true, indexHalf, onFinished);
                break;
            case 3:
                moveCutJeu(0, true, indexHalf, doNexTActionEvent());
                break;
        }
    }
    
    public void updatePlayerReverted(){
    	revertMyCards(doNexTActionEvent());
    }
    
    private final static double ORGANIZE_CARD_TIME = 0.5;
    public void updatePlayerCardsOrganized() {
        CardModel card;
        for (int i = 0; i < model.getMyDeckSize(); i++) {
            card = model.getMyCards(i);
            cardViews.get(card.getName()).getView().setTranslateZ(card.getZ());
            moveCard(cardViews.get(card.getName()), card, null);
        }
        waiter(ORGANIZE_CARD_TIME, doNexTActionEvent());
    }

    public void updatePetitSec(Pair<Boolean, Integer> petitSec) {
        if (!petitSec.getKey()) {
        	showActionButtons();
        } else {
        	unrealElementsGroup.getChildren().add(createPetitSecLabel(petitSec.getValue()));
            waiter(3, nouvelleDonneEvent());
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
    
    private void updateDinosaurs(){
    	for(int i=0; i < model.getMyDeckSize(); i++){
    		if(cardViews.get(model.getMyCards(i).getName()).getDinosaurType() != null){
    			distributionGroup.getChildren().add(new Dinosaur3D(model.getMyCards(i),
    					cardViews.get(model.getMyCards(i).getName()).getDinosaurType(),
    					distributionGroup.getRotate()).getView());
    		}
    	}
    }
    
    ///<-UPDATES MOVE CARDS->
    
    private static final double CUT_TIME = 0.2;
    private void moveCutJeu(double xShiftValue, boolean trueZ, Integer indexCut, EventHandler<ActionEvent> onFinished) {
        CardModel card;
        double xShift;
        double z;
        for (int i = 0; i < model.getJeuSize(); i++) {
            card = model.getJeu(i);
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

            if (i != model.getJeuSize() - 1) {
                moveCard(CUT_TIME, cardViews.get(card.getName()), card.getX() + xShift, null, z, null);
            } else {
            	moveCard(CUT_TIME, cardViews.get(card.getName()), card.getX() + xShift, null, z, onFinished);
            }
        }
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
    
    public final static double REVERT_CARD_WAIT_COEF = 0.1; //TODO Remetre a 0.1
    public void revertMyCards(EventHandler<ActionEvent> onFinished) {
        for (int i = 0; i < model.getMyDeckSize(); i++) {
        	if(i != model.getMyDeckSize()-1){//Ca n'est pas la derniere carte du deck
        		waiter(REVERT_CARD_DURATION * REVERT_CARD_WAIT_COEF * (i+1), revertCardEvent(model.getMyCards(i), null));
        	}
        	else{
        		waiter(REVERT_CARD_DURATION * REVERT_CARD_WAIT_COEF * (i+1), revertCardEvent(model.getMyCards(i), onFinished));
        	}
            
        }
    }
    
    public EventHandler<ActionEvent> revertCardEvent(CardModel card, EventHandler<ActionEvent> onFinished){
    	return new EventHandler<ActionEvent>(){
            public void handle(ActionEvent t) {
            	revertCard(cardViews.get(card.getName()), onFinished);
            }
        };
    }

    public final static double REVERT_CARD_DURATION = 0.8; // TODO REMETTRE A 0.7
    private final static double REVERT_CARD_Z = -300;
    private void revertCard(CardView cardView, EventHandler<ActionEvent> onFinished) {
        EventHandler<ActionEvent> continueAnimation = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {

                moveMeshView(REVERT_CARD_DURATION/2, cardView.getView(), null, null,  1.0, 360.0, onFinished);
            }
        };
        moveMeshView(REVERT_CARD_DURATION/2, cardView.getView(), null, null, REVERT_CARD_Z, 270.0, continueAnimation);
    }
    
    /// <-MOVE CARDS
    
    public EventHandler<ActionEvent> doNexTActionEvent() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                controller.doNextAction();
            }
        };
    }
    
    public void waiter(double duration, EventHandler<ActionEvent> event){
    	if(duration == 0){
    		/*On a decide de ne pas lancer l'exception pour ne pas surcharger les passages
    		de code qui utilise waiter avec des try cacth. De plus le printStackTrace() montre
    		deja la fonction d'ou waiter() a ete appellee.*/
            // TODO DECOMMENTER
            /*
    		UselessWaiterException e = new UselessWaiterException();
			e.printStackTrace();
			*/
    	}
    	else{
    		Timeline timeLine = new Timeline();
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(duration), event);
            timeLine.getKeyFrames().add(keyFrame);
            timeLine.play();
    	}
    }
    
    private Label createPetitSecLabel(Integer player){
    	Label l = new Label(" Petit Sec Player " + player + " ");
    	l.setTextFill(Color.WHITE);
    	l.setFont(new Font(Model.SCREEN_W/12));
    	l.setTranslateX((Model.SCREEN_W/6.4));
    	l.setTranslateY((Model.SCREEN_W/6.4)*(Model.SCREEN_H/Model.SCREEN_W));
    	l.setTranslateZ(-Model.SCREEN_W/6.4);
    	l.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
    	return l;
    }
    
    private void showActionButtons(){
    	for(Button b : actionButtons.values()){
    		unrealElementsGroup.getChildren().add(b);
    	}
    }

    private void nouvelleDonne() {
        distributionGroup.nouvelleDonne();
        unrealElementsGroup.getChildren().clear();

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
