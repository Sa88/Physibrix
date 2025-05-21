package com.sa.game;
public interface BlockSelectionListener {
    void onBlockSelected(MaterialType material, BlockType type, BlockShapeType shape);

    void confirmPlacement();

    void cancelPlacement();
    boolean isReadyToConfirm();
}
