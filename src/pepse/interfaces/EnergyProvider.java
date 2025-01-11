package pepse.interfaces;

/**
 * A functional interface representing a provider for an entity's current energy.
 *
 * <p>Implementing classes or lambdas should override {@link #getCurrentEnergy()} to supply the
 * current energy level of an entity or system. The energy value can be used in game mechanics
 * such as movement, consumption, or any feature that relies on energy levels.</p>
 *
 * @author Joshua Kolodny, Itamar Lev Ari
 */
@FunctionalInterface
public interface EnergyProvider {
    /**
     * Retrieves the current energy level.
     *
     * @return the current energy level as a float
     */
    float getCurrentEnergy();
}
