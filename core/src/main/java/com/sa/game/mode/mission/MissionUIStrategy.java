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

        // Create a container table
        Table missionPanel = new Table();
        missionPanel.top().left();
        missionPanel.setFillParent(true);
        missionPanel.pad(50);

        overallProgressLabel = new Label("Mission Progress", skin);
        overallProgressBar = new ProgressBar(0, 100, 1, false, skin);

        // Add to panel instead of directly
        missionPanel.add(overallProgressLabel).row();
        missionPanel.add(overallProgressBar).width(200).row();

        goalsTable = new Table();
        goalsTable.top().left();
        goalsTable.pad(50);

        for (MissionGoal goal : mission.getGoals()) {
            Label goalLabel = new Label(goal.getGoalDescription(), skin);
            goalLabels.add(goalLabel);
            goalsTable.add(goalLabel).row();
        }

        missionPanel.add(goalsTable).row();
        ui.getStage().addActor(missionPanel);
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
