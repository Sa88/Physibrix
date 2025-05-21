package com.sa.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sa.game.assets.Assets;
import com.sa.game.mode.GameModeManager;
import com.sa.game.mode.GameModeType;
import com.sa.game.mode.GameModeUI;
import com.sa.game.ui.FontFactory;
public class ModeSelectionScreen implements Screen {

    private Stage stage;
    private Skin skin;

    private final Main game;
    private GameModeManager gameModeManager;
    private final SpriteBatch spriteBatch;

    public ModeSelectionScreen(Main game) {
        this.game = game;

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
            game.setScreen(new GameplayScreen(game, selectedMode));
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
        spriteBatch.draw(Assets.getInstance().concrete, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        spriteBatch.dispose();
        stage.dispose();
        skin.dispose();
    }
}
