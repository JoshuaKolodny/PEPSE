package pepse.world;

import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.interfaces.JumpObserver;
import pepse.interfaces.RainDropper;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a moving cloud structure composed of multiple {@link Block} objects.
 * <p>Once a jump event is detected, this cloud triggers rainfall via {@link RainDropper}.</p>
 *
 * <p>Uses a looping horizontal transition to move each block.</p>
 *
 * <p>See {@link #createBlocks(Vector2)} for block formation and transitions.</p>
 *
 * @author
 *     Joshua Kolodny, Itamar Lev Ari
 */
public class Cloud implements JumpObserver {
    private static final Color BASE_CLOUD_COLOR = new Color(255, 255, 255);
    private static final Vector2 INIT_LOCATION = new Vector2(-Block.SIZE * 8, 3 * Block.SIZE);
    private static final int CLOUD_TRANSITION_TIME = 15;
    private final List<Block> cloudBlocks;
    private RainDropper rainDropper;

    /**
     * Provides a read-only list of the cloud blocks used to form the cloud.
     */
    public List<Block> getCloudBlocks() {
        return cloudBlocks;
    }

    /**
     * Creates a new cloud and initializes its blocks and movement.
     *
     * @param windowDimensions Dimensions used for calculating the transition range.
     */
    public Cloud(Vector2 windowDimensions) {
        this.cloudBlocks = new ArrayList<>();
        createBlocks(windowDimensions);
    }

    /**
     * Sets a {@link RainDropper} to trigger rainfall when a jump is detected.
     */
    public void setRainDropper(RainDropper rainDropper) {
        this.rainDropper = rainDropper;
    }

    /**
     * Builds a 2D pattern of blocks and applies a repeating horizontal shift transition.
     *
     * @param windowDimensions The size of the window for calculating shift distance.
     */
    private void createBlocks(Vector2 windowDimensions) {
        int[][] cloud =
                {{0, 1, 1, 0, 0, 0},
                        {1, 1, 1, 0, 1, 0},
                        {1, 1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 1, 1},
                        {0, 1, 1, 1, 0},
                        {0, 0, 0, 0, 0, 0}};
        for (int i = 0; i < cloud.length; i++) {
            for (int j = 0; j < cloud[i].length; j++) {
                if (cloud[i][j] == 1) {
                    Vector2 blockLocation = new Vector2(INIT_LOCATION.x() + j * Block.SIZE,
                            INIT_LOCATION.y() + i * Block.SIZE);
                    Renderable renderable = new RectangleRenderable(ColorSupplier
                            .approximateMonoColor(BASE_CLOUD_COLOR));
                    Block cloudBlock = new Block(blockLocation, renderable);
                    Consumer<Float> lambdaCloud = (Float shiftRight) ->
                            cloudBlock.setCenter(blockLocation.add(Vector2.RIGHT.mult(shiftRight)));
                    new Transition<>(
                            cloudBlock,
                            lambdaCloud,
                            0f,
                            windowDimensions.x() - INIT_LOCATION.x(),
                            Transition.LINEAR_INTERPOLATOR_FLOAT,
                            CLOUD_TRANSITION_TIME,
                            Transition.TransitionType.TRANSITION_LOOP,
                            null
                    );
                    cloudBlock.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
                    cloudBlocks.add(cloudBlock);
                }
            }
        }
    }

    /**
     * Triggered when a jump occurs. Invokes {@code MakeItRain()} on the assigned {@link RainDropper}.
     */
    @Override
    public void updateJump() {
        rainDropper.MakeItRain();
    }
}
