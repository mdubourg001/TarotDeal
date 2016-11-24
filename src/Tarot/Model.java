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
	
	double[][][] playerCardsPositions = new double[NB_PLAYERS][NB_CARDS][2];
	
	private int distributedCards = 0;

	private int currentPlayer = 0;
	private int chienCardX = CHIEN_CARD_X_START;
	
	public Model(){
		gameDecks.put("jeu", new ArrayList<CardModel>());
		gameDecks.put("chien", new ArrayList<CardModel>());
		gameDecks.put("gap", new ArrayList<CardModel>());
		
		for(int i=0; i<NB_PLAYERS; i++){
			playersDecks.add(new ArrayList<CardModel>());
		}
		
		loadCards();
		loadPlayersCardsPositions();
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
		for(int i = FIRST_TRUMP; i <= LAST_TRUMP; i++){
			gameDecks.get("jeu").add(new CardModel("Trump" + Integer.toString(i)));
		}
		gameDecks.get("jeu").add(new CardModel("Excuse"));
	}

	private void loadColoredCards(String color){
		for(String value : cardValues()){
			gameDecks.get("jeu").add(new CardModel(color + value));
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
	
	// TODO
	private void loadPlayersCardsPositions(){
		double[][] positions = new double[NB_PLAYERS][2];
		positions[0][0] = DIST_CARD_X_START;
		positions[0][1] = DIST_CARD_Y1;
		
		for(int i = 0; i<NB_CARDS; i++){
			playerCardsPositions[0][i][0] = positions[0][0];
			playerCardsPositions[0][i][1] = positions[0][1];
			updateMyDistCardPosition(positions[0]);
			
			updatePlayer1CardsPositions(i, positions[1]);
			playerCardsPositions[1][i][0] = positions[1][0];
			playerCardsPositions[1][i][1] = positions[1][1];
			
			updatePlayer2CardsPositions(i, positions[2]);
			playerCardsPositions[2][i][0] = positions[2][0];
			playerCardsPositions[2][i][1] = positions[2][1];
			
			updatePlayer3CardsPositions(i, positions[3]);
			playerCardsPositions[3][i][0] = positions[3][0];
			playerCardsPositions[3][i][1] = positions[3][1];
		}
	}
	
	private void updateMyDistCardPosition(double pos[]){
		pos[0] += (CardModel.CARD_W + DIST_CARD_X_DIFF);
		if(pos[0] > DIST_CARD_X_START + 8*(CardModel.CARD_W + DIST_CARD_X_DIFF)){
			pos[0] = DIST_CARD_X_START;
			pos[1] = DIST_CARD_Y2;
		}
	}
	
	private void updatePlayer1CardsPositions(int index, double pos[]){
		pos[0] = PLAYERS_2_4_X_SHIFT+SCREEN_W;
		switch(index%3){
		case 0:
			pos[1] = PLAYERS_2_4_Y1;
			break;
		case 1:
			pos[1] = PLAYERS_2_4_Y2;
			break;
		case 2:
			pos[1] = PLAYERS_2_4_Y3;
			break;
		}
	}
	
	private void updatePlayer2CardsPositions(int index, double pos[]){
		pos[1] = PLAYER_3_Y;
		switch(index%3){
		case 0:
			pos[0] = (SCREEN_W-CardModel.CARD_W)/2-(CardModel.CARD_W + DIST_CARD_X_DIFF);
			break;
		case 1:
			pos[0] = (SCREEN_W-CardModel.CARD_W)/2;
			break;
		case 2:
			pos[0] = (SCREEN_W-CardModel.CARD_W)/2+(CardModel.CARD_W + DIST_CARD_X_DIFF);
			break;
		}
	}
	
	private void updatePlayer3CardsPositions(int index, double pos[]){
		pos[0] = -PLAYERS_2_4_X_SHIFT-CardModel.CARD_W;
		switch(index%3){
		case 0:
			pos[1] = PLAYERS_2_4_Y1;
			break;
		case 1:
			pos[1] = PLAYERS_2_4_Y2;
			break;
		case 2:
			pos[1] = PLAYERS_2_4_Y3;
			break;
		}
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
        ArrayList<CardModel> firstHalf = subDeck(gameDecks.get("jeu") ,0, NB_CARDS/2);
        ArrayList<CardModel> secondHalf = subDeck(gameDecks.get("jeu") ,NB_CARDS/2, NB_CARDS);

        gameDecks.replace("jeu", mergeDecks(secondHalf, firstHalf));
        adaptZJeu();

        setChanged();
        Pair<TarotAction, Integer> arg = new Pair<TarotAction, Integer>(TarotAction.DECK_CUT, NB_CARDS/2);
        notifyObservers(arg);
    }
	
	private ArrayList<CardModel> subDeck(ArrayList<CardModel> deck, int iMin, int iMax){
		ArrayList<CardModel> subDeck = new ArrayList<CardModel>();
		for(int i = iMin; i < iMax; i++){
			subDeck.add(deck.get(i));
        }
		return subDeck;
	}
	
	private ArrayList<CardModel> mergeDecks(ArrayList<CardModel> first, ArrayList<CardModel> second){
		first.addAll(second);
		return first;
	}
	
	private void adaptZJeu(){
		double z = DECK_Z_TOP;
        for(CardModel card : gameDecks.get("jeu")){
        	card.setZ(z);
			z += DECK_Z_DIFF;
        }
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
		case 0 :
			move3CardsToMe();
			break;
		case 1 :
		case 2 :
		case 3 :
			move3CardsToOther();
			break;
		}
	}
	
	private void move3CardsToMe(){
		int[] cardOrders = choose3CardsOrder();
		
		for(int i=0; i<3; i++){
			playersDecks.get(0).add(gameDecks.get("jeu").get(cardOrders[i]));
			playersDecks.get(0).get(playersDecks.get(0).size()-1).moveTo(playerCardsPositions[0][playersDecks.get(0).size()-1][0], 
																		playerCardsPositions[0][playersDecks.get(0).size()-1][1], 1);
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
	
	//Avoid cards to pass thought each others
	private int[] choose3CardsOrder(){
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
		return cardOrders;
	}
	
	private void move3CardsToOther(){
		int player = currentPlayer;
		
		for(int i=0; i<3; i++){
			playersDecks.get(player).add(gameDecks.get("jeu").get(0));
			playersDecks.get(player).get(playersDecks.get(player).size()-1).moveTo(playerCardsPositions[player][playersDecks.get(player).size()-1][0], 
																			playerCardsPositions[player][playersDecks.get(player).size()-1][1], 1);
			gameDecks.get("jeu").remove(0);
		}
		changePlayer();
		
		setChanged();
		Pair<TarotAction, Pair<Boolean, CardModel[]>> arg = new Pair<TarotAction, Pair<Boolean, CardModel[]>>(TarotAction.CARDS_DISTRIBUTED,
				new Pair<Boolean, CardModel[]>(gameDecks.get("jeu").isEmpty(), 
						new CardModel[]{playersDecks.get(player).get(playersDecks.get(player).size()-3), 
								playersDecks.get(player).get(playersDecks.get(player).size()-2), 
								playersDecks.get(player).get(playersDecks.get(player).size()-1)}));
		notifyObservers(arg);
	}
	
	private void changePlayer(){
		currentPlayer++;
		currentPlayer %= NB_PLAYERS;
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

		currentPlayer = 0;
		chienCardX = CHIEN_CARD_X_START;
		
		nbCardInGap = 0;
	}
}
