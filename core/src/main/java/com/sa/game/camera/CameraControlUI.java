package com.sa.game.camera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
public class CameraControlUI {
    private Stage stage;
    private Skin skin;
    private boolean zoomIn;
    private boolean zoomOut;
    private boolean rotateLeft;
    private boolean rotateRight;
    private boolean reset = false;

    public CameraControlUI() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table table = new Table();
        table.bottom().right();
        table.setFillParent(true);

        TextButton btnZoomIn = new TextButton("-", skin);
        TextButton btnZoomOut = new TextButton("+", skin);
        TextButton btnRotateLeft = new TextButton("ROTATE LEFT", skin);
        TextButton btnRotateRight = new TextButton("ROTATE RIGHT", skin);
        TextButton btnReset = new TextButton("RESET", skin);


        // Listeners para pressionar e soltar
        addHoldListener(btnZoomIn, () -> zoomIn = true, () -> zoomIn = false);
        addHoldListener(btnZoomOut, () -> zoomOut = true, () -> zoomOut = false);
        addHoldListener(btnRotateLeft, () -> rotateLeft = true, () -> rotateLeft = false);
        addHoldListener(btnRotateRight, () -> rotateRight = true, () -> rotateRight = false);

        // Layout dos botões
        table.add().colspan(3);
        table.row();
        table.add(btnZoomIn).width(100).height(100);
        table.add(btnZoomOut).width(100).height(100);
        table.add(btnReset).width(100).height(100);
        table.add();

        btnReset.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                reset = true;
            }
        });

        stage.addActor(table);

        Table tableRotate = new Table();
        tableRotate.top().right();
        tableRotate.setFillParent(true);

        tableRotate.add().colspan(2);
        tableRotate.row();
        tableRotate.add(btnRotateLeft).width(100).height(100);
        tableRotate.add(btnRotateRight).width(100).height(100);

        stage.addActor(tableRotate);
    }

    private void addHoldListener(TextButton button, Runnable onPress, Runnable onRelease) {
        button.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onPress.run();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onRelease.run();
            }
        });
    }



    public void render(CameraController cameraController) {
        if (zoomIn) cameraController.zoom(-1);
        if (zoomOut) cameraController.zoom(1);
        if (rotateLeft) cameraController.rotate(-1);
        if (rotateRight) cameraController.rotate(1);
        if (reset) {
            cameraController.resetCamera();
            reset = false;
        }

        stage.act();
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
