package com.sa.game.physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.sa.game.blocks.Block;

import java.util.List;

/**
 * WindSystem applies realistic wind forces to structures.
 * Wind pushes blocks horizontally and can cause structures to collapse.
 */
public class WindSystem {

    private float windIntensity = 0f;
    private float maxWindIntensity = 100f;
    private float windDuration = 0f;
    private float windElapsed = 0f;
    private boolean isWindActive = false;
    private float windDirection = 1f; // 1 for positive X, -1 for negative X

    private static final float WIND_RAMP_UP = 0.5f; // seconds to reach max wind
    private static final float WIND_RAMP_DOWN = 0.3f; // seconds to stop wind

    public void startWind(float intensity, float duration) {
        this.maxWindIntensity = intensity;
        this.windDuration = duration;
        this.windElapsed = 0f;
        this.isWindActive = true;
        this.windIntensity = 0f;
        // Randomly choose wind direction
        this.windDirection = Math.random() > 0.5f ? 1f : -1f;
    }

    public void stopWind() {
        this.isWindActive = false;
    }

    public void update(float deltaTime, List<Block> blocks) {
        if (!isWindActive) {
            return;
        }

        windElapsed += deltaTime;

        if (windElapsed >= windDuration) {
            isWindActive = false;
            windIntensity = 0f;
            return;
        }

        // Calculate wind intensity with ramp-up and ramp-down
        float timeFromStart = windElapsed;
        float timeToEnd = windDuration - windElapsed;
        float rampUpProgress = Math.min(1f, timeFromStart / WIND_RAMP_UP);
        float rampDownProgress = Math.min(1f, timeToEnd / WIND_RAMP_DOWN);
        float intensityFactor = rampUpProgress * rampDownProgress;

        windIntensity = maxWindIntensity * intensityFactor;

        // Apply wind force to all blocks
        applyWindForce(blocks, deltaTime);
    }

    private void applyWindForce(List<Block> blocks, float deltaTime) {
        for (Block block : blocks) {
            btRigidBody body = block.getBody();
            if (body == null || body.isKinematicObject()) {
                continue;
            }

            // Get block position
            Vector3 blockPos = block.getModelInstance().transform.getTranslation(new Vector3());

            // Wave center moves across X axis over the wind duration
            float progress = windElapsed / windDuration;
            float waveCenter = (progress * 50f) - 25f;  // Moves from -25 to +25

            // Distance from wave center
            float distanceFromWave = Math.abs(blockPos.x - waveCenter);
            float waveWidth = 5f;
            float waveFalloff = (float) Math.exp(-distanceFromWave * distanceFromWave / (waveWidth * waveWidth));

            // Only apply force if block is in the wave
            if (waveFalloff < 0.01f) {
                continue;
            }

            // Get real surface area from bounding box
            float surfaceArea = block.getBoundingBox().getWidth() * block.getBoundingBox().getHeight();

            // MUCH STRONGER force to overcome constraints
            float baseForce = windIntensity * surfaceArea * waveFalloff * 10f;  // 10x multiplier

            // Add turbulence
            float turbulence = 1f + (float) Math.sin(blockPos.y * 10 + blockPos.z * 10 + windElapsed * 5) * 0.3f;
            float finalForce = baseForce * turbulence;

            // Use IMPULSE instead of continuous force (better for constraints)
            Vector3 windImpulse = new Vector3(
                finalForce * windDirection * deltaTime,
                finalForce * 0.15f * deltaTime,
                (float) Math.sin(windElapsed * 3 + blockPos.z) * finalForce * 0.2f * deltaTime
            );

            // Apply impulse at block center
            Vector3 blockCenter = block.getModelInstance().transform.getTranslation(new Vector3());
            body.applyImpulse(windImpulse, blockCenter);
        }
    }

    public float getCurrentWindIntensity() {
        return windIntensity;
    }

    public float getMaxWindIntensity() {
        return maxWindIntensity;
    }

    public boolean isWindActive() {
        return isWindActive;
    }

    public float getWindProgress() {
        if (!isWindActive || windDuration == 0f) {
            return 0f;
        }
        return windElapsed / windDuration;
    }

    public float getTimeRemaining() {
        if (!isWindActive) {
            return 0f;
        }
        return Math.max(0f, windDuration - windElapsed);
    }
}
