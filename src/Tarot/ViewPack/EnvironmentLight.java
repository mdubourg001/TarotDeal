package Tarot.ViewPack;

import javafx.geometry.Point3D;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;

public class EnvironmentLight extends PointLight {
	public EnvironmentLight(Color c, Point3D pos){
        super(c);
        setTranslateX(pos.getX());
        setTranslateY(pos.getY());
        setTranslateZ(pos.getZ());
    }
}
