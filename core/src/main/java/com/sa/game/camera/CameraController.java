package com.sa.game.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class CameraController {
    private PerspectiveCamera camera;
    private final Vector3 initialPosition = new Vector3(10f, 10f, 0f);
    private final Vector3 initialLookAt = new Vector3(0f, 5f, 0f);

    public CameraController() {
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        resetCamera();
    }

    public void update(float delta) {
        camera.viewportWidth = Gdx.graphics.getWidth();
        camera.viewportHeight = Gdx.graphics.getHeight();
        camera.update();
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public void move(float dx, float dy, float dz) {
        float moveSpeed = 10f;
        Vector3 direction = new Vector3(dx, dy, dz).nor().scl(moveSpeed * Gdx.graphics.getDeltaTime());

        if(camera.position.x + dx > -40f && camera.position.x + dx < 40f && camera.position.z + dz > -40f && camera.position.z + dz < 40f) {
            camera.position.add(direction);
            camera.update();
        }

    }

    public void zoom(float amount) {

        Vector3 direction = new Vector3(camera.direction).nor();
        float zoomSpeed = 1f;
        Vector3 newPosition = new Vector3(camera.position).add(direction.scl(amount * zoomSpeed));

        if (newPosition.y >= 5f && newPosition.y <= 20f) {
            camera.position.set(newPosition);
            camera.update();
        }
    }

    public void rotate(float degrees) {
        camera.rotateAround(camera.position, new Vector3(0f,1f,0f), degrees);
        camera.update();
    }


    public void resetCamera() {
        camera.position.set(initialPosition);
        camera.up.set(Vector3.Y);
        camera.lookAt(initialLookAt);
        camera.near = 0.1f;
        camera.far = 100f;
        camera.update();
    }

}
