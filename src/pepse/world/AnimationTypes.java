package pepse.world;

/**
 * An enumeration of possible animation types for in-game objects (e.g. an avatar or NPC).
 *
 * <ul>
 *     <li>{@link #STILL}: The object is idle with no movement.</li>
 *     <li>{@link #RUNNING}: The object is in a running state.</li>
 *     <li>{@link #JUMPING}: The object is in a jumping state.</li>
 * </ul>
 *
 * <p>This enum can be used to switch between different sprite sheets or animations
 * in the rendering system based on the current action of a character.</p>
 **
 * @author
 *     Joshua Kolodny, Itamar Lev Ari
 */
public enum AnimationTypes {
    /**
     * The object is idle with no movement.
     */
    STILL,

    /**
     * The object is in a running state, typically involving horizontal movement.
     */
    RUNNING,

    /**
     * The object is in a jumping state, typically involving vertical movement.
     */
    JUMPING
}
