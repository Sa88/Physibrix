package com.sa.game.mode.mission;
import com.sa.game.mode.mission.goal.MissionGoal;

import java.util.List;
public class GenericMission {
    private final String name;
    private final String description;
    private final List<MissionGoal> goals;

    public GenericMission(String name, String description, List<MissionGoal> goals) {
        this.name = name;
        this.description = description;
        this.goals = goals;
    }

    public boolean isCompleted() {
        for (MissionGoal goal : goals) {
            if (!goal.isGoalReached()) {
                return false;
            }
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
