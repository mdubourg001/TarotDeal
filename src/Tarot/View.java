package Tarot;

import java.util.Map;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

public class View implements Observer{
	private Controller controller;
	private Model model;

	private Group group = new Group();
	private Scene scene = new Scene(group, Model.SCREEN_W, Model.SCREEN_H);
	
	private Map<String, CardView> cardViews;
	
	public View(Controller controller){
		this.controller = controller;
		this.model = controller.getModel();
		
		for(CardModel card : model.getCards()){
			cardViews.put(card.getName(), new CardView(card.getPath(), card.getX(), card.getY()));
		}
		
		scene.setFill(Color.BLACK);
	}
	
	public Scene getScene(){
		return scene;
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
	}
}
