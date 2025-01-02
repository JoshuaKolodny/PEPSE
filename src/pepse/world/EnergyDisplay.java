package pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.interfaces.EnergyProvider;

public class EnergyDisplay extends GameObject {
    private final EnergyProvider energyProvider;
    private final TextRenderable textRenderable;

    public EnergyDisplay(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                         EnergyProvider energyProvider) {
        super(topLeftCorner, dimensions, renderable);
        this.energyProvider = energyProvider;
        this.textRenderable = new TextRenderable(Integer.toString((int) energyProvider.getCurrentEnergy()));
        this.renderer().setRenderable(textRenderable);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        textRenderable.setString(Integer.toString((int) energyProvider.getCurrentEnergy()));
    }

}
