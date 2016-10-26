package Tarot;

import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardView{
	private static Image back = new Image("file:./cards/back.jpg");
	
	private Image image;
	private ImageView view;
	
	public CardView(String file, int x, int y) {
		this.image = new Image(file);
		this.view = new ImageView(CardView.back);
		this.view.setFitWidth(CardModel.CARD_W);
		this.view.setFitHeight(CardModel.CARD_H);
		this.view.setX(x);
		this.view.setY(y);
		this.view.setRotationAxis(new Point3D(0,1,0));
		this.view.setRotate(180);
	}

	public ImageView getView() {
		return view;
	}
	
	public Image getImage() {
		return image;
	}
	
	public void changeImage(){
		view.setImage(image);
	}
}
