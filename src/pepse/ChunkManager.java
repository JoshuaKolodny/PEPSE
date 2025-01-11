package pepse;

import danogl.GameObject;
import danogl.collisions.Layer;
import pepse.world.Block;
import pepse.world.Terrain;
import pepse.world.trees.Flora;
import pepse.world.trees.Fruit;
import pepse.world.trees.Leaf;
import pepse.world.trees.Tree;

import java.util.*;

public class ChunkManager {

    // Each chunk is 300px wide (for example).
    private static final int CHUNK_WIDTH = 300;

    /**
     * chunkIndex -> map of (GameObject -> layer).
     * For each chunkIndex, we remember which objects are part of that chunk
     * and what layer they belong to.
     */
    private final Map<Integer, Map<GameObject, Integer>> chunkObjectsMap = new HashMap<>();

    private final Terrain terrain;
    private final Flora flora;

    public ChunkManager(Terrain terrain, Flora flora) {
        this.terrain = terrain;
        this.flora = flora;
    }

    /**
     * Converts an x-coordinate to its chunk index via floor division.
     */
    public static int worldToChunkIndex(float x) {
        return (int) Math.floor(x / CHUNK_WIDTH);
    }

    /**
     * Loads all chunks in [chunkStart..chunkEnd], skipping already-loaded chunks.
     */
    public void loadChunks(int chunkStart, int chunkEnd, danogl.collisions.GameObjectCollection gameObjects) {
        for (int chunkIndex = chunkStart; chunkIndex <= chunkEnd; chunkIndex++) {
            if (!chunkObjectsMap.containsKey(chunkIndex)) {
                createChunk(chunkIndex, gameObjects);
            }
        }
    }

    /**
     * Identify which chunks lie strictly outside [start..end].
     */
    public Set<Integer> findChunksOutsideRange(int start, int end) {
        Set<Integer> toRemove = new HashSet<>();
        for (Integer idx : chunkObjectsMap.keySet()) {
            if (idx < start || idx > end) {
                toRemove.add(idx);
            }
        }
        return toRemove;
    }

    /**
     * Remove the chunk data from our local map, returning that chunk’s objects
     * so the caller can remove them from the game.
     */
    public Map<GameObject, Integer> popChunk(int chunkIndex) {
        return chunkObjectsMap.remove(chunkIndex);
    }

    /**
     * Actually generate a chunk, storing the objects in chunkObjectsMap and
     * adding them to the game.
     */
    private void createChunk(int chunkIndex, danogl.collisions.GameObjectCollection gameObjects) {
        Map<GameObject, Integer> objectsInThisChunk = new HashMap<>();

        int minX = chunkIndex * CHUNK_WIDTH;
        int maxX = minX + CHUNK_WIDTH;

        // 1) Generate terrain in [minX..maxX]
        List<Block> blocks = terrain.createInRange(minX, maxX);
        for (Block block : blocks) {
            objectsInThisChunk.put(block, Layer.STATIC_OBJECTS);
            gameObjects.addGameObject(block, Layer.STATIC_OBJECTS);
        }

        // 2) Generate flora in [minX..maxX]
        List<Tree> trees = flora.createInRange(minX, maxX);
        for (Tree tree : trees) {
            // Stem blocks
            for (Block stemBlock : tree.getStem().getStemBlocks()) {
                objectsInThisChunk.put(stemBlock, Layer.STATIC_OBJECTS);
                gameObjects.addGameObject(stemBlock, Layer.STATIC_OBJECTS);
            }
            // Leaves
            for (Leaf leaf : tree.getLeaves()) {
                objectsInThisChunk.put(leaf, Layer.FOREGROUND);
                gameObjects.addGameObject(leaf, Layer.FOREGROUND);
            }
            // Fruits
            for (Fruit fruit : tree.getFruits()) {
                objectsInThisChunk.put(fruit, Layer.STATIC_OBJECTS);
                gameObjects.addGameObject(fruit, Layer.STATIC_OBJECTS);
            }
        }

        chunkObjectsMap.put(chunkIndex, objectsInThisChunk);
    }

}
