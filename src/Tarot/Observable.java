package Tarot;

public interface Observable {
	  public void addObserver(Observer obs);
	  public void removeObserver();
	  public void notifyDeckMixed();
	  public void notify3CardsDistributed(CardModel card1, CardModel card2,CardModel card3);
	  public void notifyCardAddToChien(CardModel card);
	  public void notifyPlayerCardsOrganized();
}