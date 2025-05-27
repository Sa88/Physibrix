package com.sa.game.blocks;

import com.sa.game.MaterialType;
public record BlockModelKey(MaterialType material, BlockType type, BlockShapeType shape) {
}
