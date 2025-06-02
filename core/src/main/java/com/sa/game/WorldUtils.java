package com.sa.game;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.sa.game.blocks.Block;
import com.sa.game.blocks.BlockType;
import com.sa.game.grid.GridUtils;
public class WorldUtils {

    public static float getMaxBlockHeight(World world) {
        return world.getBlockManager().getBlocks().stream().map(b -> b.getBoundingBox().getHeight()).max(Float::compareTo).orElse(0f);
    }

    public static Vector3 getSnappedWorldCoordinates(World world, int screenX, int screenY, BlockType blockType) {
        return GridUtils.snapToGrid(world.getWorldCoordinates(screenX, screenY), blockType);
    }

    public static boolean isPointInsideBlockArea(float x, float z, BoundingBox blockBox, Vector3 blockPos, BoundingBox blockBox2) {
        // Calcula os limites do BoundingBox em X e Z, baseados na posição do bloco e no tamanho do BoundingBox
        float minX = blockPos.x + blockBox.min.x + blockBox2.min.x;
        float maxX = blockPos.x + blockBox.max.x + blockBox2.max.x;
        float minZ = blockPos.z + blockBox.min.z + blockBox2.min.z;
        float maxZ = blockPos.z + blockBox.max.z + blockBox2.max.z;

        // Verifica se o ponto (x, z) está dentro da área
        return (x >= minX && x <= maxX) && (z >= minZ && z <= maxZ);
    }

    public static float getHeightAt(World world, float x, float z, Block tempBlock) {

        return (float) world.getBlockManager().getBlocks().stream()
            .filter(b -> {

                // Pega a posição central do bloco
                Vector3 pos = b.getModelInstance().transform.getTranslation(new Vector3());

                // Verifica se o ponto (x, z) está dentro da base do bloco
                return isPointInsideBlockArea(x, z, b.getBoundingBox(), pos, tempBlock.getBoundingBox());

            })
            .mapToDouble(b -> {
                Vector3 pos = b.getModelInstance().transform.getTranslation(new Vector3());
                float height = b.getBoundingBox().getHeight();
                return pos.y + height / 2f; // retorna o topo do bloco
            })
            .max() // queremos a altura do bloco mais alto que cobre esse ponto
            .orElse(0f);
    }


    public static boolean areAdjacent(Block a, Block b) {
        Vector3 posA = a.getModelInstance().transform.getTranslation(new Vector3());
        Vector3 posB = b.getModelInstance().transform.getTranslation(new Vector3());

        var aBoundingBox = a.getBoundingBox();
        var bBoundingBox = b.getBoundingBox();

        float axMin = posA.x - aBoundingBox.getWidth() / 2f;
        float axMax = posA.x + aBoundingBox.getWidth() / 2f;
        float ayMin = posA.y - aBoundingBox.getHeight() / 2f;
        float ayMax = posA.y + aBoundingBox.getHeight() / 2f;
        float azMin = posA.z - aBoundingBox.getDepth() / 2f;
        float azMax = posA.z + aBoundingBox.getDepth() / 2f;

        float bxMin = posB.x - bBoundingBox.getWidth() / 2f;
        float bxMax = posB.x + bBoundingBox.getWidth() / 2f;
        float byMin = posB.y - bBoundingBox.getHeight() / 2f;
        float byMax = posB.y + bBoundingBox.getHeight() / 2f;
        float bzMin = posB.z - bBoundingBox.getDepth() / 2f;
        float bzMax = posB.z + bBoundingBox.getDepth() / 2f;

        // Define tolerância para evitar erros de ponto flutuante
        float epsilon = 0.001f;

        boolean xAdjacent = Math.abs(axMax - bxMin) < epsilon || Math.abs(bxMax - axMin) < epsilon;
        boolean yOverlap = ayMax > byMin + epsilon && ayMin < byMax - epsilon;
        boolean zOverlap = azMax > bzMin + epsilon && azMin < bzMax - epsilon;

        boolean yAdjacent = Math.abs(ayMax - byMin) < epsilon || Math.abs(byMax - ayMin) < epsilon;
        boolean xOverlap = axMax > bxMin + epsilon && axMin < bxMax - epsilon;

        boolean zAdjacent = Math.abs(azMax - bzMin) < epsilon || Math.abs(bzMax - azMin) < epsilon;

        // São adjacentes se:
        // - estão tocando em apenas 1 eixo
        // - e se sobrepõem nos outros dois eixos
        if (xAdjacent && yOverlap && zOverlap) return true;
        if (yAdjacent && xOverlap && zOverlap) return true;
        if (zAdjacent && xOverlap && yOverlap) return true;

        return false;
    }

}
