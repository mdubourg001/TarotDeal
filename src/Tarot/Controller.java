package Tarot;

public class Controller {
    private Model model;

    public Controller(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    private int currentAction = -1;

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
                model.organizePlayerCards();//Only if it use gap
                break;
            case 8:
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
    	currentAction += 2;
        doNextAction();
    }
    
    public void nouvelleDonne() {
        model.nouvelleDonne();

        currentAction = -1;
        doNextAction();
    }
}
