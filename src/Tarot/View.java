package Tarot;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class View implements Observer{
	private Controller controller;
	private Model model;

	private Group group = new Group();
	private Scene scene = new Scene(group, Model.SCREEN_W, Model.SCREEN_H);

	private Map<String, CardView> cardViews = new HashMap<String, CardView>();

	public View(Controller controller){
		this.controller = controller;
		this.model = controller.getModel();

		scene.setFill(Color.BLACK);
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
			revertPlayer();
			break;
		case 3 :
			model.organizePlayerCards();
			break;
		case 4 :
			drawChoseButtons();
			break;
		case 5 :
			model.organizePlayerCards();//Only if it use gap
			break;
		case 6 :
			//continue
			break;
		}
	}

	@Override
	public void updateDeckMixed() {
		CardModel card;
		for(int i = model.getDeckCards().size()-1; i >= 0; i--){
			card = model.getDeckCards().get(i);
			cardViews.put(card.getName(), new CardView(card.getPath(), card.getX(), card.getY()));
			group.getChildren().add(cardViews.get(card.getName()).getView());
		}
		doNextAction();
	}

	@Override
	public void update3CardsDistributed(CardModel card1, CardModel card2,CardModel card3, boolean dealFinished) {
		Timeline animationMove3Cards = new Timeline();

		KeyValue kVMoveXCard1 = new KeyValue(cardViews.get(card1.getName()).getView().xProperty(), card1.getX());
		KeyValue kVMoveYCard1 = new KeyValue(cardViews.get(card1.getName()).getView().yProperty(), card1.getY());

		KeyValue kVMoveXCard2 = new KeyValue(cardViews.get(card2.getName()).getView().xProperty(), card2.getX());
		KeyValue kVMoveYCard2 = new KeyValue(cardViews.get(card2.getName()).getView().yProperty(), card2.getY());

		KeyValue kVMoveXCard3 = new KeyValue(cardViews.get(card3.getName()).getView().xProperty(), card3.getX());
		KeyValue kVMoveYCard3 = new KeyValue(cardViews.get(card3.getName()).getView().yProperty(), card3.getY());

		Duration duration0P5S = Duration.seconds(0.5);

		EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				if(!dealFinished){
					model.distributeCards();
				}
				else{
					doNextAction();
				}
			}
		};

		KeyFrame keyFrame = new KeyFrame(duration0P5S, onFinished, kVMoveXCard1, kVMoveYCard1, kVMoveXCard2, kVMoveYCard2, kVMoveXCard3, kVMoveYCard3);
		animationMove3Cards.getKeyFrames().add(keyFrame);

		animationMove3Cards.play();
	}

	@Override
	public void updateCardMoved(CardModel card) {
		Timeline animationMoveCard = new Timeline();

		KeyValue kVMoveXCard = new KeyValue(cardViews.get(card.getName()).getView().xProperty(), card.getX());
		KeyValue kVMoveYCard = new KeyValue(cardViews.get(card.getName()).getView().yProperty(), card.getY());

		Duration duration0P5S = Duration.seconds(0.5);

		KeyFrame keyFrame = new KeyFrame(duration0P5S, kVMoveXCard, kVMoveYCard);
		animationMoveCard.getKeyFrames().add(keyFrame);

		animationMoveCard.play();
	}

	private Timeline revertChienAnimation1;
	private Timeline revertChienAnimation2;
	private int chienIndex = 0;
	private KeyValue revertChienKVRevertCard1;
	private KeyValue revertChienKVRevertCard2;
	private KeyFrame revertChienKeyFrame1;
	private KeyFrame revertChienKeyFrame2;
	private EventHandler<ActionEvent> revertChienOnFinished1;
	private EventHandler<ActionEvent> revertChienOnFinished2;
	public void revertChien() {
		Duration duration0P1S = Duration.seconds(0.1);

		revertChienOnFinished1 = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				cardViews.get(model.getChienCards().get(chienIndex).getName()).changeImage();
				revertChienAnimation2.play();
			}
		};

		revertChienOnFinished2 = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				chienIndex++;
				if(chienIndex < Model.CHIEN_SIZE){
					initRevertChien(duration0P1S);
					revertChienAnimation1.play();
				}
			}
		};

		initRevertChien(duration0P1S);
		revertChienAnimation1.play();
	}

	private void initRevertChien(Duration duration){
		revertChienAnimation1 = new Timeline();
		revertChienAnimation2 = new Timeline();

		revertChienKVRevertCard1 = new KeyValue(cardViews.get(model.getChienCards().get(chienIndex).getName()).getView().rotateProperty(), 270);
		revertChienKVRevertCard2 = new KeyValue(cardViews.get(model.getChienCards().get(chienIndex).getName()).getView().rotateProperty(), 360);

		revertChienKeyFrame1 = new KeyFrame(duration, revertChienOnFinished1, revertChienKVRevertCard1);
		revertChienKeyFrame2 = new KeyFrame(duration, revertChienOnFinished2, revertChienKVRevertCard2);

		revertChienAnimation1.getKeyFrames().add(revertChienKeyFrame1);
		revertChienAnimation2.getKeyFrames().add(revertChienKeyFrame2);
	}

	private Timeline revertPlayerAnimation1;
	private Timeline revertPlayerAnimation2;
	private int playerIndex = 0;
	private KeyValue revertPlayerKVRevertCard1;
	private KeyValue revertPlayerKVRevertCard2;
	private KeyFrame revertPlayerKeyFrame1;
	private KeyFrame revertPlayerKeyFrame2;
	private EventHandler<ActionEvent> revertPlayerOnFinished1;
	private EventHandler<ActionEvent> revertPlayerOnFinished2;
	public void revertPlayer() {
		Duration duration0P1S = Duration.seconds(0.1);

		revertPlayerOnFinished1 = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				cardViews.get(model.getMyCards().get(playerIndex).getName()).changeImage();
				revertPlayerAnimation2.play();
			}
		};

		revertPlayerOnFinished2 = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				playerIndex++;
				if(playerIndex < Model.PLAYER_NB_CARDS){
					initRevertPlayer(duration0P1S);
					revertPlayerAnimation1.play();
				}
				else{
					doNextAction();
				}
			}
		};

		initRevertPlayer(duration0P1S);
		revertPlayerAnimation1.play();
	}

	private void initRevertPlayer(Duration duration){
		revertPlayerAnimation1 = new Timeline();
		revertPlayerAnimation2 = new Timeline();

		revertPlayerKVRevertCard1 = new KeyValue(cardViews.get(model.getMyCards().get(playerIndex).getName()).getView().rotateProperty(), 270);
		revertPlayerKVRevertCard2 = new KeyValue(cardViews.get(model.getMyCards().get(playerIndex).getName()).getView().rotateProperty(), 360);

		revertPlayerKeyFrame1 = new KeyFrame(duration, revertPlayerOnFinished1, revertPlayerKVRevertCard1);
		revertPlayerKeyFrame2 = new KeyFrame(duration, revertPlayerOnFinished2, revertPlayerKVRevertCard2);

		revertPlayerAnimation1.getKeyFrames().add(revertPlayerKeyFrame1);
		revertPlayerAnimation2.getKeyFrames().add(revertPlayerKeyFrame2);
	}

	@Override
	public void updatePlayerCardsOrganized() {
		Timeline animationOrganizePlayerCards = new Timeline();

		KeyValue[] kVMoveCards = new KeyValue[2*Model.NB_CARDS_PLAYER];

		int i = 0;
		for(CardModel card : model.getMyCards()){
			kVMoveCards[i] = new KeyValue(cardViews.get(card.getName()).getView().xProperty(), card.getX());
			kVMoveCards[i+1] = new KeyValue(cardViews.get(card.getName()).getView().yProperty(), card.getY());
			i += 2;
		}

		Duration duration1S = Duration.seconds(1);

		EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				doNextAction();
			}
		};

		KeyFrame keyFrame = new KeyFrame(duration1S, onFinished, kVMoveCards);
		animationOrganizePlayerCards.getKeyFrames().add(keyFrame);

		animationOrganizePlayerCards.play();
	}

	private static final int BUTTON_X_START = 50;
	private static final int BUTTON_X_DIFF = 210;
	private static final int BUTTON_Y = 750;
	private static final int BUTTON_W = 200;
	private static final int BUTTON_H = 100;

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
		group.getChildren().add(button);
	}

	@Override
	public void updateActionChosen(PlayerAction action) {
		group.getChildren().remove(priseBut);
		group.getChildren().remove(gardeBut);
		group.getChildren().remove(gardeSansBut);
		group.getChildren().remove(gardeContreBut);

		revertChien();
		if(action == PlayerAction.PRISE || action == PlayerAction.GARDE){
			doGap();
		}
		else{
			currentAction++;
			doNextAction();
		}
	}
	
	private int selectedCardXStart = 0;
	private int selectedCardYStart = 0;
	private void doGap(){
		for(CardModel card : model.getMyCards()){
			cardViews.get(card.getName()).getView().setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override public void handle(MouseEvent event) {
					putOnFirstGround(cardViews.get(card.getName()).getView());
					selectedCardXStart = (int) cardViews.get(card.getName()).getView().getX();
					selectedCardYStart = (int) cardViews.get(card.getName()).getView().getY();
				}
			});
			cardViews.get(card.getName()).getView().setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override public void handle(MouseEvent event) {
					cardViews.get(card.getName()).getView().setX(event.getSceneX() - CardModel.CARD_W/2);
					cardViews.get(card.getName()).getView().setY(event.getSceneY() - CardModel.CARD_H/2);
				}
			});
			cardViews.get(card.getName()).getView().setOnMouseReleased(new EventHandler<MouseEvent>() {
				@Override public void handle(MouseEvent event) {
					if(cardViews.get(card.getName()).getView().getY() < Model.GAP_Y){
						cardViews.get(card.getName()).getView().setX(selectedCardXStart);
						cardViews.get(card.getName()).getView().setY(selectedCardYStart);
					}
					else{
						controller.addCardToGap(card);
						removeListeners(cardViews.get(card.getName()));
					}
				}
			});
		}
	}
	
	private void putOnFirstGround(Node node){
		group.getChildren().remove(node);
		group.getChildren().add(node);
	}

	@Override
	public void updateGapDone() {
		for(CardModel card : model.getMyCards()){
			removeListeners(cardViews.get(card.getName()));
		}
	}
	
	private void removeListeners(CardView cardView){
		cardView.getView().setOnMousePressed(null);
		cardView.getView().setOnMouseDragged(null);
		cardView.getView().setOnMouseReleased(null);
	}
}
