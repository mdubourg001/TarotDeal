package Tarot;

import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardView{
	private static Image back = new Image("file:./cards/back.jpg");
	
	private Image image;
	private ImageView backIV;
	private ImageView frontIV;
	
	public CardView(String file, int x, int y, double z) {
		this.image = new Image(file);
		
		this.backIV = new ImageView(this.back);
		this.backIV.setFitWidth(CardModel.CARD_W);
		this.backIV.setFitHeight(CardModel.CARD_H);
		this.backIV.setX(x);
		this.backIV.setY(y);
		this.backIV.setTranslateZ(z);
		this.backIV.setRotationAxis(new Point3D(0,1,0));
		this.backIV.setRotate(180);
		
		this.frontIV = new ImageView(this.image);
		this.frontIV.setFitWidth(CardModel.CARD_W);
		this.frontIV.setFitHeight(CardModel.CARD_H);
		this.frontIV.setX(x);
		this.frontIV.setY(y);
		this.frontIV.setTranslateZ(z + 0.1);
		this.frontIV.setRotationAxis(new Point3D(0,1,0));
		this.frontIV.setRotate(180);
	}
	
	public ImageView getBack() {
		return backIV;
	}

	public ImageView getFront() {
		return frontIV;
	}
	
	public Image getImage() {
		return image;
	}
}
