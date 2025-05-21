package com.sa.game.mode.mission;
import com.sa.game.mode.mission.goals.MissionGoal;

import java.util.List;
public class Mission {
    private final String name;
    private final String description;
    private final List<MissionGoal> goals;

    public Mission(String name, String description, List<MissionGoal> goals) {
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
