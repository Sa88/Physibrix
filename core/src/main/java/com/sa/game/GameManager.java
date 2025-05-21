package com.sa.game;
import com.sa.game.mode.GameMode;
import com.sa.game.mode.GameModeType;
public class GameManager {
    private GameModeType currentModeType;
    private GameMode currentMode;

    public void switchTo(GameModeType mode) {
        this.currentModeType = mode;
        this.currentMode = mode.createInstance();
    }
}
