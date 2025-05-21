package com.sa.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sa.game.assets.Assets;
import com.sa.game.ui.FontFactory;
public class LoadingScreen extends ScreenAdapter {

    private final Main game;
    private final SpriteBatch batch;
    private final BitmapFont font;

    public LoadingScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.font = FontFactory.generateBigFont(64); // ou carregar de asset
        Assets.getInstance().loadAllAsync(); // Inicia o carregamento
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Assets.getInstance().update()) {
            Assets.getInstance().assignTexturesIfLoaded();
            game.setScreen(new ModeSelectionScreen(game));
        } else {
            float progress = Assets.getInstance().getProgress();
            batch.begin();
            font.draw(batch, "Loading... " + (int)(progress * 100) + "%", 100, 150);
            batch.end();
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
