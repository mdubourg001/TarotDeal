package Tarot.ViewPack;

import Tarot.ModelPack.Model;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;


public class SettingsView {
    private static final double CARDBACK_Y = Model.SCREEN_H / 10;
    private static final double CARDBACK1_X = Model.SCREEN_W / 1.9;
    private static final double CARDBACK2_X = Model.SCREEN_W / 1.52;
    private static final double CARDBACK3_X = Model.SCREEN_W / 1.27;

    private List<Node> components = new ArrayList<>();

    private View principalView = null;

    private ImageView settingsBackground = new ImageView("file:./res/settings_background.png");

    private ImageButton leftArrow = new ImageButton("file:./res/arrowleft.png", 0, 0, Model.SCREEN_W / 8, Model.SCREEN_H / 6);
    private ImageButton rightArrow = new ImageButton("file:./res/arrowright.png", 0, 0, Model.SCREEN_W / 8, Model.SCREEN_H / 6);
    private ImageButton soundMuteButton = new ImageButton("file:./res/soundicon.png", 0, 0, Model.SCREEN_W / 8, Model.SCREEN_H / 6);
    private ImageButton backMenuButton = new ImageButton("file:./res/backtomenu.png", 0, 0, Model.SCREEN_W / 8, Model.SCREEN_H / 6);

    private Slider soundSlider = new Slider(0, 100, 70);
    private Slider zoomSlider = new Slider(-400, 0, 0);
    private Slider rotationSlider = new Slider(0, 30, 25);

    private ImageView cardBack1 = new ImageView("file:./res/backcard.png");
    private ImageView cardBack2 = new ImageView("file:./res/backcard2.png");
    private ImageView cardBack3 = new ImageView("file:./res/backcard3.png");
    private Rectangle cbSelectionRect = new Rectangle(Model.SCREEN_W / 12.5, Model.SCREEN_H / 5.3);
    private ImageView selectedCardBack = cardBack1;
    private ImageView cardsBackImage = new ImageView("file:./res/cardsback.png");
    private ImageView soundImage = new ImageView("file:./res/sound.png");
    private ImageView zoomImage = new ImageView("file:./res/zoom.png");
    private ImageView rotationImage = new ImageView("file:./res/camera.png");

    public SettingsView(View principalView) {
        components.add(settingsBackground);
        components.add(leftArrow);
        components.add(rightArrow);
        components.add(soundMuteButton);
        components.add(backMenuButton);
        components.add(leftArrow.getImage());
        components.add(rightArrow.getImage());
        components.add(soundMuteButton.getImage());
        components.add(backMenuButton.getImage());
        components.add(soundSlider);
        components.add(zoomSlider);
        components.add(rotationSlider);
        components.add(cardBack1);
        components.add(cardBack2);
        components.add(cardBack3);
        components.add(cbSelectionRect);
        components.add(cardsBackImage);
        components.add(soundImage);
        components.add(zoomImage);
        components.add(rotationImage);

        this.principalView = principalView;

        cardBack1.setFitWidth(Model.SCREEN_W / 13);
        cardBack1.setFitHeight(Model.SCREEN_H / 5.5);
        cardBack1.setTranslateX(CARDBACK1_X);
        cardBack1.setTranslateY(CARDBACK_Y);

        cardBack2.setFitWidth(Model.SCREEN_W / 13);
        cardBack2.setFitHeight(Model.SCREEN_H / 5.5);
        cardBack2.setTranslateX(CARDBACK2_X);
        cardBack2.setTranslateY(CARDBACK_Y);

        cardBack3.setFitWidth(Model.SCREEN_W / 13);
        cardBack3.setFitHeight(Model.SCREEN_H / 5.5);
        cardBack3.setTranslateX(CARDBACK3_X);
        cardBack3.setTranslateY(CARDBACK_Y);

        cbSelectionRect.setStrokeWidth(5);
        cbSelectionRect.setStroke(Paint.valueOf("white"));
        cbSelectionRect.setTranslateX(Model.SCREEN_W / 1.9 - cbSelectionRect.getStrokeWidth() / 1.5);
        cbSelectionRect.setTranslateY(Model.SCREEN_H / 10 - cbSelectionRect.getStrokeWidth() / 1.5);
        cbSelectionRect.setFill(Paint.valueOf("transparent"));

        cardsBackImage.setFitWidth(Model.SCREEN_W / 6);
        cardsBackImage.setFitHeight(Model.SCREEN_H / 6);
        cardsBackImage.setTranslateX(Model.SCREEN_W / 10);
        cardsBackImage.setTranslateY(Model.SCREEN_H/10);

        soundImage.setFitWidth(Model.SCREEN_W / 6);
        soundImage.setFitHeight(Model.SCREEN_H / 6);
        soundImage.setTranslateX(Model.SCREEN_W / 10);
        soundImage.setTranslateY(Model.SCREEN_H / 3);

        zoomImage.setFitWidth(Model.SCREEN_W / 6);
        zoomImage.setFitHeight(Model.SCREEN_H / 6);
        zoomImage.setTranslateX(Model.SCREEN_W / 10);
        zoomImage.setTranslateY(Model.SCREEN_H / 1.75);

        rotationImage.setFitWidth(Model.SCREEN_W / 6);
        rotationImage.setFitHeight(Model.SCREEN_H / 5);
        rotationImage.setTranslateX(Model.SCREEN_W / 10);
        rotationImage.setTranslateY(Model.SCREEN_H / 1.3);

        leftArrow.setImageSize(Model.SCREEN_W / 10, Model.SCREEN_H / 8);
        leftArrow.setPosition(Model.SCREEN_W / 2.8, Model.SCREEN_H / 9);
        leftArrow.setButtonSize(Model.SCREEN_W / 10, Model.SCREEN_H / 8);
        rightArrow.setImageSize(Model.SCREEN_W / 10, Model.SCREEN_H / 8);
        rightArrow.setPosition(Model.SCREEN_W / 1.2, Model.SCREEN_H / 9);
        rightArrow.setButtonSize(Model.SCREEN_W / 10, Model.SCREEN_H / 8);

        soundMuteButton.setImageSize(Model.SCREEN_W / 10, Model.SCREEN_H / 8);
        soundMuteButton.setPosition(Model.SCREEN_W / 2.8, Model.SCREEN_H / 3.2);
        soundMuteButton.setButtonSize(Model.SCREEN_W / 10, Model.SCREEN_H / 8);
        soundSlider.setMinWidth(Model.SCREEN_W / 3);
        soundSlider.setTranslateX(Model.SCREEN_W / 1.8);
        soundSlider.setTranslateY(Model.SCREEN_H / 2.5);

        zoomSlider.setMinWidth(Model.SCREEN_W / 3);
        zoomSlider.setTranslateX(Model.SCREEN_W / 2.55);
        zoomSlider.setTranslateY(Model.SCREEN_H / 1.6);

        rotationSlider.setMinWidth(Model.SCREEN_W / 3);
        rotationSlider.setTranslateX(Model.SCREEN_W / 2.55);
        rotationSlider.setTranslateY(Model.SCREEN_H / 1.2);

        backMenuButton.setImageSize(Model.SCREEN_W / 6, Model.SCREEN_H / 6);
        backMenuButton.setPosition(Model.SCREEN_W / 1.3, Model.SCREEN_H / 1.3);
        backMenuButton.setButtonSize(Model.SCREEN_W / 6, Model.SCREEN_H / 6);

        leftArrow.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(selectedCardBack == cardBack1) {
                    cbSelectionRect.setTranslateX(CARDBACK3_X - cbSelectionRect.getStrokeWidth() / 1.5);
                    selectedCardBack = cardBack3;
                }
                else if (selectedCardBack == cardBack2) {
                    cbSelectionRect.setTranslateX(CARDBACK1_X - cbSelectionRect.getStrokeWidth() / 1.5);
                    selectedCardBack = cardBack1;
                }
                else if(selectedCardBack == cardBack3) {
                    cbSelectionRect.setTranslateX(CARDBACK2_X - cbSelectionRect.getStrokeWidth() / 1.5);
                    selectedCardBack = cardBack2;
                }
                CardView.changeCardBack(false);
            }
        });
        leftArrow.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { leftArrow.inflate(); }
        });
        leftArrow.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { leftArrow.deflate(); }
        });
        leftArrow.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.BACK_SPACE)
                    principalView.displayMenu();
            }
        });
        rightArrow.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(selectedCardBack == cardBack2) {
                    cbSelectionRect.setTranslateX(CARDBACK3_X - cbSelectionRect.getStrokeWidth() / 1.5);
                    selectedCardBack = cardBack3;
                }
                else if (selectedCardBack == cardBack3) {
                    cbSelectionRect.setTranslateX(CARDBACK1_X - cbSelectionRect.getStrokeWidth() / 1.5);
                    selectedCardBack = cardBack1;
                }
                else if(selectedCardBack == cardBack1) {
                    cbSelectionRect.setTranslateX(CARDBACK2_X - cbSelectionRect.getStrokeWidth() / 1.5);
                    selectedCardBack = cardBack2;
                }
                CardView.changeCardBack(true);
            }
        });
        rightArrow.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { rightArrow.inflate(); }
        });
        rightArrow.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { rightArrow.deflate(); }
        });
        soundMuteButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (soundMuteButton.getPath().contains("mute")) {
                    soundMuteButton.setImage("file:./res/soundicon.png");
                    soundSlider.setValue(70);
                }
                else {
                    soundMuteButton.setImage("file:./res/mute.png");
                    soundSlider.setValue(0);
                }
            }
        });
        soundMuteButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { soundMuteButton.inflate(); }
        });
        soundMuteButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { soundMuteButton.deflate(); }
        });
        soundSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(soundSlider.getValue() == 0)
                    soundMuteButton.setImage("file:./res/mute.png");
                else
                    soundMuteButton.setImage("file:./res/soundicon.png");
            }
        });

        backMenuButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                principalView.displayMenu();
            }
        });
        backMenuButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                backMenuButton.inflate();
            }
        });
        backMenuButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                backMenuButton.deflate();
            }
        });
    }

    public double getRotationValue() {
        return -rotationSlider.getValue();
    }

    public double getZoomValue() {
        return -zoomSlider.getValue();
    }

    public void display(Group settingsGroup, Group root) {
        root.getChildren().clear();
        settingsGroup.getChildren().clear();
        settingsGroup.getChildren().addAll(components);
        root.getChildren().add(settingsGroup);
    }

}
