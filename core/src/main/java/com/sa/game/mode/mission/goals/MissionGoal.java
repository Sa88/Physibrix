package com.sa.game.mode.mission.goals;
public interface MissionGoal {
    /**
     * Check if the goal has been reached
     */
    boolean isGoalReached();

    // NEW: Better lifecycle management
    void onGoalStarted();
    void onGoalCompleted();
    void onGoalFailed();

    // NEW: Get goal metadata
    String getGoalDescription();
    String getGoalType();

    // NEW: Progress tracking (0-100%)
    float getProgress();

    // NEW: Get current status/message
    String getCurrentStatus();
}
