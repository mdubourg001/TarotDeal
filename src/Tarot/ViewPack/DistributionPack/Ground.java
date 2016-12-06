package Tarot.ViewPack.DistributionPack;

import Tarot.ModelPack.Model;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;

public class Ground extends Group{
    private static final double DISTRIBUTION_AREA_SHIFT = Model.SCREEN_W/2;
	
	private MeshView view;
	
	/*Occupe une grande zone dans le group pour eviter qu'il se redimensionne apres
    en deplacant des elements*/
    private Rectangle distributionArea = new Rectangle(-DISTRIBUTION_AREA_SHIFT, -DISTRIBUTION_AREA_SHIFT, 
    		Model.SCREEN_W+2*DISTRIBUTION_AREA_SHIFT, Model.SCREEN_H + 2*DISTRIBUTION_AREA_SHIFT);
	
	public Ground(){
        TriangleMesh mesh = new TriangleMesh();

        mesh.getPoints().addAll(
                0, 0, 2.1f,
                (float)Model.SCREEN_W, 0, 2.1f,
                0, (float)Model.SCREEN_H, 2.1f,
                (float)Model.SCREEN_W, (float)Model.SCREEN_H, 2.1f
        );
        mesh.getTexCoords().addAll(
                0f, 0f,
                1f, 0f,
                0f, 1f,
                1f, 1f
        );
        mesh.getFaces().addAll(
                0, 0, 3, 3, 1, 1,
                3, 3, 0, 0, 2, 2
        );

        Image groundTexture = new Image("file:./res/ground_tarot.png");
        PhongMaterial groundMaterial = new PhongMaterial();
        groundMaterial.setDiffuseMap(groundTexture);

        view = new MeshView(mesh);
        view.setMaterial(groundMaterial);
        
        this.getChildren().add(view);
        
    	CardsZone[] zones = new CardsZone[]{
    			new CardsZone("Chien", Model.CHIEN_X-5, Model.CHIEN_Y-5, Model.CHIEN_W+10, Model.CHIEN_H+10),
    			new CardsZone("Your Cards", Model.MY_CARDS_X-5, Model.MY_CARDS_Y-5, Model.MY_CARDS_W+10, Model.MY_CARDS_H+10),
    			new CardsZone("Gap", Model.GAP_X-5, Model.GAP_Y-5, Model.GAP_W+10, Model.GAP_H+10)
    					};
    	for(CardsZone zone : zones){
    		this.getChildren().add(zone.getZone());
    		this.getChildren().add(zone.getLab());
    	}
    	
    	distributionArea.setTranslateZ(3);
    	this.getChildren().add(distributionArea);
    	
    }
	
	public void resize(double rotate, double shiftZ){
		view.setScaleX(1 + shiftZ/1330 - rotate/100);
		view.setScaleY(1 + shiftZ/1330 - rotate/100);
	}
}
