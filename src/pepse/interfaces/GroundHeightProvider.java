package pepse.interfaces;

/**
 * A functional interface used to provide the ground height at a specific horizontal coordinate.
 *
 * <p>Implementers should override {@link #getGroundHeight(float)} to determine the height of the
 * ground at the given x-coordinate. The ground height is useful for terrain generation and
 * collision detection in a 2D game environment.</p>
 *
 * @author Joshua Kolodny, Itamar Lev Ari
 */
@FunctionalInterface
public interface GroundHeightProvider {
    /**
     * Returns the height of the ground at the specified x-coordinate.
     *
     * @param x the horizontal coordinate
     * @return the ground height as a float
     */
    float getGroundHeight(float x);
}
