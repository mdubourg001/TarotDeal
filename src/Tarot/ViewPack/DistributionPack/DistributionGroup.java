package Tarot.ViewPack.DistributionPack;

import javafx.geometry.Point3D;
import javafx.scene.Group;

public class DistributionGroup extends Group {
	
	private Ground ground = new Ground();
	
	public DistributionGroup(){
		setRotationAxis(new Point3D(1, 0, 0));
		getChildren().add(ground);
	}
	
	public void updateRotateAndZoom(double rotate, double shiftZ) {
        setRotate(rotate);
        setTranslateY(15*rotate);
        setTranslateZ(shiftZ);
        
        ground.resize(getRotate(), getTranslateZ());
    }
	
	public void nouvelleDonne(){
		getChildren().clear();
		getChildren().add(ground);
	}
}
