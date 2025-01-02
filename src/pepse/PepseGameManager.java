package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.constants.Constants;
import pepse.interfaces.EnergyProvider;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;

import java.util.List;

public class PepseGameManager extends GameManager {

    private static final int SEED = 123;
    private static final Vector2 ENERGY_LOCATION = Vector2.ONES.mult(20);
    private static final Vector2 ENERGY_DIMENSIONS = Vector2.ONES.mult(20);

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        Vector2 windowDimensions = windowController.getWindowDimensions();
        // create sky
        GameObject sky = Sky.create(windowDimensions);
        gameObjects().addGameObject(sky, Layer.BACKGROUND);
        // create cloud
        GameObject cloud = Cloud.create(imageReader);
        gameObjects().addGameObject(cloud, Layer.BACKGROUND);
        // Terrain
        Terrain terrain = new Terrain(windowDimensions, SEED);
        List<Block> blocks = terrain.createInRange(0,(int) windowDimensions.x());
        for (Block block : blocks){
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }
        // Night
        GameObject night = Night.create(windowDimensions, Constants.CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.FOREGROUND);
        // Sun
        GameObject sun = Sun.create(windowDimensions, Constants.CYCLE_LENGTH);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
        // Sun Halo
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);
        // Avatar
        Vector2 initialPosition = new Vector2(0f,
                windowDimensions.mult(Constants.INITIAL_GROUND_FACTOR).y() - Constants.AVATAR_DIMENSIONS.y());
        Avatar avatar = new Avatar(initialPosition, inputListener, imageReader);
        gameObjects().addGameObject(avatar);
        // Energy
        EnergyProvider energyProvider = avatar::getEnergy;
        EnergyDisplay energyDisplay = new EnergyDisplay(ENERGY_LOCATION, ENERGY_DIMENSIONS, null,
                energyProvider);
        gameObjects().addGameObject(energyDisplay, Layer.UI);
    }

    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
