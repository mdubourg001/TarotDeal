package Tarot.ViewPack;

import Tarot.ModelPack.Model;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import static Tarot.ViewPack.ImageButton.BUTTON_W;

public class ImageButton extends Button{

    public static final double BUTTON_W = Model.SCREEN_W /  5;
    public static final double BUTTON_H = Model.SCREEN_H / 6;

    private static final Color BUTTON_BACKGROUND = Color.TRANSPARENT;

    private ImageView image = null;
    private String path = null;
    private double yShift = 0;
    private double xShift = 0;

    public ImageButton(String path, double x, double y, double width, double height) {
        super();
        image = new ImageView(path);
        this.path = path;
        this.setPrefSize(width, height);
        this.setTranslateX(0);
        this.setTranslateY(0);
        this.setTranslateZ(-5);
        this.setTextAlignment(TextAlignment.CENTER);
        this.setBackground(new Background(new BackgroundFill(BUTTON_BACKGROUND, new CornerRadii(50), null)));
        image.setTranslateZ(-4);
        image.setFitWidth(Model.SCREEN_W / 5);
        image.setFitHeight(Model.SCREEN_H / 5);
    }

    public ImageButton(String path, double x, double y) {
        this(path, x, y, BUTTON_W, BUTTON_H);
    }

    public ImageView getImage(){ return this.image; }

    public void setImage(String path) {
        this.image.setImage(new Image(path));
        this.path = path;
    }

    public void setButtonSize(double width, double height) {
        this.setPrefSize(width, height);
        this.setTranslateX(image.getTranslateX());
        this.setTranslateY(image.getTranslateY());
    }

    public String getPath() {
        return this.path;
    }

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
        yShift = ((Model.SCREEN_H / 2 - (this.getTranslateY() + this.BUTTON_H / 2)) * 0.20);
        xShift = ((Model.SCREEN_W / 2 - (this.getTranslateX() + this.BUTTON_W / 2)) * 0.20);
    }

    public void inflate() {
        image.setTranslateZ(image.getTranslateZ() - 400);
        this.setTranslateZ(this.getTranslateZ() - 401);
        this.setTranslateY(this.getTranslateY() + yShift);
        image.setTranslateY(image.getTranslateY() + yShift);
        this.setTranslateX(this.getTranslateX() + xShift);
        image.setTranslateX(image.getTranslateX() + xShift);
    }

    public void deflate() {
        image.setTranslateZ(image.getTranslateZ() + 400);
        this.setTranslateZ(this.getTranslateZ() + 401);
        this.setTranslateY(this.getTranslateY() - yShift);
        image.setTranslateY(image.getTranslateY() - yShift);
        this.setTranslateX(this.getTranslateX() - xShift);
        image.setTranslateX(image.getTranslateX() - xShift);
    }
}
