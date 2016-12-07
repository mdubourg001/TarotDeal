package Tarot.ViewPack.DistributionPack;

import java.util.ArrayList;
import java.util.Observable;

import Tarot.Controller;
import Tarot.ModelPack.CardModel;
import Tarot.ModelPack.Model;
import Tarot.ModelPack.TarotAction;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.util.Pair;

/*Regroupe toutes les fonctions permettant de distribuer les cartes depuis le Jeu.*/
public class DistributionManager {
	
    private static final double TIME_BETWEEN_DISTRIBUTIONS = 0.2; //TODO Remettre a 0.2
    
    @SuppressWarnings({ "unchecked", "incomplete-switch" })
    public static void update(View view, Observable arg0, Object arg1) {
        switch (((Pair<TarotAction, Object>) arg1).getKey()) {
            case CARDS_DISTRIBUTED:
                update3CardsDistributed(view, ((Pair<TarotAction, CardModel[]>) arg1).getValue());
                break;
            case CARD_MOVED_TO_CHIEN:
                moveCardFromJeu(view, view.getCardView(((Pair<TarotAction, CardModel>) arg1).getValue().getName()),
                        ((Pair<TarotAction, CardModel>) arg1).getValue(), null);
                break;
        }
    }
    
    public static void update3CardsDistributed(View view, CardModel[] arg) {
    	moveCardFromJeu(view, view.getCardView(arg[0].getName()), arg[0], null);
    	moveCardFromJeu(view, view.getCardView(arg[1].getName()), arg[1], null);
    	moveCardFromJeu(view, view.getCardView(arg[2].getName()), arg[2], null);
    	
        view.waiter(TIME_BETWEEN_DISTRIBUTIONS, continueCardDistribution(view.getController()));
    }

    private static  EventHandler<ActionEvent> continueCardDistribution(Controller controller) {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                controller.doNextAction();
            }
        };
    }
	
	/*Attend que les cartes soit totalement sortient du cercle forme par la diagonale du Jeu
    avant de descendre (en z). Evite que les cartes passe au travers du deck.*/
    public static void moveCardFromJeu(View view, CardView cardView, CardModel card, EventHandler<ActionEvent> onFinished2) {
        Point2D firstDest = calculFirstDest(cardView, card);

        EventHandler<ActionEvent> onFinished1 = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                view.moveCard(cardView, card, onFinished2);
            }
        };
        view.moveCard(cardView, firstDest.getX(), firstDest.getY(), cardView.getView().getTranslateZ(), onFinished1);
    }

    private static Point2D calculFirstDest(CardView cardView, CardModel card) {
        Point2D viewP = new Point2D(cardView.getView().getTranslateX(), cardView.getView().getTranslateY());
        Point2D cardP = new Point2D(card.getX(), card.getY());

        if ((viewP.getX() - cardP.getX()) != 0) {
            double a = calculCoef(viewP, cardP);
            if (a != 0) {
                double b = viewP.getY() - a * viewP.getX();
                if (cardP.getX() - viewP.getX() < 0)
                    return getIntersectionsLigneCircle(a, b, viewP.getX(), viewP.getY(), CardModel.CARD_DIAG).get(0);
                else
                    return getIntersectionsLigneCircle(a, b, viewP.getX(), viewP.getY(), CardModel.CARD_DIAG).get(1);
            } else {
                if (cardP.getX() - viewP.getX() > 0) {
                    return new Point2D(viewP.getX() + CardModel.CARD_DIAG, viewP.getY());
                } else {
                    return new Point2D(viewP.getX() - CardModel.CARD_DIAG, viewP.getY());
                }
            }
        } else {
            if (cardP.getY() - viewP.getY() > 0) {
                return new Point2D(viewP.getX(), Model.DECK_Y + CardModel.CARD_DIAG);
            } else {
                return new Point2D(viewP.getX(), Model.DECK_Y - CardModel.CARD_DIAG);
            }
        }
    }

    private static double calculCoef(Point2D a, Point2D b) {
        return (b.getY() - a.getY()) / (b.getX() - a.getX());
    }

    private static ArrayList<Point2D> getIntersectionsLigneCircle(double a, double b, double cx, double cy, double r) {
        ArrayList<Point2D> intersections = new ArrayList<Point2D>();

        double A = 1 + a * a;
        double B = 2 * (-cx + a * b - a * cy);
        double C = cx * cx + cy * cy + b * b - 2 * b * cy - r * r;
        double delta = B * B - 4 * A * C;

        if (delta > 0) {
            double x = (-B - (float) Math.sqrt(delta)) / (2 * A);
            double y = a * x + b;
            intersections.add(new Point2D(x, y));

            x = (-B + (float) Math.sqrt(delta)) / (2 * A);
            y = a * x + b;
            intersections.add(new Point2D(x, y));
        } else if (delta == 0) {
            double x = -B / (2 * A);
            double y = a * x + b;

            intersections.add(new Point2D(x, y));
        }
        return intersections;
    }
}
