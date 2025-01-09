////////////////////////////////////////
// PepseGameManager.java
////////////////////////////////////////

package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.*;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.constants.Constants;
import pepse.interfaces.*;
import pepse.util.ColorSupplier;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Flora;
import pepse.world.trees.Fruit;
import pepse.world.trees.Leaf;
import pepse.world.trees.Tree;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

public class PepseGameManager extends GameManager {

    private Avatar avatar;
    private Terrain terrain;
    private Flora flora;
    private ChunkManager chunkManager;

    private static final int CHUNK_RENDER_DISTANCE = 3;
    private int minChunkIndexLoaded;
    private int maxChunkIndexLoaded;

    private Vector2 windowDimensions;

    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        windowDimensions = windowController.getWindowDimensions();
        Random random = new Random();
        int seed = random.nextInt();

        // 1) Create sky
        GameObject sky = Sky.create(windowDimensions);
        gameObjects().addGameObject(sky, Layer.BACKGROUND);

        // 2) Create terrain and flora with a fixed seed
        terrain = new Terrain(windowDimensions, seed); // for consistent ground
        flora = new Flora(terrain::findNearestValidLocation, terrain::groundHeightAt, seed);

        // 3) Chunk manager
        chunkManager = new ChunkManager(terrain, flora);

        // 4) Night
        GameObject night = Night.create(windowDimensions, Constants.CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.FOREGROUND);

        // 5) Sun
        GameObject sun = Sun.create(windowDimensions, Constants.CYCLE_LENGTH);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);

        // 6) Sun halo
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);

        // 7) Create avatar

        // Make sure we place it above the ground, etc. (Simplified example)
        float groundY = terrain.groundHeightAt(Constants.AVATAR_START_X_POSITION);
        Vector2 initialPosition = new Vector2(Constants.AVATAR_START_X_POSITION,
                groundY - Avatar.SIZE.y());

        avatar = new Avatar(initialPosition, inputListener, imageReader);
        gameObjects().addGameObject(avatar);

        // 8) Camera follows the avatar
        setCamera(new Camera(
                avatar,
                windowDimensions.mult(0.5f).subtract(initialPosition),  // offset
                windowDimensions,
                windowDimensions
        ));

        // 9) Energy display
        EnergyProvider energyProvider = avatar::getEnergy;
        EnergyDisplay energyDisplay = new EnergyDisplay(
                Vector2.ONES.mult(20),
                Vector2.ONES.mult(40),
                null,
                energyProvider
        );
        gameObjects().addGameObject(energyDisplay, Layer.UI);

        // 10) Example clouds & rain
        Cloud cloud = new Cloud(windowDimensions);
        RainDropper rainDropper = () -> createRain(cloud);
        cloud.setRainDropper(rainDropper);
        avatar.addJumpObserver(cloud);
        addCloudBlocks(cloud);
        createRain(cloud);

        // Initialize the first chunks
        int avatarChunkIndex = ChunkManager.worldToChunkIndex(avatar.getTopLeftCorner().x());
        minChunkIndexLoaded = avatarChunkIndex - CHUNK_RENDER_DISTANCE;
        maxChunkIndexLoaded = avatarChunkIndex + CHUNK_RENDER_DISTANCE;
        chunkManager.loadChunks(minChunkIndexLoaded, maxChunkIndexLoaded, gameObjects());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Check if we need to load/unload chunks
        float avatarX = avatar.getTopLeftCorner().x();
        int avatarChunkIndex = ChunkManager.worldToChunkIndex(avatarX);

        // Expand loaded range if needed
        while (avatarChunkIndex - CHUNK_RENDER_DISTANCE < minChunkIndexLoaded) {
            minChunkIndexLoaded--;
            chunkManager.loadChunks(minChunkIndexLoaded, minChunkIndexLoaded, gameObjects());
        }
        while (avatarChunkIndex + CHUNK_RENDER_DISTANCE > maxChunkIndexLoaded) {
            maxChunkIndexLoaded++;
            chunkManager.loadChunks(maxChunkIndexLoaded, maxChunkIndexLoaded, gameObjects());
        }

        // Identify and unload chunks outside our desired range
        int desiredMin = avatarChunkIndex - CHUNK_RENDER_DISTANCE;
        int desiredMax = avatarChunkIndex + CHUNK_RENDER_DISTANCE;
        Set<Integer> chunksToRemove = chunkManager.findChunksOutsideRange(desiredMin, desiredMax);

        // Remove them from the game
        for (int chunkIndex : chunksToRemove) {
            Map<GameObject, Integer> chunkObjects = chunkManager.popChunk(chunkIndex);
            if (chunkObjects != null) {
                for (Map.Entry<GameObject, Integer> entry : chunkObjects.entrySet()) {
                    gameObjects().removeGameObject(entry.getKey(), entry.getValue());
                }
            }
        }

        // Update chunk boundaries
        minChunkIndexLoaded = desiredMin;
        maxChunkIndexLoaded = desiredMax;
    }

    /**
     * Example method for creating falling rain from cloud blocks.
     */
    private void createRain(Cloud cloud) {
        Random random = new Random();
        for (Block cloudBlock : cloud.getCloudBlocks()) {
            int rainNum = random.nextInt(Constants.PROBABILITY_DENOMINATOR);
            if (rainNum < 3) {
                RectangleRenderable rainRenderable = new RectangleRenderable(
                        ColorSupplier.approximateColor(Color.BLUE)
                );
                Block rainBlock = new Block(cloudBlock.getTopLeftCorner(), rainRenderable);
                rainBlock.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
                rainBlock.setDimensions(rainBlock.getDimensions().mult(0.3f));
                rainBlock.transform().setAccelerationY(Constants.GRAVITY);
                addRainTransition(rainBlock);
                gameObjects().addGameObject(rainBlock, Layer.BACKGROUND);
            }
        }
    }

    private void addRainTransition(Block rainBlock) {
        Consumer<Float> lambdaRain = opacity -> rainBlock.renderer().setOpaqueness(opacity);
        new Transition<>(
                rainBlock,
                lambdaRain,
                1f,  // start opaqueness
                0f,  // end opaqueness
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                1,   // how long the raindrop is visible
                Transition.TransitionType.TRANSITION_ONCE,
                () -> gameObjects().removeGameObject(rainBlock)
        );
    }

    private void addCloudBlocks(Cloud cloud) {
        for (Block cloudBlock : cloud.getCloudBlocks()) {
            gameObjects().addGameObject(cloudBlock, Layer.FOREGROUND);
        }
    }

    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
