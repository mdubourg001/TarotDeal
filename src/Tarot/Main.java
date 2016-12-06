package Tarot;

import Tarot.ModelPack.Model;
import Tarot.ViewPack.DistributionPack.View;
import javafx.application.Application;
import javafx.scene.input.KeyCombination;
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
		stage.setFullScreenExitHint("");
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
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
}
