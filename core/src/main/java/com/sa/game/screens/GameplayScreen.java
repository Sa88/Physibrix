package com.sa.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.input.GestureDetector;
import com.sa.game.DragHandler;
import com.sa.game.Main;
import com.sa.game.World;
import com.sa.game.camera.CameraControlUI;
import com.sa.game.camera.CameraGestureListener;
import com.sa.game.mode.GameMode;
import com.sa.game.mode.GameModeUIStrategy;
import com.sa.game.mode.creative.CreativeMode;
import com.sa.game.mode.creative.CreativeUIStrategy;
import com.sa.game.mode.mission.MissionMode;
import com.sa.game.mode.mission.MissionUIStrategy;
import com.sa.game.mode.survival.SurvivalMode;
import com.sa.game.mode.survival.SurvivalUIStrategy;
import com.sa.game.ui.UI;

public class GameplayScreen extends ScreenAdapter {

    private final Main game;
    private GameMode gameMode;

    private GameModeUIStrategy modeUIStrategy;

    private World world;
    private UI ui;
    private DragHandler dragHandler;
    private CameraControlUI cameraControlUI;

    private CameraGestureListener gestureListener;

    public GameplayScreen(Main game, GameMode gameMode, World world) {
        this.game = game;
        this.gameMode = gameMode;
        this.world = world;
        this.modeUIStrategy = createUIStrategy(gameMode);
    }

    private GameModeUIStrategy createUIStrategy(GameMode mode) {
        return switch (mode) {
            case CreativeMode creativeMode -> new CreativeUIStrategy();
            case SurvivalMode survivalMode -> new SurvivalUIStrategy();
            case MissionMode missionMode -> new MissionUIStrategy(missionMode.getCurrentMission());
            case null, default -> null;
        };
    }

    @Override
    public void show() {

        if (world == null) {
            world = new World();
        }
        dragHandler = new DragHandler(world);
        ui = new UI(dragHandler, world);
        cameraControlUI = new CameraControlUI();

        modeUIStrategy.initialize(world, ui);

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
        gameMode.update(delta);
        world.render(dragHandler, delta);
        ui.render();
        modeUIStrategy.render(delta);
        cameraControlUI.render(world.getCameraController());
    }

    @Override
    public void dispose() {
        world.dispose();
        ui.dispose();
        cameraControlUI.dispose();
        modeUIStrategy.dispose();
    }
}
