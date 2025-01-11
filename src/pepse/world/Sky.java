package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the sky as a large rectangle in camera coordinates.
 * <p>Serves as the backdrop for the game world.</p>
 *
 * <p>Uses the provided window dimensions to fill the entire screen.</p>
 *
 * <p>Returns a {@link GameObject} that can be added to the game objects collection.</p>
 *
 * @author
 *     Joshua Kolodny, Itamar Lev Ari
 */
public class Sky {
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

    /**
     * Factory method to create a sky object spanning the entire screen.
     *
     * @param windowDimensions The full dimensions of the window.
     * @return A {@link GameObject} representing the sky.
     */
    public static GameObject create(Vector2 windowDimensions){
        GameObject sky = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        return sky;
    }
}
