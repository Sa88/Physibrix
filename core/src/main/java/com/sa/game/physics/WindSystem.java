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
            
            // Calculate wind force based on block surface area
            // Assume each block is roughly 1x1x1 unit, surface area = 2 (front face)
            float surfaceArea = 2f;
            
            // Wind force = intensity * surface area
            float force = windIntensity * surfaceArea;
            
            // Apply force horizontally (X direction)
            Vector3 windForce = new Vector3(force * windDirection, 0f, 0f);
            body.applyCentralForce(windForce);
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
