package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.constants.Constants;

import java.awt.*;

/**
 * Represents the night-time overlay in the game world.
 *
 * <p>The {@code Night} class creates a black rectangle that transitions between
 * a transparent state at midday and a semi-transparent state at midnight. This
 * transition simulates the cycle of day and night in the game environment.</p>
 *
 * <p>It uses a {@link Transition} to animate its opacity.</p>
 *
 * <p>The static {@link #create(Vector2, float)} method serves as a factory method
 * to set up the night overlay in your game world.</p>
 *
 * @author Joshua Kolodny, Itamar Lev Ari
 *
 */
public class Night {
    private static final Float MIDDAY_OPACITY = 0f;
    private static final Float MIDNIGHT_OPACITY = 0.5f;

    /**
     * Creates a night overlay {@link GameObject} that transitions its opacity over a specified cycle length.
     *
     * @param windowDimensions the dimensions of the game window
     * @param cycleLength the duration (in seconds or frames, depending on the engine) for a full day-night cycle
     * @return a {@link GameObject} representing the night overlay
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        RectangleRenderable renderable = new RectangleRenderable(Color.black);
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions, renderable);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        new Transition<>(
                night,
                night.renderer()::setOpaqueness,
                MIDDAY_OPACITY,
                MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength / 2,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
        return night;
    }
}
