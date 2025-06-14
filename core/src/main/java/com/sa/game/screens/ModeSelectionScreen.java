package com.sa.game.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sa.game.Main;
import com.sa.game.assets.Assets;
import com.sa.game.assets.TextureType;
import com.sa.game.mode.GameModeManager;
import com.sa.game.mode.GameModeType;
import com.sa.game.mode.GameModeUI;
import com.sa.game.ui.FontFactory;
public class ModeSelectionScreen extends ScreenAdapter {

    private final Stage stage;
    private final Skin skin;

    private final GameModeManager gameModeManager;
    private final SpriteBatch spriteBatch;

    public ModeSelectionScreen(Main game) {

        spriteBatch = new SpriteBatch();

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        BitmapFont bigFont = FontFactory.generateBigFont(64);
        skin.add("bigFont", bigFont);

        gameModeManager = new GameModeManager(GameModeType.CREATIVE.createInstance());

        GameModeUI gameModeUI = new GameModeUI(skin, gameModeManager);

        // Adiciona o menu de seleção
        stage.addActor(gameModeUI.getRoot());

        // Define que a entrada vai ser processada pela Stage
        Gdx.input.setInputProcessor(stage);

        // Quando o botão for clicado, troca para o GameplayScreen
        gameModeUI.setStartButtonListener(() -> {
            GameModeType selectedMode = gameModeUI.getSelectedMode();
            switch (selectedMode) {
                case CREATIVE: game.setScreen(new GameplayScreen(game));
                    break;
                case MISSION: game.setScreen(new MissionSelectScreen(game, gameModeManager.getCurrentMode(), skin));
                    break;
                case SURVIVAL: game.setScreen(new GameplayScreen(game));
                break;
            }
        });
    }

    @Override
    public void show() {
        // Já foi inicializado no construtor
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(Assets.getInstance().get(TextureType.CONCRETE), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
