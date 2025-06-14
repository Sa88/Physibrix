package com.sa.game.blocks;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.util.ArrayList;
import java.util.List;
public class BlockManager {
    private final List<Block> blocks = new ArrayList<>();

    public void addBlock(Block block) {
        blocks.add(block);
    }

    public void removeBlock(Block block) {
        blocks.remove(block);
    }

    public Block removeLastBlock() {
        if (!blocks.isEmpty()) {
            return blocks.remove(blocks.size() - 1);
        }
        return null;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void clearBlocks() {
        blocks.clear();
    }

    public boolean canPlaceBlock(Vector3 position, Model blockModel) {
        BoundingBox newBlockBounds = new BoundingBox();
        blockModel.calculateBoundingBox(newBlockBounds);
        float newBlockHeight = newBlockBounds.getHeight();
        float newBlockWidth = newBlockBounds.getWidth();
        float newBlockDepth = newBlockBounds.getDepth();

        // Posição do centro da base do novo bloco
        Vector3 baseCenter = new Vector3(position).sub(0, newBlockHeight / 2f, 0);

        if (!checkSupport(baseCenter, newBlockHeight)) {
            return false; // Se não tiver suporte, não pode colocar
        }

        if (checkCollision(position, newBlockWidth, newBlockHeight, newBlockDepth)) {
            return false; // Se colidir com outro bloco, não pode colocar
        }

        return true;
    }

    private boolean checkSupport(Vector3 baseCenter, float newBlockHeight) {

        // Consider base of world as support
        if (Math.abs(baseCenter.y) < 0.05f) {
            return true;
        }

        for (Block existingBlock : blocks) {
            Vector3 existingPos = existingBlock.getModelInstance().transform.getTranslation(new Vector3());
            float existingHeight = existingBlock.getBoundingBox().getHeight();

            Vector3 topOfExisting = new Vector3(existingPos).add(0, existingHeight / 2f, 0);

            float verticalGap = Math.abs(topOfExisting.y - baseCenter.y);
            float horizontalDistance = topOfExisting.dst(new Vector3(baseCenter.x, topOfExisting.y, baseCenter.z));

            if (verticalGap < 0.1f && horizontalDistance < 0.6f) {
                return true; // has support
            }
        }

        return false; // No support
    }

    private boolean checkCollision(Vector3 position, float newBlockWidth, float newBlockHeight, float newBlockDepth) {
        for (Block existingBlock : blocks) {
            Vector3 existingPos = existingBlock.getModelInstance().transform.getTranslation(position);
            var existingBoundingBox = existingBlock.getBoundingBox();
            float existingHeight = existingBoundingBox.getHeight();
            float existingWidth = existingBoundingBox.getWidth();
            float existingDepth = existingBoundingBox.getDepth();

            // Verificação de colisão lateral
            float dx = Math.abs(position.x - existingPos.x);
            float dy = Math.abs(position.y - existingPos.y);
            float dz = Math.abs(position.z - existingPos.z);

            float overlapX = (newBlockWidth + existingWidth) / 2f;
            float overlapY = (newBlockHeight + existingHeight) / 2f;
            float overlapZ = (newBlockDepth + existingDepth) / 2f;

            if (dx < overlapX && dy < overlapY && dz < overlapZ) {
                return true; // Collision detected
            }
        }
        return false; // No collision
    }
}
