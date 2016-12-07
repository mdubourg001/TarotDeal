package Tarot.ViewPack.DistributionPack;

import java.util.Observable;

import Tarot.ModelPack.CardModel;
import Tarot.ModelPack.Model;
import Tarot.ModelPack.TarotAction;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.util.Pair;

/*Regroupe toutes les fonctions permettant de realiser le glisser-deposer pour faire l'ecart.*/
public class GapManager {
	public static final double ZONE_X = Model.GAP_X - 40;
    public static final double ZONE_Y = Model.GAP_Y - 40;
    public static final double ZONE_W = 2 * CardModel.CARD_W + Model.DIST_CARD_X_SHIFT + 80;
    public static final double ZONE_H = 3 * (CardModel.CARD_H + Model.DIST_CARD_Y_SHIFT) + 80;
    
    private static  CardView viewSelected = null;
    private static  double selectedCardXSave = 0;
    private static  double selectedCardYSave = 0;
    
    @SuppressWarnings({ "unchecked", "incomplete-switch" })
	public static void update(View view, Observable arg0, Object arg1){
    	switch (((Pair<TarotAction, Object>) arg1).getKey()) {
        case CHIEN_REVERTED:
        	revertChien(view);
            break;
        case CARD_ADDED_GAP:
        	view.moveCard(view.getCardView(((Pair<TarotAction, CardModel>) arg1).getValue().getName()),
                    ((Pair<TarotAction, CardModel>) arg1).getValue(), 0.0, null);
        	break;
        case GAP_DONE:
            updateGapDone(view);
            break;
    	}
    }
    
    public static void revertChien(View view) {
        for (int i = 0; i < view.getModel().getChienSize(); i++) {
        	if(i != view.getModel().getChienSize()-1){//Ca n'est pas la derniere carte du deck
        		view.waiter(View.REVERT_CARD_DURATION * View.REVERT_CARD_WAIT_COEF * (i+1), view.revertCardEvent(view.getModel().getChienCards(i), null));
        	}
        	else{
        		view.waiter(View.REVERT_CARD_DURATION * View.REVERT_CARD_WAIT_COEF * (i+1), view.revertCardEvent(view.getModel().getChienCards(i), doGap(view)));
        	}
        }
    }

    public static EventHandler<ActionEvent> doGap(View v) {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                for (int i=0; i < v.getModel().getMyDeckSize(); i++) {
                    updateGapableCard(v, v.getModel().getMyCards(i));
                }
                for (int i=0; i < v.getModel().getChienSize(); i++) {
                    updateGapableCard(v, v.getModel().getChienCards(i));
                }
                v.getController().doNextAction();
            }
        };
    }
    
    private static void updateGapableCard(View v, CardModel card){
    	CardView cardView = v.getCardView(card.getName());
    	cardView.getView().setOnMousePressed(selectCardEvent(v, cardView));
    	cardView.getView().setOnMouseDragged(followMouseEvent(v, cardView));
    	cardView.getView().setOnMouseReleased(tryAddCardToGapEvent(v, cardView, card));
    }

    private static EventHandler<MouseEvent> selectCardEvent(View v, CardView cardView) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	if(cardView.canBeSelected()){
                    selectCardEvent(v, cardView, event);
            	}
            }
        };
    }

    private static void selectCardEvent(View v, CardView cardView, MouseEvent event){
        selectedCardXSave = (int) cardView.getView().getTranslateX();
        selectedCardYSave = (int) cardView.getView().getTranslateY();
        riseCard(v, cardView, event);
        viewSelected = cardView;
        cardView.canBeSelected(false);
    }

    private static void riseCard(View v, CardView cardView, MouseEvent event){
        cardView.getView().setRotationAxis(Rotate.X_AXIS);
        cardView.getView().setRotate(-v.getDistGroup().getRotate());
        cardView.getView().setTranslateZ(-1 + CardModel.CARD_H * v.getDistGroup().getRotate()/90);
        followMouse(v, cardView, event);
    }

    private static EventHandler<MouseEvent> followMouseEvent(View v, CardView cardView) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	if(viewSelected == cardView){
                    followMouse(v, cardView, event);
            	}
            }
        };
    }

    private static void followMouse(View v, CardView cardView, MouseEvent event){
    	/*Formule inexacte qui essaye de garder la carte au niveau de la souris, lors du glisser deposer,
    	en prenant en compte l'inclinaison de la camera et le dezoom. Ci ces 2 valeurs sont a 0 le centre
    	de la carte est toujours au niveau de la souris.*/
        cardView.getView().setTranslateX((event.getSceneX() - CardModel.CARD_W / 2)
        		+ 0.0004*(event.getSceneX()-Model.SCREEN_W/2)*v.getDistGroup().getTranslateZ()
        		+ 0.002*(event.getSceneX()-Model.SCREEN_W/2)*(-v.getDistGroup().getRotate())*(1-event.getSceneY()/(0.2*Model.SCREEN_H)));
        cardView.getView().setTranslateY((event.getSceneY() - CardModel.CARD_H / 2)
        		+ 0.0004*(event.getSceneY()-Model.SCREEN_H/2)*v.getDistGroup().getTranslateZ()
        		+ 0.004*event.getSceneY()*(-v.getDistGroup().getRotate()));
    }

    private static EventHandler<MouseEvent> tryAddCardToGapEvent(View v, CardView cardView, CardModel card){
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tryAddCardToGap(v, cardView, card);
            }
        };
    }

    private static void tryAddCardToGap(View v, CardView cardView, CardModel card){
        if(viewSelected == cardView){
        	viewSelected = null;
            if (v.getModel().ungapableCards().contains(card.getName()) || GapManager.cardViewInGap(cardView)) {
                v.moveCard(cardView, selectedCardXSave, selectedCardYSave, card.getZ(), 0.0, canSelectView(cardView));
            }
            else{
                v.getController().addCardToGap(card);
                removeListeners(cardView);
            }
        }
    }

    private static EventHandler<ActionEvent> canSelectView(CardView view){
    	return new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				view.canBeSelected(true);
			}
    	};
    }

    private static void removeListeners(CardView cardView) {
        cardView.getView().setOnMousePressed(null);
        cardView.getView().setOnMouseDragged(null);
        cardView.getView().setOnMouseReleased(null);
    }
    
    private static boolean cardViewInGap(CardView view) {
        return view.getView().getTranslateX() + CardModel.CARD_W / 2 < ZONE_X
                || view.getView().getTranslateX() + CardModel.CARD_W / 2 > ZONE_X + ZONE_W
                || view.getView().getTranslateY() + CardModel.CARD_H / 2 < ZONE_Y
                || view.getView().getTranslateY() + CardModel.CARD_H / 2 > ZONE_Y + ZONE_H;
    }
    
    public static void updateGapDone(View v) {
        for (int i=0; i < v.getModel().getMyDeckSize(); i++) {
            removeListeners(v.getCardView(v.getModel().getMyCards(i).getName()));
        }
        v.getController().doNextAction();
    }
}
