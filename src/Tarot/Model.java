package Tarot;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javafx.util.Pair;

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
	
	Point2D[][] playerCardsPositions = new Point2D[NB_PLAYERS][NB_CARDS_PLAYER];
	
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
	
	private void setAndNotifyChanged(Object arg){
		setChanged();
		notifyObservers(arg);
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
		
		values.add("Excuse");
		values.add("ClubsKing");
		values.add("DiamondsKing");
		values.add("HeartsKing");
		values.add("SpadesKing");
		
		if(ungableTrumps){
			for(int i = FIRST_TRUMP; i <= LAST_TRUMP; i++){
				values.add("Trump" + Integer.toString(i));
			}
		}
		else{
			values.add("Trump1");
			values.add("Trump21");
		}
		
		return values;
	}
	
	private boolean ungableTrumps = false;
	private boolean ungableTrumps(){
		int nbColoredCard = 0;
		for(CardModel card : playersDecks.get(0)){
			if(!card.getName().contains("Trump")){
				nbColoredCard++;
			}
			if(nbColoredCard == NB_CARD_GAP){
				return true;
			}
		}
		return false;
	}
	
	// TODO
	private void loadPlayersCardsPositions(){
		Point2D[] positions = new Point2D[NB_PLAYERS];
		positions[0] = new Point2D.Double(DIST_CARD_X_START, DIST_CARD_Y1);
		
		for(int i = 0; i<NB_CARDS_PLAYER; i++){
			playerCardsPositions[0][i] = updateMyDistCardPosition(i);
			playerCardsPositions[1][i] = updatePlayer1CardsPositions(i);
			playerCardsPositions[2][i] = updatePlayer2CardsPositions(i);
			playerCardsPositions[3][i] = updatePlayer3CardsPositions(i);
		}
	}
	
	private Point2D updateMyDistCardPosition(int index){
		double x,y;
		x = DIST_CARD_X_START + (CardModel.CARD_W + DIST_CARD_X_DIFF)*(index%9);
		if(index < 9){
			y = DIST_CARD_Y1;
		}
		else{
			y = DIST_CARD_Y2;
		}
		return new Point2D.Double(x, y);
	}
	
	private Point2D updatePlayer1CardsPositions(int index){
		switch(index%3){
		case 0:
			return new Point2D.Double(PLAYERS_2_4_X_SHIFT+SCREEN_W, PLAYERS_2_4_Y1);
		case 1:
			return new Point2D.Double(PLAYERS_2_4_X_SHIFT+SCREEN_W, PLAYERS_2_4_Y2);
		default:
			return new Point2D.Double(PLAYERS_2_4_X_SHIFT+SCREEN_W, PLAYERS_2_4_Y3);
		}
	}
	
	private Point2D updatePlayer2CardsPositions(int index){
		switch(index%3){
		case 0:
			return new Point2D.Double((SCREEN_W-CardModel.CARD_W)/2-(CardModel.CARD_W + DIST_CARD_X_DIFF), PLAYER_3_Y);
		case 1:
			return new Point2D.Double((SCREEN_W-CardModel.CARD_W)/2, PLAYER_3_Y);
		default:
			return new Point2D.Double((SCREEN_W-CardModel.CARD_W)/2+(CardModel.CARD_W + DIST_CARD_X_DIFF), PLAYER_3_Y);
		}
	}
	
	private Point2D updatePlayer3CardsPositions(int index){
		switch(index%3){
		case 0:
			return new Point2D.Double(-PLAYERS_2_4_X_SHIFT-CardModel.CARD_W, PLAYERS_2_4_Y1);
		case 1:
			return new Point2D.Double(-PLAYERS_2_4_X_SHIFT-CardModel.CARD_W, PLAYERS_2_4_Y2);
		default:
			return new Point2D.Double(-PLAYERS_2_4_X_SHIFT-CardModel.CARD_W, PLAYERS_2_4_Y3);
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
		setAndNotifyChanged(new Pair<TarotAction, Object>(TarotAction.DECK_MIXED, null));
	}

	public void cutDeck(){
        ArrayList<CardModel> firstHalf = subDeck(gameDecks.get("jeu") ,0, NB_CARDS/2);
        ArrayList<CardModel> secondHalf = subDeck(gameDecks.get("jeu") ,NB_CARDS/2, NB_CARDS);

        gameDecks.replace("jeu", mergeDecks(secondHalf, firstHalf));
        adaptZJeu();
        
        setAndNotifyChanged(new Pair<TarotAction, Integer>(TarotAction.DECK_CUT, NB_CARDS/2));
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
			move3CardsToPlayer(currentPlayer, choose3CardsOrder());
			break;
		case 1 :
		case 2 :
		case 3 :
			move3CardsToPlayer(currentPlayer, new int[]{0, 1, 0});
			break;
		}
	}
	
	private void move3CardsToPlayer(int player, int[] order){
		for(int i=0; i<3; i++){
			playersDecks.get(player).add(gameDecks.get("jeu").get(order[i]));
			playersDecks.get(player).get(playersDecks.get(player).size()-1).moveTo(playerCardsPositions[player][playersDecks.get(player).size()-1].getX(), 
																			playerCardsPositions[player][playersDecks.get(player).size()-1].getY(), 1);
			gameDecks.get("jeu").remove(order[i]);
		}
		changePlayer();
		
		setAndNotifyChanged(new Pair<TarotAction, Pair<Boolean, CardModel[]>>(TarotAction.CARDS_DISTRIBUTED,
				new Pair<Boolean, CardModel[]>(gameDecks.get("jeu").isEmpty(), 
						new CardModel[]{playersDecks.get(player).get(playersDecks.get(player).size()-3), 
								playersDecks.get(player).get(playersDecks.get(player).size()-2), 
								playersDecks.get(player).get(playersDecks.get(player).size()-1)})));
	}
	
	//Avoid cards to pass thought each others
	private int[] choose3CardsOrder(){
		int[] order = new int[3];
		if(playersDecks.get(0).size()%9 == 0){
			order[0] = 0; order[1] = 0; order[2] = 0;
		}
		else if(playersDecks.get(0).size()%9 == 3){
			order[0] = 0; order[1] = 1; order[2] = 0;
		}
		else{
			order[0] = 2; order[1] = 1; order[2] = 0;
		}
		return order;
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

		setAndNotifyChanged(new Pair<TarotAction, CardModel>(TarotAction.CARD_MOVED_FROM_DECK, 
				gameDecks.get("chien").get(gameDecks.get("chien").size()-1)));
	}
	
	public void revertPlayer() {
		ungableTrumps = ungableTrumps();
		
		for(CardModel card : playersDecks.get(0)){
			card.setOnFront(true);
		}
		
		setAndNotifyChanged(new Pair<TarotAction, Object>(TarotAction.PLAYER_REVERTED, null));
	}
	
	public void organizePlayerCards(){
		Collections.sort(playersDecks.get(0));
		
		double newZ = 1.0;
		for(int i = 0; i<NB_CARDS_PLAYER; i++){
			playersDecks.get(0).get(i).setX(playerCardsPositions[0][i].getX());
			playersDecks.get(0).get(i).setY(playerCardsPositions[0][i].getY());
			playersDecks.get(0).get(i).setZ(newZ);
			newZ -= 0.01;
		}
		
		setAndNotifyChanged(new Pair<TarotAction, Object>(TarotAction.PLAYER_ORGANIZED, null));
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
		
		setAndNotifyChanged(new Pair<TarotAction, Boolean>(TarotAction.PETIT_SEC_DETECTED, petitSec));
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
		setAndNotifyChanged(new Pair<TarotAction, PlayerAction>(TarotAction.ACTION_CHOSEN, myAction));
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
		
		setAndNotifyChanged(new Pair<TarotAction, CardModel>(TarotAction.CARD_ADDED_GAP, card));
		
		if(nbCardInGap == NB_CARD_GAP){
			finalizeGap();
		}
	}
	
	private void finalizeGap(){
		while(gameDecks.get("chien").size() > 0){
			playersDecks.get(0).add(gameDecks.get("chien").get(0));
			gameDecks.get("chien").remove(0);
		}
		
		setAndNotifyChanged(new Pair<TarotAction, Object>(TarotAction.GAP_DONE, null));
	}
	
	public void revertChien() {
		for(CardModel card : gameDecks.get("chien")){
			card.setOnFront(true);
		}
		
		setAndNotifyChanged(new Pair<TarotAction, Object>(TarotAction.CHIEN_REVERTED, null));
	}
	
	public void finish(){
		setAndNotifyChanged(new Pair<TarotAction, Object>(TarotAction.DISTRIBUTION_DONE, null));
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
