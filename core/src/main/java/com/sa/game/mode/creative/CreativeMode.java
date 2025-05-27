package com.sa.game.mode.creative;
import com.sa.game.blocks.Block;
import com.sa.game.mode.GameMode;
public class CreativeMode implements GameMode {
    @Override
    public void update(float deltaTime) {
        // Nada especial no update do modo criativo
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
        return true; // Física opcional, pode mudar por configuração
    }

    @Override
    public String getModeInstructions() {
        return "Modo Criativo: Construa livremente sem limites!";
    }
}
