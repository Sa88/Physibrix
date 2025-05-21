package com.sa.game.mode.mission.goals;
import com.sa.game.World;
import com.sa.game.WorldUtils;
public class BuildHeightGoal implements MissionGoal {

    private final World world;
    private final int requiredHeight;

    public BuildHeightGoal(World world, int requiredHeight) {
        this.world = world;
        this.requiredHeight = requiredHeight;
    }

    @Override
    public boolean isGoalReached() {
        float maxHeight = WorldUtils.getMaxBlockHeight(world);
        return maxHeight >= requiredHeight;
    }
}
