package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.constants.Constants;

import java.awt.*;

public class Fruit extends GameObject {
    public static final Vector2 SIZE = Vector2.ONES.mult(18);
    boolean isActive;
    private Renderable originalRenderable;

    public Fruit(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        this.setTag(Constants.FRUIT_TAG);
        this.originalRenderable = renderable;
    }

    public void deactivate() {
        this.isActive = false;
        this.renderer().setRenderable(null);
        setDimensions(Vector2.ZERO);
    }

    public void activate() {
        this.isActive = true;
        this.renderer().setRenderable(originalRenderable);
        setDimensions(SIZE);
    }
}
