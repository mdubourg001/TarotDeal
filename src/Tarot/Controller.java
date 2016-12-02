package Tarot;

import Tarot.ModelPack.CardModel;
import Tarot.ModelPack.Model;
import Tarot.ModelPack.PlayerAction;

public class Controller {
    private Model model;

    public Controller(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    private int currentAction = -1;
    
    public void startIfNeeded(){
    	if(currentAction == -1){
    		doNextAction();
    	}
    }

    public void doNextAction() {
        currentAction++;
        switch (currentAction) {
            case 0:
                model.mixDeck();
                break;
            case 1:
                model.cutDeck();
                break;
            case 2:
                model.distributeCards();
                break;
            case 3:
                model.revertPlayer();
                break;
            case 4:
                model.organizePlayerCards();
                break;
            case 5:
                model.detectPetitSec();
                break;
            case 6:
                model.revertChien();//Only if it use gap
                break;
            case 7:
                model.moveChienToPlayer();//Only if it use gap
                break;
            case 8:
                model.organizePlayerCards();//Only if it use gap
                break;
            case 9:
                model.finish();
                break;
        }
    }

    public void chooseAction(PlayerAction action) {
        model.chooseAction(action);
    }

    public void addCardToGap(CardModel card) {
        model.addCardToGap(card);
    }
    
    public void skipGap(){
    	currentAction += 3;
        doNextAction();
    }
    
    public void nouvelleDone() {
        model.nouvelleDonne();
        currentAction = -1;
    }
    
    public void restart() {
    	nouvelleDone();
        doNextAction();
    }
}
