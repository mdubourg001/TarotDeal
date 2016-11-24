package Tarot;

import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class Model extends Observable {

	private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public static final int SCREEN_W = (int)screenSize.getWidth();
	public static final int SCREEN_H = (int)screenSize.getHeight();
	
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
	
	Map<String, ArrayList<CardModel>> gameDecks = new HashMap<String, ArrayList<CardModel>>();
	ArrayList<ArrayList<CardModel>> playersDecks = new ArrayList<ArrayList<CardModel>>();
	
	private int distributedCards = 0;

	private int currentPlayer = 1;
	private int myDistCardX = DIST_CARD_X_START;
	private int myDistCardY = DIST_CARD_Y1;
	private int chienCardX = CHIEN_CARD_X_START;
	
	public Model(){
		gameDecks.put("jeu", new ArrayList<CardModel>());
		gameDecks.put("chien", new ArrayList<CardModel>());
		gameDecks.put("gap", new ArrayList<CardModel>());
		
		for(int i=0; i<4; i++){
			playersDecks.add(new ArrayList<CardModel>());
		}
		
		loadCards();
	}

	public ArrayList<CardModel> getJeu() {
		return gameDecks.get("jeu");
	}
	
	public ArrayList<CardModel> getMyCards() {
		return playersDecks.get(0);
	}
	
	public ArrayList<CardModel> getChienCards() {
		return gameDecks.get("chien");
	}

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
			fullName = "res/Tarot_nouveau_-_Grimaud_-_1898_-_Trumps_-_";
			if(i < 10){
				fullName += "0";
			}
			fullName += Integer.toString(i) + ".jpg";
			gameDecks.get("jeu").add(new CardModel("Trump" + Integer.toString(i), fullName));
		}
		fullName = "res/Tarot_nouveau_-_Grimaud_-_1898_-_Trumps_-_Excuse.jpg";
		gameDecks.get("jeu").add(new CardModel("Excuse", fullName));
	}

	private void loadColoredCards(String color){
		String fullName;
		for(String value : cardValues()){
			fullName = "res/Tarot_nouveau_-_Grimaud_-_1898_-_" + color + "_-_" + value + ".jpg";
			gameDecks.get("jeu").add(new CardModel(color + value, fullName));
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
		Collections.shuffle(gameDecks.get("jeu"));
		double z = DECK_Z_TOP;
		for(CardModel card : gameDecks.get("jeu")){
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
        	firstHalf.add(gameDecks.get("jeu").get(i));
        }
        for(int i = indexHalf; i < NB_CARDS; i++){
        	secondHalf.add(gameDecks.get("jeu").get(i));
        }

        secondHalf.addAll(firstHalf);
        gameDecks.replace("jeu", secondHalf);
        
        double z = DECK_Z_TOP;
        for(CardModel card : gameDecks.get("jeu")){
        	card.setZ(z);
			z += DECK_Z_DIFF;
        }

        setChanged();
        Pair<TarotAction, Integer> arg = new Pair<TarotAction, Integer>(TarotAction.DECK_CUT, indexHalf);
        notifyObservers(arg);
    }
	
	public void distributeCards(){
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
			move3CardsToOther(1,
					new int[]{PLAYERS_2_4_X_SHIFT+SCREEN_W, PLAYERS_2_4_X_SHIFT+SCREEN_W, PLAYERS_2_4_X_SHIFT+SCREEN_W}, 
					new int[]{PLAYERS_2_4_Y1, PLAYERS_2_4_Y2, PLAYERS_2_4_Y3});
			break;
		case 3 :
			move3CardsToOther(2,
					new int[]{(SCREEN_W-CardModel.CARD_W)/2-(CardModel.CARD_W + DIST_CARD_X_DIFF),(SCREEN_W-CardModel.CARD_W)/2,(SCREEN_W-CardModel.CARD_W)/2+(CardModel.CARD_W + DIST_CARD_X_DIFF)},
					new int[]{PLAYER_3_Y, PLAYER_3_Y, PLAYER_3_Y});
			break;
		case 4 :
			move3CardsToOther(3,
					new int[]{-PLAYERS_2_4_X_SHIFT-CardModel.CARD_W, -PLAYERS_2_4_X_SHIFT-CardModel.CARD_W, -PLAYERS_2_4_X_SHIFT-CardModel.CARD_W}, 
					new int[]{PLAYERS_2_4_Y1, PLAYERS_2_4_Y2, PLAYERS_2_4_Y3});
			break;
		}
	}
	
	private void move3CardsToMe(){
		int[] cardOrders = new int[3];
		if(playersDecks.get(0).size()%9 == 0){
			cardOrders[0] = 0; cardOrders[1] = 0; cardOrders[2] = 0;
		}
		else if(playersDecks.get(0).size()%9 == 3){
			cardOrders[0] = 0; cardOrders[1] = 1; cardOrders[2] = 0;
		}
		else{
			cardOrders[0] = 2; cardOrders[1] = 1; cardOrders[2] = 0;
		}
		
		for(int i=0; i<3; i++){
			playersDecks.get(0).add(gameDecks.get("jeu").get(cardOrders[i]));
			playersDecks.get(0).get(playersDecks.get(0).size()-1).moveTo(myDistCardX, myDistCardY, 1);
			myDistCardX += (CardModel.CARD_W + DIST_CARD_X_DIFF);
			if(myDistCardX > DIST_CARD_X_START + 8*(CardModel.CARD_W + DIST_CARD_X_DIFF)){
				myDistCardX = DIST_CARD_X_START;
				myDistCardY = DIST_CARD_Y2;
			}
			gameDecks.get("jeu").remove(cardOrders[i]);
		}
		changePlayer();
		
		setChanged();
		Pair<TarotAction, Pair<Boolean, CardModel[]>> arg = new Pair<TarotAction, Pair<Boolean, CardModel[]>>(TarotAction.CARDS_DISTRIBUTED,
				new Pair<Boolean, CardModel[]>(gameDecks.get("jeu").isEmpty(), 
						new CardModel[]{playersDecks.get(0).get(playersDecks.get(0).size()-3), 
								playersDecks.get(0).get(playersDecks.get(0).size()-2), 
								playersDecks.get(0).get(playersDecks.get(0).size()-1)}));
		notifyObservers(arg);
	}
	
	private void move3CardsToOther(int index, int x[], int y[]){
		
		for(int i=0; i<3; i++){
			playersDecks.get(index).add(gameDecks.get("jeu").get(0));
			playersDecks.get(index).get(playersDecks.get(index).size()-1).moveTo(x[i], y[i], 1);
			gameDecks.get("jeu").remove(0);
		}
		changePlayer();
		
		setChanged();
		Pair<TarotAction, Pair<Boolean, CardModel[]>> arg = new Pair<TarotAction, Pair<Boolean, CardModel[]>>(TarotAction.CARDS_DISTRIBUTED,
				new Pair<Boolean, CardModel[]>(gameDecks.get("jeu").isEmpty(), 
						new CardModel[]{playersDecks.get(index).get(playersDecks.get(index).size()-3), 
								playersDecks.get(index).get(playersDecks.get(index).size()-2), 
								playersDecks.get(index).get(playersDecks.get(index).size()-1)}));
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
		moveCardTochien();
	}
	
	private void moveCardTochien(){
		gameDecks.get("chien").add(gameDecks.get("jeu").get(0));
		gameDecks.get("chien").get(gameDecks.get("chien").size()-1).moveTo(chienCardX, CHIEN_CARD_Y, 1);
		chienCardX += (CardModel.CARD_W + DIST_CARD_X_DIFF);
		gameDecks.get("jeu").remove(0);
		
		setChanged();
		Pair<TarotAction, CardModel> arg = new Pair<TarotAction, CardModel>(TarotAction.CARD_MOVED_FROM_DECK, gameDecks.get("chien").get(gameDecks.get("chien").size()-1));
		notifyObservers(arg);
	}
	
	public void organizePlayerCards(){
		Collections.sort(playersDecks.get(0));
		
		int newX = DIST_CARD_X_START;
		int newY = DIST_CARD_Y1;
		double newZ = 1.0;
		for(CardModel card : playersDecks.get(0)){
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
		for(ArrayList<CardModel> deck : playersDecks){
			for(CardModel card : deck){
				if(card.getName().contentEquals("Trump1")){
					petitSec = true;
				}
				else if(othersTrumps().contains(card.getName())){
					petitSec = false;
					break;
				}
			}
			if(petitSec){
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
		gameDecks.get("gap").add(card);
		playersDecks.get(0).remove(card);

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
			while(gameDecks.get("chien").size() != 0){
				playersDecks.get(0).add(gameDecks.get("chien").get(0));
				gameDecks.get("chien").remove(0);
			}
			organizePlayerCards();

			setChanged();
			Pair<TarotAction, Object> argGapDone = new Pair<TarotAction, Object>(TarotAction.GAP_DONE, null);
			notifyObservers(argGapDone);
		}
	}
	
	public void revertPlayer() {
		for(CardModel card : playersDecks.get(0)){
			card.onFront = true;
		}
		setChanged();
		Pair<TarotAction, Object> argGapDone = new Pair<TarotAction, Object>(TarotAction.PLAYER_REVERTED, null);
		notifyObservers(argGapDone);
	}
	
	public void revertChien() {
		for(CardModel card : gameDecks.get("chien")){
			card.onFront = true;
		}
		setChanged();
		Pair<TarotAction, Object> argGapDone = new Pair<TarotAction, Object>(TarotAction.CHIEN_REVERTED, null);
		notifyObservers(argGapDone);
	}
	
	public void nouvelleDonne(){
		for(ArrayList<CardModel> deck : gameDecks.values()){
			deck.clear();
		}
		for(ArrayList<CardModel> deck : playersDecks){
			deck.clear();
		}
		
		loadCards();
		
		distributedCards = 0;

		currentPlayer = 1;
		myDistCardX = DIST_CARD_X_START;
		myDistCardY = DIST_CARD_Y1;
		chienCardX = CHIEN_CARD_X_START;
		
		nbCardInGap = 0;
	}
}
