package com.sa.game.mode.mission.goal;
import com.sa.game.World;
public class WindResistanceGoal implements MissionGoal {

    private final World world;
    private final int windValue;
    private final int windDuration;

    public WindResistanceGoal(World world, int value, int duration) {
        this.world = world;
        this.windValue = value;
        this.windDuration = duration;
    }

    @Override
    public boolean isGoalReached() {
        return true;
    }
}
