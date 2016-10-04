package Tarot;

import javafx.application.Application;
import javafx.stage.Stage;

public class TarotDeal extends Application {
	public static void main (String[] args){
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		
		Controller controller = new Controller();
		
		stage.setTitle("Tarot deal");
		stage.setScene(controller.getView().getScene());
		stage.show();
	}
}
