package Tarot.ViewPack;

import Tarot.ModelPack.Model;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MenuView {
    private List<Node> components = new ArrayList<>();

    private ImageView menuBackground = new ImageView("file:./res/menu_background.jpg");
    private ImageView menuTitle = new ImageView("file:./res/title.png");

    private Map<String, ImageButton> imageButtons = new HashMap<String, ImageButton>() {{
        put("play", new ImageButton("file:./res/play.png", Model.SCREEN_W / 2 - ImageButton.BUTTON_W / 2, Model.SCREEN_H / 4 + ImageButton.BUTTON_H / 2));
        put("settings", new ImageButton("file:./res/settings.png", Model.SCREEN_W / 2 - ImageButton.BUTTON_W / 1.5, Model.SCREEN_H / 2 + ImageButton.BUTTON_H / 4.5));
        put("quit", new ImageButton("file:./res/quit.png", Model.SCREEN_W / 2 - ImageButton.BUTTON_W / 1.8, Model.SCREEN_H / 2 + ImageButton.BUTTON_H * 1.7));
    }};

    private View principalView = null;

    public MenuView(View principalView) {
        components.add(menuBackground);
        components.add(menuTitle);
        components.add(imageButtons.get("play"));
        components.add(imageButtons.get("settings"));
        components.add(imageButtons.get("quit"));

        this.principalView = principalView;

        double ratioBackImage = Model.SCREEN_H / menuBackground.getImage().getHeight();
        menuBackground.setScaleY(ratioBackImage);
        menuBackground.setScaleX(ratioBackImage);

        menuBackground.setX(Model.SCREEN_W / 2 - menuBackground.getImage().getWidth() / 2);
        menuBackground.setY(Model.SCREEN_H / 2 - menuBackground.getImage().getHeight() / 2);

        menuTitle.setTranslateX(Model.SCREEN_W / 2 - menuTitle.getImage().getWidth() / 2);
        menuTitle.setTranslateY(Model.SCREEN_H / 10);

        imageButtons.get("play").setImageSize(Model.SCREEN_W / 5, Model.SCREEN_H / 5);

        imageButtons.get("settings").setImageSize(Model.SCREEN_W / 4, Model.SCREEN_H / 4);

        imageButtons.get("quit").setImageSize(Model.SCREEN_W / 5, Model.SCREEN_H / 5);

        imageButtons.get("play").setOnMouseClick(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                principalView.displayDistribution();
                principalView.getController().startIfNeeded();
            }
        });
        imageButtons.get("settings").setOnMouseClick(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                principalView.displaySettings();
            }
        });
        imageButtons.get("quit").setOnMouseClick(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.exit(0);
            }
        });
    }

    public void display(Group menuGroup, Group root) {
        root.getChildren().clear();
        menuGroup.getChildren().clear();
        menuGroup.getChildren().addAll(components);
        root.getChildren().add(menuGroup);
    }
}
