package Tarot.ViewPack;

import Tarot.ModelPack.CardModel;
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
    private static final double CARD_BACK_Y = Model.SCREEN_H / 10;
    private static final double CARD_BACKS_X[] = new double[]{ 
    		Model.SCREEN_W / 1.92,
    		Model.SCREEN_W / 1.64,
    		Model.SCREEN_W / 1.43,
    		Model.SCREEN_W / 1.27
    		};

    private List<Node> components = new ArrayList<>();

    private ImageView settingsBackground = new ImageView("file:./res/settings_background.png");

    private ImageButton leftArrow = new ImageButton("file:./res/arrowleft.png", 0, 0, Model.SCREEN_W / 8, Model.SCREEN_H / 6);
    private ImageButton rightArrow = new ImageButton("file:./res/arrowright.png", 0, 0, Model.SCREEN_W / 8, Model.SCREEN_H / 6);
    private ImageButton soundMuteButton = new ImageButton("file:./res/soundicon.png", 0, 0, Model.SCREEN_W / 8, Model.SCREEN_H / 6);
    private ImageButton backMenuButton = new ImageButton("file:./res/backtomenu.png", 0, 0, Model.SCREEN_W / 8, Model.SCREEN_H / 6);

    private Slider soundSlider = new Slider(0, 100, 70);
    private Slider zoomSlider = new Slider(-400, 0, 0);
    private Slider rotationSlider = new Slider(0, 30, 25);

    private ImageView[] cardBacks = new ImageView[]{
    		new ImageView("file:./res/backcard.png"),
    		new ImageView("file:./res/backcard2.png"),
    		new ImageView("file:./res/backcard3.png"),
    		new ImageView("file:./res/backcard4.png")
    };
    private Rectangle cbSelectionRect = new Rectangle(CardModel.CARD_W, CardModel.CARD_H);
    private int selectedCardBack = 0;
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
        
        for(int i=0; i<CardView.NB_CARD_BACKS; i++){
        	cardBacks[i].setFitWidth(CardModel.CARD_W);
        	cardBacks[i].setFitHeight(CardModel.CARD_H);
        	
        	cardBacks[i].setTranslateX(CARD_BACKS_X[i]);
        	
        	cardBacks[i].setTranslateY(CARD_BACK_Y);
        	
        	components.add(cardBacks[i]);
        }
        
        components.add(cbSelectionRect);
        components.add(cardsBackImage);
        components.add(soundImage);
        components.add(zoomImage);
        components.add(rotationImage);

        cbSelectionRect.setStrokeWidth(5);
        cbSelectionRect.setStroke(Paint.valueOf("white"));
        cbSelectionRect.setTranslateX(CARD_BACKS_X[0]);
        cbSelectionRect.setTranslateY(CARD_BACK_Y);
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
        leftArrow.setPosition(Model.SCREEN_W / 2.9, Model.SCREEN_H / 9);
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
                selectedCardBack--;
                if(selectedCardBack == -1)
                	selectedCardBack = CardView.NB_CARD_BACKS-1;
                cbSelectionRect.setTranslateX(CARD_BACKS_X[selectedCardBack]);
                CardView.changeCardBack(selectedCardBack);
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
            	selectedCardBack++;
                selectedCardBack %= CardView.NB_CARD_BACKS;
                cbSelectionRect.setTranslateX(CARD_BACKS_X[selectedCardBack]);
                CardView.changeCardBack(selectedCardBack);
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
                    principalView.setMusicVolume(0.7);
                }
                else {
                    soundMuteButton.setImage("file:./res/mute.png");
                    soundSlider.setValue(0);
                    principalView.setMusicVolume(0);
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
                principalView.setMusicVolume(soundSlider.getValue() / 10.0);
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
