package Tarot.ViewPack;

import Tarot.ModelPack.CardModel;
import Tarot.ModelPack.Model;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;


public class SettingsView {
    private static final double CARD_BACK_Y = Model.SCREEN_H / 10;
    private static final double CARD_BACK_X = Model.SCREEN_W / 1.95;
    private static final double CARD_BACK_SHIFT = CardModel.CARD_W *1.3;

    private ImageView settingsBackground = new ImageView("file:./res/settings_background.png");

    private List<ImageButton> buttons = new ArrayList<ImageButton>(){{
        add(new ImageButton("file:./res/arrowleft.png", Model.SCREEN_W / 2.6, Model.SCREEN_H / 8));
        add(new ImageButton("file:./res/arrowright.png", Model.SCREEN_W / 1.15, Model.SCREEN_H / 8));
        add(new ImageButton("file:./res/soundicon.png", Model.SCREEN_W / 2.6, Model.SCREEN_H / 3));
        add(new ImageButton("file:./res/backtomenu.png", Model.SCREEN_W / 1.3, Model.SCREEN_H / 1.3));
    }};

    private List<Slider> sliders = new ArrayList<Slider>() {{
        add(new Slider(0, 100, 70));
        add(new Slider(-400, 0, 0));
        add(new Slider(0, 30, 25));
    }};

    private List<ImageView> labels = new ArrayList<ImageView>() {{
        add(new ImageView("file:./res/cardsback.png"));
        add(new ImageView("file:./res/sound.png"));
        add(new ImageView("file:./res/zoom.png"));
        add(new ImageView("file:./res/camera.png"));
    }};

    private ImageView[] cardBacks = new ImageView[]{
            new ImageView("file:./res/backcard.png"),
            new ImageView("file:./res/backcard2.png"),
            new ImageView("file:./res/backcard3.png"),
            new ImageView("file:./res/backcard4.png")
    };

    private Rectangle cbSelectionRect = new Rectangle(CardModel.CARD_W, CardModel.CARD_H);

    private int selectedCardBack = 0;

    public SettingsView(View principalView) {
        for(int i = 0; i< CardView.NB_CARD_BACKS; i++){
            cardBacks[i].setFitWidth(CardModel.CARD_W);
            cardBacks[i].setFitHeight(CardModel.CARD_H);
            cardBacks[i].setTranslateX(CARD_BACK_X + i * CARD_BACK_SHIFT);
            cardBacks[i].setTranslateY(CARD_BACK_Y);
        }

        cbSelectionRect.setStrokeWidth(5);
        cbSelectionRect.setStroke(Paint.valueOf("white"));
        cbSelectionRect.setFill(Paint.valueOf("transparent"));
        cbSelectionRect.setTranslateX(CARD_BACK_X);
        cbSelectionRect.setTranslateY(CARD_BACK_Y);

        for(ImageView i : labels) {
            i.setFitWidth(Model.SCREEN_W / 6);
            i.setFitHeight(Model.SCREEN_H / 6);
            i.setTranslateX(Model.SCREEN_W / 10);
        }

        labels.get(0).setTranslateY(Model.SCREEN_H/10);
        labels.get(1).setTranslateY(Model.SCREEN_H / 3);
        labels.get(2).setTranslateY(Model.SCREEN_H / 1.75);
        labels.get(3).setTranslateY(Model.SCREEN_H / 1.3);

        for(ImageButton i : buttons) {
            i.setImageSize(Model.SCREEN_W / 10, Model.SCREEN_H / 8);
        }

        for(Slider s : sliders) {
            s.setMinWidth(Model.SCREEN_W / 3);
            s.setTranslateX(Model.SCREEN_W / 2.55);
        }

        sliders.get(0).setTranslateX(Model.SCREEN_W / 1.8);
        sliders.get(0).setTranslateY(Model.SCREEN_H / 2.5);
        sliders.get(1).setTranslateY(Model.SCREEN_H / 1.6);
        sliders.get(2).setTranslateY(Model.SCREEN_H / 1.2);

        buttons.get(3).setImageSize(Model.SCREEN_W / 6, Model.SCREEN_H / 6);

        buttons.get(0).setOnMouseClick(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                selectedCardBack--;
                if(selectedCardBack == -1)
                    selectedCardBack = CardView.NB_CARD_BACKS-1;
                cbSelectionRect.setTranslateX(CARD_BACK_X + selectedCardBack * CARD_BACK_SHIFT);
                CardView.changeCardBack(selectedCardBack);
            }
        });

        buttons.get(1).setOnMouseClick(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                selectedCardBack++;
                selectedCardBack %= CardView.NB_CARD_BACKS;
                cbSelectionRect.setTranslateX(CARD_BACK_X + selectedCardBack * CARD_BACK_SHIFT);
                CardView.changeCardBack(selectedCardBack);
            }
        });

        buttons.get(2).setOnMouseClick(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (buttons.get(2).getPath().contains("mute")) {
                    buttons.get(2).setImage("file:./res/soundicon.png");
                    sliders.get(0).setValue(70);
                    principalView.setMusicVolume(0.7);
                }
                else {
                    buttons.get(2).setImage("file:./res/mute.png");
                    sliders.get(0).setValue(0);
                    principalView.setMusicVolume(0);
                }
            }
        });

        sliders.get(0).setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(sliders.get(0).getValue() == 0)
                    buttons.get(2).setImage("file:./res/mute.png");
                else
                    buttons.get(2).setImage("file:./res/soundicon.png");
                principalView.setMusicVolume(sliders.get(0).getValue() / 10.0);
            }
        });

        buttons.get(3).setOnMouseClick(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                principalView.displayMenu();
            }
        });
    }

    public double getRotationValue() {
        return -sliders.get(2).getValue();
    }

    public double getZoomValue() {
        return -sliders.get(1).getValue();
    }

    public void display(Group settingsGroup, Group root) {
        root.getChildren().clear();
        settingsGroup.getChildren().clear();
        settingsGroup.getChildren().addAll(settingsBackground);
        settingsGroup.getChildren().addAll(labels);
        settingsGroup.getChildren().addAll(buttons);
        settingsGroup.getChildren().addAll(sliders);
        settingsGroup.getChildren().addAll(cardBacks);
        settingsGroup.getChildren().add(cbSelectionRect);
        root.getChildren().add(settingsGroup);
    }

}