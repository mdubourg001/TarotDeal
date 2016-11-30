package Tarot.ModelPack;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Random;

import javafx.util.Pair;

public class Model extends Observable {

	private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

	public static final double SCREEN_W = SCREEN_SIZE.getWidth();
	public static final double SCREEN_H = SCREEN_SIZE.getHeight();
	
	public static final int FIRST_TRUMP = 1;
	public static final int LAST_TRUMP = 21;
	
	public static final int NB_CARDS = 78;
	
	private static final int NB_PLAYERS = 4;
	
	public static final double DECK_X = (SCREEN_W / 2.5) - CardModel.CARD_W / 2 ;
	public static final double DECK_Y = SCREEN_H/200;

	public static final double DIST_CARD_X_SHIFT = 5;
	public static final double DIST_CARD_Y_SHIFT = 25;

	public static final int CHIEN_SIZE = 6;
	public static final double CHIEN_W = CHIEN_SIZE * CardModel.CARD_W + (CHIEN_SIZE-1) * DIST_CARD_X_SHIFT;
	public static final double CHIEN_H = CardModel.CARD_H;
	public static final double CHIEN_X = (SCREEN_W / 2.5) - CHIEN_W/2;
	public static final double CHIEN_Y = DECK_Y + CardModel.CARD_H + DIST_CARD_Y_SHIFT;

	public static final int DIST_CARD_SIZE = 9;
	public static final double MY_CARDS_W = DIST_CARD_SIZE * CardModel.CARD_W + (DIST_CARD_SIZE-1) * DIST_CARD_X_SHIFT;
	public static final double MY_CARDS_H = 2 * CardModel.CARD_H + DIST_CARD_Y_SHIFT;
	public static final double MY_CARDS_X = (SCREEN_W / 2.5) - MY_CARDS_W/2;
	public static final double MY_CARDS_Y = CHIEN_Y + CHIEN_H + DIST_CARD_Y_SHIFT;

	private static final double PLAYER_3_Y = -SCREEN_W/2.4;
	private static final double PLAYER_3_X_SHIFT = 200;
	
	private static final double PLAYERS_2_4_X = SCREEN_H/1.8;
	private static final double PLAYERS_2_4_SHIFT_Y = 200;
	
	Map<String, ArrayList<CardModel>> gameDecks = new HashMap<String, ArrayList<CardModel>>();
	
	Player[] players = new Player[]{
			new Player(MY_CARDS_X, MY_CARDS_Y, CardModel.CARD_W + DIST_CARD_X_SHIFT, CardModel.CARD_H + DIST_CARD_Y_SHIFT, true),
			new Player(Model.SCREEN_W + PLAYERS_2_4_X, DECK_Y, 0, PLAYERS_2_4_SHIFT_Y),
			new Player(DECK_X, PLAYER_3_Y, PLAYER_3_X_SHIFT, 0),
			new Player(-PLAYERS_2_4_X, DECK_Y, 0, PLAYERS_2_4_SHIFT_Y)
	};
	
	private int distributedCards = 0;

	private int currentPlayer = 0;
	private double chienCardX = CHIEN_X;
	
	public Model(){
		gameDecks.put("jeu", new ArrayList<CardModel>());
		gameDecks.put("chien", new ArrayList<CardModel>());
		gameDecks.put("gap", new ArrayList<CardModel>());
		
		loadCards();
	}

	public ArrayList<CardModel> getJeu() {
		return gameDecks.get("jeu");
	}

	public ArrayList<CardModel> getChienCards() {
		return gameDecks.get("chien");
	}
	
	public ArrayList<CardModel> getMyCards() {
		return players[0].getDeck();
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
		for(CardModel card : players[0].getDeck()){
			if(!card.getName().contains("Trump")){
				nbColoredCard++;
			}
			if(nbColoredCard == GAP_SIZE){
				return true;
			}
		}
		return false;
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
		int cutIndex = new Random().nextInt(NB_CARDS-8) + 4;
        ArrayList<CardModel> firstHalf = subDeck(gameDecks.get("jeu") ,0, cutIndex);
        ArrayList<CardModel> secondHalf = subDeck(gameDecks.get("jeu") ,cutIndex, NB_CARDS);

        gameDecks.replace("jeu", mergeDecks(secondHalf, firstHalf));
        adaptZJeu();
        
        setAndNotifyChanged(new Pair<TarotAction, Integer>(TarotAction.DECK_CUT, cutIndex));
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

	//Adapter pour eviter des bugs graphiques dus a des cartes qui se croisent
	public void distributeCards(){
		switch(distributedCards){
		case 6:
		case 19:
		case 32:
		case 45:
		case 58:
		case 71:
			addCardToChien();
			break;
		}
		distribute3Cards();
	}
	
	private void distribute3Cards(){
		distributedCards += 3;
		
		switch(currentPlayer){
		case 0 :
			move3CardsToPlayer(currentPlayer, chooseMy3CardsOrder());
			break;
		case 1 :
		case 2 :
		case 3 :
			move3CardsToPlayer(currentPlayer, new int[]{0, 1, 0});
			break;
		}
	}
	
	/*Le parametre order fait en sorte que, plus une carte aura de distance a parcourir apres 
	distribution, plus elle sera traitee tot. Cela evite que les cartes se croisent lors
	d'un d�placement continue. Ainsi la Vue n'a pas a gerer ce bug graphique.*/
	private void move3CardsToPlayer(int player, int[] order){
		for(int i=0; i<3; i++){
			players[player].getDeck().add(gameDecks.get("jeu").get(order[i]));
			players[player].getDeck().get(players[player].getDeck().size()-1).moveTo(players[player].getLastCardPosition().getX(), 
					players[player].getLastCardPosition().getY(), 1);
			gameDecks.get("jeu").remove(order[i]);
		}
		changePlayer();
		
		setAndNotifyChanged(new Pair<TarotAction, Pair<Boolean, CardModel[]>>(TarotAction.CARDS_DISTRIBUTED,
				new Pair<Boolean, CardModel[]>(gameDecks.get("jeu").isEmpty(), 
						new CardModel[]{players[player].getDeck().get(players[player].getDeck().size()-3), 
								players[player].getDeck().get(players[player].getDeck().size()-2), 
								players[player].getDeck().get(players[player].getDeck().size()-1)})));
	}
	
	//Expliquer avant "move3CardsToPlayer(int player, int[] order){...}".
	private int[] chooseMy3CardsOrder(){
		int[] order = new int[3];
		if(players[0].getDeck().size()%9 == 0){//Les cartes sont a gauche du Jeu.
			order[0] = 0; order[1] = 0; order[2] = 0;
		}
		else if(players[0].getDeck().size()%9 == 3){//1 carte a gauche, 1 a droite et 1 au niveau du Jeu.
			order[0] = 0; order[1] = 1; order[2] = 0;
		}
		else{//Les cartes sont a droite du Jeu.
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
		gameDecks.get("chien").get(gameDecks.get("chien").size()-1).moveTo(chienCardX, CHIEN_Y, 1);
		chienCardX += (CardModel.CARD_W + DIST_CARD_X_SHIFT);
		gameDecks.get("jeu").remove(0);

		setAndNotifyChanged(new Pair<TarotAction, CardModel>(TarotAction.CARD_MOVED_FROM_DECK, 
				gameDecks.get("chien").get(gameDecks.get("chien").size()-1)));
	}
	
	public void revertPlayer() {
		ungableTrumps = ungableTrumps();
		
		for(CardModel card : players[0].getDeck()){
			card.setOnFront(true);
		}
		
		setAndNotifyChanged(new Pair<TarotAction, Object>(TarotAction.PLAYER_REVERTED, null));
	}
	
	public void organizePlayerCards(){
		Collections.sort(players[0].getDeck());
		
		/*Modifier tres legerement le z d'une carte a l'autre evite les croisements de cartes si elles
		sont bougees, d'abord en z, puis en x et y. La Vue peut alors gerer facilement ce bug graphique.*/
		double newZ = 1.0;
		for(int i = 0; i<Player.NB_CARDS; i++){
			players[0].getDeck().get(i).setX(players[0].getCardPosition(i).getX());
			players[0].getDeck().get(i).setY(players[0].getCardPosition(i).getY());
			players[0].getDeck().get(i).setZ(newZ);
			newZ -= 0.01;
		}
		
		setAndNotifyChanged(new Pair<TarotAction, Object>(TarotAction.PLAYER_ORGANIZED, null));
	}

	public void detectPetitSec(){
		Boolean petitSec = false;
		Integer playerNb = 1;
		for(Player p : players){
			for(CardModel card : p.getDeck()){
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
			playerNb++;
		}
		setAndNotifyChanged(new Pair<TarotAction, Pair<Boolean, Integer>>(TarotAction.PETIT_SEC_DETECTED, 
				new Pair<Boolean, Integer>(petitSec, playerNb)));
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

	public static final int GAP_SIZE = 6;
	public static final double GAP_W = 2*CardModel.CARD_W + DIST_CARD_X_SHIFT;
	public static final double GAP_H = (GAP_SIZE/2) * CardModel.CARD_H + (GAP_SIZE/2 - 1) * DIST_CARD_Y_SHIFT;
	public static final double GAP_X = 4 * (SCREEN_W / 5);
	public static final double GAP_Y = SCREEN_H / 5;
	
	private int nbCardInGap = 0;
	public void addCardToGap(CardModel card){
		gameDecks.get("gap").add(card);
		players[0].getDeck().remove(card);

		if(nbCardInGap % 2 == 0){
			card.setX(GAP_X);
		}
		else{
			card.setX(GAP_X + (CardModel.CARD_W + DIST_CARD_X_SHIFT));
		}
		card.setY(GAP_Y + (CardModel.CARD_H + DIST_CARD_Y_SHIFT)*(nbCardInGap/2));
		nbCardInGap++;
		
		setAndNotifyChanged(new Pair<TarotAction, CardModel>(TarotAction.CARD_ADDED_GAP, card));
		
		if(nbCardInGap == GAP_SIZE){
			setAndNotifyChanged(new Pair<TarotAction, Object>(TarotAction.GAP_DONE, null));
		}
	}
	
	public void moveChienToPlayer(){
		while(gameDecks.get("chien").size() > 0){
			players[0].getDeck().add(gameDecks.get("chien").get(0));
			gameDecks.get("chien").remove(0);
		}
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
		for(Player p : players){
			p.getDeck().clear();
		}
		
		CardModel.reinitOrder();
		
		loadCards();
		
		distributedCards = 0;
		currentPlayer = 0;
		chienCardX = CHIEN_X;
		nbCardInGap = 0;
	}
}
