package pepse.interfaces;

/**
 * An interface for observing jump events.
 *
 * <p>Classes that implement this interface should provide an implementation of
 * {@link #updateJump()} to execute logic whenever a jump event occurs. This can be used to
 * handle consequences or triggers in response to a jump action (e.g., updating UI or game state).</p>
 *
 * @author Joshua Kolodny, Itamar Lev Ari
 */
public interface JumpObserver {
    /**
     * Invoked when a jump occurs. Implementing classes define what should happen on a jump event.
     */
    void updateJump();
}
