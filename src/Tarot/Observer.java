package Tarot;

public interface Observer {
	public void updateDeckMixed();
	public void update3CardsDistributed(CardModel card1, CardModel card2,CardModel card3, boolean dealFinished);
	public void updateCardAddToChien(CardModel card);
}
