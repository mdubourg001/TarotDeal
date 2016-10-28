package Tarot;

public interface Observer {
	public void updateDeckMixed();
	public void update3CardsDistributed(CardModel card1, CardModel card2,CardModel card3, boolean dealFinished);
	public void updateCardMoved(CardModel card);
	public void updatePlayerCardsOrganized();
	public void updatePetitSec(boolean petitSec);
	public void updateActionChosen(PlayerAction action);
	public void updateGapDone();
}
