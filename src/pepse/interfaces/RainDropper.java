package pepse.interfaces;

/**
 * A functional interface representing a trigger to initiate rainfall in the game world.
 *
 * <p>Classes or lambdas implementing this interface should override {@link #MakeItRain()} to
 * start raining in the game environment. This can be used for weather systems or water-based
 * mechanics.</p>
 *
 * @author Joshua Kolodny, Itamar Lev Ari
 */
@FunctionalInterface
public interface RainDropper {
    /**
     * Triggers rainfall in the game world.
     */
    void MakeItRain();
}
