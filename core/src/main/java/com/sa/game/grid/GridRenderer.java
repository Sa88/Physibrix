package com.sa.game.grid;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import lombok.Getter;
import lombok.Setter;
public class GridRenderer {
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private static final int GRID_SIZE = 100; // grid size (from -gridSize to +gridSize)
    private static final float CELL_SIZE = 0.25f;

    @Getter
    @Setter
    private boolean buildMode = false;

    public void render(Camera camera) {
        if(buildMode) {
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl.glDepthMask(true);

            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(buildMode ? new Color(0f, 1f, 0f, 0.4f) : Color.LIGHT_GRAY);

            for (int i = -GRID_SIZE; i <= GRID_SIZE; i++) {
                // Lines parallel to the Z-axis
                shapeRenderer.line(
                    new Vector3(i * CELL_SIZE, 0, -GRID_SIZE * CELL_SIZE),
                    new Vector3(i * CELL_SIZE, 0, GRID_SIZE * CELL_SIZE)
                );
                // Lines parallel to the X-axis
                shapeRenderer.line(
                    new Vector3(-GRID_SIZE * CELL_SIZE, 0, i * CELL_SIZE),
                    new Vector3(GRID_SIZE * CELL_SIZE, 0, i * CELL_SIZE)
                );
            }

            // X-axis - Red
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.line(0, 0, 0, GRID_SIZE, 0, 0);

            // Y-axis - Green
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.line(0, 0, 0, 0, GRID_SIZE, 0);

            // Z-axis - Blue
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.line(0, 0, 0, 0, 0, GRID_SIZE);

            shapeRenderer.end();
        }

    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
