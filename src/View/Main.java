package View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Controller.Controller;
import Model.Model;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main (String[] args){
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		
		Model model = new Model();
		Controller controller = new Controller();
		
		Group group = new Group();
		Scene scene = new Scene(group, 1280, 900);
		
		Map<String, Image> images = new HashMap<String, Image>();
		Map<String, ImageView> cards = new HashMap<String, ImageView>();
		
		loadCardsImages(images, cards);
		
		scene.setFill(Color.BLACK);

		stage.setTitle("Let's play Tarot !");
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}
	
	private void loadCardsImages(Map<String, Image> images, Map<String, ImageView> cards){
		images.put("Back", new Image("file:./cards/back.jpg"));
		loadTrumps(images, cards);
		loadCards(images, cards);
	}
	
	private void loadTrumps(Map<String, Image> images, Map<String, ImageView> cards){
		String fullName;
		for(int i = 1; i <= 21; i++){
			fullName = "file:./cards/Tarot_nouveau_-_Grimaud_-_1898_-_Trumps_-_";
			if(i < 10){
				fullName += "0";
			}
			fullName += Integer.toString(i) + ".jpg";
			images.put("Trump" + Integer.toString(i), new Image(fullName));
			cards.put("Trump" + Integer.toString(i), new ImageView(images.get("Back")));
		}
	}
	
	private void loadCards(Map<String, Image> images, Map<String, ImageView> cards){
		String fullName;
		for(String color : cardColors()){
			for(String value : cardValues()){
				fullName = "file:./cards/Tarot_nouveau_-_Grimaud_-_1898_-_" + color + "_-_" + value + ".jpg";
				images.put(color + value, new Image(fullName));
				cards.put(color + value, new ImageView(images.get("Back")));
			}
		}
	}
	
	private ArrayList<String> cardColors(){
		ArrayList<String> colors = new ArrayList<String>();
		colors.add("Clubs");
		colors.add("Diamonds");
		colors.add("Hearts");
		colors.add("Spades");
		return colors;
	}
	
	private ArrayList<String> cardValues(){
		ArrayList<String> values = new ArrayList<String>();
		values.add("Ace");
		for(int i = 2; i <= 9; i++){
			values.add("0" + Integer.toString(i));
		}
		values.add("10");
		values.add("Jack");
		values.add("Knight");
		values.add("Queen");
		values.add("King");
		return values;
	}
}
