package Tarot;

import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class CardView {
	//public static final double SHIFT = 0.5;
	
    /*private static Image back = new Image("file:./res/back.jpg");

    private Image image;
    private ImageView backIV;
    private ImageView frontIV;*/

    //Mesh
    private static Image cardsTexture = new Image("file:./res/textureCards.jpg");

    private TriangleMesh mesh = new TriangleMesh();
    private MeshView meshView = new MeshView(mesh);

    PhongMaterial cardMaterial = new PhongMaterial();

    public CardView(CardModel card) {
        /*this.image = new Image("file:./" + card.getPath());

        this.backIV = new ImageView(this.back);
        this.backIV.setFitWidth(CardModel.CARD_W);
        this.backIV.setFitHeight(CardModel.CARD_H);
        this.backIV.setTranslateX(card.getX());
        this.backIV.setTranslateY(card.getY());
        this.backIV.setTranslateZ(card.getZ());
        this.backIV.setRotationAxis(new Point3D(0, 1, 0));
        this.backIV.setRotate(180);

        this.frontIV = new ImageView(this.image);
        this.frontIV.setFitWidth(CardModel.CARD_W);
        this.frontIV.setFitHeight(CardModel.CARD_H);
        this.frontIV.setTranslateX(card.getX());
        this.frontIV.setTranslateY(card.getY());
        this.frontIV.setTranslateZ(card.getZ() + SHIFT);
        this.frontIV.setRotationAxis(new Point3D(0, 1, 0));
        this.frontIV.setRotate(180);*/

        //Mesh

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

        meshView.setDrawMode(DrawMode.FILL);
        meshView.setMaterial(cardMaterial);
        meshView.setTranslateX(card.getX());
        meshView.setTranslateY(card.getY());
        meshView.setTranslateZ(card.getZ());
        meshView.setRotationAxis(new Point3D(0, 1, 0));
        meshView.setRotate(180);
    }

    /*
    public ImageView getBack() {
        return backIV;
    }

    public ImageView getFront() {

        return frontIV;
    }

    public Image getImage() {

        return image;
    }
    */

    //Mesh

    public MeshView getMeshView(){
        return meshView;
    }
}
