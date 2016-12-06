package Tarot.ViewPack;

import Tarot.ModelPack.Model;
import Tarot.ViewPack.DistributionPack.View;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
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
        put("play", new ImageButton("file:./res/play.png", Model.SCREEN_W / 2 - ImageButton.BUTTON_W / 2, Model.SCREEN_W / 2));
        put("settings", new ImageButton("file:./res/settings.png", Model.SCREEN_W / 2 - (ImageButton.BUTTON_W + Model.SCREEN_W / 19.2) / 2, Model.SCREEN_H / 2 + Model.SCREEN_H / 20));
        put("quit", new ImageButton("file:./res/quit.png", Model.SCREEN_W / 2 - (ImageButton.BUTTON_W + Model.SCREEN_W / 19.2) / 2, Model.SCREEN_H / 2 + Model.SCREEN_H / 3.6));
    }};

    private View principalView = null;

    public MenuView(View principalView) {
        components.add(menuBackground);
        components.add(menuTitle);
        components.add(imageButtons.get("play"));
        components.add(imageButtons.get("settings"));
        components.add(imageButtons.get("quit"));
        components.add(imageButtons.get("play").getImage());
        components.add(imageButtons.get("settings").getImage());
        components.add(imageButtons.get("quit").getImage());

        this.principalView = principalView;

        double ratioBackImage = Model.SCREEN_H / menuBackground.getImage().getHeight();
        menuBackground.setScaleY(ratioBackImage);
        menuBackground.setScaleX(ratioBackImage);

        menuBackground.setX(Model.SCREEN_W / 2 - menuBackground.getImage().getWidth() / 2);
        menuBackground.setY(Model.SCREEN_H / 2 - menuBackground.getImage().getHeight() / 2);

        menuTitle.setTranslateX(Model.SCREEN_W / 2 - menuTitle.getImage().getWidth() / 2);
        menuTitle.setTranslateY(Model.SCREEN_H / 10);

        imageButtons.get("play").setImageSize(Model.SCREEN_W / 5, Model.SCREEN_H / 5);
        imageButtons.get("play").setPosition(Model.SCREEN_W / 2 - ImageButton.BUTTON_W / 2.5,
                Model.SCREEN_H / 4 + ImageButton.BUTTON_H / 2);

        imageButtons.get("settings").setImageSize(Model.SCREEN_W / 4, Model.SCREEN_H / 4);
        imageButtons.get("settings").setPosition(Model.SCREEN_W / 2 - ImageButton.BUTTON_W / 2,
                Model.SCREEN_H / 2 + ImageButton.BUTTON_H / 3);

        imageButtons.get("quit").setImageSize(Model.SCREEN_W / 5, Model.SCREEN_H / 5);
        imageButtons.get("quit").setPosition(Model.SCREEN_W / 2 - ImageButton.BUTTON_W / 2,
                Model.SCREEN_H / 2 + ImageButton.BUTTON_H * 1.7);

        imageButtons.get("play").setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                principalView.displayDistribution();
            }
        });
        imageButtons.get("play").setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                imageButtons.get("play").inflate();
            }
        });
        imageButtons.get("play").setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                imageButtons.get("play").deflate();
            }
        });
        imageButtons.get("settings").setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                principalView.displaySettings();
            }
        });
        imageButtons.get("settings").setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                imageButtons.get("settings").inflate();
            }
        });
        imageButtons.get("settings").setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                imageButtons.get("settings").deflate();
            }
        });
        imageButtons.get("quit").setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.exit(0);
            }
        });
        imageButtons.get("quit").setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                imageButtons.get("quit").inflate();
            }
        });
        imageButtons.get("quit").setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                imageButtons.get("quit").deflate();
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
