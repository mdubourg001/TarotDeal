package Tarot.ViewPack;

import Tarot.ModelPack.Model;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class Ground{
	private MeshView view;
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

    }
	
	public void resize(){
		view.setScaleX(1 + View.DISTRIBUTION_GROUP_SHIFT_Z/1330 - View.DISTRIBUTION_GROUP_ROTATE/100);
		view.setScaleY(1 + View.DISTRIBUTION_GROUP_SHIFT_Z/1330 - View.DISTRIBUTION_GROUP_ROTATE/100);
	}
	
	public MeshView getView(){
		return view;
	}
}
