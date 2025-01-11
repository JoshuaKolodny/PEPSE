package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.interfaces.EnergyProvider;

/**
 * Displays a numeric value representing the player's energy.
 * <p>The {@link EnergyProvider} is polled each frame to update the displayed energy.</p>
 *
 * <p>Uses a {@link TextRenderable} to render the energy value to the screen.</p>
 *
 * <p>Placed in camera coordinates so it remains fixed in the UI.</p>
 *
 * <p>See {@link #update(float)} for refreshing the displayed value.</p>
 *
 * @author
 *     Joshua Kolodny, Itamar Lev Ari
 */
public class EnergyDisplay extends GameObject {
    private final EnergyProvider energyProvider;
    private final TextRenderable textRenderable;

    /**
     * Creates a new energy display with a given provider for energy levels.
     *
     * @param topLeftCorner  Display location on the screen in camera coordinates.
     * @param dimensions     Size of the text display region.
     * @param renderable     Initial background or display {@link Renderable}.
     * @param energyProvider Source of the current energy value.
     */
    public EnergyDisplay(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                         EnergyProvider energyProvider) {
        super(topLeftCorner, dimensions, renderable);
        this.energyProvider = energyProvider;
        this.textRenderable = new TextRenderable(Integer.toString((int) energyProvider.getCurrentEnergy()));
        this.renderer().setRenderable(textRenderable);
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    /**
     * Updates the displayed energy every frame based on the {@link EnergyProvider}.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        textRenderable.setString(Integer.toString((int) energyProvider.getCurrentEnergy()));
    }
}
