package Tarot.ViewPack.DistributionPack;

import Tarot.ViewPack.DistributionPack.EnvironmentLight;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class LampLight extends EnvironmentLight {
	public LampLight(Color c, Point3D pos, double hookZ, double speed){
		super(c, pos);
        getTransforms().add(new Rotate(180, (pos.getZ() - hookZ)*speed, 0, 0, Rotate.Z_AXIS));
        Timeline timeLine = new Timeline();

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(3/speed), new KeyValue(rotateProperty(), 360));

        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.getKeyFrames().addAll(keyFrame);
        timeLine.play();
	}
}
