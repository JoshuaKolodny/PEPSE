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

public class Tree extends GameObject {
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    private static final int LEAF_PROBABILITY_NOMINATOR = 4;
    private static final int FRUIT_PROBABILITY_NOMINATOR = 8;
    private final List<Leaf> leavesArray = new ArrayList<>();
    private final List<Fruit> fruitsArray = new ArrayList<>();
    private final Stem stem;

    public Tree(Vector2 topLeftCorner, Vector2 stemDimensions, int numLeaves, Renderable renderable) {
        super(topLeftCorner, stemDimensions, renderable);
        this.stem = createStem(topLeftCorner, (int) stemDimensions.y());
        createLeavesAndFruit(topLeftCorner, numLeaves);
        this.setTag(Constants.TREE_TAG);
    }

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
                    Renderable leafRenderable = new RectangleRenderable
                            (ColorSupplier.approximateColor(LEAF_COLOR));
                    leavesArray.add(new Leaf(new Vector2(leafX, leafY), Leaf.SIZE, leafRenderable));
                } else {
                    randomFruitInt = random.nextInt(Constants.PROBABILITY_DENOMINATOR);
                    if (randomFruitInt > FRUIT_PROBABILITY_NOMINATOR) {
                        Renderable fruitRenderable = new OvalRenderable(
                                (ColorSupplier.approximateColor(Color.RED)));
                        fruitsArray.add(new Fruit(new Vector2(leafX, leafY), Fruit.SIZE, fruitRenderable));
                    }
                }
            }
        }
    }

    private Stem createStem(Vector2 topLeftCorner, int stemHeight) {
        return new Stem(topLeftCorner, stemHeight);
    }

    @Override
    public boolean shouldCollideWith(GameObject other) {
        if (other.getTag().equals(Constants.GROUND_TAG)) {
            return false;
        }
        return super.shouldCollideWith(other);
    }

    public Stem getStem() {
        return stem;
    }

    public List<Leaf> getLeaves() {
        return leavesArray;
    }

    public List<Fruit> getFruits() {
        return fruitsArray;
    }
}
