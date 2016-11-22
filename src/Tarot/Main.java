package Tarot;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
	private static Model model;
	private static Controller controller;
	private static View view;
	
	public static void main (String[] args){
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		stage.setFullScreen(true);
		model = new Model();
		controller = new Controller(model);
		view = new View(controller);
		model.addObserver(view);
		
		stage.setTitle("Martinez Dubourg S3D");
		stage.setScene(view.getScene());
		stage.sizeToScene();
		stage.show();
		
		view.displayMenu();
	}
	
	public void nouvelleDonne(){
		model = new Model();
		controller = new Controller(model);
		view = new View(controller);
		model.addObserver(view);
		
		view.doNextAction();
	}
}
