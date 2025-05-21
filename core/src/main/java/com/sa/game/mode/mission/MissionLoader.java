package com.sa.game.mode.mission;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.sa.game.World;
import com.sa.game.mode.mission.goal.*;

import java.util.ArrayList;
import java.util.List;

public class MissionLoader {

    public static List<GenericMission> loadMissions(World world) {
        Json json = new Json();
        Array<MissionData> missionDataArray = json.fromJson(Array.class, MissionData.class, Gdx.files.internal("missions/missions.json"));

        List<GenericMission> missions = new ArrayList<>();
        for (MissionData data : missionDataArray) {
            List<MissionGoal> goals = createGoalsFromData(world, data.goals);
            missions.add(new GenericMission(data.name, data.description, goals));
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
            default -> throw new IllegalArgumentException("Tipo de missão desconhecido: " + data.type);
        };
    }
}
