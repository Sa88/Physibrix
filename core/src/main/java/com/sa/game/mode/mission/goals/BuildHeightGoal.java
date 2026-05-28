package com.sa.game.mode.mission.goals;
import com.sa.game.World;
import com.sa.game.WorldUtils;
public class BuildHeightGoal extends AbstractMissionGoal {
    private final int requiredHeight;

    public BuildHeightGoal(World world, int requiredHeight) {
        super(world);
        this.requiredHeight = requiredHeight;
    }

    @Override
    public String getGoalDescription() {
        return "Construa uma torre com pelo menos " + requiredHeight + " blocos de altura.";
    }
    @Override
    public String getGoalType() {
        return "height";
    }
    @Override
    public boolean isGoalReached() {
        float maxHeight = WorldUtils.getMaxBlockHeight(world);
        return maxHeight >= requiredHeight;
    }

    @Override
    public float getProgress() {
        float currentHeight = WorldUtils.getMaxBlockHeight(world);
        return Math.min(100f, (currentHeight / requiredHeight) * 100f);
    }

    @Override
    public String getCurrentStatus() {
        if (completed) return "Completed ✓";
        if (failed) return "Failed ❌";
        float current = WorldUtils.getMaxBlockHeight(world);
        return String.format("%.1f / %d blocos", current, requiredHeight);
    }
}
