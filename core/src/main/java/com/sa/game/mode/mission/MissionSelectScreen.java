package com.sa.game.mode.mission;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sa.game.GameplayScreen;
import com.sa.game.LoadingScreen;
import com.sa.game.Main;
import com.sa.game.World;
import com.sa.game.assets.Assets;
import com.sa.game.mode.GameMode;

import java.util.List;
public class MissionSelectScreen extends ScreenAdapter {

    private final Main main;
    private final Stage stage;
    private final World world;
    private final MissionMode missionMode;
    private final Skin skin;
    private final SpriteBatch spriteBatch;

    public MissionSelectScreen(Main main, GameMode gameMode, Skin skin) {
        this.main = main;
        this.stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        this.world = new World();
        this.missionMode = (MissionMode) gameMode;
        this.skin = skin;
        this.spriteBatch = new SpriteBatch();
    }

    @Override
    public void show() {

        BitmapFont bigFont = skin.getFont("bigFont");

        Gdx.input.setInputProcessor(stage);

        List<Mission> missions = MissionLoader.loadMissions(world);
        Table table = new Table();
        table.setFillParent(true);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = bigFont;

        TextButton backButton = new TextButton("Back", textButtonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new LoadingScreen(main));
            }
        });


        for (Mission mission : missions) {
            TextButton button = new TextButton(mission.getName(), textButtonStyle);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    missionMode.startMission(mission);
                    main.setScreen(new GameplayScreen(main, mission));
                }
            });
            table.add(button).width(80f).height(80f).row();
            Label label = new Label(mission.getDescription(), skin, "bigFont", Color.BLACK);
            table.add(label).row();
        }

        table.add(backButton).width(80f).height(80f).row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(Assets.getInstance().steel, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        stage.dispose();
        skin.dispose();
    }
}
