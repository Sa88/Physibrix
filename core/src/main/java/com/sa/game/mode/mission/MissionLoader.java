package com.sa.game.mode.mission;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.sa.game.World;
import com.sa.game.mode.mission.goals.*;

import java.util.ArrayList;
import java.util.List;

public class MissionLoader {

    public static List<Mission> loadMissions(World world) {
        Json json = new Json();
        List<MissionData> missionDataList = json.fromJson(List.class, MissionData.class, Gdx.files.internal("missions/missions.json"));

        List<Mission> missions = new ArrayList<>();
        for (MissionData data : missionDataList) {
            List<MissionGoal> goals = createGoalsFromData(world, data.goals);
            missions.add(new Mission(data.name, data.description, goals));
        }
        return missions;
    }

    public static List<Mission> loadMissions() {
        Json json = new Json();
        List<MissionData> missionDataArray = json.fromJson(List.class, MissionData.class, Gdx.files.internal("missions/missions.json"));

        List<Mission> missions = new ArrayList<>();
        for (MissionData data : missionDataArray) {
            missions.add(new Mission(data.name, data.description));
        }
        return missions;
    }

    private static List<MissionGoal> createGoalsFromData(World world, List<MissionDataGoal> goals) {
        List<MissionGoal> goalList = new ArrayList<>();
        for (MissionDataGoal goal : goals) {
            var missionGoal = createGoalFromData(world, goal);
            goalList.add(missionGoal);
        }
        return goalList;
    }

    private static MissionGoal createGoalFromData(World world, MissionDataGoal data) {
        return switch (data.type) {
            case "height" -> new BuildHeightGoal(world, data.value);
            case "length" -> new BuildLenghtGoal(world, data.value);
            case "wind_resistance" -> new WindResistanceGoal(world, data.value, data.duration);
            default -> throw new IllegalArgumentException("Cannot find mission goal type: " + data.type);
        };
    }
}
