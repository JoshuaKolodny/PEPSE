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

/**
 * A helper class that creates and stores the stem blocks of a tree.
 *
 * <p>The {@code Stem} object manages an array of {@link Block} instances,
 * stacked vertically to form the trunk of a tree.</p>
 *
 * <p>The stem is created in the {@link #createBlocks()} method, which
 * instantiates each block at the appropriate vertical position.</p>
 *
 * <p>Use {@link #getStemBlocks()} to retrieve the blocks for rendering
 * or collision management.</p>
 *
 * <p>The color of the stem is approximated by {@link ColorSupplier} to
 * vary slightly between stems.</p>
 *
 * <p>This class does not extend {@link GameObject}, rather it manages
 * a set of game objects (the blocks).</p>
 *
 * <p>See also {@link Tree} for how {@code Stem} integrates with the rest
 * of the tree structure.</p>
 *
 * <p><strong>Note:</strong> {@code height} should be a multiple of
 * {@code Block.SIZE} for a clean, block-aligned stem.</p>
 *
 * <p>Example usage: {@code new Stem(new Vector2(x, y), totalHeightInPixels);}</p>
 *
 * <p>After creation, {@link #getStemBlocks()} returns an array of
 * {@link Block} objects for further manipulation or addition to the
 * game's object manager.</p>
 *
 * @author
 *     Joshua Kolodny, Itamar Lev Ari
 */
public class Stem {
    private static final Color STEM_COLOR = new Color(100, 50, 20);
    private final Vector2 topLeftCorner;
    private final Renderable renderable;
    Block[] stemBlocks;

    /**
     * Constructs a new {@code Stem} at a given top-left corner with a specified height.
     *
     * <p>This constructor prepares the color renderable for the stem, then
     * calls {@link #createBlocks()} to fill the stem with individual blocks.</p>
     *
     * @param topLeftCorner The top-left corner of the stem.
     * @param height        The total height of the stem in pixels.
     */
    public Stem(Vector2 topLeftCorner, int height) {
        this.topLeftCorner = topLeftCorner;
        this.renderable = new RectangleRenderable(ColorSupplier.approximateColor(STEM_COLOR));
        this.stemBlocks = new Block[height / Block.SIZE];
        createBlocks();
    }

    /**
     * Internal method to create the array of {@link Block} objects that
     * compose the stem.
     */
    private void createBlocks() {
        for (int i = 0; i < stemBlocks.length; i++) {
            stemBlocks[i] = new Block(new Vector2(topLeftCorner.x(),
                    topLeftCorner.y() + i * Block.SIZE), renderable);
        }
    }

    /**
     * Retrieves the array of blocks that make up this stem.
     *
     * @return an array of {@link Block} objects forming the stem.
     */
    public Block[] getStemBlocks() {
        return stemBlocks;
    }
}
