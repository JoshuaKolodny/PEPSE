package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.constants.Constants;

import java.awt.*;
import java.util.function.Consumer;

public class SunHalo {
    private static final Vector2 SIZE = Vector2.ONES.mult(100);
    private static final Color COLOR = new Color(255,255,0,20);


    public static GameObject create(GameObject sun) {
        OvalRenderable ovalRenderable = new OvalRenderable(COLOR);
        GameObject sunHalo = new GameObject(sun.getTopLeftCorner(), SIZE, ovalRenderable);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(Constants.SUN_HALO_TAG);
        return sunHalo;
    }
}
