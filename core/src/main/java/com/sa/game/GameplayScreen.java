package com.sa.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.input.GestureDetector;
import com.sa.game.camera.CameraControlUI;
import com.sa.game.camera.CameraGestureListener;
import com.sa.game.mode.GameModeType;
import com.sa.game.ui.UI;

public class GameplayScreen implements Screen {

    private final Main game;
    private final GameModeType modeType;

    private World world;
    private UI ui;
    private DragHandler dragHandler;
    private CameraControlUI cameraControlUI;

    private CameraGestureListener gestureListener;

    public GameplayScreen(Main game, GameModeType modeType) {
        this.game = game;
        this.modeType = modeType;
    }

    @Override
    public void show() {

        world = new World();
        dragHandler = new DragHandler(world);
        ui = new UI(dragHandler, world);
        cameraControlUI = new CameraControlUI();

        dragHandler.setUIStage(ui.getStage());


        gestureListener = new CameraGestureListener(world.getCameraController());
        gestureListener.setDragHandler(dragHandler);
        gestureListener.setUIStage(ui.getStage());
        GestureDetector gestureDetector = new GestureDetector(gestureListener);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(ui.getStage());
        inputMultiplexer.addProcessor(cameraControlUI.getStage());
        inputMultiplexer.addProcessor(gestureDetector);
        inputMultiplexer.addProcessor(dragHandler);


        Gdx.input.setInputProcessor(inputMultiplexer);

    }
    @Override
    public void render(float delta) {
        world.render(dragHandler);
        ui.render();
        cameraControlUI.render(world.getCameraController());
    }
    @Override
    public void resize(int width, int height) {

    }
    @Override
    public void pause() {

    }
    @Override
    public void resume() {

    }
    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        world.dispose();
        ui.dispose();
        cameraControlUI.dispose();
    }
}
