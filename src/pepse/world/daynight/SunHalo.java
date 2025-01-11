package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.constants.Constants;

import java.awt.*;

/**
 * Represents a halo effect around the sun in the game world.
 *
 * <p>The {@code SunHalo} class creates a larger, semi-transparent {@link GameObject} in the shape of
 * an oval. It follows the sun's position to visually enhance the sun, giving it a glowing aura.</p>
 *
 * <p>The static {@link #create(GameObject)} method provides a convenient way to generate and link
 * the halo to an existing sun object.</p>
 *
 * <p>Its size and color can be modified to adjust the visual intensity of the halo.</p>
 *
 * @author
 *     Joshua Kolodny, Itamar Lev Ari
 */
public class SunHalo {
    private static final Vector2 SIZE = Vector2.ONES.mult(100);
    private static final Color COLOR = new Color(255, 255, 0, 20);

    /**
     * Creates a halo {@link GameObject} that follows the sun's location, simulating a glow around it.
     *
     * @param sun the {@link GameObject} representing the sun
     * @return a {@link GameObject} representing the sun's halo
     */
    public static GameObject create(GameObject sun) {
        OvalRenderable ovalRenderable = new OvalRenderable(COLOR);
        GameObject sunHalo = new GameObject(sun.getTopLeftCorner(), SIZE, ovalRenderable);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        return sunHalo;
    }
}
