package com.sa.game.physics;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
public class WindRenderer {
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final WindSystem windSystem;

    public WindRenderer(WindSystem windSystem) {
        this.windSystem = windSystem;
    }

    public void render(Camera camera) {
        if (!windSystem.isWindActive()) {
            return;
        }

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Wave center position
        float waveCenter = (windSystem.getWindProgress() * 50f) - 25f;
        float waveWidth = 5f;

        // Draw wave cone/shape
        float intensity = windSystem.getCurrentWindIntensity() / windSystem.getMaxWindIntensity();

        // Color based on intensity
        shapeRenderer.setColor(
            0.7f * intensity,  // Red
            0.8f * intensity,  // Green
            1.0f,              // Blue
            0.3f               // Alpha (transparent)
        );

        // Draw vertical plane showing wind wave
        float gridSize = 50f;
        float waveStart = waveCenter - waveWidth;
        float waveEnd = waveCenter + waveWidth;

        // Draw filled rectangle representing the wind wave
        shapeRenderer.box(
            waveStart,
            -10f,
            -gridSize,
            waveWidth * 2,
            20f,
            gridSize * 2
        );

        shapeRenderer.end();

        // Draw wave center line
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 0f, 1f); // Yellow line for center
        shapeRenderer.line(
            new Vector3(waveCenter, -10f, -gridSize),
            new Vector3(waveCenter, 10f, gridSize)
        );
        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
