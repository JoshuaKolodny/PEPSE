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

public class Terrain {
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 24;
    private final float groundHeightAtX0;
    private final NoiseGenerator noiseGenerator;

    public Terrain(Vector2 windowDimensions, int seed) {
        groundHeightAtX0 = windowDimensions.mult(Constants.INITIAL_GROUND_FACTOR).y();
        this.noiseGenerator = new NoiseGenerator(seed, (int) groundHeightAtX0);
    }

    public float groundHeightAt(float x) {
        float noise = (float) noiseGenerator.noise(x, Block.SIZE * 7);
        float height = groundHeightAtX0 + noise;
        // Round up to the nearest number divisible by 30
        return (float) (Math.ceil(height / 30) * 30);
    }

    public List<Block> createInRange(int minX, int maxX) {
        List<Block> blockList = new ArrayList<>();
        int initX = findNearestValidLocation(minX);
        int endX = findNearestValidLocation(maxX);
        int currentX = initX;
        while (currentX <= endX) {
            int currentY = (int) Math.floor(groundHeightAt(currentX) / Block.SIZE) * Block.SIZE;
            List<Block> columnBlockList = addAllInColumn(currentX, currentY);
            blockList.addAll(columnBlockList);
            currentX += Block.SIZE;
        }
        return blockList;
    }

    private List<Block> addAllInColumn(int currentX, int currentY) {
        List<Block> columnBlockList = new ArrayList<>();
        int countBlocks = TERRAIN_DEPTH - (currentY / Block.SIZE);
        for (int i = 0; i < countBlocks; i++) {
            RectangleRenderable renderable = new RectangleRenderable
                    (ColorSupplier.approximateColor(BASE_GROUND_COLOR));
            Block block = new Block(new Vector2(currentX, (currentY + i * Block.SIZE)), renderable);
            columnBlockList.add(block);
        }
        return columnBlockList;
    }

    public int findNearestValidLocation(int x) {
        while (x % Block.SIZE != 0) {
            x--;
        }
        return x;
    }
}
