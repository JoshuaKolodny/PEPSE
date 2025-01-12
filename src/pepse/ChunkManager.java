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

/**
 * Manages loading and unloading of chunks, dividing the game world into sections.
 * Each chunk contains terrain and flora objects for efficient memory and performance management.
 *
 * @author
 *     Joshua Kolodny, Itamar Lev Ari
 */
public class ChunkManager {

    /** Width of each chunk in pixels. */
    private static final int CHUNK_WIDTH = 300;

    /** Maps chunk indices to their objects and layers. */
    private final Map<Integer, Map<GameObject, Integer>> chunkObjectsMap = new HashMap<>();

    private final Terrain terrain;
    private final Flora flora;

    /**
     * Initializes the chunk manager with terrain and flora generators.
     *
     * @param terrain Terrain generator for ground blocks.
     * @param flora   Flora generator for trees and related objects.
     */
    public ChunkManager(Terrain terrain, Flora flora) {
        this.terrain = terrain;
        this.flora = flora;
    }

    /**
     * Converts a world x-coordinate to its corresponding chunk index.
     *
     * @param x X-coordinate in the game world.
     * @return Chunk index.
     */
    public static int worldToChunkIndex(float x) {
        return (int) Math.floor(x / CHUNK_WIDTH);
    }

    /**
     * Loads all chunks within the specified range, skipping already-loaded chunks.
     *
     * @param chunkStart  Start of the chunk range.
     * @param chunkEnd    End of the chunk range.
     * @param gameObjects Game object collection for adding loaded objects.
     */
    public void loadChunks(int chunkStart, int chunkEnd, danogl.collisions.GameObjectCollection gameObjects) {
        for (int chunkIndex = chunkStart; chunkIndex <= chunkEnd; chunkIndex++) {
            if (!chunkObjectsMap.containsKey(chunkIndex)) {
                createChunk(chunkIndex, gameObjects);
            }
        }
    }

    /**
     * Identifies chunks outside the specified range.
     *
     * @param start Start of the range.
     * @param end   End of the range.
     * @return Chunk indices outside the range.
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
     * Removes all objects in the specified chunk.
     *
     * @param chunkIndex Chunk index to unload.
     * @return Map of objects and their layers in the chunk, or {@code null} if the chunk does not exist.
     */
    public Map<GameObject, Integer> popChunk(int chunkIndex) {
        return chunkObjectsMap.remove(chunkIndex);
    }

    /**
     * Creates a new chunk, adds objects to the game, and stores them in the chunk map.
     *
     * @param chunkIndex  Chunk index to create.
     * @param gameObjects Game object collection for adding objects.
     */
    private void createChunk(int chunkIndex, danogl.collisions.GameObjectCollection gameObjects) {
        Map<GameObject, Integer> objectsInThisChunk = new HashMap<>();
        int minX = chunkIndex * CHUNK_WIDTH;
        int maxX = minX + CHUNK_WIDTH;

        // Generate terrain
        List<Block> blocks = terrain.createInRange(minX, maxX);
        for (Block block : blocks) {
            objectsInThisChunk.put(block, Layer.STATIC_OBJECTS);
            gameObjects.addGameObject(block, Layer.STATIC_OBJECTS);
        }

        // Generate flora
        List<Tree> trees = flora.createInRange(minX, maxX);
        for (Tree tree : trees) {
            for (Block stemBlock : tree.getStem().getStemBlocks()) {
                objectsInThisChunk.put(stemBlock, Layer.STATIC_OBJECTS);
                gameObjects.addGameObject(stemBlock, Layer.STATIC_OBJECTS);
            }
            for (Leaf leaf : tree.getLeaves()) {
                objectsInThisChunk.put(leaf, Layer.FOREGROUND);
                gameObjects.addGameObject(leaf, Layer.FOREGROUND);
            }
            for (Fruit fruit : tree.getFruits()) {
                objectsInThisChunk.put(fruit, Layer.STATIC_OBJECTS);
                gameObjects.addGameObject(fruit, Layer.STATIC_OBJECTS);
            }
        }

        chunkObjectsMap.put(chunkIndex, objectsInThisChunk);
    }
}
