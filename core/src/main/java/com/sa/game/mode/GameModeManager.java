package com.sa.game.mode;
import com.sa.game.blocks.Block;
public class GameModeManager {

    private GameMode currentMode;

    public GameModeManager(GameMode defaultMode) {
        this.currentMode = defaultMode;
    }

    public void setMode(GameMode mode) {
        this.currentMode = mode;
    }

    public GameMode getCurrentMode() {
        return currentMode;
    }

    // Delegações úteis
    public void update(float deltaTime) {
        currentMode.update(deltaTime);
    }

    public boolean canPlaceBlock(Block block) {
        return currentMode.canPlaceBlock(block);
    }

    public boolean canRemoveBlock() {
        return currentMode.canRemoveBlock();
    }

    public boolean isPhysicsEnabled() {
        return currentMode.isPhysicsEnabled();
    }

    public String getModeInstructions() {
        return currentMode.getModeInstructions();
    }
}
