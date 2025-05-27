package com.sa.game.mode.survival;
import com.sa.game.blocks.Block;
import com.sa.game.mode.GameMode;
public class SurvivalMode implements GameMode {

    private float survivalTime = 0;

    @Override
    public void update(float deltaTime) {
        survivalTime += deltaTime;
        // Pode incluir lógica de eventos aleatórios aqui
    }

    @Override
    public boolean canPlaceBlock(Block block) {
        return checkResources(block);
    }

    @Override
    public boolean canRemoveBlock() {
        return false; // Talvez só ferramentas especiais possam
    }

    @Override
    public boolean isPhysicsEnabled() {
        return true;
    }

    @Override
    public String getModeInstructions() {
        return "Modo Sobrevivência: Resista o máximo possível!";
    }

    private boolean checkResources(Block block) {
        // Simulação: limite de blocos por tipo
        return true; // Placeholder
    }
}
