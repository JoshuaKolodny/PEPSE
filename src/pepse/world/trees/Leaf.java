package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.constants.Constants;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Leaf extends GameObject {
    public static final Vector2 SIZE = Vector2.ONES.mult(20);
    private static final float LEAF_ANGLE = 10f;
    private static final float ANGLE_TRANSITION_TIME = 5f;
    private static final float DIMENSION_TRANSITION_TIME = 2.5f;
    private static final float TASK_DELAY = 5;
    private static final Vector2 DIMENSION_WIDTH_ADDITION = Vector2.RIGHT.mult(5);


    public Leaf(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        Consumer<Float> lambdaAngle = angle -> this.renderer().setRenderableAngle(angle);
        Supplier<Transition<Float>> angleSupplier = createAngleSupplier(lambdaAngle);
        Supplier<Transition<Vector2>> widthSupplier = createWidthSupplier(dimensions);
        createTransitionDelay(angleSupplier, widthSupplier);
        this.setTag(Constants.LEAF_TAG);
    }

    private Supplier<Transition<Vector2>> createWidthSupplier(Vector2 dimensions) {
        return () ->
                new Transition<>(
                        this,
                        this::setDimensions,
                        dimensions,
                        dimensions.add(DIMENSION_WIDTH_ADDITION),
                        Transition.LINEAR_INTERPOLATOR_VECTOR,
                        DIMENSION_TRANSITION_TIME,
                        Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                        null);
    }

    private Supplier<Transition<Float>> createAngleSupplier(Consumer<Float> lambdaAngle) {
        return () ->
                new Transition<>(this,
                        lambdaAngle,
                        -LEAF_ANGLE, LEAF_ANGLE,
                        Transition.LINEAR_INTERPOLATOR_FLOAT,
                        ANGLE_TRANSITION_TIME,
                        Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                        null);
    }

    private void createTransitionDelay(Supplier<Transition<Float>> angleSupplier,
                                       Supplier<Transition<Vector2>> widthSupplier) {
        Random random = new Random();
        float taskDelay = random.nextFloat(TASK_DELAY);
        new ScheduledTask(this, taskDelay, false, angleSupplier::get);
        new ScheduledTask(this, taskDelay, false, widthSupplier::get);
    }
}

