package pepse.world.trees;

import danogl.util.Vector2;
import pepse.constants.Constants;
import pepse.interfaces.GroundHeightProvider;
import pepse.interfaces.NearestLocationProvider;
import pepse.world.Block;

import java.util.*;
public class Flora {
    private final NearestLocationProvider nearestLocationProvider;
    private final GroundHeightProvider groundHeightProvider;
    private final int seed;

    public Flora(NearestLocationProvider nearestLocationProvider, GroundHeightProvider groundHeightProvider,
                 int seed) {
        this.nearestLocationProvider = nearestLocationProvider;
        this.groundHeightProvider = groundHeightProvider;
        this.seed = seed;
    }

    public List<Tree> createInRange(int minX, int maxX) {
        List<Tree> treeLocationsList = new ArrayList<>();

        // Use nearestLocationProvider to snap min/max to valid tree positions.
        int initX = nearestLocationProvider.getNearestLocation(minX);
        int endX = nearestLocationProvider.getNearestLocation(maxX);

        int currentX = initX;
        while (currentX <= endX) {
            // For reproducible randomness at this specific X:
            Random placementRand = new Random(Objects.hash(currentX, seed));

            // Decide whether to create a tree at this X
            int isCreated = placementRand.nextInt(10);
            if (isCreated == 0 && currentX != Constants.AVATAR_START_X_POSITION) {
                // If creating a tree, determine its height and leaf count
                int stemHeight = placementRand.nextInt(3, 6) * Block.SIZE;
                int numLeaves = 2 * placementRand.nextInt(3, 6) + 1;

                // Compute the tree’s top-left corner (just above the ground at this X)
                float groundY = groundHeightProvider.getGroundHeight(currentX);
                Vector2 treeTopLeftCorner = new Vector2(currentX, groundY - stemHeight);

                // Create the tree
                Tree tree = new Tree(treeTopLeftCorner,
                        new Vector2(Block.SIZE, stemHeight),
                        numLeaves,
                        null);
                treeLocationsList.add(tree);
            }
            // Move to the next potential X position
            currentX += Block.SIZE;
        }
        return treeLocationsList;
    }


}