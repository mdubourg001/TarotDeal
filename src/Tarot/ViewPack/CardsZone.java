package Tarot.ViewPack;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;

public class CardsZone{
	private static final double LAB_SHIFT_Y = -30;
	
	private Rectangle zone;
	private Label lab;
	public CardsZone(String name, double x, double y, double w, double h){
		zone = new Rectangle(x, y, w, h);
		zone.setFill(new Color(1, 1, 1, 0.25));
		zone.setStroke(new Color(1, 1, 1, 1));
		zone.setStrokeLineCap(StrokeLineCap.SQUARE);
		zone.setStrokeWidth(5);
		zone.setTranslateZ(2);
        
		lab = new Label(name);
		lab.setFont(new Font(20));
		lab.setTextFill(Color.WHITE);
		lab.setTranslateX(x);
		lab.setTranslateY(y + LAB_SHIFT_Y);
	}
	
	public Rectangle getZone() {
		return zone;
	}
	
	public Label getLab() {
		return lab;
	}
}
