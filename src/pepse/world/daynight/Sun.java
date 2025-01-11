package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.constants.Constants;

import java.awt.*;
import java.util.function.Consumer;

/**
 * Represents the sun in the game world.
 *
 * <p>The {@code Sun} class creates a circular {@link GameObject} that moves in a circular path
 * (via a {@link Transition}) across the sky to simulate the sun's daily traversal. The path
 * and location are determined by the given window dimensions and the specified cycle length.</p>
 *
 * <p>The static {@link #create(Vector2, float)} method serves as a factory method to place and
 * initialize the sun in your game world.</p>
 *
 * <p>This movement can be customized by modifying the cycle length or the sun's coordinates.</p>
 *
 * @author
 *     Joshua Kolodny, Itamar Lev Ari
 */
public class Sun {
    private static final Float FACTOR = 0.5f;
    private static final Float FINAL_ANGLE = 360f;
    private static final Float INIT_ANGLE = 0f;
    private static final Vector2 SIZE = Vector2.ONES.mult(50);

    /**
     * Creates a sun {@link GameObject} that orbits around the center of the screen.
     *
     * @param windowDimensions the dimensions of the game window
     * @param cycleLength the duration (in seconds or frames, depending on the engine) for a full orbit
     * @return a {@link GameObject} representing the sun
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        Vector2 initialSunLocation = windowDimensions.mult(FACTOR);
        Vector2 cycleCenter = new Vector2(windowDimensions.x() / 2,
                windowDimensions.mult(Constants.INITIAL_GROUND_FACTOR).y());
        OvalRenderable ovalRenderable = new OvalRenderable(Color.YELLOW);
        GameObject sun = new GameObject(initialSunLocation, SIZE, ovalRenderable);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

        Consumer<Float> lambdaSun = (Float angle) -> sun.setCenter(
                initialSunLocation
                        .subtract(cycleCenter)
                        .rotated(angle)
                        .add(cycleCenter)
        );

        new Transition<>(
                sun,
                lambdaSun,
                INIT_ANGLE,
                FINAL_ANGLE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null
        );
        return sun;
    }
}
