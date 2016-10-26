package Tarot;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
	private Model model;
	private Controller controller;
	private View view;
	
	public static void main (String[] args){
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		model = new Model();
		controller = new Controller(model);
		view = new View(controller);
		model.addObserver(view);
		
		view.copyModelCards();
		
		stage.setTitle("Martinez Dubourg S3D");
		stage.setScene(view.getScene());
		stage.sizeToScene();
		stage.show();
	}
}
