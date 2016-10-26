package Tarot;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
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
	
	public void copyModelCards(){
		for(CardModel card : model.getDeckCards()){
			cardViews.put(card.getName(), new CardView(card.getPath(), card.getX(), card.getY()));
		}
		model.mixDeck();
	}

	@Override
	public void updateDeckMixed() {
		for(CardModel card : model.getDeckCards()){
			group.getChildren().add(cardViews.get(card.getName()).getView());
		}
		model.distributeCards();
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
            		revertChien();
            	}
            }
        };
        
        KeyFrame keyFrame = new KeyFrame(duration0P5S, onFinished, kVMoveXCard1, kVMoveYCard1, kVMoveXCard2, kVMoveYCard2, kVMoveXCard3, kVMoveYCard3);
        animationMove3Cards.getKeyFrames().add(keyFrame);
        
        animationMove3Cards.play();
	}

	@Override
	public void updateCardAddToChien(CardModel card) {
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
		Duration duration0P25S = Duration.seconds(0.25);
		
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
            		initRevertChien(duration0P25S);
            		revertChienAnimation1.play();
            	}
            	else{
            		revertPlayer();
            	}
            }
        };
		
        initRevertChien(duration0P25S);
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
		Duration duration0P25S = Duration.seconds(0.25);
		
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
            		initrevertPlayer(duration0P25S);
            		revertPlayerAnimation1.play();
            	}
            }
        };
		
        initrevertPlayer(duration0P25S);
		revertPlayerAnimation1.play();
	}
	
	private void initrevertPlayer(Duration duration){
		revertPlayerAnimation1 = new Timeline();
		revertPlayerAnimation2 = new Timeline();
		
		revertPlayerKVRevertCard1 = new KeyValue(cardViews.get(model.getMyCards().get(playerIndex).getName()).getView().rotateProperty(), 270);
		revertPlayerKVRevertCard2 = new KeyValue(cardViews.get(model.getMyCards().get(playerIndex).getName()).getView().rotateProperty(), 360);
		
		revertPlayerKeyFrame1 = new KeyFrame(duration, revertPlayerOnFinished1, revertPlayerKVRevertCard1);
		revertPlayerKeyFrame2 = new KeyFrame(duration, revertPlayerOnFinished2, revertPlayerKVRevertCard2);
		
		revertPlayerAnimation1.getKeyFrames().add(revertPlayerKeyFrame1);
		revertPlayerAnimation2.getKeyFrames().add(revertPlayerKeyFrame2);
	}
}
