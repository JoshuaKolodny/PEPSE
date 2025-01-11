package pepse.interfaces;

/**
 * A functional interface to determine the nearest relevant location based on an integer coordinate.
 *
 * <p>Implementing classes or lambdas should override {@link #getNearestLocation(int)}. This can be used,
 * for instance, in pathfinding or aligning objects to the nearest grid point in the game world.</p>
 *
 * @author Joshua Kolodny, Itamar Lev Ari
 */
@FunctionalInterface
public interface NearestLocationProvider {
    /**
     * Determines the nearest location or grid point for a given x-coordinate.
     *
     * @param x the current x-coordinate
     * @return the integer value representing the nearest location
     */
    int getNearestLocation(int x);
}
