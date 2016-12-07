package TestsJUnit;

import Tarot.Controller;
import Tarot.ModelPack.*;
import javafx.util.Pair;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

public class ModelTest {
    @Test
    public void constructorTest(){
    	Model model = new Model();
        assertTrue(model.getJeuSize() == Model.NB_CARDS);
        
        CardModel.reinitOrder();
        for(int i = 0; i < model.getJeuSize(); i++){
        	if(model.getJeu(i).getName().equals("ClubsAce")){
        		assertEquals(0, model.getJeu(i).getOrder());
        	}
        	if(model.getJeu(i).getName().equals("SpadesKing")){
        		assertEquals(Model.NB_CARDS-1, model.getJeu(i).getOrder());
        	}
        }
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void actionsTest(){
    	Model model = new Model();
        Controller controller = new Controller(model);
        TestView view = new TestView();
        model.addObserver(view);
    	
        assertTrue(view.getArg() == null);
        
        controller.startIfNeeded(); //MIX DECK
        
        assertTrue(view.getArg() instanceof Pair<?, ?>);
        assertEquals(TarotAction.JEU_MIXED, ((Pair<TarotAction, Object>)view.getArg()).getKey());
        
        String firstCard = model.getJeu(0).getName();
        String lastCard = model.getJeu(Model.NB_CARDS-1).getName();
        
        controller.doNextAction(); //CUT DECK
        
        assertTrue(view.getArg() instanceof Pair<?, ?>);
        Integer cut = ((Pair<TarotAction, Integer>)view.getArg()).getValue();
        
        //On verifie que les cartes du dessus et du dessous soient passees au niveau de la coupe
        assertTrue(firstCard.equals(model.getJeu(Model.NB_CARDS-cut).getName()));
        assertTrue(lastCard.equals(model.getJeu(Model.NB_CARDS-cut-1).getName()));
        
        controller.doNextAction(); //CARD DISTRIBUTED premiere iteration
        
        assertEquals(3, model.getMyDeckSize());
        
        //On verifie que les 3 premieres cartes distribuees soient au bon endroit
        for(int i = 0; i<3; i++){
        	assertTrue(Math.abs(Model.MY_CARDS_X + i*(CardModel.CARD_W+Model.DIST_CARD_X_SHIFT) - model.getMyCards(i).getX()) < 0.01);
        	assertTrue(Math.abs(Model.MY_CARDS_Y - model.getMyCards(i).getY()) < 0.01);
        }
        
        while(model.getJeuSize() != 0){
        	controller.doNextAction(); //CARD DISTRIBUTED tout le reste
        }
        
        assertEquals(Player.NB_CARDS, model.getMyDeckSize());
        
        for(int i = 0; i < model.getMyDeckSize(); i++){
        	assertFalse(model.getMyCards(i).isOnFront());
        }
        
        controller.doNextAction(); //REVERT PLAYER
        
        for(int i = 0; i < model.getMyDeckSize(); i++){
        	assertTrue(model.getMyCards(i).isOnFront());
        }
        
        controller.doNextAction(); //ORGANISE PLAYER
        
        int currentOrder = Model.NB_CARDS; //Order max + 1
        for(int i = 0; i < model.getMyDeckSize(); i++){
        	assertTrue(model.getMyCards(i).getOrder() < currentOrder);
        	currentOrder = model.getMyCards(i).getOrder();
        }
        
        simulatePetitSec(model);
        
        controller.doNextAction(); //DETECT PETIT SEC
        
        assertTrue(view.getArg() instanceof Pair<?, ?>);
        //On doit recevoir petit sec du joueur 1 soit (TarotAction.PETIT_SEC_DETECTED, (true, 1))
        assertEquals(TarotAction.PETIT_SEC_DETECTED, ((Pair<TarotAction, Pair<Boolean, Integer>>)view.getArg()).getKey());
        assertEquals(true, ((Pair<TarotAction, Pair<Boolean, Integer>>)view.getArg()).getValue().getKey());
        assertEquals(new Integer(1), ((Pair<TarotAction, Pair<Boolean, Integer>>)view.getArg()).getValue().getValue());

        controller.chooseAction(PlayerAction.GARDE);

        assertTrue(view.getArg() instanceof Pair<?, ?>);
        assertEquals(TarotAction.ACTION_CHOSEN, ((Pair<TarotAction, PlayerAction>)view.getArg()).getKey());
        assertEquals(PlayerAction.GARDE, ((Pair<TarotAction, PlayerAction>)view.getArg()).getValue());

        controller.doNextAction(); //REVERT CHIEN

        for(int i = 0; i < model.getChienSize(); i++){
            assertTrue(model.getChienCards(i).isOnFront());
        }

        for(int i = 0; i < Model.GAP_SIZE; i++){
            controller.addCardToGap(model.getMyCards(0));
        }

        assertTrue(view.getArg() instanceof Pair<?, ?>);
        assertEquals(TarotAction.GAP_DONE, ((Pair<TarotAction, Object>)view.getArg()).getKey());

        controller.doNextAction(); //MOVE CHIEN TO PLAYER
        assertEquals(Player.NB_CARDS, model.getMyDeckSize());

        controller.doNextAction(); //ORGANISE PLAYER

        /*Les fausses cartes ont ete crees sans reinitOrder() du coup elles sont avant les 6 cartes
        du chien ajouter au joueur parce que leur order est plus grand.*/

        for(int i = 0; i < Player.NB_CARDS-model.GAP_SIZE; i++){
            assertTrue(model.getMyCards(i).getName().equals("Joker " + Integer.toString(Player.NB_CARDS-i-1)));
        }

        /*Les 6 dernieres cartes du joueur sont celles du chien qui n'ont pas ete remplacees par des
        fausses dans simulatePetitSec().*/
        for(int i = Player.NB_CARDS-model.GAP_SIZE; i < Player.NB_CARDS; i++){
            assertTrue(model.getMyCards(i).getName().contains("Trump")
                    || model.getMyCards(i).getName().contains("Clubs")
                    || model.getMyCards(i).getName().contains("Diamonds")
                    || model.getMyCards(i).getName().contains("Hearts")
                    || model.getMyCards(i).getName().contains("Spades")
                    || model.getMyCards(i).getName().equals("Excuse"));
        }

        controller.doNextAction(); //FINISH
        assertTrue(view.getArg() instanceof Pair<?, ?>);
        assertEquals(TarotAction.DISTRIBUTION_DONE, ((Pair<TarotAction, Object>)view.getArg()).getKey());
    }
    
    public void simulatePetitSec(Model model){
    	Field field;//On utilise la reflection pour modifier les donnees du model
    	Object value = null;
		try {
			field = model.getClass().getDeclaredField("players");
			field.setAccessible(true);
			value = field.get(model);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		Player player = ((Player[])value)[0];
		ArrayList<CardModel> deck = player.getDeck();
		
    	deck.clear();
    	deck.add(new CardModel("Trump1")); //On ajoute l'Atout 1
    	for(int i = 0; i<Player.NB_CARDS-1; i++){
    		deck.add(new CardModel("Joker " + Integer.toString(i+1))); //Et des fausses cartes sans Trump dans leur nom
    	}
    }
}