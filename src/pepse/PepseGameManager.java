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

import java.awt.*;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Manages the primary gameplay loop, objects, and systems for the "Pepse" world.
 *
 * <p>Initializes terrain, flora, sky, day/night cycle, avatar, camera, and cloud-based rain.
 * Loads/unloads chunks around the avatar for performance optimization. Uses {@link GameManager}
 * as the base class.</p>
 *
 * <p>Chunk management logic is in {@link #update(float)}, ensuring relevant game objects are
 * added or removed as the avatar moves.</p>
 *
 * <p>Entry point in {@link #main(String[])}.</p>
 *
 * @author
 *     Joshua Kolodny, Itamar Lev Ari
 */
public class PepseGameManager extends GameManager {

    private static final Vector2 ENERGY_DISPLAY_LOCATION = Vector2.ONES.mult(20);
    private static final Vector2 ENERGY_DISPLAY_DIMENSIONS = Vector2.ONES.mult(40);
    private static final float RAIN_DROP_FACTOR = 0.3f;
    private Avatar avatar;
    private ChunkManager chunkManager;
    private static final int CHUNK_RENDER_DISTANCE = 3;
    private int minChunkIndexLoaded;
    private int maxChunkIndexLoaded;

    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        Vector2 windowDimensions = windowController.getWindowDimensions();
        Random random = new Random();
        int seed = random.nextInt();

        // 1) Create sky
        createSky(windowDimensions);

        // 2) Create terrain and flora
        Terrain terrain = new Terrain(windowDimensions, seed);
        Flora flora = new Flora(terrain::findNearestValidLocation, terrain::groundHeightAt, seed);

        // 3) Chunk manager
        chunkManager = new ChunkManager(terrain, flora);

        // 4) Night
        createNight(windowDimensions);

        // 5) Sun
        GameObject sun = createSun(windowDimensions);

        // 6) Sun halo
        createSunHalo(sun);

        // 7) Avatar
        createAvatar(imageReader, inputListener, terrain);

        // 8) Camera
        setCamera(new Camera(
                avatar,
                new Vector2(0, -Avatar.SIZE.y()),
                windowDimensions,
                windowDimensions
        ));

        // 9) Energy display
        createEnergyDisplay();

        // 10) Clouds and rain
        createCloudAndRain(windowDimensions);

        // Load initial chunks
        initializeFirstChunks();
    }

    /**
     * Loads the initial set of chunks around the avatar.
     */
    private void initializeFirstChunks() {
        int avatarChunkIndex = ChunkManager.worldToChunkIndex(avatar.getTopLeftCorner().x());
        minChunkIndexLoaded = avatarChunkIndex - CHUNK_RENDER_DISTANCE;
        maxChunkIndexLoaded = avatarChunkIndex + CHUNK_RENDER_DISTANCE;
        chunkManager.loadChunks(minChunkIndexLoaded, maxChunkIndexLoaded, gameObjects());
    }

    /**
     * Creates a cloud object and associates it with a {@link RainDropper} and
     * {@link JumpObserver} for rain on jump.
     */
    private void createCloudAndRain(Vector2 windowDimensions) {
        Cloud cloud = new Cloud(windowDimensions);
        RainDropper rainDropper = () -> createRain(cloud);
        cloud.setRainDropper(rainDropper);
        avatar.addJumpObserver(cloud);
        addCloudBlocks(cloud);
        createRain(cloud);
    }

    /**
     * Adds an energy display UI element that tracks the avatar's energy level.
     */
    private void createEnergyDisplay() {
        EnergyProvider energyProvider = avatar::getEnergy;
        EnergyDisplay energyDisplay = new EnergyDisplay(
                ENERGY_DISPLAY_LOCATION,
                ENERGY_DISPLAY_DIMENSIONS,
                null,
                energyProvider
        );
        gameObjects().addGameObject(energyDisplay, Layer.UI);
    }

    /**
     * Instantiates the avatar above the ground at a default x-position.
     */
    private void createAvatar(ImageReader imageReader, UserInputListener inputListener, Terrain terrain) {
        float groundY = terrain.groundHeightAt(Constants.AVATAR_START_X_POSITION);
        Vector2 initialPosition = new Vector2(
                Constants.AVATAR_START_X_POSITION,
                groundY - Avatar.SIZE.y()
        );
        avatar = new Avatar(initialPosition, inputListener, imageReader);
        gameObjects().addGameObject(avatar);
    }

    /**
     * Adds a halo effect behind the sun to simulate a glow.
     */
    private void createSunHalo(GameObject sun) {
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);
    }

    /**
     * Creates the sun object with a day/night cycle.
     */
    private GameObject createSun(Vector2 windowDimensions) {
        GameObject sun = Sun.create(windowDimensions, Constants.CYCLE_LENGTH);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
        return sun;
    }

    /**
     * Creates a night overlay that transitions opacity to simulate day/night.
     */
    private void createNight(Vector2 windowDimensions) {
        GameObject night = Night.create(windowDimensions, Constants.CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.FOREGROUND);
    }

    /**
     * Creates a sky background that covers the entire game window.
     */
    private void createSky(Vector2 windowDimensions) {
        GameObject sky = Sky.create(windowDimensions);
        gameObjects().addGameObject(sky, Layer.BACKGROUND);
    }

    /**
     * Updates chunk loading around the avatar and removes distant chunks.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Check for chunk loading/unloading
        float avatarX = avatar.getTopLeftCorner().x();
        int avatarChunkIndex = ChunkManager.worldToChunkIndex(avatarX);

        loadNewChunks(avatarChunkIndex);

        // Identify and unload chunks beyond the desired range
        int desiredMin = avatarChunkIndex - CHUNK_RENDER_DISTANCE;
        int desiredMax = avatarChunkIndex + CHUNK_RENDER_DISTANCE;
        removeChunksFromGame(desiredMin, desiredMax);

        // Update chunk boundaries
        minChunkIndexLoaded = desiredMin;
        maxChunkIndexLoaded = desiredMax;
    }

    /**
     * Loads new chunks around the avatar's current position if it moves beyond the current boundaries.
     *
     * @param avatarChunkIndex The chunk index where the avatar is currently located.
     */
    private void loadNewChunks(int avatarChunkIndex) {
        while (avatarChunkIndex - CHUNK_RENDER_DISTANCE < minChunkIndexLoaded) {
            minChunkIndexLoaded--;
            chunkManager.loadChunks(minChunkIndexLoaded, minChunkIndexLoaded, gameObjects());
        }
        while (avatarChunkIndex + CHUNK_RENDER_DISTANCE > maxChunkIndexLoaded) {
            maxChunkIndexLoaded++;
            chunkManager.loadChunks(maxChunkIndexLoaded, maxChunkIndexLoaded, gameObjects());
        }
    }

    /**
     * Removes chunks outside the desired range from the game.
     *
     * @param desiredMin The minimum chunk index to keep.
     * @param desiredMax The maximum chunk index to keep.
     */
    private void removeChunksFromGame(int desiredMin, int desiredMax) {
        Set<Integer> chunksToRemove = chunkManager.findChunksOutsideRange(desiredMin, desiredMax);

        for (int chunkIndex : chunksToRemove) {
            Map<GameObject, Integer> chunkObjects = chunkManager.popChunk(chunkIndex);
            if (chunkObjects != null) {
                for (Map.Entry<GameObject, Integer> entry : chunkObjects.entrySet()) {
                    gameObjects().removeGameObject(entry.getKey(), entry.getValue());
                }
            }
        }
    }


    /**
     * Creates raindrops below cloud blocks. Raindrops disappear after a short transition.
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
                rainBlock.setDimensions(rainBlock.getDimensions().mult(RAIN_DROP_FACTOR));
                rainBlock.transform().setAccelerationY(Constants.GRAVITY);
                addRainTransition(rainBlock);
                gameObjects().addGameObject(rainBlock, Layer.BACKGROUND);
            }
        }
    }

    /**
     * Adds a transition to fade out the raindrop before removing it from the game.
     */
    private void addRainTransition(Block rainBlock) {
        Consumer<Float> lambdaRain = opacity -> rainBlock.renderer().setOpaqueness(opacity);
        new Transition<>(
                rainBlock,
                lambdaRain,
                1f,
                0f,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                1,
                Transition.TransitionType.TRANSITION_ONCE,
                () -> gameObjects().removeGameObject(rainBlock)
        );
    }

    /**
     * Places cloud blocks on the foreground layer of the game.
     */
    private void addCloudBlocks(Cloud cloud) {
        for (Block cloudBlock : cloud.getCloudBlocks()) {
            gameObjects().addGameObject(cloudBlock, Layer.FOREGROUND);
        }
    }

    /**
     * Launches the Pepse game.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
