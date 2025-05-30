package com.sa.game.physics;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.sa.game.blocks.Block;
import com.sa.game.blocks.BlockDimensions;
import com.sa.game.blocks.BlockShapeType;
import com.sa.game.blocks.BlockType;
import com.sa.game.MaterialType;
public class PhysicsProperties {

    public static float getContactArea(Block blockA, Block blockB) {
        Vector3 sizeA = BlockDimensions.getFullExtents(blockA.getBlockType());
        Vector3 sizeB = BlockDimensions.getFullExtents(blockB.getBlockType());

        // Considerar a área da base que entra em contato (X x Z)
        float contactWidth = Math.min(sizeA.x, sizeB.x);
        float contactDepth = Math.min(sizeA.z, sizeB.z);
        return contactWidth * contactDepth; // Área em m²
    }

    public static float getMass(MaterialType material) {
        return switch (material) {
            case CONCRETE -> 2400f; //2400 kg/m³
            case REINFORCED_CONCRETE -> 2500f; //2500 kg/m³
            case BRICK -> 1900f;
            case STONE -> 2600f;
            case STEEL -> 7850f; //7850 kg/m³
            case GLASS -> 2500f; //2500 kg/m³
            case WOOD -> 500f; //500 kg/m³
        };
    }

    public static float getDamping(MaterialType material) {
        return switch (material) {
            case CONCRETE -> 0.2f;
            case REINFORCED_CONCRETE -> 0.2f;
            case BRICK -> 0.1f;
            case STONE -> 0.1f;
            case STEEL -> 0.05f;
            case GLASS -> 0.15f;
            case WOOD -> 0.1f;
        };
    }

    public static float getFriction(MaterialType material) {
        return switch (material) {
            case BRICK -> 0.6f;
            case STONE -> 0.8f;
            case STEEL -> 0.4f;               // Aço é escorregadio
            case CONCRETE -> 0.7f;            // Concreto tem boa aderência
            case REINFORCED_CONCRETE -> 0.75f; // Um pouco mais que concreto comum
            case WOOD -> 0.5f;                // Madeira média
            case GLASS -> 0.2f;               // Muito escorregadio
        };
    }

    public static float getRestitution(MaterialType material) {
        return switch (material) {
            case GLASS -> 0.05f;               // quase nada de quique, frágil
            case WOOD -> 0.2f;                 // madeira pode quicar um pouco
            case CONCRETE -> 0.1f;             // concreto é pesado, quase sem quique
            case REINFORCED_CONCRETE -> 0.05f; // um pouco menos que concreto comum
            case BRICK -> 0.05f;
            case STONE -> 0.02f;
            case STEEL -> 0.3f;                // aço pode quicar bem quando colide com força
        };
    }

    public static float getCompressiveStrength(MaterialType material) {
        return switch (material) {
            case CONCRETE -> 25e6f;
            case REINFORCED_CONCRETE -> 40e6f;
            case BRICK -> 15e6f;
            case STONE -> 60e6f;
            case STEEL -> 300e6f; // 300 MPa = 300,000,000 N/m²
            case GLASS -> 8e6f;
            case WOOD -> 5e6f;
        };
    }

    public static btCollisionShape getShape(BlockType blockType, BlockShapeType blockShapeType) {

        Vector3 halfExtents = BlockDimensions.getHalfExtents(blockType);

        return switch (blockShapeType) {
            case BOX -> new btBoxShape(halfExtents.cpy());
            case CONE -> new btConeShape(halfExtents.x /2 , halfExtents.y);
            case CYLINDER -> new btCylinderShape(halfExtents.cpy());
        };
    }
}
