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
	
	public static final int DECK_X = (int)(SCREEN_W / 2.5) - CardModel.CARD_W / 2 ;
	public static final int DECK_Y = SCREEN_H / 20;

	public static final int DIST_CARD_X_DIFF = 5;
	public static final int DIST_CARD_Y_DIFF = 25;

	public static final int CHIEN_CARD_X_START = (int)(SCREEN_W / 2.5) - (6 * CardModel.CARD_W + 5 * DIST_CARD_X_DIFF) / 2;
	public static final int CHIEN_CARD_Y = DECK_Y + CardModel.CARD_H + DIST_CARD_Y_DIFF;

	public static final int DIST_CARD_X_START = (int)(SCREEN_W / 2.5) - (9 * CardModel.CARD_W + 8 * DIST_CARD_X_DIFF) / 2;
	public static final int DIST_CARD_Y1 = CHIEN_CARD_Y + CardModel.CARD_H + DIST_CARD_Y_DIFF;
	public static final int DIST_CARD_Y2 = DIST_CARD_Y1 + CardModel.CARD_H + DIST_CARD_Y_DIFF;
	

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
			fullName = "cards/Tarot_nouveau_-_Grimaud_-_1898_-_Trumps_-_";
			if(i < 10){
				fullName += "0";
			}
			fullName += Integer.toString(i) + ".jpg";
			deckCards.add(new CardModel("Trump" + Integer.toString(i), fullName, currentCardOrder));
			currentCardOrder++;
		}
		fullName = "cards/Tarot_nouveau_-_Grimaud_-_1898_-_Trumps_-_Excuse.jpg";
		deckCards.add(new CardModel("Excuse", fullName, currentCardOrder));
		currentCardOrder++;
	}

	private void loadColoredCards(String color){
		String fullName;
		for(String value : cardValues()){
			fullName = "cards/Tarot_nouveau_-_Grimaud_-_1898_-_" + color + "_-_" + value + ".jpg";
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
	
	public static final double DECK_Z_TOP = -76;
	public static final double DECK_Z_DIFF = 1;
	
	public void mixDeck(){
		Collections.shuffle(deckCards);
		double z = DECK_Z_TOP;
		for(CardModel card : deckCards){
			card.setX(DECK_X);
			card.setY(DECK_Y);
			card.setZ(z);
			z += DECK_Z_DIFF;
		}
		setChanged();
		Pair<TarotAction, Object> arg = new Pair<TarotAction, Object>(TarotAction.DECK_MIXED, null);
		notifyObservers(arg);
	}

	public void cutDeck(){
	    Integer indexHalf;
        indexHalf = NB_CARDS/2;

        ArrayList<CardModel> firstHalf = new ArrayList<CardModel>();
        ArrayList<CardModel> secondHalf = new ArrayList<CardModel>();
        
        for(int i = 0; i < indexHalf; i++){
        	firstHalf.add(deckCards.get(i));
        }
        for(int i = indexHalf; i < NB_CARDS; i++){
        	secondHalf.add(deckCards.get(i));
        }

        secondHalf.addAll(firstHalf);
        deckCards = secondHalf;
        
        double z = DECK_Z_TOP;
        for(CardModel card : deckCards){
        	card.setZ(z);
			z += DECK_Z_DIFF;
        }

        setChanged();
        Pair<TarotAction, Integer> arg = new Pair<TarotAction, Integer>(TarotAction.DECK_CUT, indexHalf);
        notifyObservers(arg);
    }
	
	public void distributeCards(){
		System.out.println(distributedCards);
		switch(distributedCards){
		case 3:
		case 7:
		case 11:
		case 18:
		case 22:
		case 26:
			addCardToChien();
			break;
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
					new int[]{(SCREEN_W-CardModel.CARD_W)/2-(CardModel.CARD_W + DIST_CARD_X_DIFF),(SCREEN_W-CardModel.CARD_W)/2,(SCREEN_W-CardModel.CARD_W)/2+(CardModel.CARD_W + DIST_CARD_X_DIFF)},
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
		
		int[] cardOrders = new int[3];
		if(myIndexCard%9 == 0){
			cardOrders[0] = 0; cardOrders[1] = 0; cardOrders[2] = 0;
		}
		else if(myIndexCard%9 == 3){
			cardOrders[0] = 0; cardOrders[1] = 1; cardOrders[2] = 0;
		}
		else{
			cardOrders[0] = 2; cardOrders[1] = 1; cardOrders[2] = 0;
		}
		
		for(int i=0; i<3; i++){
			myCards.add(deckCards.get(cardOrders[i]));
			myCards.get(myIndexCard+i).moveTo(myDistCardX, myDistCardY, 1);
			myDistCardX += (CardModel.CARD_W + DIST_CARD_X_DIFF);
			if(myDistCardX > DIST_CARD_X_START + 8*(CardModel.CARD_W + DIST_CARD_X_DIFF)){
				myDistCardX = DIST_CARD_X_START;
				myDistCardY = DIST_CARD_Y2;
			}
			deckCards.remove(cardOrders[i]);
		}
		changePlayer();
		
		setChanged();
		Pair<TarotAction, Pair<Boolean, CardModel[]>> arg = new Pair<TarotAction, Pair<Boolean, CardModel[]>>(TarotAction.CARDS_DISTRIBUTED,
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
		Pair<TarotAction, Pair<Boolean, CardModel[]>> arg = new Pair<TarotAction, Pair<Boolean, CardModel[]>>(TarotAction.CARDS_DISTRIBUTED,
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
		chienCardX += (CardModel.CARD_W + DIST_CARD_X_DIFF);
		deckCards.remove(0);
		
		setChanged();
		Pair<TarotAction, CardModel> arg = new Pair<TarotAction, CardModel>(TarotAction.CARD_MOVED_FROM_DECK, chienCards.get(chienIndexCard));
		notifyObservers(arg);
	}
	
	public void organizePlayerCards(){
		Collections.sort(myCards);
		
		int newX = DIST_CARD_X_START;
		int newY = DIST_CARD_Y1;
		double newZ = 1.0;
		for(CardModel card : myCards){
			card.setX(newX);
			card.setY(newY);
			card.setZ(newZ);
			newX += (CardModel.CARD_W + DIST_CARD_X_DIFF);
			if(newX > DIST_CARD_X_START + 8*(CardModel.CARD_W + DIST_CARD_X_DIFF)){
				newX = DIST_CARD_X_START;
				newY = DIST_CARD_Y2;
			}
			newZ -= 0.1;
		}

		setChanged();
		Pair<TarotAction, Object> arg = new Pair<TarotAction, Object>(TarotAction.PLAYER_ORGANIZED, null);
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
		Pair<TarotAction, Boolean> arg = new Pair<TarotAction, Boolean>(TarotAction.PETIT_SEC_DETECTED, petitSec);
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
		Pair<TarotAction, PlayerAction> arg = new Pair<TarotAction, PlayerAction>(TarotAction.ACTION_CHOSEN, myAction);
		notifyObservers(arg);
	}

	public static final int NB_CARD_GAP = 6;
	public static final int GAP_X1= 4 * (SCREEN_W / 5);
	public static final int GAP_X2 = GAP_X1 + (CardModel.CARD_W + DIST_CARD_X_DIFF);
	public static final int GAP_Y_START = SCREEN_H / 5;
	private int nbCardInGap = 0;
	
	public void addCardToGap(CardModel card){
		gapCards.add(card);
		myCards.remove(card);

		if(nbCardInGap % 2 == 0){
			card.setX(GAP_X1);
		}
		else{
			card.setX(GAP_X2);
		}
		card.setY(GAP_Y_START + (CardModel.CARD_H + DIST_CARD_Y_DIFF)*(nbCardInGap/2));
		nbCardInGap++;
		
		setChanged();
		Pair<TarotAction, CardModel> argCardMoved = new Pair<TarotAction, CardModel>(TarotAction.CARD_MOVED, card);
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
		Pair<TarotAction, Object> argGapDone = new Pair<TarotAction, Object>(TarotAction.PLAYER_REVERTED, null);
		notifyObservers(argGapDone);
	}
	
	public void revertChien() {
		for(CardModel card : chienCards){
			card.onFront = true;
		}
		setChanged();
		Pair<TarotAction, Object> argGapDone = new Pair<TarotAction, Object>(TarotAction.CHIEN_REVERTED, null);
		notifyObservers(argGapDone);
	}
	
	public void nouvelleDonne(){
		deckCards.clear();
		myCards.clear();
		othersCards.clear();
		chienCards.clear();
		gapCards.clear();
		
		loadCards();
		
		distributedCards = 0;
		nbCardsInChien = 0;

		currentPlayer = 1;
		myDistCardX = DIST_CARD_X_START;
		myDistCardY = DIST_CARD_Y1;
		chienCardX = CHIEN_CARD_X_START;
		
		myIndexCard = -3;
		othersIndexCard = -3;
		chienIndexCard = -1;
		nbCardInGap = 0;
	}
}
