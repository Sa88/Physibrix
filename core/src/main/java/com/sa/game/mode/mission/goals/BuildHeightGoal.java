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


}
