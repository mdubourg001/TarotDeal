package Tarot;

import java.util.ArrayList;

public class Model implements Observable {
	public static final int SCREEN_W = 1280;
	public static final int SCREEN_H = 900;
	
	public static final int FIRST_TRUMP = 1;
	public static final int LAST_TRUMP = 21;
	
	private ArrayList<Observer> listObserver = new ArrayList<Observer>();
	
	private ArrayList<CardModel> cards = new ArrayList<CardModel>();
	
	public Model(){
		loadCards();
	}
	
	@Override
	public void addObserver(Observer obs) {
		this.listObserver.add(obs);
	}

	@Override
	public void removeObserver() {
		listObserver = new ArrayList<Observer>();
	}

	@Override
	public void notifyObserver() {
		// TODO Auto-generated method stub
	}

	public ArrayList<CardModel> getCards() {
		return cards;
	}
	
	private void loadCards(){
		loadTrumps();
		loadColoredCards();
	}

	private void loadTrumps(){
		String fullName;
		for(int i = FIRST_TRUMP; i <= LAST_TRUMP; i++){
			fullName = "file:./cards/Tarot_nouveau_-_Grimaud_-_1898_-_Trumps_-_";
			if(i < 10){
				fullName += "0";
			}
			fullName += Integer.toString(i) + ".jpg";
			cards.add(new CardModel("Trump" + Integer.toString(i), fullName, (SCREEN_W-CardModel.CARD_W)/2, (SCREEN_H-CardModel.CARD_H)/2));
		}
	}

	private void loadColoredCards(){
		String fullName;
		for(String color : cardColors()){
			for(String value : cardValues()){
				fullName = "file:./cards/Tarot_nouveau_-_Grimaud_-_1898_-_" + color + "_-_" + value + ".jpg";
				cards.add(new CardModel(color + value, fullName, (SCREEN_W-CardModel.CARD_W)/2, (SCREEN_H-CardModel.CARD_H)/2));
			}
		}
	}

	private ArrayList<String> cardColors(){
		ArrayList<String> colors = new ArrayList<String>();
		colors.add("Clubs");
		colors.add("Diamonds");
		colors.add("Hearts");
		colors.add("Spades");
		return colors;
	}

	private ArrayList<String> cardValues(){
		ArrayList<String> values = new ArrayList<String>();
		values.add("Ace");
		for(int i = 2; i <= 9; i++){
			values.add("0" + Integer.toString(i));
		}
		values.add("10");
		values.add("Jack");
		values.add("Knight");
		values.add("Queen");
		values.add("King");
		return values;
	}
}
