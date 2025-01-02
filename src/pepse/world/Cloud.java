package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import pepse.constants.Constants;

public class Cloud {

    private static final String CLOUD_IMAGE = "src/assets/cloud.jpeg";

    public static GameObject create(ImageReader imageReader){
        ImageRenderable cloudImage = imageReader.readImage(CLOUD_IMAGE, false);
        GameObject cloud = new GameObject(new Vector2(100,40), new Vector2(300,300),
                cloudImage);
        cloud.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        cloud.setTag(Constants.CLOUD_TAG);
        return cloud;
    }
}
