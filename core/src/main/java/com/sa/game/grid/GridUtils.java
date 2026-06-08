package com.sa.game.grid;
import com.badlogic.gdx.math.Vector3;
import com.sa.game.blocks.BlockDimensions;
import com.sa.game.blocks.BlockType;
public class GridUtils {


    private static final float CELL_SIZE = 0.25f;


    /**
     * Snaps a world position to the construction grid based on the specified block type.
     *
     * <p>The X and Z coordinates are rounded to the nearest grid cell using the
     * configured {@code CELL_SIZE}. The Y coordinate is adjusted according to the
     * block dimensions so the block aligns correctly with the grid vertically.
     *
     * <p>This method ensures that blocks of different heights are positioned
     * consistently and rest properly on top of other blocks or the base plane.
     *
     * <p>Typical use cases include:
     * <ul>
     *     <li>Block placement preview</li>
     *     <li>Grid-aligned building systems</li>
     *     <li>Structural positioning</li>
     *     <li>Physics-aligned construction</li>
     * </ul>
     *
     * @param position the original world position to snap
     * @param type the type of block being positioned
     * @return a new {@link Vector3} containing the snapped grid position
     */
    public static Vector3 snapToGrid(Vector3 position, BlockType type) {
        Vector3 halfExtents = BlockDimensions.getHalfExtents(type);
        float fullHeight = halfExtents.y;

        var roundedVector = roundVector(position,CELL_SIZE, 0.001f);

        //float snappedY = Math.round(roundedVector.y / fullHeight) * fullHeight + halfExtents.y;

        float snappedY = position.y;

        return new Vector3(roundedVector.x, snappedY, roundedVector.z);
    }

    /**
     * Rounds a vector to the nearest grid coordinates using the specified grid size.
     *
     * <p>Each component (X, Y, and Z) is rounded independently to the nearest
     * multiple of {@code gridSize}. Values that are very close to zero are
     * clamped to exactly zero using the provided epsilon threshold to avoid
     * floating-point precision artifacts.
     *
     * <p>This method is commonly used for:
     * <ul>
     *     <li>Grid snapping</li>
     *     <li>Stable block positioning</li>
     *     <li>Reducing floating-point inaccuracies</li>
     *     <li>Consistent physics alignment</li>
     * </ul>
     *
     * @param vec the vector to round
     * @param gridSize the size of each grid cell
     * @param epsilon the tolerance used to clamp near-zero values to zero
     * @return a new {@link Vector3} containing the rounded coordinates
     */
    public static Vector3 roundVector(Vector3 vec, float gridSize, float epsilon) {
        float x = Math.round(vec.x / gridSize) * gridSize;
        float y = Math.round(vec.y / gridSize) * gridSize;
        float z = Math.round(vec.z / gridSize) * gridSize;

        // If is close to zero, we force the zero
        x = Math.abs(x) < epsilon ? 0f : x;
        y = Math.abs(y) < epsilon ? 0f : y;
        z = Math.abs(z) < epsilon ? 0f : z;

        return new Vector3(x, y, z);
    }

}
