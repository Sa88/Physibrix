package com.sa.game.camera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.sa.game.DragHandler;
public class CameraGestureListener implements GestureDetector.GestureListener {

    private PerspectiveCamera camera;
    private DragHandler dragHandler;
    private Stage uiStage;

    private float zoomSpeed = 0.1f;
    private final float panSpeed = 0.01f;
    public CameraGestureListener(CameraController cameraController) {
        this.camera = cameraController.getCamera();
    }

    public void setDragHandler(DragHandler dragHandler) {
        this.dragHandler = dragHandler;
    }

    public void setUIStage(Stage stage) {
        this.uiStage = stage;
    }

    private boolean isInteractionBlocked() {
        // Se estiver fazendo drag ou se estiver sobre a UI, bloquear gestos
        if ((dragHandler != null && dragHandler.hasTempBlock()) ||
            (uiStage != null && uiStage.hit(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), true) != null)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {

        if (isInteractionBlocked()) return false;

        Vector3 right = new Vector3();
        right.set(camera.direction).crs(camera.up).nor();

        // Remove a componente Y para manter no plano XZ
        right.y = 0;
        right.nor();

        // Calcula o vetor "forward" (frente) da câmera no plano XZ
        Vector3 forward = new Vector3();
        forward.set(camera.direction);
        forward.y = 0;
        forward.nor();

        // Escala os vetores com a velocidade e movimento do mouse
        right.scl(-deltaX * panSpeed);     // negativo para mover na direção certa
        forward.scl(deltaY * panSpeed);   // negativo para seguir a frente

        // Move a câmera no plano XZ
        camera.position.add(right);
        camera.position.add(forward);

        camera.position.add(right);
        camera.update();
        return true;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {

//        if (isInteractionBlocked()) return false;

        float ratio = initialDistance / distance;
        camera.position.add(camera.direction.cpy().scl((ratio - 1) * zoomSpeed));
        camera.update();
        return true;
    }

    @Override public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        // Nada — sem rotação
        return false;
    }

    // Outros métodos não utilizados
    @Override public void pinchStop() {}
    @Override public boolean fling(float velocityX, float velocityY, int button) { return false; }
    @Override public boolean longPress(float x, float y) { return false; }
    @Override public boolean panStop(float x, float y, int pointer, int button) { return false; }
    @Override public boolean tap(float x, float y, int count, int button) { return false; }
    @Override public boolean touchDown(float x, float y, int pointer, int button) { return false; }


}
