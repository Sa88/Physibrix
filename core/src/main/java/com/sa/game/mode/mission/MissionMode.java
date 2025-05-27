package com.sa.game.mode.mission;
import com.sa.game.blocks.Block;
import com.sa.game.mode.GameMode;
public class MissionMode implements GameMode {
    private Mission currentMission;

    public void startMission(Mission mission) {
        this.currentMission = mission;
        // carregar mapa, objetivos e restrições
    }

    public void update(float deltaTime) {
        // verificar progresso
        if (currentMission.isCompleted()) {
            // mostrar mensagem de vitória
        }
    }
    @Override
    public boolean canPlaceBlock(Block block) {
        return false;
    }
    @Override
    public boolean canRemoveBlock() {
        return false;
    }
    @Override
    public boolean isPhysicsEnabled() {
        return false;
    }
    @Override
    public String getModeInstructions() {
        return "";
    }
}
