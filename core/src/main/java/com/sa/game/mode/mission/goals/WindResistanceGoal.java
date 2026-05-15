package com.sa.game.mode.mission.goals;

import com.sa.game.World;
import com.sa.game.physics.WindSystem;

/**
 * WindResistanceGoal tests if a structure can withstand wind forces.
 * The goal is reached if the structure survives the specified wind intensity and duration.
 */
public class WindResistanceGoal implements MissionGoal {

    private final World world;
    private final int windValue;
    private final int windDuration;
    private WindSystem windSystem;
    private boolean windTestStarted = false;
    private boolean structureCollapsed = false;

    public WindResistanceGoal(World world, int value, int duration) {
        this.world = world;
        this.windValue = value;
        this.windDuration = duration;
    }

    @Override
    public boolean isGoalReached() {
        // Lazy initialization of wind system
        if (windSystem == null) {
            windSystem = world.getWindSystem();
        }

        // Start wind test on first call
        if (!windTestStarted) {
            windTestStarted = true;
            windSystem.startWind(windValue, windDuration);
            return false; // Not reached yet, still testing
        }

        // Check if wind is still active
        if (windSystem.isWindActive()) {
            return false; // Wind is still testing, not reached yet
        }

        // Wind test is complete - goal reached if structure didn't collapse
        return !structureCollapsed;
    }

    public void notifyStructureFailure() {
        structureCollapsed = true;
    }

    public boolean isWindTestActive() {
        return windTestStarted && windSystem != null && windSystem.isWindActive();
    }

    public float getWindIntensity() {
        return windSystem != null ? windSystem.getCurrentWindIntensity() : 0f;
    }

    public float getWindProgress() {
        return windSystem != null ? windSystem.getWindProgress() : 0f;
    }

    public float getTimeRemaining() {
        return windSystem != null ? windSystem.getTimeRemaining() : 0f;
    }
}
