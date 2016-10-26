package Tarot;

import java.util.ArrayList;
import java.util.Collections;

public class Model implements Observable {
	public static final int SCREEN_W = 1280;
	public static final int SCREEN_H = 900;
	
	public static final int FIRST_TRUMP = 1;
	public static final int LAST_TRUMP = 21;
	
	private static final int NB_PLAYERS = 4;
	
	private static final int DIST_CARD_X_START = 50;
	private static final int DIST_CARD_Y1 = 300;
	private static final int DIST_CARD_Y2 = 500;
	private static final int DIST_CARD_X_DIFF = 135;
	
	private static final int PLAYER_3_Y = -200;
	private static final int PLAYERS_2_4_X1 = -200;
	private static final int PLAYERS_2_4_X2 = 0;
	private static final int PLAYERS_2_4_X3 = 200;
	
	private ArrayList<Observer> listObserver = new ArrayList<Observer>();
	
	private ArrayList<CardModel> deckCards = new ArrayList<CardModel>();
	private ArrayList<CardModel> myCards = new ArrayList<CardModel>();
	private ArrayList<CardModel> othersCards = new ArrayList<CardModel>();

	private int currentPlayer = 1;
	private int myDistCardX = DIST_CARD_X_START;
	private int myDistCardY = DIST_CARD_Y1;
	
	private int myIndexCard = -3;
	private int othersIndexCard = -3;
	
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

	public ArrayList<CardModel> getCards() {
		return deckCards;
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
			deckCards.add(new CardModel("Trump" + Integer.toString(i), fullName, (SCREEN_W-CardModel.CARD_W)/2, 100));
		}
	}

	private void loadColoredCards(){
		String fullName;
		for(String color : cardColors()){
			for(String value : cardValues()){
				fullName = "file:./cards/Tarot_nouveau_-_Grimaud_-_1898_-_" + color + "_-_" + value + ".jpg";
				deckCards.add(new CardModel(color + value, fullName, (SCREEN_W-CardModel.CARD_W)/2, 100));
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
	
	public void mixDeck(){
		Collections.shuffle(deckCards);
		notifyDeckMixed();
	}
	
	@Override
	public void notifyDeckMixed(){
		for(Observer obs : listObserver){
			obs.updateDeckMixed();
		}
	}
	
	public void distribute3Cards(){
		switch(currentPlayer){
		case 1 :
			move3CardsToMe();
			break;
		case 2 :
			move3CardsToOther(
					new int[]{-CardModel.CARD_W,-CardModel.CARD_W,-CardModel.CARD_W}, 
					new int[]{PLAYERS_2_4_X1, PLAYERS_2_4_X2, PLAYERS_2_4_X3});
			break;
		case 3 :
			move3CardsToOther(
					new int[]{(SCREEN_W-CardModel.CARD_W)/2-DIST_CARD_X_DIFF,(SCREEN_W-CardModel.CARD_W)/2,(SCREEN_W-CardModel.CARD_W)/2+DIST_CARD_X_DIFF}, 
					new int[]{PLAYER_3_Y,PLAYER_3_Y,PLAYER_3_Y});
			break;
		case 4 :
			move3CardsToOther(
					new int[]{SCREEN_W,SCREEN_W,SCREEN_W}, 
					new int[]{PLAYERS_2_4_X1, PLAYERS_2_4_X2, PLAYERS_2_4_X3});
			break;
		}
	}
	
	public void move3CardsToMe(){
		myIndexCard += 3;
		for(int i=0; i<3; i++){
			myCards.add(deckCards.get(0));
			myCards.get(myIndexCard+i).moveTo(myDistCardX, myDistCardY);
			myDistCardX += DIST_CARD_X_DIFF;
			if(myDistCardX > DIST_CARD_X_START + 8*DIST_CARD_X_DIFF){
				myDistCardX = DIST_CARD_X_START;
				myDistCardY = DIST_CARD_Y2;
			}
			deckCards.remove(0);
		}
		changePlayer();
		notify3CardsDistributed(myCards.get(myIndexCard), 
				myCards.get(myIndexCard+1), 
				myCards.get(myIndexCard+2));
	}
	
	public void move3CardsToOther(int x[], int y[]){
		othersIndexCard += 3;
		for(int i=0; i<3; i++){
			othersCards.add(deckCards.get(0));
			othersCards.get(othersIndexCard+i).moveTo(x[i], y[i]);
			deckCards.remove(0);
		}
		changePlayer();
		notify3CardsDistributed(othersCards.get(othersIndexCard), 
				othersCards.get(othersIndexCard+1), 
				othersCards.get(othersIndexCard+2));
	}
	
	private void changePlayer(){
		currentPlayer++;
		if(currentPlayer > NB_PLAYERS){
			currentPlayer = 1;
		}
	}

	@Override
	public void notify3CardsDistributed(CardModel card1, CardModel card2,CardModel card3) {
		for(Observer obs : listObserver){
			obs.update3CardsDistributed(card1, card2, card3);
		}
	}
}
