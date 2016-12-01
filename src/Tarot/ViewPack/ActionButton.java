package Tarot.ViewPack;

import Tarot.ModelPack.Model;
import Tarot.ModelPack.PlayerAction;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class ActionButton extends Button {
    public static final double BUTTON_W = Model.SCREEN_W/6.5;
    public static final double BUTTON_H = BUTTON_W/2.33;
    public static final double BUTTON_X_DIFF = Model.SCREEN_W/200;
    public static final double BUTTON_X_START = (Model.SCREEN_W - (5 * BUTTON_W + 4 * BUTTON_X_DIFF)) / 2;
    public static final double BUTTON_Y = Model.SCREEN_H / 10;

    private static Font font = new Font(BUTTON_W/8.75);

    private PlayerAction action;

    ActionButton(String name, double x, double y, double z, PlayerAction action){
        super(name);
        this.action = action;
        setFont(font);
        setLayoutX(x);
        setLayoutY(y);
        setTranslateZ(z);
        setTextAlignment(TextAlignment.CENTER);
        setPrefSize(BUTTON_W, BUTTON_H);
        setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(50), null)));
        setTextFill(Color.WHITE);
        setOnMouseEntered(mouseEvent -> changeColorButton(true));
        setOnMouseExited(mouseEvent -> changeColorButton(false));
    }

    private void changeColorButton(boolean toWhite) {
        if (toWhite) {
            setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(50), null)));
            setTextFill(Color.BLACK);
        } else {
            setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(50), null)));
            setTextFill(Color.WHITE);
        }
    }
    
    public PlayerAction getAction(){
    	return action;
    }
}
