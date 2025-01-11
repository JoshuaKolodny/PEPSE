package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.constants.Constants;
import pepse.util.ColorSupplier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a tree object composed of a vertical stem, leaves, and possibly fruits.
 *
 * <p>The {@code Tree} extends {@link GameObject} primarily to maintain a bounding box
 * for the entire tree, though the individual components (stem blocks, leaves, and fruits)
 * are created and managed internally.</p>
 *
 * <ul>
 *     <li>{@link Stem} is used to represent the tree trunk.</li>
 *     <li>{@link Leaf} objects are placed around the trunk, based on a random
 *     probability within a specified grid of positions.</li>
 *     <li>{@link Fruit} objects are also randomly created among the leaves.</li>
 * </ul>
 *
 * <p>Use {@link #getStem()}, {@link #getLeaves()}, and {@link #getFruits()} to access
 * the generated elements of the tree.</p>
 *
 * <p>The {@link #shouldCollideWith(GameObject)} method is overridden to disable collision
 * with ground objects, which can prevent odd intersection behaviors.</p>
 *
 * <p>Example usage: new Tree(treePosition, stemDimensions, numberOfLeaves, null);</p>
 *
 * <p>This class is often used by a {@code Flora} manager to populate the game world
 * with multiple trees.</p>
 *
 * @author
 *     Joshua Kolodny, Itamar Lev Ari
 */
public class Tree extends GameObject {
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    private static final int LEAF_PROBABILITY_NOMINATOR = 4;
    private static final int FRUIT_PROBABILITY_NOMINATOR = 8;
    private final List<Leaf> leavesArray = new ArrayList<>();
    private final List<Fruit> fruitsArray = new ArrayList<>();
    private final Stem stem;

    /**
     * Constructs a new tree at the given position with specified stem size and number of leaves.
     *
     * @param topLeftCorner  The top-left corner of the tree.
     * @param stemDimensions The dimensions of the stem (width should be Block.SIZE for typical usage).
     * @param numLeaves      The total number of leaf positions in a grid dimension (width and height).
     * @param renderable     The {@link Renderable} for the tree bounding box itself (may be null).
     */
    public Tree(Vector2 topLeftCorner, Vector2 stemDimensions, int numLeaves, Renderable renderable) {
        super(topLeftCorner, stemDimensions, renderable);
        this.stem = createStem(topLeftCorner, (int) stemDimensions.y());
        createLeavesAndFruit(topLeftCorner, numLeaves);
    }

    /**
     * Randomly places {@link Leaf} or {@link Fruit} objects around the top-left corner in a grid.
     *
     * <p>For each grid cell, the method generates a random number to decide whether to place
     * a leaf, a fruit, or neither.</p>
     *
     * @param topLeftCorner The top-left corner from which the leaves/fruits are offset.
     * @param numLeaves     The grid dimension for leaves/fruits along each axis.
     */
    private void createLeavesAndFruit(Vector2 topLeftCorner, int numLeaves) {
        Random random = new Random();
        int randomLeafInt, randomFruitInt;
        for (int i = 0; i < numLeaves; i++) {
            for (int j = 0; j < numLeaves; j++) {
                randomLeafInt = random.nextInt(Constants.PROBABILITY_DENOMINATOR);
                int leafX = (int) (topLeftCorner.x() - (float) numLeaves / Constants.HALF_RATIO *
                        Leaf.SIZE.x() + j * Leaf.SIZE.x());
                int leafY = (int) (topLeftCorner.y() - (float) numLeaves / Constants.HALF_RATIO *
                        Leaf.SIZE.y() + i * Leaf.SIZE.y());

                if (randomLeafInt > LEAF_PROBABILITY_NOMINATOR) {
                    // Create a leaf in this position
                    Renderable leafRenderable = new RectangleRenderable(
                            ColorSupplier.approximateColor(LEAF_COLOR));
                    leavesArray.add(new Leaf(new Vector2(leafX, leafY), Leaf.SIZE, leafRenderable));
                } else {
                    // Alternatively, try placing a fruit
                    randomFruitInt = random.nextInt(Constants.PROBABILITY_DENOMINATOR);
                    if (randomFruitInt > FRUIT_PROBABILITY_NOMINATOR) {
                        Renderable fruitRenderable = new OvalRenderable(
                                ColorSupplier.approximateColor(Color.RED));
                        fruitsArray.add(new Fruit(new Vector2(leafX, leafY), Fruit.SIZE, fruitRenderable));
                    }
                }
            }
        }
    }

    /**
     * Creates a {@link Stem} for this tree.
     *
     * @param topLeftCorner The top-left corner of the stem.
     * @param stemHeight    The total height of the stem in pixels.
     * @return A new {@link Stem} object.
     */
    private Stem createStem(Vector2 topLeftCorner, int stemHeight) {
        return new Stem(topLeftCorner, stemHeight);
    }

    /**
     * Determines whether this tree should collide with another {@link GameObject}.
     *
     * <p>The method returns {@code false} if the other object's tag equals {@link Constants#BLOCK_TAG},
     * preventing collision with the ground. Otherwise, it relies on the superclass's collision logic.</p>
     *
     * @param other The other {@link GameObject}.
     * @return {@code false} if the other object is ground, otherwise uses superclass collision settings.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        if (other.getTag().equals(Constants.BLOCK_TAG)) {
            return false;
        }
        return super.shouldCollideWith(other);
    }

    /**
     * Retrieves the {@link Stem} object that forms the trunk of this tree.
     *
     * @return the {@link Stem} object
     */
    public Stem getStem() {
        return stem;
    }

    /**
     * Retrieves a list of all {@link Leaf} objects associated with this tree.
     *
     * @return a list of {@link Leaf} objects
     */
    public List<Leaf> getLeaves() {
        return leavesArray;
    }

    /**
     * Retrieves a list of all {@link Fruit} objects associated with this tree.
     *
     * @return a list of {@link Fruit} objects
     */
    public List<Fruit> getFruits() {
        return fruitsArray;
    }
}
