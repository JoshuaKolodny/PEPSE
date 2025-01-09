package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;
import pepse.constants.Constants;
import pepse.interfaces.JumpObserver;
import pepse.world.trees.Fruit;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Avatar extends GameObject {
    // Constants
    public static final Vector2 SIZE = Vector2.ONES.mult(45);
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -650;
    private static final String AVATAR_IMAGE = "src/assets/idle_0.png";
    // Energy
    private static final float MAX_ENERGY = 100;
    private static final float MOVE_REDUCTION = -0.3f;
    private static final float JUMP_REDUCTION = -10;
    private static final float MIN_ENERGY_TO_MOVE = 5;
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
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(other.getTag().equals(Constants.GROUND_TAG)) {
            this.transform().setVelocityY(0);
        } else if (other.getTag().equals(Constants.FRUIT_TAG)) {
            updateEnergy(FRUIT_ENERGY);
            Fruit fruit = (Fruit) other;
            fruit.deactivate();
            new ScheduledTask(fruit, 30, false, fruit::activate);
            }
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // 1. Movement (horizontal, jump, resting)
        handleHorizontalMovement();
        handleJump();
        handleRestingEnergy();

        // 3. Animation
        updateAnimation();
    }

    private void notifyJumpObservers(){
        for (JumpObserver jumpObserver: jumpObservers){
            jumpObserver.updateJump();
        }
    }

    private void handleHorizontalMovement() {
        float xVel = 0;

        // Check if the player wants to move left or right, but only if energy allows
        boolean movingLeft  = inputListener.isKeyPressed(KeyEvent.VK_LEFT)  && energy >= MIN_ENERGY_TO_MOVE;
        boolean movingRight = inputListener.isKeyPressed(KeyEvent.VK_RIGHT) && energy >= MIN_ENERGY_TO_MOVE;

        // Adjust the X velocity
        if (movingLeft) {
            xVel -= VELOCITY_X;
            // Flip horizontally when moving left
            renderer().setIsFlippedHorizontally(true);
        }
        if (movingRight) {
            xVel += VELOCITY_X;
            // No flip (or flip back) when moving right
            renderer().setIsFlippedHorizontally(false);
        }

        // Set the computed velocity
        transform().setVelocityX(xVel);

        // Reduce energy if actually moving horizontally
        if (xVel != 0) {
            updateEnergy(MOVE_REDUCTION);
        }
    }


    private void handleJump() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0
                && energy >= -JUMP_REDUCTION) {
            notifyJumpObservers();
            updateEnergy(JUMP_REDUCTION);
            transform().setVelocityY(VELOCITY_Y);
        }
    }

    private void handleRestingEnergy() {
        if (getVelocity().equals(Vector2.ZERO)) {
            updateEnergy(RESTING_ENERGY);
        }
    }

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
        // Running + jumping could combine in some games,
        // but here we check specifically for vertical movement w/o horizontal
        return (velocity.y() != 0) && (velocity.x() == 0) && (currentAnimation != AnimationTypes.JUMPING);
    }

    private boolean isStill(Vector2 velocity) {
        return velocity.equals(Vector2.ZERO) && currentAnimation != AnimationTypes.STILL;
    }

    private void setAnimation(AnimationTypes newAnimation, String[] paths) {
        currentAnimation = newAnimation;
        renderer().setRenderable(new AnimationRenderable(paths, imageReader, true, TIME_BETWEEN_ANIMATIONS));
    }

    public void updateEnergy(float delta) {
        energy = Math.max(0, Math.min(MAX_ENERGY, energy + delta));
    }

    public float getEnergy() {
        return energy;
    }

    public void addJumpObserver(JumpObserver gameObject) {
        jumpObservers.add(gameObject);
    }
}
