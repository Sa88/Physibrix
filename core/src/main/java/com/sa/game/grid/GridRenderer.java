package com.sa.game.grid;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
public class GridRenderer {
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final int gridSize = 100; // tamanho total do grid (de -gridSize a +gridSize)
    private final float cellSize = 0.25f; // tamanho da célula
    private boolean buildMode = false;

    public void setBuildMode(boolean enabled) {
        this.buildMode = enabled;
    }

    public void render(Camera camera) {
        if(buildMode) {
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST); // <- Adiciona esta linha
            Gdx.gl.glDepthMask(true);

            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(buildMode ? new Color(0f, 1f, 0f, 0.4f) : Color.LIGHT_GRAY);

            for (int i = -gridSize; i <= gridSize; i++) {
                // Linhas paralelas ao eixo Z
                shapeRenderer.line(
                    new Vector3(i * cellSize, 0, -gridSize * cellSize),
                    new Vector3(i * cellSize, 0, gridSize * cellSize)
                );

                // Linhas paralelas ao eixo X
                shapeRenderer.line(
                    new Vector3(-gridSize * cellSize, 0, i * cellSize),
                    new Vector3(gridSize * cellSize, 0, i * cellSize)
                );
            }

            // Eixo X - Vermelho
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.line(0, 0, 0, gridSize, 0, 0);

            // Eixo Y - Verde
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.line(0, 0, 0, 0, gridSize, 0);

            // Eixo Z - Azul
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.line(0, 0, 0, 0, 0, gridSize);

            shapeRenderer.end();
        }

    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
