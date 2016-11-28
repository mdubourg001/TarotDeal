package Tarot;

import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class CardView {
    private static Image cardsTexture = new Image("file:./res/textureCards.jpg");
    private boolean canBeSelected = true;

    private MeshView meshView = new MeshView();

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
        		texX*0.1f, texY*0.125f,
        		(texX+1)*0.1f, texY*0.125f,
                texX*0.1f, (texY+1)*0.125f,
                (texX+1)*0.1f, (texY+1)*0.125f, //Front (depend of card order)

                0.8f, 0.875f,
                0.9f, 0.875f,
                0.8f, 1.0f,
                0.9f, 1.0f  //Back
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
}
