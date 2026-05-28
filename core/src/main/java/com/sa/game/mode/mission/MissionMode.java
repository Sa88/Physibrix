package com.sa.game.mode.mission;
import com.sa.game.blocks.Block;
import com.sa.game.mode.GameMode;
public class MissionMode implements GameMode {
    private Mission currentMission;



    public void startMission(Mission mission) {
        this.currentMission = mission;
        // carregar mapa, objetivos e restrições
    }

    @Override
    public void onModeStarted() {

    }
    @Override
    public void onModePaused() {

    }
    @Override
    public void onModeResumed() {

    }
    @Override
    public void onModeEnded() {

    }

    public void update(float deltaTime) {
        // verificar progresso
        if (currentMission.isCompleted()) {
            // mostrar mensagem de vitória
        }
    }
    @Override
    public boolean canPlaceBlock(Block block) {
        return true;
    }
    @Override
    public boolean canRemoveBlock() {
        return true;
    }
    @Override
    public boolean isPhysicsEnabled() {
        return true;
    }
    @Override
    public String getModeInstructions() {
        return "";
    }
    public Mission getCurrentMission() {
        return currentMission;
    }
}
