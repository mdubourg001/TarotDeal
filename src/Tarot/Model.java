package Tarot;

import javafx.stage.Screen;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;

public class Model extends Observable {
	public static final int SCREEN_W = (int)Screen.getPrimary().getBounds().getWidth();
	public static final int SCREEN_H = (int)Screen.getPrimary().getBounds().getHeight();
	
	public static final int FIRST_TRUMP = 1;
	public static final int LAST_TRUMP = 21;
	
	public static final int NB_CARDS = 78;
	public static final int NB_CARDS_PLAYER = 18;
	public static final int CHIEN_SIZE = 6;
	public static final int PLAYER_NB_CARDS = 18;
	
	private static final int NB_PLAYERS = 4;
	
	public static final int DECK_X_START = (SCREEN_W / 2) - CardModel.CARD_W / 2 ;
	public static final int DECK_Y_START = SCREEN_H / 10;

	public static final int DIST_CARD_X_DIFF = SCREEN_W / 14;

	public static final int CHIEN_CARD_X_START = (int)((SCREEN_W / 2) - (CardModel.CARD_W + 5 * DIST_CARD_X_DIFF) / 2);
	public static final int CHIEN_CARD_Y = DECK_Y_START + CardModel.CARD_H + 50;

	public static final int DIST_CARD_X_START = (int)((SCREEN_W / 2) - (CardModel.CARD_W + 8 * DIST_CARD_X_DIFF) / 2);
	public static final int DIST_CARD_Y1 = CHIEN_CARD_Y + CardModel.CARD_H + 50;
	public static final int DIST_CARD_Y2 = DIST_CARD_Y1 + CardModel.CARD_H + 50;
	

	private static final int PLAYER_3_Y = -400;
	private static final int PLAYERS_2_4_X_SHIFT = 200;
	private static final int PLAYERS_2_4_Y1 = -200;
	private static final int PLAYERS_2_4_Y2 = 0;
	private static final int PLAYERS_2_4_Y3 = 200;
	
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
			deckCards.add(new CardModel("Trump" + Integer.toString(i), fullName, currentCardOrder));
			currentCardOrder++;
		}
		fullName = "file:./cards/Tarot_nouveau_-_Grimaud_-_1898_-_Trumps_-_Excuse.jpg";
		deckCards.add(new CardModel("Excuse", fullName, currentCardOrder));
		currentCardOrder++;
	}

	private void loadColoredCards(String color){
		String fullName;
		for(String value : cardValues()){
			fullName = "file:./cards/Tarot_nouveau_-_Grimaud_-_1898_-_" + color + "_-_" + value + ".jpg";
			deckCards.add(new CardModel(color + value, fullName, currentCardOrder));
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
	
	public ArrayList<String> ungapableCards(){
		ArrayList<String> values = new ArrayList<String>();
		values.add("Trump1");
		values.add("Trump21");
		values.add("Excuse");
		values.add("ClubsKing");
		values.add("DiamondsKing");
		values.add("HeartsKing");
		values.add("SpadesKing");
		return values;
	}
	
	public void mixDeck(){
		Collections.shuffle(deckCards);
		double z = -38;
		for(CardModel card : deckCards){
			card.setX(DECK_X_START);
			card.setY(DECK_Y_START);
			card.setZ(z);
			z += 0.5;
		}
		setChanged();
		Pair<TarotAction, Object> arg = new Pair<TarotAction, Object>(TarotAction.MIX_DECK, null);
		notifyObservers(arg);
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
					new int[]{-PLAYERS_2_4_X_SHIFT-CardModel.CARD_W, -PLAYERS_2_4_X_SHIFT-CardModel.CARD_W, -PLAYERS_2_4_X_SHIFT-CardModel.CARD_W}, 
					new int[]{PLAYERS_2_4_Y1, PLAYERS_2_4_Y2, PLAYERS_2_4_Y3});
			break;
		case 3 :
			move3CardsToOther(
					new int[]{(SCREEN_W-CardModel.CARD_W)/2-DIST_CARD_X_DIFF,(SCREEN_W-CardModel.CARD_W)/2,(SCREEN_W-CardModel.CARD_W)/2+DIST_CARD_X_DIFF}, 
					new int[]{PLAYER_3_Y, PLAYER_3_Y, PLAYER_3_Y});
			break;
		case 4 :
			move3CardsToOther(
					new int[]{PLAYERS_2_4_X_SHIFT+SCREEN_W, PLAYERS_2_4_X_SHIFT+SCREEN_W, PLAYERS_2_4_X_SHIFT+SCREEN_W}, 
					new int[]{PLAYERS_2_4_Y1, PLAYERS_2_4_Y2, PLAYERS_2_4_Y3});
			break;
		}
	}
	
	private void move3CardsToMe(){
		myIndexCard += 3;
		for(int i=0; i<3; i++){
			myCards.add(deckCards.get(0));
			myCards.get(myIndexCard+i).moveTo(myDistCardX, myDistCardY, 1);
			myDistCardX += DIST_CARD_X_DIFF;
			if(myDistCardX > DIST_CARD_X_START + 8*DIST_CARD_X_DIFF){
				myDistCardX = DIST_CARD_X_START;
				myDistCardY = DIST_CARD_Y2;
			}
			deckCards.remove(0);
		}
		changePlayer();
		
		setChanged();
		Pair<TarotAction, Pair<Boolean, CardModel[]>> arg = new Pair<TarotAction, Pair<Boolean, CardModel[]>>(TarotAction.DISTRIBUTE_3_CARDS,
				new Pair<Boolean, CardModel[]>(deckCards.isEmpty(), new CardModel[]{myCards.get(myIndexCard), myCards.get(myIndexCard+1), myCards.get(myIndexCard+2)}));
		notifyObservers(arg);
	}
	
	private void move3CardsToOther(int x[], int y[]){
		othersIndexCard += 3;
		for(int i=0; i<3; i++){
			othersCards.add(deckCards.get(0));
			othersCards.get(othersIndexCard+i).moveTo(x[i], y[i], 1);
			deckCards.remove(0);
		}
		changePlayer();
		
		setChanged();
		Pair<TarotAction, Pair<Boolean, CardModel[]>> arg = new Pair<TarotAction, Pair<Boolean, CardModel[]>>(TarotAction.DISTRIBUTE_3_CARDS,
				new Pair<Boolean, CardModel[]>(deckCards.isEmpty(), new CardModel[]{othersCards.get(othersIndexCard), othersCards.get(othersIndexCard+1), othersCards.get(othersIndexCard+2)}));
		notifyObservers(arg);
	}
	
	private void changePlayer(){
		currentPlayer++;
		if(currentPlayer > NB_PLAYERS){
			currentPlayer = 1;
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
		chienCards.get(chienIndexCard).moveTo(chienCardX, CHIEN_CARD_Y, 1);
		chienCardX += DIST_CARD_X_DIFF;
		deckCards.remove(0);
		
		setChanged();
		Pair<TarotAction, CardModel> arg = new Pair<TarotAction, CardModel>(TarotAction.MOVE_CARD, chienCards.get(chienIndexCard));
		notifyObservers(arg);
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
		
		setChanged();
		Pair<TarotAction, Object> arg = new Pair<TarotAction, Object>(TarotAction.ORGANIZE_PLAYER, null);
		notifyObservers(arg);
	}
	
	public void detectPetitSec(){
		Boolean petitSec = false;
		for(CardModel card : myCards){
			if(card.getName().contentEquals("Trump1")){
				petitSec = true;
			}
			else if(othersTrumps().contains(card.getName())){
				petitSec = false;
				break;
			}
		}
		
		setChanged();
		Pair<TarotAction, Boolean> arg = new Pair<TarotAction, Boolean>(TarotAction.DETECT_PETIT_SEC, petitSec);
		notifyObservers(arg);
	}
	
	private ArrayList<String> othersTrumps(){
		ArrayList<String> othersTrumps = new ArrayList<String>();
		for(int i = FIRST_TRUMP+1; i <= LAST_TRUMP; i++){
			othersTrumps.add("Trump"+Integer.toString(i));
		}
		return othersTrumps;
	}
	
	private PlayerAction myAction;
	public void chooseAction(PlayerAction action){
		myAction = action;
		
		setChanged();
		Pair<TarotAction, PlayerAction> arg = new Pair<TarotAction, PlayerAction>(TarotAction.CHOOSE_ACTION, myAction);
		notifyObservers(arg);
	}
	
	public static final int NB_CARD_GAP = 6;
	public static final int GAP_X_START = CHIEN_CARD_X_START;
	public static final int GAP_Y = View.BUTTON_Y;
	private int nbCardInGap = 0;
	
	public void addCardToGap(CardModel card){
		gapCards.add(card);
		myCards.remove(card);
		card.setX(GAP_X_START + nbCardInGap*DIST_CARD_X_DIFF);
		card.setY(GAP_Y);
		nbCardInGap++;
		
		setChanged();
		Pair<TarotAction, CardModel> argCardMoved = new Pair<TarotAction, CardModel>(TarotAction.MOVE_CARD, card);
		notifyObservers(argCardMoved);
		
		if(nbCardInGap == NB_CARD_GAP){
			while(chienCards.size() != 0){
				myCards.add(chienCards.get(0));
				chienCards.remove(0);
			}
			organizePlayerCards();


			setChanged();
			Pair<TarotAction, Object> argGapDone = new Pair<TarotAction, Object>(TarotAction.GAP_DONE, null);
			notifyObservers(argGapDone);
		}
	}
	
	public void revertPlayer() {
		for(CardModel card : myCards){
			card.onFront = true;
		}
		setChanged();
		Pair<TarotAction, Object> argGapDone = new Pair<TarotAction, Object>(TarotAction.REVERT_PLAYER, null);
		notifyObservers(argGapDone);
	}
	
	public void revertChien() {
		for(CardModel card : chienCards){
			card.onFront = true;
		}
		setChanged();
		Pair<TarotAction, Object> argGapDone = new Pair<TarotAction, Object>(TarotAction.REVERT_CHIEN, null);
		notifyObservers(argGapDone);
	}
}
