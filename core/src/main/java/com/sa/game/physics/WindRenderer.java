package com.sa.game.physics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.sa.game.ui.FontFactory;

import java.util.Locale;

/**
 * Enhanced WindRenderer provides realistic wind visualization with:
 * - Multi-layer wind flow particles
 * - Dynamic wave propagation
 * - Pressure gradient visualization
 * - Turbulence effects
 * - Height-based wind variation
 * - Directional flow indicators
 */
public class WindRenderer {
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private final BitmapFont font;
    private final SpriteBatch spriteBatch;
    private final WindSystem windSystem;

    // Wind visualization parameters
    private static final float GRID_SIZE = 50f;
    private static final float WAVE_HEIGHT = 20f;
    private static final int FLOW_LINES = 12;
    private static final int WAVE_PARTICLES = 30;
    private static final float PARTICLE_SPEED = 0.3f;

    private static final float FORCE_TO_KMPH_CONVERSION = 0.36f; // Conversion factor from force units to km/h

    public WindRenderer(WindSystem windSystem) {
        this.windSystem = windSystem;
        this.spriteBatch = new SpriteBatch();
        this.font = FontFactory.generateBigFont(16);
    }

    public void render(Camera camera) {
        if (!windSystem.isWindActive()) {
            return;
        }

        shapeRenderer.setProjectionMatrix(camera.combined);

        float waveCenter = (windSystem.getWindProgress() * 50f) - 25f;
        float intensity = windSystem.getCurrentWindIntensity() / windSystem.getMaxWindIntensity();

        // Layer 1: Draw base pressure gradient (low alpha)
        drawPressureGradient(waveCenter, intensity);

        // Layer 2: Draw wind flow lines showing direction
        drawFlowLines(waveCenter, intensity);

        // Layer 3: Draw wind particles for motion effect
        drawWindParticles(waveCenter, intensity);

        // Layer 4: Draw wave propagation front (the main wind wave)
        drawWaveFont(waveCenter, intensity);

        // Layer 5: Draw height-based turbulence visualization
        drawTurbulenceLayer(waveCenter, intensity);

        // Layer 6: Draw intensity indicators (center lines and edges)
        drawIntensityIndicators(waveCenter, intensity);

        renderWindSpeedUI();
    }

    /**
     * Renders the wind speed UI on screen (HUD)
     * Displays speed in km/h and other wind statistics
     */
    private void renderWindSpeedUI() {
        spriteBatch.begin();

        // Calculate wind speed in km/h
        float windSpeedKmph = calculateWindSpeedKmph();

        // Create readable text with shadow effect
        String windText = String.format(Locale.ENGLISH, "Wind Speed: %.1f km/h", windSpeedKmph);
        String intensityText = String.format(Locale.ENGLISH,"Intensity: %.0f%%",
            (windSystem.getCurrentWindIntensity() / windSystem.getMaxWindIntensity()) * 100f);
        String timeText = String.format(Locale.ENGLISH,"Time Remaining: %.1f s", windSystem.getTimeRemaining());

        // Set font color based on intensity
        float intensity = windSystem.getCurrentWindIntensity() / windSystem.getMaxWindIntensity();
        Color textColor = getColorByIntensity(intensity);
        font.setColor(textColor);

        // Draw shadow (black offset text)
        font.setColor(0, 0, 0, 0.7f);
        font.draw(spriteBatch, windText, 20f, 700f);
        font.draw(spriteBatch, intensityText, 20f, 670f);
        font.draw(spriteBatch, timeText, 20f, 640f);

        // Draw main text (colored)
        font.setColor(textColor);
        font.draw(spriteBatch, windText, 18f, 702f);
        font.draw(spriteBatch, intensityText, 18f, 672f);
        font.draw(spriteBatch, timeText, 18f, 642f);

        spriteBatch.end();
    }

    /**
     * Calculates wind speed in km/h from the current wind intensity
     * Conversion: Wind intensity force -> km/h using realistic scaling
     */
    private float calculateWindSpeedKmph() {
        float windIntensity = windSystem.getCurrentWindIntensity();

        // Convert force units to km/h
        // Formula: The higher the intensity, the higher the speed
        // Max intensity (100) should correspond to a significant wind speed (e.g., 100+ km/h)
        float kmph = windIntensity * FORCE_TO_KMPH_CONVERSION;

        return Math.max(0f, kmph); // Ensure no negative values
    }

    /**
     * Returns a color based on wind intensity for visual feedback
     * Low intensity: Green/Blue
     * Medium intensity: Yellow/Orange
     * High intensity: Red
     */
    private Color getColorByIntensity(float intensity) {
        if (intensity < 0.3f) {
            // Low wind: Blue-Green
            return new Color(0.3f, 0.8f, 1f, 1f);
        } else if (intensity < 0.6f) {
            // Medium wind: Yellow-Orange
            float blend = (intensity - 0.3f) / 0.3f;
            return new Color(1f, 0.5f + (0.3f * blend), 0.2f, 1f);
        } else {
            // High wind: Orange-Red
            float blend = (intensity - 0.6f) / 0.4f;
            return new Color(1f, Math.max(0.2f, 0.8f - (0.6f * blend)), 0.1f, 1f);
        }
    }

    /**
     * Draws a pressure gradient showing where the wind will hit hardest.
     * Uses a Gaussian distribution for realistic wind spread.
     */
    private void drawPressureGradient(float waveCenter, float intensity) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float waveWidth = 8f + (intensity * 4f); // Width increases with intensity

        // Draw multiple overlapping boxes for gradient effect
        for (int layer = 5; layer >= 0; layer--) {
            float layerWidth = waveWidth * (1f + layer * 0.3f);
            float layerAlpha = 0.08f * intensity * (1f - (layer / 6f));
            float layerIntensity = 1f - (layer / 6f);

            // Color: low intensity (blue) -> high intensity (red/orange)
            float red = Math.min(1f, layerIntensity * 1.5f);
            float green = Math.max(0f, 1f - layerIntensity);
            float blue = Math.max(0.2f, 1f - layerIntensity * 0.8f);

            shapeRenderer.setColor(red, green, blue, layerAlpha);

            shapeRenderer.box(
                waveCenter - layerWidth,
                -5f,
                -GRID_SIZE,
                layerWidth * 2,
                WAVE_HEIGHT + 10f,
                GRID_SIZE * 2
            );
        }

        shapeRenderer.end();
    }

    /**
     * Draws directional flow lines showing wind direction and speed.
     * Lines are wavy to indicate turbulence.
     */
    private void drawFlowLines(float waveCenter, float intensity) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        float lineSpacing = WAVE_HEIGHT / FLOW_LINES;
        float waveAmplitude = 2f + (intensity * 2f); // Amplitude increases with intensity
        float windProgress = windSystem.getWindProgress();

        for (int i = 0; i < FLOW_LINES; i++) {
            float y = -WAVE_HEIGHT / 2f + (i * lineSpacing);

            // Color intensity based on height (stronger at middle)
            float heightFactor = 1f - (Math.abs(i - FLOW_LINES / 2f) / (FLOW_LINES / 2f));
            float lineIntensity = intensity * heightFactor;

            // Color gradient
            float red = Math.min(1f, lineIntensity * 1.5f);
            float green = Math.max(0.3f, 1f - lineIntensity);
            float blue = Math.max(0.4f, 1f - lineIntensity * 0.6f);

            shapeRenderer.setColor(red, green, blue, 0.5f * lineIntensity);

            // Create wavy lines for turbulence effect
            float segmentLength = 2f;
            Vector3 prevPoint = new Vector3(waveCenter - 15f, y, -GRID_SIZE);

            for (float x = waveCenter - 15f; x <= waveCenter + 15f; x += segmentLength) {
                float wobble = (float) Math.sin((x - waveCenter) * 0.3f + windProgress * 5f + i) * waveAmplitude;
                float turbulence = (float) Math.cos(windProgress * 3f + i * 0.5f) * waveAmplitude * 0.5f;

                Vector3 currentPoint = new Vector3(
                    x + wobble,
                    y + turbulence * 0.3f,
                    -GRID_SIZE + turbulence * 0.2f
                );

                shapeRenderer.line(prevPoint, currentPoint);
                prevPoint = currentPoint;
            }
        }

        shapeRenderer.end();
    }

    /**
     * Draws particle-like dots showing wind motion through space.
     * Particles move in the wind direction and fade with distance from center.
     */
    private void drawWindParticles(float waveCenter, float intensity) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float waveWidth = 6f + (intensity * 3f);
        float windProgress = windSystem.getWindProgress();

        for (int i = 0; i < WAVE_PARTICLES; i++) {
            // Calculate particle position using Perlin-like noise
            float particleOffset = (i / (float) WAVE_PARTICLES) - 0.5f;
            float particleX = waveCenter + (particleOffset * waveWidth * 3f);
            float particleY = -WAVE_HEIGHT / 2f + ((i % 6) / 6f) * WAVE_HEIGHT;

            // Animate particle Z position based on wind progress
            float animationCycle = (windProgress + i * 0.1f) % 1f;
            float particleZ = -GRID_SIZE + (animationCycle * GRID_SIZE * 2f);

            // Particle alpha fades based on distance from wave center
            float distanceFromCenter = Math.abs(particleX - waveCenter);
            float distanceFalloff = (float) Math.exp(-distanceFromCenter * distanceFromCenter / (waveWidth * waveWidth));
            float alpha = intensity * distanceFalloff * 0.4f;

            if (alpha > 0.02f) {
                // Color based on intensity
                float red = Math.min(1f, intensity * 1.8f);
                float green = Math.max(0.2f, 1f - intensity * 0.9f);
                float blue = Math.max(0.3f, 1f - intensity * 0.7f);

                shapeRenderer.setColor(red, green, blue, alpha);

                // Draw particle as small sphere approximation
                float particleSize = 0.2f + (intensity * 0.3f);
                shapeRenderer.box(
                    particleX - particleSize,
                    particleY - particleSize,
                    particleZ - particleSize,
                    particleSize * 2,
                    particleSize * 2,
                    particleSize * 2
                );
            }
        }

        shapeRenderer.end();
    }

    /**
     * Draws the main wind wave front with expanding cone effect.
     * The cone expands as intensity increases.
     */
    private void drawWaveFont(float waveCenter, float intensity) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float baseWaveWidth = 3f;
        float expandedWaveWidth = baseWaveWidth + (intensity * 5f); // Cone expands with intensity
        float waveHeight = WAVE_HEIGHT * (0.8f + intensity * 0.2f);

        // Main wave front - solid bright color
        float red = Math.min(1f, intensity * 2f);
        float green = Math.max(0.4f, 1f - intensity);
        float blue = Math.min(1f, 0.3f + intensity);

        shapeRenderer.setColor(red, green, blue, 0.4f * intensity);

        shapeRenderer.box(
            waveCenter - expandedWaveWidth,
            -waveHeight / 2f,
            -GRID_SIZE,
            expandedWaveWidth * 2,
            waveHeight,
            GRID_SIZE * 2
        );

        shapeRenderer.end();
    }

    /**
     * Draws height-based turbulence showing how wind varies at different heights.
     * Higher intensity creates more pronounced turbulence.
     */
    private void drawTurbulenceLayer(float waveCenter, float intensity) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        float waveWidth = 5f + (intensity * 2f);
        float windProgress = windSystem.getWindProgress();
        int turbulenceLines = (int) (5 + intensity * 5);

        for (int layer = 0; layer < turbulenceLines; layer++) {
            float layerHeight = -WAVE_HEIGHT / 2f + (layer / (float) turbulenceLines) * WAVE_HEIGHT;

            // Turbulence increases with height variation
            float heightVariation = Math.abs(layerHeight) / (WAVE_HEIGHT / 2f);
            float turbulenceAmplitude = intensity * heightVariation * 3f;

            float alpha = intensity * (1f - heightVariation * 0.5f) * 0.3f;
            shapeRenderer.setColor(1f, 0.6f, 0.2f, alpha); // Orange for turbulence

            // Draw turbulent wave pattern
            float segmentLength = 1.5f;
            Vector3 prevPoint = new Vector3(waveCenter - waveWidth * 2, layerHeight, -GRID_SIZE);

            for (float x = waveCenter - waveWidth * 2; x <= waveCenter + waveWidth * 2; x += segmentLength) {
                float turbulentWobble = (float) Math.sin(
                    (x - waveCenter) * 0.5f + windProgress * 4f + layer * 0.3f
                ) * turbulenceAmplitude;

                Vector3 currentPoint = new Vector3(
                    x,
                    layerHeight + turbulentWobble,
                    -GRID_SIZE + (float) Math.cos(windProgress * 3f + layer) * 2f
                );

                shapeRenderer.line(prevPoint, currentPoint);
                prevPoint = currentPoint;
            }
        }

        shapeRenderer.end();
    }

    /**
     * Draws intensity indicators: center line and edge markers.
     * These help players understand wind direction and intensity.
     */
    private void drawIntensityIndicators(float waveCenter, float intensity) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        float indicatorWidth = 8f + (intensity * 4f);

        // Center line - bright indicator of wind direction
        shapeRenderer.setColor(1f, 1f, 0f, 0.8f * intensity); // Yellow

        shapeRenderer.line(
            new Vector3(waveCenter, -WAVE_HEIGHT / 2f, -GRID_SIZE),
            new Vector3(waveCenter, WAVE_HEIGHT / 2f, GRID_SIZE)
        );

        // Edge markers - show wind cone boundaries
        shapeRenderer.setColor(0.8f, 0.3f, 0.8f, 0.5f * intensity); // Magenta

        // Left edge
        shapeRenderer.line(
            new Vector3(waveCenter - indicatorWidth, -WAVE_HEIGHT / 2f, -GRID_SIZE),
            new Vector3(waveCenter - indicatorWidth, WAVE_HEIGHT / 2f, GRID_SIZE)
        );

        // Right edge
        shapeRenderer.line(
            new Vector3(waveCenter + indicatorWidth, -WAVE_HEIGHT / 2f, -GRID_SIZE),
            new Vector3(waveCenter + indicatorWidth, WAVE_HEIGHT / 2f, GRID_SIZE)
        );

        shapeRenderer.end();

        // Draw directional arrow at top
        drawDirectionalArrow(waveCenter, WAVE_HEIGHT / 2f, intensity);
    }

    /**
     * Draws an arrow indicating wind direction (positive or negative X).
     */
    private void drawDirectionalArrow(float x, float y, float intensity) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float arrowSize = 2f;
        float windDirection = windSystem.getWindDirection();

        shapeRenderer.setColor(1f, 0.5f, 0f, 0.8f * intensity); // Orange arrow

        // Arrow shaft
        shapeRenderer.line(
            new Vector3(x - arrowSize * windDirection, y, 0),
            new Vector3(x + arrowSize * windDirection * 2, y, 0)
        );

        // Arrow head - left side
        shapeRenderer.line(
            new Vector3(x + arrowSize * windDirection * 2, y, 0),
            new Vector3(x + arrowSize * windDirection, y + arrowSize, 0)
        );

        // Arrow head - right side
        shapeRenderer.line(
            new Vector3(x + arrowSize * windDirection * 2, y, 0),
            new Vector3(x + arrowSize * windDirection, y - arrowSize, 0)
        );

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
