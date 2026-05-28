package com.sa.game.mode.survival;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.sa.game.World;
import com.sa.game.mode.GameModeUIStrategy;
import com.sa.game.ui.UI;


public class SurvivalUIStrategy implements GameModeUIStrategy {
    private Label survivalTimeLabel;
    private Label waveLabel;
    private float elapsedTime = 0f;


    @Override
    public void initialize(World world, UI ui) {
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        survivalTimeLabel = new Label("Time: 0s", skin);
        waveLabel = new Label("Wave: 1", skin);
        ui.getStage().addActor(survivalTimeLabel);
        ui.getStage().addActor(waveLabel);
    }

    @Override
    public void render(float delta) {
        // Update and render survival-specific HUD
        elapsedTime += delta;
        survivalTimeLabel.setText(String.format("Time: %.0fs", elapsedTime));
    }

    @Override
    public void dispose() {}
}
