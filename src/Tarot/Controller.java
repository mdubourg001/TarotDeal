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

    /*Commence a -1 et s'incremente au debut de doNextAction() (et pas a la fin)
    car la fonction est appellee recursivement.*/
    private int currentAction = -1;
    
    private int nbDistribution = 0;
    
    /*Utilise lorsque qu'on repasse du menu a l'ecran de distribution avec le buton play
     pour eviter de faire doNextAction() alors que le jeu est deja lance.*/
    public void startIfNeeded(){
    	if(currentAction == -1){
    		doNextAction();
    	}
    }

    public void doNextAction() {
        currentAction++;
        switch (currentAction) {
            case 0:
                model.mixJeu();
                break;
            case 1:
                model.cutJeu();
                break;
            case 2:
                distribute();
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
    
    public void distribute(){
    	if(nbDistribution == Model.NB_DISTRIBUTION-1){//derniere distribution
    		model.distributeCards();
    	}
    	else{
    		model.distributeCards();
    		currentAction--;//On reste sur l'action distibute()
    	}
    	nbDistribution++;
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
    
    public void restart() {
        model.nouvelleDonne();
        currentAction = -1;
        nbDistribution = 0;
    }
    
    public void nouvelleDonne() {
    	restart();
        doNextAction();
    }
}
