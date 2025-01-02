package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.constants.Constants;

import java.awt.*;
import java.util.function.Consumer;

public class Sun {
    private static final Float FACTOR = 0.5f;
    private static final Float FINAL_ANGLE = 360f;
    private static final Float INIT_ANGLE = 0f;
    private static final Vector2 SIZE = Vector2.ONES.mult(50);

    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        Vector2 initialSunLocation = windowDimensions.mult(FACTOR);
        Vector2 cycleCenter = new Vector2(windowDimensions.x() / 2,
                windowDimensions.mult(Constants.INITIAL_GROUND_FACTOR).y());
        OvalRenderable ovalRenderable = new OvalRenderable(Color.YELLOW);
        GameObject sun = new GameObject(initialSunLocation, SIZE, ovalRenderable);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(Constants.SUN_TAG);
        Consumer<Float> lambdaSun = (Float angle) -> sun.setCenter(initialSunLocation.subtract(cycleCenter)
                .rotated(angle).add(cycleCenter));
        new Transition<Float>(sun, lambdaSun, INIT_ANGLE,
                FINAL_ANGLE, Transition.LINEAR_INTERPOLATOR_FLOAT, Constants.CYCLE_LENGTH,
                Transition.TransitionType.TRANSITION_LOOP, null);
        return sun;
    }
}
