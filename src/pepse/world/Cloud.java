package pepse.world;

import danogl.GameObject;
import danogl.collisions.Layer;
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


public class Cloud implements JumpObserver {
    private static final Color BASE_CLOUD_COLOR = new Color(255, 255, 255);
    private static final Vector2 INIT_LOCATION = new Vector2(-Block.SIZE * 8, 3 * Block.SIZE);
    private final List<Block> cloudBlocks;
    private RainDropper rainDropper;

    public List<Block> getCloudBlocks() {
        return cloudBlocks;
    }

    public Cloud(Vector2 windowDimensions) {
        this.cloudBlocks = new ArrayList<>();
        createBlocks(windowDimensions);
    }

    public void setRainDropper(RainDropper rainDropper) {
        this.rainDropper = rainDropper;
    }

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
                    Renderable renderable = new RectangleRenderable(ColorSupplier.approximateMonoColor(BASE_CLOUD_COLOR));
                    Block cloudBlock = new Block(blockLocation, renderable);
                    Consumer<Float> lambdaCloud = (Float shiftRight) ->
                            cloudBlock.setCenter(blockLocation.add
                                    (Vector2.RIGHT.mult(shiftRight)));
                    new Transition<Float>(cloudBlock, lambdaCloud, 0f,
                            windowDimensions.x() - INIT_LOCATION.x(),
                            Transition.LINEAR_INTERPOLATOR_FLOAT, 15,
                            Transition.TransitionType.TRANSITION_LOOP, null);
                    cloudBlock.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
                    cloudBlocks.add(cloudBlock);
                }
            }
        }
    }

    @Override
    public void updateJump() {
        rainDropper.MakeItRain();
    }
}
