package Tarot;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class View implements Observer{
	public final static int CAMERA_SHIFT_Y = -300;
	public final static int CAMERA_ROTATE = 20;
	public final static int CAMERA_SHIFT_Z = 200;

	private Controller controller;
	private Model model;

	private Group group = new Group();
	private Scene scene = new Scene(group, Model.SCREEN_W, Model.SCREEN_H, true, SceneAntialiasing.BALANCED);
	private PerspectiveCamera camera = new PerspectiveCamera();
	
	private Map<String, CardView> cardViews = new HashMap<String, CardView>();

	private Rectangle ground = new Rectangle(0, 0, Model.SCREEN_W, Model.SCREEN_H - CAMERA_SHIFT_Y);

	public View(Controller controller){
		this.controller = controller;
		this.model = controller.getModel();
		
		group.setDepthTest(DepthTest.ENABLE); 
		
		if(!Platform.isSupported(ConditionalFeature.SCENE3D)){
			throw new RuntimeException("SCENE3D not supported");
		}

		scene.setFill(Color.BLACK);
		scene.setCamera(camera);
		camera.setRotationAxis(new Point3D(1,0,0));
		camera.setRotate(CAMERA_ROTATE);
		camera.setTranslateZ(CAMERA_SHIFT_Y);
		camera.setTranslateY(CAMERA_SHIFT_Z);

		ground.setFill(Color.GREEN);
		ground.setTranslateZ(2.1);
		group.getChildren().add(ground);
	}

	public Scene getScene(){
		return scene;
	}
	
	private void waiter(double duration, EventHandler<ActionEvent> event){
		Timeline timeLine = new Timeline();
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(duration), event);
		timeLine.getKeyFrames().add(keyFrame);
		timeLine.play();
	}
	
	private int currentAction = -1;
	public void doNextAction(){
		currentAction++;
		switch(currentAction){
		case 0 :
			model.mixDeck();
			break;
		case 1 :
			model.cutDeck();
			break;
		case 2 :
			model.distributeCards();
			break;
		case 3 :
			model.revertPlayer();
			break;
		case 4 :
			model.organizePlayerCards();
			break;
		case 5 :
			model.detectPetitSec();
			break;
		case 6 :
			model.revertChien();
			break;
		case 7 :
			drawChoseButtons();
			break;
		case 8 :
			model.organizePlayerCards();//Only if it use gap
			break;
		case 9 :
			//continue
			break;
		}
	}
	
	public EventHandler<ActionEvent> doNextActionEvent(){
		return new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				doNextAction();
			}
		};
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		switch(((Pair<TarotAction, Object>)arg1).getKey()){
			case DECK_MIXED:
				updateDeckMixed();
				break;
			case DECK_CUT:
				updateDeckCut(((Pair<TarotAction, Integer>)arg1).getValue(), 1);
				break;
			case CARDS_DISTRIBUTED :
				update3CardsDistributed(((Pair<TarotAction, Pair<Boolean, CardModel[]>>)arg1).getValue());
				break;
			case CARD_MOVED_FROM_DECK :
				moveCardFromDeck(cardViews.get(((Pair<TarotAction, CardModel>)arg1).getValue().getName()), 
						((Pair<TarotAction, CardModel>)arg1).getValue(), null);
				break;
			case CARD_MOVED :
				moveCard(cardViews.get(((Pair<TarotAction, CardModel>)arg1).getValue().getName()), 
						((Pair<TarotAction, CardModel>)arg1).getValue(), null);
				break;
			case PLAYER_REVERTED :
				revertDeck(model.getMyCards(), Model.NB_CARDS_PLAYER);
				break;
			case PLAYER_ORGANIZED :
				updatePlayerCardsOrganized();
				break;
			case PETIT_SEC_DETECTED :
				updatePetitSec(((Pair<TarotAction, Boolean>)arg1).getValue());
				break;
			case ACTION_CHOSEN :
				updateActionChosen(((Pair<TarotAction, PlayerAction>)arg1).getValue());
				break;
			case CHIEN_REVERTED :
				revertDeck(model.getChienCards(), Model.CHIEN_SIZE);
				break;
			case GAP_DONE :
				updateGapDone();
				break;
		}
	}
	
	public void updateDeckMixed() {
		for(CardModel card : model.getDeckCards()){
			cardViews.put(card.getName(), new CardView(card.getPath(), card.getX(), card.getY(), card.getZ()));
			group.getChildren().add(cardViews.get(card.getName()).getBack());
			group.getChildren().add(cardViews.get(card.getName()).getFront());
		}
		doNextAction();
	}
	
	public void updateDeckCut(Integer indexHalf, int iteration){
		EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				updateDeckCut(indexHalf, iteration+1);
			}
		};
		switch(iteration){
		case 1:
			moveCutDeck(CardModel.CARD_W/2 + 10, false, indexHalf, onFinished);
			break;
		case 2:
			moveCutDeck(CardModel.CARD_W/2 + 10, true, indexHalf, onFinished);
			break;
		case 3:
			moveCutDeck(0, true, indexHalf, doNextActionEvent());
			break;
		}
	}
	
	private void moveCutDeck(double xShiftValue, boolean trueZ, Integer indexHalf, EventHandler<ActionEvent> onFinished){
		CardModel card;
		double xShift;
		double z;
		for(int i = 0; i < model.getDeckCards().size(); i++){
			card = model.getDeckCards().get(i);
			if(i < indexHalf){
				xShift = - xShiftValue;
			}
			else{
				xShift = xShiftValue;
			}
			
			if(trueZ){
				z = card.getZ();
			}
			else{
				z = cardViews.get(card.getName()).getFront().getTranslateZ();
			}
			
			if(i != model.getDeckCards().size()-1){
				moveCard(cardViews.get(card.getName()), card.getX() + xShift, card.getY(), z, 0.2, false, null);
			}
			else{
				moveCard(cardViews.get(card.getName()), card.getX() + xShift, card.getY(), z, 0.2, false, onFinished);
			}
		}
	}
	
	private int currentPlayer = -1;
	public void update3CardsDistributed(Pair<Boolean, CardModel[]> arg) {
		currentPlayer++;
		
		EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				if(!arg.getKey()){
					model.distributeCards();
				}
				else{
					doNextAction();
				}
			}
		};
		
		if(currentPlayer%4 == 0){
			moveCardFromDeck(cardViews.get(arg.getValue()[0].getName()), arg.getValue()[0], null);
			moveCardFromDeck(cardViews.get(arg.getValue()[1].getName()), arg.getValue()[1], null);
			moveCardFromDeck(cardViews.get(arg.getValue()[2].getName()), arg.getValue()[2], onFinished);
		}
		else{
			moveCardXY(cardViews.get(arg.getValue()[0].getName()), arg.getValue()[0], null);
			moveCardXY(cardViews.get(arg.getValue()[1].getName()), arg.getValue()[1], null);
			moveCardXY(cardViews.get(arg.getValue()[2].getName()), arg.getValue()[2], onFinished);
		}
	}
	
	public void updateGapDone() {
		for(CardModel card : model.getMyCards()){
			removeListeners(cardViews.get(card.getName()));
		}
	}
	
	private void moveCard(CardView cardView, CardModel card, EventHandler<ActionEvent> onFinished) {
		moveCard(cardView, card.getX(), card.getY(), card.getZ(), card.onFront, onFinished);
	}
	
	private void moveCardXY(CardView cardView, CardModel card, EventHandler<ActionEvent> onFinished) {
		moveCard(cardView, card.getX(), card.getY(), cardView.getBack().getTranslateZ(), card.onFront, onFinished);
	}
	
	private void moveCard(CardView cardView, CardModel card, double speed, EventHandler<ActionEvent> onFinished) {
		moveCard(cardView, card.getX(), card.getY(), card.getZ(), speed, card.onFront, onFinished);
	}
	
	private void moveCard(CardView cardView, double x, double y, double z, boolean onFront, EventHandler<ActionEvent> onFinished) {
		moveCard(cardView, x, y, z, 1, onFront, onFinished);
	}
	
	private void moveCard(CardView cardView, double x, double y, double z, double speed, boolean onFront, EventHandler<ActionEvent> onFinished) {
		double time = calculTime(new double[]{Math.abs(cardView.getBack().getX() - x), 
				Math.abs(cardView.getBack().getY() - y),
				Math.abs(cardView.getBack().getTranslateZ() - z)}, speed);
		
		if(!onFront){
			moveImageView(cardView.getBack(), x, y, z, time, null);
			moveImageView(cardView.getFront(), x, y, z+0.5, time, onFinished);
		}
		else{
			moveImageView(cardView.getBack(), x, y, z+0.5, time, null);
			moveImageView(cardView.getFront(), x, y, z, time, onFinished);
		}
	}
	
	private void moveImageView(ImageView view, double x, double y, double z, double time, EventHandler<ActionEvent> onFinished){
		Timeline animationMoveCard = new Timeline();

		KeyValue kVMoveX = new KeyValue(view.xProperty(), x);
		KeyValue kVMoveY = new KeyValue(view.yProperty(), y);
		KeyValue kVMoveZ = new KeyValue(view.translateZProperty(), z);
		
		Duration duration = Duration.seconds(time);

		KeyFrame keyFrame = new KeyFrame(duration, onFinished, kVMoveX, kVMoveY, kVMoveZ);
		animationMoveCard.getKeyFrames().add(keyFrame);

		animationMoveCard.play();
	}
	
	private double calculTime(double[] deltas, double speed){
		double time = 0;
		for(double d : deltas){
			time += d;
		}
		time /= (2000*speed); // TODO je sais plus
		return time;
	}
	
	private void moveCardFromDeck(CardView cardView, CardModel card, EventHandler<ActionEvent> onFinished2){
		Point2D firstDest = calculFirstDest(cardView, card);
		
		EventHandler<ActionEvent> onFinished1 = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				moveCard(cardView, card, onFinished2);
			}
		};
		moveCard(cardView, firstDest.getX(), firstDest.getY(), cardView.getBack().getTranslateZ(), card.onFront, onFinished1);
	}
	
	private Point2D calculFirstDest(CardView cardView, CardModel card){
		Point2D viewP = new Point2D(cardView.getBack().getX(), cardView.getBack().getY());
		Point2D cardP = new Point2D(card.getX(), card.getY());
		
		if((viewP.getX() - cardP.getX()) != 0){
			double a = calculCoef(viewP, cardP);
			if(a != 0){
				double b = viewP.getY() - a*viewP.getX();
				if(cardP.getX() - viewP.getX() < 0)
					return getIntersectionsLigneCircle(a, b, viewP.getX(), viewP.getY(), CardModel.CARD_DIAG).get(0);
				else
					return getIntersectionsLigneCircle(a, b, viewP.getX(), viewP.getY(), CardModel.CARD_DIAG).get(1);
			}
			else{
				return new Point2D(viewP.getX(), viewP.getY()-CardModel.CARD_DIAG);
			}
		}
		else{
			return new Point2D(viewP.getX(), Model.DECK_Y + CardModel.CARD_H);
		}
	}
	
	private double calculCoef(Point2D a, Point2D b){
		return (b.getY() - a.getY())/(b.getX() - a.getX());
	}
	
	private ArrayList<Point2D> getIntersectionsLigneCircle(double a, double b, double cx, double cy, double r){
		ArrayList<Point2D> intersections = new ArrayList<Point2D>();
		
		double A = 1 + a * a;
		double B = 2 * (-cx + a * b - a * cy);
		double C = cx * cx + cy * cy + b * b - 2 * b * cy - r * r;
		double delta = B * B - 4 * A * C;
	 
	    if (delta > 0)
	    {
	    	double x = (-B - (float)Math.sqrt(delta)) / (2 * A);
	    	double y = a * x + b;
	    	intersections.add(new Point2D(x, y));
	                 
	        x = (-B + (float)Math.sqrt(delta)) / (2 * A);
	        y = a * x + b;
	        intersections.add(new Point2D(x, y));
	    }
	    else if (delta == 0)
	    {
	    	double x = -B / (2 * A);
	    	double y = a * x + b;
	 
	    	intersections.add(new Point2D(x, y));
	    }	
		return intersections;
	}
	
	public void revertDeck(ArrayList<CardModel> deck, int size) {
		int i = 1;
		for(CardModel card : deck){
			waiter(0.1*i, new EventHandler<ActionEvent>() {
				public void handle(ActionEvent t) {
					if(deck.indexOf(card) != size-1)
						revertCard(cardViews.get(card.getName()), null);
					else{
						revertCard(cardViews.get(card.getName()), doNextActionEvent());
					}
				}
			});
			i++;
		}
	}

	private final static double RETURN_CARD_DURATION = 0.6; // TODO REMETTRE A 0.6
	private void revertCard(CardView cardView, EventHandler<ActionEvent> onFinished){
		revertCardMove(cardView, true);
		
		Timeline revertPlayerAnimation = new Timeline();
		
		KeyValue kVRevertB = new KeyValue(cardView.getBack().rotateProperty(), 360);
		KeyValue kVRevertF = new KeyValue(cardView.getFront().rotateProperty(), 360);
		
		KeyFrame revertPlayerKeyFrame = new KeyFrame(Duration.seconds(RETURN_CARD_DURATION), onFinished, kVRevertB, kVRevertF);
		
		revertPlayerAnimation.getKeyFrames().add(revertPlayerKeyFrame);
		
		revertPlayerAnimation.play();
	}
	
	private void revertCardMove(CardView cardView, boolean pursue){
		Timeline moveXAnimation = new Timeline();
		
		double shiftX ,shiftZF ,shiftZB;
		
		if(pursue){
			shiftX = 0.1;
			shiftZF = shiftZB = -300;
		}
		else{
			shiftX = -0.1;
			shiftZF = 1;
			shiftZB = 1.1;
		}
		
		KeyValue kVMoveXB = new KeyValue(cardView.getBack().xProperty(), cardView.getBack().getX()-shiftX);
		KeyValue kVMoveXF = new KeyValue(cardView.getFront().xProperty(), cardView.getFront().getX()+shiftX);
		
		KeyValue kVMoveZB = new KeyValue(cardView.getBack().translateZProperty(), shiftZB);
		KeyValue kVMoveZF = new KeyValue(cardView.getFront().translateZProperty(), shiftZF);
		
		EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				if(pursue)
					revertCardMove(cardView, false);
			}
		};
		
		KeyFrame kFMoveX = new KeyFrame(Duration.seconds(RETURN_CARD_DURATION / 2), onFinished, kVMoveXB, kVMoveXF, kVMoveZB, kVMoveZF);
		
		moveXAnimation.getKeyFrames().add(kFMoveX);
		
		moveXAnimation.play();
	}

	public void updatePlayerCardsOrganized() {
		CardModel card;
		for(int i =0; i<model.getMyCards().size(); i++){
			card = model.getMyCards().get(i);
			moveCard(cardViews.get(card.getName()), card, null);
		}
		waiter(0.5, doNextActionEvent());
	}
	
	public void updatePetitSec(Boolean petitSec) {
		if(!petitSec){
			doNextAction();
		}
		else{
			nouvelleDonne();
		}
	}

	private static final int BUTTON_W = 350;
	private static final int BUTTON_H = 150;
	private static final int BUTTON_X_DIFF = 10;
	public static final int BUTTON_X_START = (Model.SCREEN_W - (5*BUTTON_W + 4*BUTTON_X_DIFF))/2;
	public static final int BUTTON_Y = Model.SCREEN_H/10;
	
	private Button passeBut = new Button("Passe");
	private Button priseBut = new Button("Prise");
	private Button gardeBut = new Button("Garde");
	private Button gardeSansBut = new Button("Garde\nsans chien");
	private Button gardeContreBut = new Button("Garde contre\nle chien");

	private Font buttonsFont = new Font(40);

	public void drawChoseButtons(){
		initButton(passeBut, BUTTON_X_START, BUTTON_Y, -50, PlayerAction.PASSE);
		initButton(priseBut, BUTTON_X_START + (BUTTON_W+BUTTON_X_DIFF), BUTTON_Y, -50, PlayerAction.PRISE);
		initButton(gardeBut, BUTTON_X_START + 2*(BUTTON_W+BUTTON_X_DIFF), BUTTON_Y, -50, PlayerAction.GARDE);
		initButton(gardeSansBut, BUTTON_X_START + 3*(BUTTON_W+BUTTON_X_DIFF), BUTTON_Y, -50, PlayerAction.GARDE_SANS);
		initButton(gardeContreBut, BUTTON_X_START + 4*(BUTTON_W+BUTTON_X_DIFF), BUTTON_Y, -50, PlayerAction.GARDE_CONTRE);
	}

	private void initButton(Button button, int x, int y, int z, PlayerAction action){
		button.setFont(buttonsFont);
		button.setLayoutX(x);
		button.setLayoutY(y);
		button.setTranslateZ(z);
		button.setTextAlignment(TextAlignment.CENTER);
		button.setPrefSize(BUTTON_W, BUTTON_H);
		button.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(50), null)));
		button.setTextFill(Color.WHITE);
		button.setOnMouseEntered(mouseEvent -> changeColorButton(button, true));
		button.setOnMouseExited(mouseEvent -> changeColorButton(button, false));
		button.setRotationAxis(new Point3D(1,0,0));
		button.setRotate(CAMERA_ROTATE);
		button.setOnMouseClicked(mouseEvent -> controller.chooseAction(action));
		group.getChildren().add(button);
	}
	
	private void changeColorButton(Button button, boolean toGray){
		if(toGray){
			button.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(50), null)));
			button.setTextFill(Color.BLACK);
		}
		else{
			button.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(50), null)));
			button.setTextFill(Color.WHITE);
		}
	}

	public static final int ECART_ZONE_X = Model.GAP_X1-40;
	public static final int ECART_ZONE_Y = Model.GAP_Y_START-40;
	public static final int ECART_ZONE_W = 2 * CardModel.CARD_W + Model.DIST_CARD_X_DIFF + 80;
	public static final int ECART_ZONE_H = 3 * (CardModel.CARD_H + Model.DIST_CARD_Y_DIFF) + 80;

	private Rectangle ecartArea = new Rectangle(ECART_ZONE_X, ECART_ZONE_Y, ECART_ZONE_W, ECART_ZONE_H);
	public void updateActionChosen(PlayerAction action) {
		group.getChildren().remove(passeBut);
		group.getChildren().remove(priseBut);
		group.getChildren().remove(gardeBut);
		group.getChildren().remove(gardeSansBut);
		group.getChildren().remove(gardeContreBut);
		
		if(action == PlayerAction.PASSE){
			nouvelleDonne();
		}
		else if(action == PlayerAction.PRISE || action == PlayerAction.GARDE){
			doGap();
			ecartArea.setFill(Color.BLUE);
			ecartArea.setTranslateZ(2);
			group.getChildren().add(ecartArea);
		}
		else{
			currentAction++;
			doNextAction();
		}
	}
	
	private int selectedCardXStart = 0;
	private int selectedCardYStart = 0;
	private void doGap(){
		for(CardModel card : model.getMyCards()){
			cardViews.get(card.getName()).getFront().setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override public void handle(MouseEvent event) {
					riseCard(cardViews.get(card.getName()), -300, true, null);
					selectedCardXStart = (int) card.getX();
					selectedCardYStart = (int) card.getY();
				}
			});
			cardViews.get(card.getName()).getFront().setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override public void handle(MouseEvent event) {
					cardViews.get(card.getName()).getFront().setX(event.getX() - CardModel.CARD_W/2);
					cardViews.get(card.getName()).getFront().setY(event.getY() - CardModel.CARD_H/2);
					
					cardViews.get(card.getName()).getBack().setX(event.getX() - CardModel.CARD_W/2);
					cardViews.get(card.getName()).getBack().setY(event.getY() - CardModel.CARD_H/2);
				}
			});
			cardViews.get(card.getName()).getFront().setOnMouseReleased(new EventHandler<MouseEvent>() {
				@Override public void handle(MouseEvent event) {
					if(model.ungapableCards().contains(card.getName())
							|| cardViews.get(card.getName()).getFront().getX() + CardModel.CARD_W/2 < ECART_ZONE_X
							|| cardViews.get(card.getName()).getFront().getX() + CardModel.CARD_W/2 > ECART_ZONE_X + ECART_ZONE_W
							|| cardViews.get(card.getName()).getFront().getY() + CardModel.CARD_H/2 < ECART_ZONE_Y
							|| cardViews.get(card.getName()).getFront().getY() + CardModel.CARD_H/2 > ECART_ZONE_Y + ECART_ZONE_H){
						moveCard(cardViews.get(card.getName()), selectedCardXStart, selectedCardYStart, card.getZ(), true, null);
					}
					else{
						controller.addCardToGap(card);
						removeListeners(cardViews.get(card.getName()));
					}
				}
			});
		}
	}
	
	private void riseCard(CardView cardView, double z, boolean onFront, EventHandler<ActionEvent> onFinished){
		riseCard(1, cardView, z, onFront, onFinished);
	}
	
	private void riseCard(int speed, CardView cardView, double z, boolean onFront, EventHandler<ActionEvent> onFinished) {
		Timeline animationMoveCard = new Timeline();
		
		KeyValue kVMoveZCardB;
		KeyValue kVMoveZCardF;
		if(!onFront){
			kVMoveZCardB = new KeyValue(cardView.getBack().translateZProperty(), z);
			kVMoveZCardF = new KeyValue(cardView.getFront().translateZProperty(), z+0.1);
		}
		else{
			kVMoveZCardB = new KeyValue(cardView.getBack().translateZProperty(), z+0.1);
			kVMoveZCardF = new KeyValue(cardView.getFront().translateZProperty(), z);
		}
		
		double delta = Math.abs(cardView.getBack().getTranslateZ() - z);
		delta *= speed;
		
		Duration duration = Duration.seconds(delta/1500);

		KeyFrame keyFrame = new KeyFrame(duration, onFinished, kVMoveZCardB, kVMoveZCardF);
		animationMoveCard.getKeyFrames().add(keyFrame);
		
		animationMoveCard.play();
	}
	
	private void removeListeners(CardView cardView){
		cardView.getFront().setOnMousePressed(null);
		cardView.getFront().setOnMouseDragged(null);
		cardView.getFront().setOnMouseReleased(null);
	}
	
	private void nouvelleDonne(){
		model.nouvelleDonne();
		
		currentAction = -1;
		
		group.getChildren().clear();
		group.getChildren().add(ground);
		
		doNextAction();
	}
}
