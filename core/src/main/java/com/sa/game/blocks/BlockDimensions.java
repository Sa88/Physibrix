package com.sa.game.blocks;
import com.badlogic.gdx.math.Vector3;
public class BlockDimensions {
    public static Vector3 getHalfExtents(BlockType type) {
        return switch (type) {
            case BASE -> new Vector3(2f, 0.25f, 2f);
            case PILLAR -> new Vector3(0.25f, 1f, 0.25f); // Mais estreito e alto
            case STEP -> new Vector3(1f, 0.25f, 1f);
            case ROOF -> new Vector3(0.5f, 1f, 0.5f);
        };
    }

    public static Vector3 getFullExtents(BlockType type) {
        return switch (type) {
            case BASE -> new Vector3(4f, 0.5f, 2f);
            case PILLAR -> new Vector3(0.5f, 2f, 0.5f); // Mais estreito e alto
            case STEP -> new Vector3(2f, 0.5f, 2f);
            case ROOF -> new Vector3(1f, 2f, 1f);
        };
    }
}
