package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;
import pepse.constants.Constants;
import pepse.interfaces.JumpObserver;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the player's avatar with basic movement, jumping, and energy mechanics.
 * <p>Handles collisions, animations, and energy state changes (e.g., collecting fruit).</p>
 *
 * <p>Includes a set of {@link JumpObserver} objects to notify when a jump occurs.</p>
 *
 * @author Joshua Kolodny, Itamar Lev Ari
 */
public class Avatar extends GameObject {
    // Constants
    /**
     * The default size of the avatar in the game, represented as a square with side lengths of 45 units.
     */
    public static final Vector2 SIZE = Vector2.ONES.mult(45);
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -650;
    private static final String AVATAR_IMAGE = "src/assets/idle_0.png";

    // Energy
    private static final float MAX_ENERGY = 100;
    private static final float MOVE_REDUCTION = -0.3f;
    private static final float JUMP_REDUCTION = -10;
    private static final float RESTING_ENERGY = 0.5f;

    // Animation
    private static final String[] RUNNING_PATHS = {
            "src/assets/run_0.png",
            "src/assets/run_1.png",
            "src/assets/run_2.png",
            "src/assets/run_3.png",
            "src/assets/run_4.png",
            "src/assets/run_5.png"
    };
    private static final String[] JUMPING_PATHS = {
            "src/assets/jump_0.png",
            "src/assets/jump_1.png",
            "src/assets/jump_2.png",
            "src/assets/jump_3.png"
    };
    private static final String[] STILL_PATHS = {
            "src/assets/idle_0.png",
            "src/assets/idle_1.png",
            "src/assets/idle_2.png",
            "src/assets/idle_3.png"
    };
    private static final double TIME_BETWEEN_ANIMATIONS = 0.1;
    private static final float FRUIT_ENERGY = 10;

    private final UserInputListener inputListener;
    private final ImageReader imageReader;
    private float energy;
    private AnimationTypes currentAnimation;
    private final List<JumpObserver> jumpObservers = new ArrayList<>();

    /**
     * Constructs an avatar with position, user input listener, and image reading capabilities.
     *
     * @param topLeftCorner Initial avatar location.
     * @param inputListener Provides keyboard and mouse inputs.
     * @param imageReader   Loads images for animations.
     */
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, SIZE, imageReader.readImage(AVATAR_IMAGE, true));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(Constants.GRAVITY);
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        energy = MAX_ENERGY;
        currentAnimation = AnimationTypes.STILL;
        renderer().setRenderable(new AnimationRenderable(STILL_PATHS, imageReader,
                true, TIME_BETWEEN_ANIMATIONS));
        this.setTag(Constants.AVATAR_TAG);
    }

    /**
     * Returns {@code false} if the other object's tag is {@code Constants.BLOCK_TAG},
     * otherwise delegates to the superclass.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        if (other.getTag().equals(Constants.BLOCK_TAG)){
            return false;
        }
        return super.shouldCollideWith(other);
    }

    /**
     * Handles collisions with blocks (resets vertical velocity) and fruits (increases energy).
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals(Constants.TOP_BLOCK_TAG)) {
            this.transform().setVelocityY(0);
        } else if (other.getTag().equals(Constants.FRUIT_TAG)) {
            updateEnergy(FRUIT_ENERGY);
        }
    }

    /**
     * Handles input-based movement, jumping, resting, and animating the avatar each frame.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        handleHorizontalMovement();
        handleJump();
        handleRestingEnergy();
        updateAnimation();
    }

    /**
     * Notifies observers that a jump has occurred.
     */
    private void notifyJumpObservers() {
        for (JumpObserver jumpObserver : jumpObservers) {
            jumpObserver.updateJump();
        }
    }

    /**
     * Moves the avatar horizontally based on input, reducing energy when moving.
     */
    private void handleHorizontalMovement() {
        float xVel = 0;

        boolean movingLeft = inputListener.isKeyPressed(KeyEvent.VK_LEFT) && energy > 0;
        boolean movingRight = inputListener.isKeyPressed(KeyEvent.VK_RIGHT) && energy > 0;

        if (movingLeft) {
            xVel -= VELOCITY_X;
            renderer().setIsFlippedHorizontally(true);
        }
        if (movingRight) {
            xVel += VELOCITY_X;
            renderer().setIsFlippedHorizontally(false);
        }

        transform().setVelocityX(xVel);
        if (xVel != 0) {
            updateEnergy(MOVE_REDUCTION);
        }
    }

    /**
     * Allows the avatar to jump if energy and grounded conditions are met.
     */
    private void handleJump() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0
                && energy >= -JUMP_REDUCTION) {
            notifyJumpObservers();
            updateEnergy(JUMP_REDUCTION);
            transform().setVelocityY(VELOCITY_Y);
        }
    }

    /**
     * Replenishes a small amount of energy when the avatar is idle.
     */
    private void handleRestingEnergy() {
        if (getVelocity().equals(Vector2.ZERO)) {
            updateEnergy(RESTING_ENERGY);
        }
    }

    /**
     * Chooses and sets an animation based on the avatar's current motion.
     */
    private void updateAnimation() {
        Vector2 velocity = getVelocity();

        if (isRunning(velocity)) {
            setAnimation(AnimationTypes.RUNNING, RUNNING_PATHS);
        } else if (isJumping(velocity)) {
            setAnimation(AnimationTypes.JUMPING, JUMPING_PATHS);
        } else if (isStill(velocity)) {
            setAnimation(AnimationTypes.STILL, STILL_PATHS);
        }
    }

    private boolean isRunning(Vector2 velocity) {
        return (velocity.x() != 0) && (currentAnimation != AnimationTypes.RUNNING);
    }

    private boolean isJumping(Vector2 velocity) {
        return (velocity.y() != 0) && (velocity.x() == 0) && (currentAnimation != AnimationTypes.JUMPING);
    }

    private boolean isStill(Vector2 velocity) {
        return velocity.equals(Vector2.ZERO) && currentAnimation != AnimationTypes.STILL;
    }

    /**
     * Updates the current animation renderable based on the new animation state.
     */
    private void setAnimation(AnimationTypes newAnimation, String[] paths) {
        currentAnimation = newAnimation;
        renderer().setRenderable(new AnimationRenderable(paths, imageReader, true,
                TIME_BETWEEN_ANIMATIONS));
    }

    /**
     * Adjusts the avatar's energy within allowable bounds.
     *
     * @param delta Change in energy (can be positive or negative).
     */
    public void updateEnergy(float delta) {
        energy = Math.max(0, Math.min(MAX_ENERGY, energy + delta));
    }

    /**
     * Returns the current energy level of the avatar.
     */
    public float getEnergy() {
        return energy;
    }

    /**
     * Subscribes a {@link JumpObserver} to receive jump notifications.
     *
     * @param gameObject The observer to be added.
     */
    public void addJumpObserver(JumpObserver gameObject) {
        jumpObservers.add(gameObject);
    }
}
