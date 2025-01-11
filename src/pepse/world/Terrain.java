package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.constants.Constants;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages procedural terrain generation using Perlin-like noise.
 * <p>Handles block creation in a specified x-range, stacking blocks to a defined depth
 * below the noise-calculated ground level.</p>
 *
 * <p>Use {@link #createInRange(int, int)} to generate terrain blocks for a horizontal segment,
 * and {@link #groundHeightAt(float)} to query the height of the terrain.</p>
 *
 * <p>Ensures all x-locations snap to multiples of {@link Block#SIZE} for alignment.</p>
 *
 * <p>Color variations are applied via {@link ColorSupplier} to create visual diversity.</p>
 *
 * <p>Example usage: {@code terrain.createInRange(-300, 300);}</p>
 *
 * <p>Generated blocks may be added to the game objects for rendering and collision.</p>
 *
 * @author
 *     Joshua Kolodny, Itamar Lev Ari
 */
public class Terrain {
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 24;
    private final float groundHeightAtX0;
    private final NoiseGenerator noiseGenerator;

    /**
     * Constructs a terrain generator for the given window dimensions and random seed.
     *
     * @param windowDimensions The main window size used to determine initial ground level.
     * @param seed             Seed for deterministic noise generation.
     */
    public Terrain(Vector2 windowDimensions, int seed) {
        groundHeightAtX0 = windowDimensions.mult(Constants.INITIAL_GROUND_FACTOR).y();
        this.noiseGenerator = new NoiseGenerator(seed, (int) groundHeightAtX0);
    }

    /**
     * Computes the ground height at a given x-coordinate using noise,
     * then snaps to a multiple of 30 (block size).
     *
     * @param x The horizontal coordinate in the world.
     * @return The y-value of the ground, aligned to block size.
     */
    public float groundHeightAt(float x) {
        float noise = (float) noiseGenerator.noise(x, Block.SIZE * 7);
        float height = groundHeightAtX0 + noise;
        return (float) (Math.ceil(height / Block.SIZE) * Block.SIZE);
    }

    /**
     * Generates terrain blocks from minX to maxX, stacking blocks down from the ground level
     * to a fixed depth.
     *
     * @param minX Start of the horizontal range.
     * @param maxX End of the horizontal range.
     * @return A list of {@link Block} objects forming the terrain in this range.
     */
    public List<Block> createInRange(int minX, int maxX) {
        List<Block> blockList = new ArrayList<>();
        int initX = findNearestValidLocation(minX);
        int endX = findNearestValidLocation(maxX);
        int currentX = initX;

        while (currentX <= endX) {
            int currentY = (int) Math.floor(groundHeightAt(currentX) / Block.SIZE) * Block.SIZE;
            blockList.addAll(addAllInColumn(currentX, currentY));
            currentX += Block.SIZE;
        }
        return blockList;
    }

    /**
     * Stacks terrain blocks starting at the ground level down to the terrain's depth.
     */
    private List<Block> addAllInColumn(int currentX, int currentY) {
        List<Block> columnBlockList = new ArrayList<>();
        int countBlocks = TERRAIN_DEPTH - (currentY / Block.SIZE);

        for (int i = 0; i < countBlocks; i++) {
            Renderable renderable = new RectangleRenderable(ColorSupplier
                    .approximateColor(BASE_GROUND_COLOR));
            Block block = new Block(new Vector2(currentX, (currentY + i * Block.SIZE)), renderable);
            columnBlockList.add(block);
        }
        return columnBlockList;
    }

    /**
     * Ensures the x-coordinate is aligned to a valid multiple of {@link Block#SIZE}.
     *
     * @param x The raw x-coordinate.
     * @return The adjusted x-coordinate aligned to the nearest multiple of 30.
     */
    public int findNearestValidLocation(int x) {
        while (x % Block.SIZE != 0) {
            x--;
        }
        return x;
    }
}
