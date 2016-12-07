package Tarot.ViewPack;

import Tarot.ModelPack.Model;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class ImageButton extends Group{

    public static final double BUTTON_W = Model.SCREEN_W /  5;
    public static final double BUTTON_H = Model.SCREEN_H / 6;
    public static final double IMAGE_WIDTH = Model.SCREEN_W / 5;
    public static final double IMAGE_HEIGHT = Model.SCREEN_H / 5;

    private static final Color BUTTON_BACKGROUND = Color.ORANGE;

    private ImageView image = null;
    private Button button = new Button();
    private String path = null;

    public ImageButton(String filepath, double x, double y) {
        super();

        path = filepath;

        image = new ImageView(path);
        image.setTranslateZ(0);
        image.setFitWidth(IMAGE_WIDTH);
        image.setFitHeight(IMAGE_HEIGHT);

        button.setPrefSize(IMAGE_WIDTH, IMAGE_HEIGHT);
        button.setBackground(new Background(new BackgroundFill(BUTTON_BACKGROUND, new CornerRadii(50), null)));
        button.setTranslateZ(-1);
        button.setOpacity(0.5);

        this.setPosition(x, y);


        this.setOnMouseEntered(mouseEvent -> inflate());
        this.setOnMouseExited(mouseEvent -> deflate());

        this.getChildren().add(image);
        this.getChildren().add(button);
    }

    public String getPath() { return this.path; }

    public ImageView getImage(){ return this.image; }

    public void setImage(String path) {
        this.image.setImage(new Image(path));
        this.path = path;
    }

    public void setOnMouseClick(EventHandler<? super MouseEvent> value) {
        button.setOnMouseClicked(value);
    }

    public void setImageSize(double width, double height) {
        image.setFitWidth(width);
        image.setFitHeight(height);
        button.setPrefSize(width, height);
    }

    public void setDisplayed(boolean displayed) {
        if(displayed) {
            this.setVisible(true);
            this.setDisable(false);
        } else {
            this.setVisible(false);
            this.setDisable(true);
        }
    }

    public void setPosition(double x, double y) {
        this.setTranslateX(x);
        this.setTranslateY(y);
    }

    public void inflate() {
        this.setScaleX(1.3);
        this.setScaleY(1.3);
    }

    public void deflate() {
        this.setScaleX(1);
        this.setScaleY(1);
    }
}
