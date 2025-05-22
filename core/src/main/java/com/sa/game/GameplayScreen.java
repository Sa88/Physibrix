package com.sa.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.input.GestureDetector;
import com.sa.game.camera.CameraControlUI;
import com.sa.game.camera.CameraGestureListener;
import com.sa.game.mode.mission.Mission;
import com.sa.game.ui.UI;

public class GameplayScreen extends ScreenAdapter {

    private final Main game;
    private Mission mission;

    private World world;
    private UI ui;
    private DragHandler dragHandler;
    private CameraControlUI cameraControlUI;

    private CameraGestureListener gestureListener;

    public GameplayScreen(Main game) {
        this.game = game;
    }

    public GameplayScreen(Main game, Mission mission) {
        this.game = game;
        this.mission = mission;
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
