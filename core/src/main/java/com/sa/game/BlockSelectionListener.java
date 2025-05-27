package com.sa.game;
import com.sa.game.blocks.BlockShapeType;
import com.sa.game.blocks.BlockType;
public interface BlockSelectionListener {
    void onBlockSelected(MaterialType material, BlockType type, BlockShapeType shape);

    void confirmPlacement();

    void cancelPlacement();
    boolean isReadyToConfirm();
}
