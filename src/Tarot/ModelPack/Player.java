package Tarot.ModelPack;

import java.util.ArrayList;

import javafx.geometry.Point2D;

public class Player {
	public static final int NB_CARDS = 18;
	
	private ArrayList<CardModel> deck = new ArrayList<CardModel>();
	private Point2D[] cardsPositions = new Point2D[NB_CARDS];
	
	//My Player
	Player(double cardsX, double cardsY, double shiftX, double shiftY, boolean split){
		double x = cardsX, y = cardsY;
		for(int i = 0; i<NB_CARDS; i++){
			cardsPositions[i] = new Point2D(x, y);
			x += shiftX;
			if(split && i == NB_CARDS/2-1){
				x = cardsX;
				y += shiftY;
			}
		}
	}
	
	//Other Player
	Player(double cardsX, double cardsY, double shiftX, double shiftY){
		for(int i = 0; i<NB_CARDS; i++){
			switch(i%3){
			case 0:
				cardsPositions[i] = new Point2D(cardsX - shiftX, cardsY - shiftY);
				break;
			case 1:
				cardsPositions[i] = new Point2D(cardsX, cardsY);
				break;
			default:
				cardsPositions[i] = new Point2D(cardsX + shiftX, cardsY + shiftY);
				break;
			}
		}
	}

	public ArrayList<CardModel> getDeck() {
		return deck;
	}

	public void setDeck(ArrayList<CardModel> deck) {
		this.deck = deck;
	}

	public Point2D getCardPosition(int i) {
		return cardsPositions[i];
	}
	
	public Point2D getLastCardPosition(){
		return cardsPositions[deck.size()-1];
	}

	public void setCardsPositions(Point2D[] cardsPositions) {
		this.cardsPositions = cardsPositions;
	}
}
