package Tarot.ViewPack;

import Tarot.ModelPack.CardModel;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class CardView {
    private static Image cardsTexture = new Image("file:./res/textureCards.jpg");
    private boolean canBeSelected = true;
    private DinosaurType dinosaurType = null;
    
    private static int cardBack = 0;

    private MeshView meshView = new MeshView();

    private static final float TEX_X_COEF = 1.0f/10;
    private static final float TEX_Y_COEF = 1.0f/9;
    public CardView(CardModel card) {
        TriangleMesh mesh = new TriangleMesh();
        PhongMaterial cardMaterial = new PhongMaterial();
        
        mesh.getPoints().addAll(
                0, 0, 0,
                CardModel.CARD_W, 0, 0,
                0, CardModel.CARD_H, 0,
                CardModel.CARD_W, CardModel.CARD_H, 0
        );
        
        int order = card.getOrder();
        float texX = order%10;
        float texY = order/10;

        mesh.getTexCoords().addAll(
        		texX*TEX_X_COEF, texY*TEX_Y_COEF,
        		(texX+1)*TEX_X_COEF, texY*TEX_Y_COEF,
                texX*TEX_X_COEF, (texY+1)*TEX_Y_COEF,
                (texX+1)*TEX_X_COEF, (texY+1)*TEX_Y_COEF, //Front (depend of card order)

                0.0f + 0.1f*cardBack, 8*TEX_Y_COEF,
                0.1f + 0.1f*cardBack, 8*TEX_Y_COEF,
                0.0f + 0.1f*cardBack, 1.0f,
                0.1f + 0.1f*cardBack, 1.0f  //Back
        );

        mesh.getFaces().addAll(
                0, 0, 3, 3, 1, 1,
                3, 3, 0, 0, 2, 2, //Front

                0, 4, 1, 5, 3, 7,
                3, 7, 2, 6, 0, 4  //Back
        );

        cardMaterial.setDiffuseMap(cardsTexture);

        meshView.setMesh(mesh);
        meshView.setDrawMode(DrawMode.FILL);
        meshView.setMaterial(cardMaterial);
        meshView.setTranslateX(card.getX());
        meshView.setTranslateY(card.getY());
        meshView.setTranslateZ(card.getZ());
        meshView.setRotationAxis(new Point3D(0, 1, 0));
        meshView.setRotate(180);
        
        updateDinosaurType(card.getName());
    }
    
    public static int getCardBack(){
    	return cardBack;
    }
    
    public static final int NB_CARD_BACKS = 4;
    public static void changeCardBack(int cardNb){
    	cardBack = cardNb;
    }
    
    public MeshView getView(){
        return meshView;
    }
    
    public boolean canBeSelected(){
        return canBeSelected;
    }
    
    public void canBeSelected(boolean b){
        canBeSelected = b;
    }
    
    public DinosaurType getDinosaurType(){
    	return dinosaurType;
    }
    
    private void updateDinosaurType(String name){
    	switch(name){
    	case "Trump21" :
    		dinosaurType = DinosaurType.LIOPLEURODON;
    		break;
    	case "Trump20" :
    		dinosaurType = DinosaurType.MOSASAURUS;
    		break;
    	case "Trump19" :
    		dinosaurType = DinosaurType.TYRANOSAURUS;
    		break;
    	case "Trump18" :
    		dinosaurType = DinosaurType.BRACHIOSAURUS;
    		break;
    	case "Trump17" :
    		dinosaurType = DinosaurType.PLESIOSAURUS;
    		break;
    	case "Trump16" :
    		dinosaurType = DinosaurType.SPINOSAURUS;
    		break;
    	case "Trump15" :
    		dinosaurType = DinosaurType.DIPLODOCUS;
    		break;
    	case "Trump14" :
    		dinosaurType = DinosaurType.CARNOTAURUS;
    		break;
    	}
    }
}
