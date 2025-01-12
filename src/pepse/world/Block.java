package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.constants.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * A solid block with fixed size that forms part of the terrain or structures.
 * <p>Prevents intersection in any direction and does not collide with other blocks.</p>
 *
 * <p>Used widely for ground, platforms, and other terrain features.</p>
 *
 * @author Joshua Kolodny, Itamar Lev Ari
 */
public class Block extends GameObject {
    /**
     * The fixed size of each block in pixels.
     */
    public static final int SIZE = 30;

    /**
     * Constructs a block at the specified location with a given renderable.
     *
     * @param topLeftCorner Position of the block's top-left corner.
     * @param renderable    A {@link Renderable} (e.g., color or texture).
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        this.setTag(Constants.BLOCK_TAG);
    }

    /**
     * Prevents collision with other blocks while allowing collision with other objects.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        List<String> staticBlockTags = List.of(
                Constants.BLOCK_TAG,
                Constants.TOP_BLOCK_TAG,
                Constants.STEM_TAG
        );
        if (staticBlockTags.contains(other.getTag())) {
            return false;
        }
        return super.shouldCollideWith(other);
    }
}
