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
		for(CardModel card : model.getCards()){
			cardViews.put(card.getName(), new CardView(card.getPath(), card.getX(), card.getY()));
		}
		model.mixDeck();
	}

	@Override
	public void updateDeckMixed() {
		for(CardModel card : model.getCards()){
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
        
        Duration duration1S = Duration.seconds(1);
        
        EventHandler onFinished = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	if(!dealFinished){
            		model.distributeCards();
            	}
            }
        };
        
        KeyFrame keyFrame = new KeyFrame(duration1S, onFinished, kVMoveXCard1, kVMoveYCard1, kVMoveXCard2, kVMoveYCard2, kVMoveXCard3, kVMoveYCard3);
        animationMove3Cards.getKeyFrames().add(keyFrame);
        
        animationMove3Cards.play();
	}

	@Override
	public void updateCardAddToChien(CardModel card) {
		Timeline animationMoveCard = new Timeline();
		
		KeyValue kVMoveXCard = new KeyValue(cardViews.get(card.getName()).getView().xProperty(), card.getX());
        KeyValue kVMoveYCard = new KeyValue(cardViews.get(card.getName()).getView().yProperty(), card.getY());
        
        Duration duration1S = Duration.seconds(1);
        
        KeyFrame keyFrame = new KeyFrame(duration1S, kVMoveXCard, kVMoveYCard);
        animationMoveCard.getKeyFrames().add(keyFrame);
        
        animationMoveCard.play();
		
	}
}
