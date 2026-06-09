package com.sa.game.mode.mission.goals;
import com.sa.game.World;
import com.sa.game.WorldUtils;

import java.util.Locale;
public class BuildHeightGoal extends AbstractMissionGoal {
    private final int requiredHeight;

    public BuildHeightGoal(World world, int requiredHeight) {
        super(world);
        this.requiredHeight = requiredHeight;
    }

    @Override
    public String getGoalDescription() {
        return "Build a tower at least with " + requiredHeight + "m height.";
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
        return String.format(Locale.ENGLISH, "%.1f / %d blocks", current, requiredHeight);
    }
}
