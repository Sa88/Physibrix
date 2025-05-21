package com.sa.game.mode.mission;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sa.game.World;

import java.util.List;
public class MissionSelectScreen extends ScreenAdapter {
    private final Stage stage;
    private final World world;
    private final MissionMode missionMode;
    private final Skin skin;

    public MissionSelectScreen(Stage stage, World world, MissionMode missionMode, Skin skin) {
        this.stage = stage;
        this.world = world;
        this.missionMode = missionMode;
        this.skin = skin;
    }

    @Override
    public void show() {
        List<Mission> missions = MissionLoader.loadMissions(world);
        Table table = new Table();
        table.setFillParent(true);
        for (Mission mission : missions) {
            TextButton button = new TextButton(mission.getName(), skin);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    missionMode.startMission(mission);
                    // TODO: switch to gameplay screen
                }
            });
            table.add(button).row();
            Label label = new Label(mission.getDescription(), skin);
            table.add(label).row();
        }
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }
}
