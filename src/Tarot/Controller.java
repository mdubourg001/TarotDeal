package Tarot;

public class Controller {
    private Model model;

    public Controller(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public int currentAction = -1;

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
                model.revertChien();
                break;
            case 7:
                model.organizePlayerCards();//Only if it use gap
                break;
            case 8:
                //continue
                break;
        }
    }

    public void chooseAction(PlayerAction action) {
        model.chooseAction(action);
    }

    public void addCardToGap(CardModel card) {
        model.addCardToGap(card);
    }
}
