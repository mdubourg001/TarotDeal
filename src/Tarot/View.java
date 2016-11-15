package Tarot;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class View implements Observer{
	private Controller controller;
	private Model model;

	private Group group = new Group();
	private Scene scene = new Scene(group, Model.SCREEN_W, Model.SCREEN_H, true, SceneAntialiasing.DISABLED);
	private PerspectiveCamera camera = new PerspectiveCamera();
	
	private Map<String, CardView> cardViews = new HashMap<String, CardView>();

	public View(Controller controller){
		this.controller = controller;
		this.model = controller.getModel();
		
		group.setDepthTest(DepthTest.ENABLE); 
		
		if(!Platform.isSupported(ConditionalFeature.SCENE3D)){
			throw new RuntimeException("SCENE3D not supported");
		}

		scene.setFill(Color.BLACK);
		scene.setCamera(camera);
		camera.setRotationAxis(new Point3D(1,0,0));
		camera.setRotate(20);
		camera.setTranslateZ(-300);
		camera.setTranslateY(200);
	}

	public Scene getScene(){
		return scene;
	}
	
	private int currentAction = -1;
	public void doNextAction(){
		currentAction++;
		switch(currentAction){
		case 0 :
			model.mixDeck();
			break;
		case 1 :
			model.distributeCards();
			break;
		case 2 :
			model.revertPlayer();
			break;
		case 3 :
			model.organizePlayerCards();
			break;
		case 4 :
			model.detectPetitSec();
			break;
		case 5 :
			model.revertChien();
			break;
		case 6 :
			drawChoseButtons();
			break;
		case 7 :
			model.organizePlayerCards();//Only if it use gap
			break;
		case 8 :
			//continue
			break;
		}
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		switch(((Pair<TarotAction, Object>)arg1).getKey()){
			case MIX_DECK :
				updateDeckMixed();
				break;
			case DISTRIBUTE_3_CARDS :
				update3CardsDistributed(((Pair<TarotAction, Pair<Boolean, CardModel[]>>)arg1).getValue());
				break;
			case MOVE_CARD :
				updateCardMoved(((Pair<TarotAction, CardModel>)arg1).getValue());
				break;
			case REVERT_PLAYER :
				revertPlayer();
				break;
			case ORGANIZE_PLAYER :
				updatePlayerCardsOrganized();
				break;
			case DETECT_PETIT_SEC :
				updatePetitSec(((Pair<TarotAction, Boolean>)arg1).getValue());
				break;
			case CHOOSE_ACTION :
				updateActionChosen(((Pair<TarotAction, PlayerAction>)arg1).getValue());
				break;
			case REVERT_CHIEN :
				updateRevertChien();
				break;
			case GAP_DONE :
				updateGapDone();
				break;
		}
	}
	
	public void updateDeckMixed() {
		for(CardModel card : model.getDeckCards()){
			cardViews.put(card.getName(), new CardView(card.getPath(), card.getX(), card.getY(), card.getZ()));
			group.getChildren().add(cardViews.get(card.getName()).getBack());
			group.getChildren().add(cardViews.get(card.getName()).getFront());
		}
		doNextAction();
	}
	
	public void updateGapDone() {
		for(CardModel card : model.getMyCards()){
			removeListeners(cardViews.get(card.getName()));
		}
	}

	public void update3CardsDistributed(Pair<Boolean, CardModel[]> arg) {
		
		EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				if(!arg.getKey()){
					model.distributeCards();
				}
				else{
					doNextAction();
				}
			}
		};
		
		moveCardTo(cardViews.get(arg.getValue()[0].getName()), arg.getValue()[0], null);
		moveCardTo(cardViews.get(arg.getValue()[1].getName()), arg.getValue()[1], null);
		moveCardTo(cardViews.get(arg.getValue()[2].getName()), arg.getValue()[2], onFinished);
	}

	public void updateCardMoved(CardModel card) {
		moveCardTo(cardViews.get(card.getName()), card, null);
	}
	
	private void moveCardTo(CardView cardView, CardModel card, EventHandler<ActionEvent> onFinished) {
		Timeline animationMoveCard = new Timeline();

		KeyValue kVMoveXCardB = new KeyValue(cardView.getBack().xProperty(), card.getX());
		KeyValue kVMoveXCardF = new KeyValue(cardView.getFront().xProperty(), card.getX());
		
		KeyValue kVMoveYCardB = new KeyValue(cardView.getBack().yProperty(), card.getY());
		KeyValue kVMoveYCardF = new KeyValue(cardView.getFront().yProperty(), card.getY());
		
		KeyValue kVMoveZCardB;
		KeyValue kVMoveZCardF;
		if(!card.onFront){
			kVMoveZCardB = new KeyValue(cardView.getBack().translateZProperty(), card.getZ());
			kVMoveZCardF = new KeyValue(cardView.getFront().translateZProperty(), card.getZ()+0.1);
		}
		else{
			kVMoveZCardB = new KeyValue(cardView.getBack().translateZProperty(), card.getZ()+0.1);
			kVMoveZCardF = new KeyValue(cardView.getFront().translateZProperty(), card.getZ());
		}
		
		double delta = Math.abs(cardView.getBack().getX() - card.getX());
		delta += Math.abs(cardView.getBack().getY() - card.getY());
		
		Duration duration = Duration.seconds(delta/6000); // TODO REMETTRE 1500

		KeyFrame keyFrame = new KeyFrame(duration, onFinished, 
				kVMoveXCardB, kVMoveYCardB, kVMoveZCardB,
				kVMoveXCardF, kVMoveYCardF, kVMoveZCardF);
		animationMoveCard.getKeyFrames().add(keyFrame);

		animationMoveCard.play();
	}

	private int playerIndex = 0;
	private EventHandler<ActionEvent> revertPlayerOnFinished;
	public void revertPlayer() {
		
		revertPlayerOnFinished = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				playerIndex++;
				if(playerIndex < Model.PLAYER_NB_CARDS){
					returnCard(cardViews.get(model.getMyCards().get(playerIndex).getName()), true, revertPlayerOnFinished);
				}
				else{
					doNextAction();
				}
			}
		};
		returnCard(cardViews.get(model.getMyCards().get(playerIndex).getName()), true, revertPlayerOnFinished);
	}

	private final static double RETURN_CARD_DURATION = 0.2; // TODO REMETTRE A 0.6
	private void returnCard(CardView cardView, boolean toFront, EventHandler<ActionEvent> onFinished){
		returnCardMoveX(cardView,  toFront);
		
		Timeline revertPlayerAnimation = new Timeline();
		
		KeyValue kVRevertCardB;
		KeyValue kVRevertCardF;
		KeyValue kVMoveCardB;
		KeyValue kVMoveCardF;
		if(toFront){
			kVRevertCardB = new KeyValue(cardView.getBack().rotateProperty(), 360);
			kVRevertCardF = new KeyValue(cardView.getFront().rotateProperty(), 360);

			kVMoveCardB = new KeyValue(cardView.getBack().translateZProperty(), 1.1);
			kVMoveCardF = new KeyValue(cardView.getFront().translateZProperty(), 1);
		}
		else{
			kVRevertCardB = new KeyValue(cardView.getBack().rotateProperty(), 180);
			kVRevertCardF = new KeyValue(cardView.getFront().rotateProperty(), 180);

			kVMoveCardB = new KeyValue(cardView.getBack().translateZProperty(), 1);
			kVMoveCardF = new KeyValue(cardView.getFront().translateZProperty(), 1.1);
		}
		
		KeyFrame revertPlayerKeyFrame = new KeyFrame(Duration.seconds(RETURN_CARD_DURATION), onFinished, kVRevertCardB, kVMoveCardB, kVRevertCardF, kVMoveCardF);
		
		revertPlayerAnimation.getKeyFrames().add(revertPlayerKeyFrame);
		
		revertPlayerAnimation.play();
	}
	
	private void returnCardMoveX(CardView cardView, boolean toFront){
		Timeline moveXAnimation1 = new Timeline();
		final Timeline moveXAnimation2 = new Timeline();
		
		double shift;
		if(toFront){
			shift = 0.1;
		}
		else{
			shift = -0.1;
		}
		
		KeyValue kVMoveXB = new KeyValue(cardView.getBack().xProperty(), cardView.getBack().getX()-shift);
		KeyValue kVMoveXF = new KeyValue(cardView.getFront().xProperty(), cardView.getFront().getX()+shift);
		
		EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				moveXAnimation2.play();
			}
		};
		
		KeyFrame kFMoveX1 = new KeyFrame(Duration.seconds(RETURN_CARD_DURATION / 2), onFinished, kVMoveXB, kVMoveXF);
		
		moveXAnimation1.getKeyFrames().add(kFMoveX1);
		
		moveXAnimation1.play();
		
		KeyValue kVMoveXB2 = new KeyValue(cardView.getBack().xProperty(), cardView.getBack().getX()+shift);
		KeyValue kVMoveXF2 = new KeyValue(cardView.getFront().xProperty(), cardView.getFront().getX()-shift);
		
		KeyFrame kFMoveX2 = new KeyFrame(Duration.seconds(RETURN_CARD_DURATION / 2), kVMoveXB2, kVMoveXF2);
		
		moveXAnimation2.getKeyFrames().add(kFMoveX2);
	}

	public void updatePlayerCardsOrganized() {
		CardModel card;
		for(int i =0; i<model.getMyCards().size(); i++){
			card = model.getMyCards().get(i);
			moveCardTo(cardViews.get(card.getName()), card, null);
		}
		
		EventHandler<ActionEvent> nextAction = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				doNextAction();
			}
		};
		waiter(1, nextAction);
	}
	
	private void waiter(double duration, EventHandler<ActionEvent> event){
		Timeline timeLine = new Timeline();
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(duration), event);
		timeLine.getKeyFrames().add(keyFrame);
		timeLine.play();
	}
	
	public void updatePetitSec(Boolean petitSec) {
		if(!petitSec){
			doNextAction();
		}
		else{
			//cancel the game
		}
	}

	private static final int BUTTON_X_DIFF = 210;
	public static final int BUTTON_Y = Model.DIST_CARD_Y2 + CardModel.CARD_H + 50;
	private static final int BUTTON_W = 200;
	private static final int BUTTON_H = 100;
	public static final int BUTTON_X_START = (int)(Model.SCREEN_W/2 - (BUTTON_W + BUTTON_X_DIFF * 3)/2);

	private Button priseBut = new Button("Prise");
	private Button gardeBut = new Button("Garde");
	private Button gardeSansBut = new Button("Garde\nsans chien");
	private Button gardeContreBut = new Button("Garde contre\nle chien");

	private Font buttonsFont = new Font(20);

	public void drawChoseButtons(){
		initButton(priseBut, BUTTON_X_START, BUTTON_Y);
		priseBut.setOnMouseClicked(mouseEvent -> controller.chooseAction(PlayerAction.PRISE));

		initButton(gardeBut, BUTTON_X_START + BUTTON_X_DIFF, BUTTON_Y);
		gardeBut.setOnMouseClicked(mouseEvent -> controller.chooseAction(PlayerAction.GARDE));

		initButton(gardeSansBut, BUTTON_X_START + 2*BUTTON_X_DIFF, BUTTON_Y);
		gardeSansBut.setOnMouseClicked(mouseEvent -> controller.chooseAction(PlayerAction.GARDE_SANS));

		initButton(gardeContreBut, BUTTON_X_START + 3*BUTTON_X_DIFF, BUTTON_Y);
		gardeContreBut.setOnMouseClicked(mouseEvent -> controller.chooseAction(PlayerAction.GARDE_CONTRE));
	}

	private void initButton(Button button, int x, int y){
		button.setFont(buttonsFont);
		button.setLayoutX(x);
		button.setLayoutY(y);
		button.setTextAlignment(TextAlignment.CENTER);
		button.setPrefSize(BUTTON_W, BUTTON_H);
		button.setRotationAxis(new Point3D(1,0,0));
		button.setRotate(20);
		group.getChildren().add(button);
	}

	public static final int ECART_ZONE_X = Model.GAP_X_START-40;
	public static final int ECART_ZONE_Y = Model.GAP_Y-40;
	public static final int ECART_ZONE_W = CardModel.CARD_W + 5*Model.DIST_CARD_X_DIFF+80;
	public static final int ECART_ZONE_H = CardModel.CARD_H + 80;


	private Rectangle ecartArea = new Rectangle(ECART_ZONE_X, ECART_ZONE_Y, ECART_ZONE_W, ECART_ZONE_H);
	public void updateActionChosen(PlayerAction action) {
		group.getChildren().remove(priseBut);
		group.getChildren().remove(gardeBut);
		group.getChildren().remove(gardeSansBut);
		group.getChildren().remove(gardeContreBut);

		if(action == PlayerAction.PRISE || action == PlayerAction.GARDE){
			doGap();
			ecartArea.setFill(Color.BLUE);
			ecartArea.setTranslateZ(1.2);
			group.getChildren().add(ecartArea);
		}
		else{
			currentAction++;
			doNextAction();
		}
	}
	
	public void updateRevertChien() {
		revertChien();
		doNextAction();
	}
	
	private int chienIndex = 0;
	private EventHandler<ActionEvent> revertChienOnFinished;
	public void revertChien() {

		revertChienOnFinished = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				chienIndex++;
				if(chienIndex < Model.CHIEN_SIZE){
					returnCard(cardViews.get(model.getChienCards().get(chienIndex).getName()), true, revertChienOnFinished);
				}
			}
		};
		
		returnCard(cardViews.get(model.getChienCards().get(chienIndex).getName()), true, revertChienOnFinished);
	}
	
	private int selectedCardXStart = 0;
	private int selectedCardYStart = 0;
	private void doGap(){
		for(CardModel card : model.getMyCards()){
			cardViews.get(card.getName()).getFront().setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override public void handle(MouseEvent event) {
					selectedCardXStart = (int) card.getX();
					selectedCardYStart = (int) card.getY();
				}
			});
			cardViews.get(card.getName()).getFront().setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override public void handle(MouseEvent event) {
					cardViews.get(card.getName()).getFront().setX(event.getX() - CardModel.CARD_W/2);
					cardViews.get(card.getName()).getFront().setY(event.getY() - CardModel.CARD_H/2);
					
					cardViews.get(card.getName()).getBack().setX(event.getX() - CardModel.CARD_W/2);
					cardViews.get(card.getName()).getBack().setY(event.getY() - CardModel.CARD_H/2);
				}
			});
			cardViews.get(card.getName()).getFront().setOnMouseReleased(new EventHandler<MouseEvent>() {
				@Override public void handle(MouseEvent event) {
					if(model.ungapableCards().contains(card.getName())
							|| cardViews.get(card.getName()).getFront().getX() + CardModel.CARD_W/2 < ECART_ZONE_X
							|| cardViews.get(card.getName()).getFront().getX() + CardModel.CARD_W/2 > ECART_ZONE_X + ECART_ZONE_W
							|| cardViews.get(card.getName()).getFront().getY() + CardModel.CARD_H/2 < ECART_ZONE_Y
							|| cardViews.get(card.getName()).getFront().getY() + CardModel.CARD_H/2 > ECART_ZONE_Y + ECART_ZONE_H){
						CardModel tmp = new CardModel(selectedCardXStart, selectedCardYStart, card.getZ(), true);
						moveCardTo(cardViews.get(card.getName()), tmp, null);
					}
					else{
						controller.addCardToGap(card);
						removeListeners(cardViews.get(card.getName()));
					}
				}
			});
		}
	}
	
	private void removeListeners(CardView cardView){
		cardView.getFront().setOnMousePressed(null);
		cardView.getFront().setOnMouseDragged(null);
		cardView.getFront().setOnMouseReleased(null);
	}
}
