package com.sa.game.mode.mission.goals;
import com.sa.game.World;
public abstract class AbstractMissionGoal implements MissionGoal {
    protected final World world;
    protected boolean completed = false;
    protected boolean failed = false;

    public AbstractMissionGoal(World world) {
        this.world = world;
    }

    @Override
    public void onGoalStarted() {
        // Default: do nothing
    }

    @Override
    public void onGoalCompleted() {
        this.completed = true;
    }

    @Override
    public void onGoalFailed() {
        this.failed = true;
    }

    @Override
    public float getProgress() {
        return isGoalReached() ? 100f : 0f; // Override for partial progress
    }

    @Override
    public String getCurrentStatus() {
        if (failed) return "Failed ❌";
        if (completed) return "Completed ✓";
        return "In Progress...";
    }

    @Override
    public abstract String getGoalDescription();

    @Override
    public abstract String getGoalType();

    @Override
    public abstract boolean isGoalReached();
}
