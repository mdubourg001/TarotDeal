package Tarot.ViewPack;

import Tarot.ModelPack.Model;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class ImageButton extends Button{

    public static final double BUTTON_W = Model.SCREEN_W /  5;
    public static final double BUTTON_H = Model.SCREEN_H / 6;

    private static final Color BUTTON_BACKGROUND = Color.TRANSPARENT;

    private ImageView image = null;

    public ImageButton(String path, double x, double y) {
        super();
        image = new ImageView(path);
        this.setPrefSize(BUTTON_W, BUTTON_H);
        this.setTranslateX(0);
        this.setTranslateY(0);
        this.setTranslateZ(-5);
        this.setTextAlignment(TextAlignment.CENTER);
        this.setBackground(new Background(new BackgroundFill(BUTTON_BACKGROUND, new CornerRadii(50), null)));
        image.setTranslateZ(-4);
        image.setFitWidth(Model.SCREEN_W / 5);
        image.setFitHeight(Model.SCREEN_H / 5);
    }

    public ImageView getImage(){ return this.image; }

    public void setImageSize(double width, double height) {
        image.setFitWidth(width);
        image.setFitHeight(height);
        // actualisation de la position après redimensionnement
        image.setTranslateX((this.getTranslateX() + this.getWidth() / 2) - image.getFitWidth() / 2);
        image.setTranslateY((this.getTranslateY() + this.getHeight() / 2) - image.getFitHeight() / 2);
    }

    public void setPosition(double x, double y) {
        this.setTranslateX(x);
        this.setTranslateY(y);
        image.setTranslateX((this.getTranslateX() + BUTTON_W / 2) - image.getFitWidth() / 2);
        image.setTranslateY((this.getTranslateY() + BUTTON_H / 2) - image.getFitHeight() / 2);
    }

    public void inflate(double shiftY) {
        image.setTranslateZ(image.getTranslateZ() - 400);
        this.setTranslateZ(this.getTranslateZ() - 401);
        this.setTranslateY(this.getTranslateY() + shiftY);
        image.setTranslateY(image.getTranslateY() + shiftY);
    }

    public void deflate(double shiftY) {
        image.setTranslateZ(image.getTranslateZ() + 400);
        this.setTranslateZ(this.getTranslateZ() + 401);
        this.setTranslateY(this.getTranslateY() + shiftY);
        image.setTranslateY(image.getTranslateY() + shiftY);
    }
}
