package pepse.world.trees;

import danogl.util.Vector2;
import pepse.constants.Constants;
import pepse.interfaces.GroundHeightProvider;
import pepse.interfaces.NearestLocationProvider;
import pepse.world.Block;

import java.util.*;

/**
 * Manages the creation of {@link Tree} objects in a given horizontal range.
 * <p>Uses {@link NearestLocationProvider} to snap x-coordinates, {@link GroundHeightProvider} to
 * determine ground heights, and randomized logic (with a seed) to ensure reproducible tree placement.</p>
 *
 * @author
 *     Joshua Kolodny, Itamar Lev Ari
 */
public class Flora {
    private static final int TREE_PROBABILITY_NOMINATOR = 0;
    private static final int MIN_BOUND = 3;
    private static final int MAX_BOUND = 6;
    private final NearestLocationProvider nearestLocationProvider;
    private final GroundHeightProvider groundHeightProvider;
    private final int seed;

    /**
     * Initializes the flora generator with required providers and a seed for randomness.
     *
     * @param nearestLocationProvider Aligns x-coordinates to valid positions.
     * @param groundHeightProvider    Determines ground height at each x-coordinate.
     * @param seed                    Seed for reproducible randomization.
     */
    public Flora(NearestLocationProvider nearestLocationProvider, GroundHeightProvider groundHeightProvider,
                 int seed) {
        this.nearestLocationProvider = nearestLocationProvider;
        this.groundHeightProvider = groundHeightProvider;
        this.seed = seed;
    }

    /**
     * Creates trees in the given range. Each potential x-location is tested for tree placement,
     * and if a tree is placed, its height and leaf count are determined randomly.
     *
     * @param minX The minimum x-coordinate of the region.
     * @param maxX The maximum x-coordinate of the region.
     * @return A list of {@link Tree} objects within the specified range.
     */
    public List<Tree> createInRange(int minX, int maxX) {
        List<Tree> treeLocationsList = new ArrayList<>();

        // Use nearestLocationProvider to snap min/max to valid tree positions.
        int initX = nearestLocationProvider.getNearestLocation(minX);
        int endX = nearestLocationProvider.getNearestLocation(maxX);

        int currentX = initX;
        while (currentX <= endX) {
            Random placementRand = new Random(Objects.hash(currentX, seed));
            if (shouldCreateTree(currentX, placementRand)) {
                int stemHeight = placementRand.nextInt(MIN_BOUND, MAX_BOUND) * Block.SIZE;
                int numLeaves = 2 * placementRand.nextInt(MIN_BOUND, MAX_BOUND) + 1;

                float groundY = groundHeightProvider.getGroundHeight(currentX);
                Vector2 treeTopLeftCorner = new Vector2(currentX, groundY - stemHeight);

                Tree tree = new Tree(treeTopLeftCorner,
                        new Vector2(Block.SIZE, stemHeight),
                        numLeaves,
                        null);
                treeLocationsList.add(tree);
            }
            currentX += Block.SIZE;
        }
        return treeLocationsList;
    }

    /**
     * Checks whether a tree should be created at the specified x-coordinate
     * based on a random value and constraints.
     *
     * @param currentX     The current x-coordinate in question.
     * @param placementRand Random generator initialized with a unique seed per x-coordinate.
     * @return {@code true} if a tree should be created; otherwise, {@code false}.
     */
    private boolean shouldCreateTree(int currentX, Random placementRand) {
        int isCreated = placementRand.nextInt(Constants.PROBABILITY_DENOMINATOR);
        return isCreated == TREE_PROBABILITY_NOMINATOR && currentX != Constants.AVATAR_START_X_POSITION;
    }
}
