package Tarot.ViewPack;

import java.util.HashMap;

import Tarot.Controller;
import Tarot.ModelPack.CardModel;
import Tarot.ModelPack.Model;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;

/*Regroupe toutes les fonctions permettant de realiser le glisser-deposer pour faire l'ecart.*/
public class GapManager {
	public static final double ZONE_X = Model.GAP_X - 40;
    public static final double ZONE_Y = Model.GAP_Y - 40;
    public static final double ZONE_W = 2 * CardModel.CARD_W + Model.DIST_CARD_X_SHIFT + 80;
    public static final double ZONE_H = 3 * (CardModel.CARD_H + Model.DIST_CARD_Y_SHIFT) + 80;
    
    private static  CardView viewSelected = null;
    private static  double selectedCardXSave = 0;
    private static  double selectedCardYSave = 0;

    public static EventHandler<ActionEvent> doGap(View v, Model m, Controller c, HashMap<String, CardView> cardViews) {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                for (CardModel card : m.getMyCards()) {
                    updateGapableCard(v, m, c, card, cardViews);
                }
                for (CardModel card : m.getChienCards()) {
                    updateGapableCard(v, m, c, card, cardViews);
                }
                c.doNextAction();
            }
        };
    }
    
    private static void updateGapableCard(View v, Model m, Controller c, CardModel card, HashMap<String, CardView> cardViews){
    	cardViews.get(card.getName()).getView().setOnMousePressed(selectCardEvent(cardViews.get(card.getName())));
    	cardViews.get(card.getName()).getView().setOnMouseDragged(followMouseEvent(cardViews.get(card.getName())));
    	cardViews.get(card.getName()).getView().setOnMouseReleased(tryAddCardToGapEvent(v, m, c, cardViews.get(card.getName()), card));
    }

    private static EventHandler<MouseEvent> selectCardEvent(CardView view) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	if(view.canBeSelected()){
                    selectCardEvent(view, event);
            	}
            }
        };
    }

    private static void selectCardEvent(CardView view, MouseEvent event){
        selectedCardXSave = (int) view.getView().getTranslateX();
        selectedCardYSave = (int) view.getView().getTranslateY();
        riseCard(view, event);
        viewSelected = view;
        view.canBeSelected(false);
    }

    private static void riseCard(CardView view, MouseEvent event){
        view.getView().setRotationAxis(Rotate.X_AXIS);
        view.getView().setRotate(-View.DISTRIBUTION_GROUP_ROTATE);
        view.getView().setTranslateZ(-1 + CardModel.CARD_H * View.DISTRIBUTION_GROUP_ROTATE/90);
        followMouse(view, event);
    }

    private static EventHandler<MouseEvent> followMouseEvent(CardView view) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	if(viewSelected == view){
                    followMouse(view, event);
            	}
            }
        };
    }

    private static void followMouse(CardView view, MouseEvent event){
    	/*Formule inexacte qui essaye de garder la carte au niveau de la souris, lors du glisser deposer,
    	en prenant en compte l'inclinaison de la camera et le dezoom. Ci ces 2 valeurs sont a 0 le centre
    	de la carte est toujours au niveau de la souris.*/
        view.getView().setTranslateX((event.getSceneX() - CardModel.CARD_W / 2)
        		+ 0.0004*(event.getSceneX()-Model.SCREEN_W/2)*View.DISTRIBUTION_GROUP_SHIFT_Z
        		+ 0.002*(event.getSceneX()-Model.SCREEN_W/2)*(-View.DISTRIBUTION_GROUP_ROTATE)*(1-event.getSceneY()/(0.2*Model.SCREEN_H)));
        view.getView().setTranslateY((event.getSceneY() - CardModel.CARD_H / 2)
        		+ 0.0004*(event.getSceneY()-Model.SCREEN_H/2)*View.DISTRIBUTION_GROUP_SHIFT_Z
        		+ 0.004*event.getSceneY()*(-View.DISTRIBUTION_GROUP_ROTATE));
    }

    private static EventHandler<MouseEvent> tryAddCardToGapEvent(View v, Model m, Controller c, CardView cardView, CardModel card){
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tryAddCardToGap(v, m, c, cardView, card);
            }
        };
    }

    private static void tryAddCardToGap(View v, Model m, Controller c, CardView cardView, CardModel card){
        if(viewSelected == cardView){
        	viewSelected = null;
            if (m.ungapableCards().contains(card.getName()) || GapManager.cardViewInGap(cardView)) {
                v.moveCard(cardView, selectedCardXSave, selectedCardYSave, card.getZ(), 0.0, canSelectView(cardView));
            }
            else{
                c.addCardToGap(card);
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
    
    public static void updateGapDone(Model m, Controller c, HashMap<String, CardView> cardViews) {
        for (CardModel card : m.getMyCards()) {
            removeListeners(cardViews.get(card.getName()));
        }
        c.doNextAction();
    }
}
