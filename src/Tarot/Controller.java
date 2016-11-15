package Tarot;

public class Controller {
    private Model model;

    public Controller(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public void chooseAction(PlayerAction action) {
        model.chooseAction(action);
    }

    public void addCardToGap(CardModel card) {
        model.addCardToGap(card);
    }
}
