package com.sa.game.grid;
import com.badlogic.gdx.math.Vector3;
import com.sa.game.blocks.BlockDimensions;
import com.sa.game.blocks.BlockType;
public class GridUtils {


    private static final float CELL_SIZE = 0.25f;


    /**
     * Alinha completamente um vetor à grid (X, Y, Z), com base no tipo do bloco.
     */
    public static Vector3 snapToGrid(Vector3 position, BlockType type) {
        Vector3 halfExtents = BlockDimensions.getHalfExtents(type);
        float fullHeight = halfExtents.y;

        var roundedVector = roundVector(position,CELL_SIZE, 0.001f);

        float snappedY = Math.round(roundedVector.y / fullHeight) * fullHeight + halfExtents.y;

        return new Vector3(roundedVector.x, snappedY, roundedVector.z);
    }

    public static Vector3 roundVector(Vector3 vec, float gridSize, float epsilon) {
        float x = Math.round(vec.x / gridSize) * gridSize;
        float y = Math.round(vec.y / gridSize) * gridSize;
        float z = Math.round(vec.z / gridSize) * gridSize;

        // Se estiver muito próximo de zero, forçamos a zero
        x = Math.abs(x) < epsilon ? 0f : x;
        y = Math.abs(y) < epsilon ? 0f : y;
        z = Math.abs(z) < epsilon ? 0f : z;

        return new Vector3(x, y, z);
    }

}
