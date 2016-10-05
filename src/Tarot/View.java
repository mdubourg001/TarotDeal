package Tarot;

import javafx.scene.Group;
import javafx.scene.Scene;

public class View {
	private Group group = new Group();
	private Scene scene = new Scene(group, 1280, 900);
	
	public Scene getScene() {
		return scene;
	}
}
