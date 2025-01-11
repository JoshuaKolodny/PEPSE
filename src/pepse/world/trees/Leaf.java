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

/**
 * Represents a leaf object in the game world.
 *
 * <p>The {@code Leaf} includes logic for continuous transition in angle and width,
 * giving it a swaying and fluttering appearance. This behavior is achieved through
 * several {@link Transition} objects.</p>
 *
 * <p>The leaf uses scheduled tasks to delay the start of these transitions for a
 * more natural, randomized look.</p>
 *
 * <p>This class extends {@link GameObject} to leverage rendering capabilities and
 * transform properties.</p>
 *
 * <p>See {@link #Leaf(Vector2, Vector2, Renderable)} for construction and the private
 * methods for how transitions are set up.</p>
 *
 * @author
 *     Joshua Kolodny, Itamar Lev Ari
 */
public class Leaf extends GameObject {
    /**
     * The default size of each leaf.
     */
    public static final Vector2 SIZE = Vector2.ONES.mult(20);

    private static final float LEAF_ANGLE = 10f;
    private static final float ANGLE_TRANSITION_TIME = 5f;
    private static final float DIMENSION_TRANSITION_TIME = 2.5f;
    private static final float TASK_DELAY = 5;
    private static final Vector2 DIMENSION_WIDTH_ADDITION = Vector2.RIGHT.mult(5);

    /**
     * Constructs a new leaf at the specified location, with randomized transition behaviors.
     *
     * @param topLeftCorner the top-left corner of the leaf
     * @param dimensions    the dimensions of the leaf
     * @param renderable    the {@link Renderable} for this leaf
     */
    public Leaf(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);

        // Lambda function to update the rendering angle of the leaf.
        Consumer<Float> lambdaAngle = angle -> this.renderer().setRenderableAngle(angle);

        // Create suppliers that generate new Transition objects for angle and width.
        Supplier<Transition<Float>> angleSupplier = createAngleSupplier(lambdaAngle);
        Supplier<Transition<Vector2>> widthSupplier = createWidthSupplier(dimensions);

        // Randomly schedule the start of the transitions to avoid synchronized movement of leaves.
        createTransitionDelay(angleSupplier, widthSupplier);

    }

    /**
     * Creates a supplier for a {@link Transition} that oscillates the leaf's width.
     *
     * @param dimensions The original dimensions of the leaf
     * @return A supplier that, when invoked, returns a Transition object to change the leaf's width.
     */
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
                        null
                );
    }

    /**
     * Creates a supplier for a {@link Transition} that oscillates the leaf's angle.
     *
     * @param lambdaAngle A consumer that sets the leaf's renderable angle.
     * @return A supplier that, when invoked, returns a Transition object to sway the leaf's angle.
     */
    private Supplier<Transition<Float>> createAngleSupplier(Consumer<Float> lambdaAngle) {
        return () ->
                new Transition<>(
                        this,
                        lambdaAngle,
                        -LEAF_ANGLE,
                        LEAF_ANGLE,
                        Transition.LINEAR_INTERPOLATOR_FLOAT,
                        ANGLE_TRANSITION_TIME,
                        Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                        null
                );
    }

    /**
     * Randomly delays the start of the angle and width transitions to produce
     * asynchronous leaf flutter effects.
     *
     * @param angleSupplier The supplier that generates the angle transition.
     * @param widthSupplier The supplier that generates the width transition.
     */
    private void createTransitionDelay(Supplier<Transition<Float>> angleSupplier,
                                       Supplier<Transition<Vector2>> widthSupplier) {
        Random random = new Random();
        float taskDelay = random.nextFloat(TASK_DELAY);

        // Schedule both transitions to start after a random delay.
        new ScheduledTask(this, taskDelay, false, angleSupplier::get);
        new ScheduledTask(this, taskDelay, false, widthSupplier::get);
    }
}
