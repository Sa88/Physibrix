package com.sa.game.mode;
import com.sa.game.blocks.Block;
public interface GameMode {

    // Chamado todo frame para atualizações específicas do modo
    void update(float deltaTime);

    // Verifica se o jogador pode colocar o bloco nesse modo
    boolean canPlaceBlock(Block block);

    // Verifica se o jogador pode remover blocos
    boolean canRemoveBlock();

    // Define se a física deve estar ativa nesse modo
    boolean isPhysicsEnabled();

    // Usado para mostrar instruções ou dicas específicas do modo
    String getModeInstructions();
}
