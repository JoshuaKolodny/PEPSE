package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.constants.Constants;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;

public class Stem {
    private static final Color STEM_COLOR = new Color(100, 50, 20);
    Block[] stemBlocks;
    private final Vector2 topLeftCorner;
    private final Renderable renderable;

    public Stem(Vector2 topLeftCorner, int height) {
        this.topLeftCorner = topLeftCorner;
        this.renderable = new RectangleRenderable(ColorSupplier.approximateColor(STEM_COLOR));
        this.stemBlocks = new Block[height / Block.SIZE];
        createBlocks();
    }

    private void createBlocks() {
        for (int i = 0; i < stemBlocks.length; i++) {
            stemBlocks[i] = new Block(new Vector2(topLeftCorner.x(),
                    topLeftCorner.y() + i * Block.SIZE), renderable);
        }
    }

    public Block[] getStemBlocks() {
        return stemBlocks;
    }
}
