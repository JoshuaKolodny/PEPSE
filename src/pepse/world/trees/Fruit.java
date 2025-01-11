package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.constants.Constants;

/**
 * Represents a fruit that can be collected once, disappears, and reappears later.
 * <p>The fruit resets itself using a scheduled task after it is picked up.</p>
 *
 * <p>Activation/deactivation toggles its visibility and collision area.</p>
 *
 * <p>Typical usage: added to a tree or terrain structure for in-game consumption.</p>
 *
 * @author
 *     Joshua Kolodny, Itamar Lev Ari
 */
public class Fruit extends GameObject {
    /**
     * The default size of the fruit when active.
     */
    public static final Vector2 SIZE = Vector2.ONES.mult(18);

    /**
     * True if the fruit is currently active (visible and collectible).
     */
    boolean isActive;
    private final Renderable originalRenderable;

    /**
     * Constructs a new {@code Fruit} object at the specified location.
     *
     * @param topLeftCorner the top-left corner of the fruit
     * @param dimensions    the dimensions of the fruit
     * @param renderable    the {@link Renderable} to display the fruit
     */
    public Fruit(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        this.setTag(Constants.FRUIT_TAG);
        this.originalRenderable = renderable;
    }

    /**
     * Deactivates the fruit upon collision with the avatar and schedules a reactivation.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision){
        deactivate();
        new ScheduledTask(this, Constants.CYCLE_LENGTH, false, this::activate);
    }

    /**
     * Restricts collision checks to the avatar only.
     */
    @Override
    public boolean shouldCollideWith(GameObject other){
        return other.getTag().equals(Constants.AVATAR_TAG);
    }

    /**
     * Deactivates the fruit, making it invisible and setting its size to zero.
     */
    public void deactivate() {
        this.isActive = false;
        this.renderer().setRenderable(null);
        setDimensions(Vector2.ZERO);
    }

    /**
     * Activates the fruit, restoring its original renderable and size.
     */
    public void activate() {
        this.isActive = true;
        this.renderer().setRenderable(originalRenderable);
        setDimensions(SIZE);
    }
}
