package Tarot.ViewPack;

import Tarot.ModelPack.Model;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;

public class LightManager {
    public static final double MOON_LIGHT_Z = -1500;
    public static final double LAMPS_HOOK_Z = -600;
    public static final double LAMP_Z = -350;
    public static final double LAMP_SHINING_Z = -300;
    public static final double LAMPS_SPEED = 1;
    private static final double PROJECTOR_SHIFT = 50;
    
    private static EnvironmentLight projector;
    
    public static void addLights(Group group){
    	projector  = new EnvironmentLight(Color.WHITE,
        		new Point3D(Model.SCREEN_W/2 + PROJECTOR_SHIFT,
        				Model.SCREEN_H/2 - 27*group.getRotate() + PROJECTOR_SHIFT,
        				-1400 - group.getTranslateZ()));
    	
        PointLight moonLight = new EnvironmentLight(Color.DARKBLUE, new Point3D(0, 0, MOON_LIGHT_Z));
        PointLight lampLight = new LampLight(Color.LIGHTBLUE, 
        		new Point3D(Model.SCREEN_W/2, Model.SCREEN_H/2, LAMP_Z), LAMPS_HOOK_Z, LAMPS_SPEED);
        PointLight lampLightShining = new LampLight(Color.WHITE, 
        		new Point3D(Model.SCREEN_W/2, Model.SCREEN_H/2, LAMP_SHINING_Z), LAMPS_HOOK_Z, LAMPS_SPEED);
        
        projector.setLightOn(false);

       group.getChildren().addAll(projector, moonLight, lampLight, lampLightShining);
    }
    
    public static void turnOnProjector(boolean b){
    	projector.setLightOn(b);
    }
}
