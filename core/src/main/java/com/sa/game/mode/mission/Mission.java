package com.sa.game.mode.mission;
import com.sa.game.mode.mission.goals.MissionGoal;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Mission {
    private final String id;
    private final String name;
    private final String description;
    private final List<MissionGoal> goals;

    public Mission(String id, String name, String description) {
        this(id, name, description, null);
    }

    public boolean isCompleted() {
        for (MissionGoal goal : goals) {
            if (!goal.isGoalReached()) {
                return false;
            }
        }
        return true;
    }
}
