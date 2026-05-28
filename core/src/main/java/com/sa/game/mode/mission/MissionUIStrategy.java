package com.sa.game.mode.mission;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.sa.game.World;
import com.sa.game.mode.GameModeUIStrategy;
import com.sa.game.mode.mission.goals.MissionGoal;
import com.sa.game.ui.UI;

import java.util.ArrayList;
import java.util.List;
public class MissionUIStrategy implements GameModeUIStrategy {
    private final Mission mission;
    private Table goalsTable;
    private Label overallProgressLabel;
    private ProgressBar overallProgressBar;
    private List<Label> goalLabels = new ArrayList<>();

    public MissionUIStrategy(Mission mission) {
        this.mission = mission;
    }

    @Override
    public void initialize(World world, UI ui) {
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        overallProgressLabel = new Label("Mission Progress", skin);
        overallProgressLabel.setFillParent(true);
        overallProgressBar = new ProgressBar(0, 100, 1, false, skin);
        overallProgressBar.setFillParent(true);

        goalsTable = new Table();
        goalsTable.top().center();
        goalsTable.setFillParent(true);

        // Create UI for each goal
        for (MissionGoal goal : mission.getGoals()) {
            Label goalLabel = new Label(goal.getGoalDescription(), skin);
            goalLabels.add(goalLabel);
            goalsTable.add(goalLabel).row();
        }

        ui.getStage().addActor(overallProgressLabel);
        ui.getStage().addActor(overallProgressBar);
        ui.getStage().addActor(goalsTable);
    }

    @Override
    public void render(float delta) {
        // Update overall progress
        float progress = mission.getOverallProgress();
        overallProgressBar.setValue(progress);
        overallProgressLabel.setText(String.format("Mission Progress: %.0f%%", progress));

        // Update individual goal statuses
        int i = 0;
        for (MissionGoal goal : mission.getGoals()) {
            if (i < goalLabels.size()) {
                goalLabels.get(i).setText(goal.getGoalDescription() + " - " + goal.getCurrentStatus());
            }
            i++;
        }
    }

    @Override
    public void dispose() {
    }
}
