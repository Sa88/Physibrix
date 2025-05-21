package com.sa.game.mode.mission.goal;
import com.sa.game.World;
import com.sa.game.WorldUtils;
public class BuildLenghtGoal implements MissionGoal {

    private final World world;
    private final int requiredLength;

    public BuildLenghtGoal(World world, int value) {
        this.world = world;
        this.requiredLength = value;
    }

    @Override
    public boolean isGoalReached() {
        float maxHeight = WorldUtils.getMaxBlockHeight(world);
        return maxHeight >= requiredLength;
    }
}
