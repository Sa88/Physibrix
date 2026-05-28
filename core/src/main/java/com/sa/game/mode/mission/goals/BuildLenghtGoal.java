package com.sa.game.mode.mission.goals;
import com.sa.game.World;
import com.sa.game.WorldUtils;
public class BuildLenghtGoal extends AbstractMissionGoal {

    private final int requiredLength;

    public BuildLenghtGoal(World world, int value) {
        super(world);
        this.requiredLength = value;
    }

    @Override
    public String getGoalDescription() {
        return "Construa uma ponte com " + requiredLength + " blocos de comprimento.";
    }
    @Override
    public String getGoalType() {
        return "length";
    }
    @Override
    public boolean isGoalReached() {
        float maxHeight = WorldUtils.getMaxBlockHeight(world);
        return maxHeight >= requiredLength;
    }


}
