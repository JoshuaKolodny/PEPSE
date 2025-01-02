package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.constants.Constants;

import java.awt.*;

public class Night {
    private static final Float MIDDAY_OPACITY = 0f;
    private static final Float MIDNIGHT_OPACITY = 0.5f;


    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        RectangleRenderable renderable = new RectangleRenderable(Color.black);
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions, renderable);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(Constants.NIGHT_TAG);
        new Transition<Float>(night, night.renderer()::setOpaqueness, MIDDAY_OPACITY,
                MIDNIGHT_OPACITY, Transition.CUBIC_INTERPOLATOR_FLOAT, Constants.HALF_CYCLE_LENGTH,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        return night;
    }
}
