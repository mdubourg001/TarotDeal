package Tarot;

import java.util.ArrayList;
import java.util.Collections;

public class Model implements Observable {
	public static final int SCREEN_W = 1280;
	public static final int SCREEN_H = 900;
	
	public static final int FIRST_TRUMP = 1;
	public static final int LAST_TRUMP = 21;
	
	public static final int NB_CARDS = 78;
	public static final int CHIEN_SIZE = 6;
	public static final int PLAYER_NB_CARDS = 18;
	
	private static final int NB_PLAYERS = 4;
	
	private static final int DECK_X_START = 185;
	private static final int DECK_Y_START = 50;
	
	private static final int DIST_CARD_X_START = 50;
	private static final int DIST_CARD_Y1 = 300;
	private static final int DIST_CARD_Y2 = 500;
	private static final int DIST_CARD_X_DIFF = 135;
	
	private static final int CHIEN_CARD_X_START = 320;
	private static final int CHIEN_CARD_Y = 50;
	
	private static final int PLAYER_3_Y = -200;
	private static final int PLAYERS_2_4_X1 = -200;
	private static final int PLAYERS_2_4_X2 = 0;
	private static final int PLAYERS_2_4_X3 = 200;
	
	private ArrayList<Observer> listObserver = new ArrayList<Observer>();
	
	private ArrayList<CardModel> deckCards = new ArrayList<CardModel>();
	private ArrayList<CardModel> myCards = new ArrayList<CardModel>();
	private ArrayList<CardModel> othersCards = new ArrayList<CardModel>();
	private ArrayList<CardModel> chienCards = new ArrayList<CardModel>();
	private ArrayList<CardModel> gapCards = new ArrayList<CardModel>();
	
	private int distributedCards = 0;
	private int nbCardsInChien = 0;

	private int currentPlayer = 1;
	private int myDistCardX = DIST_CARD_X_START;
	private int myDistCardY = DIST_CARD_Y1;
	private int chienCardX = CHIEN_CARD_X_START;
	
	private int myIndexCard = -3;
	private int othersIndexCard = -3;
	private int chienIndexCard = -1;
	
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

	public ArrayList<CardModel> getDeckCards() {
		return deckCards;
	}
	
	public ArrayList<CardModel> getMyCards() {
		return myCards;
	}
	
	public ArrayList<CardModel> getChienCards() {
		return chienCards;
	}
	
	private int currentCardOrder = 0;
	private void loadCards(){
		loadColoredCards("Clubs");
		loadColoredCards("Diamonds");
		loadTrumps();
		loadColoredCards("Hearts");
		loadColoredCards("Spades");
	}

	private void loadTrumps(){
		String fullName;
		for(int i = FIRST_TRUMP; i <= LAST_TRUMP; i++){
			fullName = "file:./cards/Tarot_nouveau_-_Grimaud_-_1898_-_Trumps_-_";
			if(i < 10){
				fullName += "0";
			}
			fullName += Integer.toString(i) + ".jpg";
			deckCards.add(new CardModel("Trump" + Integer.toString(i), fullName, DECK_X_START, DECK_Y_START, currentCardOrder));
			currentCardOrder++;
		}
		fullName = "file:./cards/Tarot_nouveau_-_Grimaud_-_1898_-_Trumps_-_Excuse.jpg";
		deckCards.add(new CardModel("Excuse", fullName, DECK_X_START, DECK_Y_START, currentCardOrder));
		currentCardOrder++;
	}

	private void loadColoredCards(String color){
		String fullName;
		for(String value : cardValues()){
			fullName = "file:./cards/Tarot_nouveau_-_Grimaud_-_1898_-_" + color + "_-_" + value + ".jpg";
			deckCards.add(new CardModel(color + value, fullName, DECK_X_START, DECK_Y_START, currentCardOrder));
			currentCardOrder++;
		}
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
	
	public void distributeCards(){
		if(distributedCards > 0 && nbCardsInChien < 6){
			addCardToChien();
		}
		distribute3Cards();
	}
	
	private void distribute3Cards(){
		distributedCards += 3;
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
	
	private void move3CardsToMe(){
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
	
	private void move3CardsToOther(int x[], int y[]){
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
			obs.update3CardsDistributed(card1, card2, card3, distributedCards == NB_CARDS);
		}
	}
	
	private void addCardToChien(){
		distributedCards++;
		nbCardsInChien++;
		moveCardTochien();
	}
	
	private void moveCardTochien(){
		chienIndexCard++;
		chienCards.add(deckCards.get(0));
		chienCards.get(chienIndexCard).moveTo(chienCardX, CHIEN_CARD_Y);
		chienCardX += DIST_CARD_X_DIFF;
		deckCards.remove(0);
		notifyCardMoved(chienCards.get(chienIndexCard));
	}

	@Override
	public void notifyCardMoved(CardModel card) {
		for(Observer obs : listObserver){
			obs.updateCardMoved(card);
		}
	}
	
	public void organizePlayerCards(){
		Collections.sort(myCards);
		
		int newX = DIST_CARD_X_START;
		int newY = DIST_CARD_Y1;
		for(CardModel card : myCards){
			card.setX(newX);
			card.setY(newY);
			newX += DIST_CARD_X_DIFF;
			if(newX > DIST_CARD_X_START + 8*DIST_CARD_X_DIFF){
				newX = DIST_CARD_X_START;
				newY = DIST_CARD_Y2;
			}
		}
		
		notifyPlayerCardsOrganized();
	}

	@Override
	public void notifyPlayerCardsOrganized() {
		for(Observer obs : listObserver){
			obs.updatePlayerCardsOrganized();
		}
	}
	
	private PlayerAction myAction;
	public void chooseAction(PlayerAction action){
		myAction = action;
		notifyActionChosen(myAction);
	}

	@Override
	public void notifyActionChosen(PlayerAction action) {
		for(Observer obs : listObserver){
			obs.updateActionChosen(action);
		}
	}
	
	public static final int NB_CARD_GAP = 6;
	public static final int GAP_X_START = 320;
	public static final int GAP_Y = 700;
	private int nbCardInGap = 0;
	
	public void addCardToGap(CardModel card){
		gapCards.add(card);
		myCards.remove(card);
		card.setX(GAP_X_START + nbCardInGap*DIST_CARD_X_DIFF);
		card.setY(GAP_Y);
		nbCardInGap++;
		notifyCardMoved(card);
		if(nbCardInGap == NB_CARD_GAP){
			notifyGapDone();
		}
	}
	
	@Override
	public void notifyGapDone() {
		for(Observer obs : listObserver){
			obs.updateGapDone();
		}
	}
}
