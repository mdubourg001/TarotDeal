package Tarot;

import com.sun.javafx.geom.Rectangle;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TaroDeal extends Application {
	public static void main (String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Controller controller = new Controller();
		
		primaryStage.setTitle("Tarot deal");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
