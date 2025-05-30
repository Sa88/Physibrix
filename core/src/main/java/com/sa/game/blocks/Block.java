package com.sa.game.blocks;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.sa.game.MaterialType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Block {
    private UUID id;
    private BlockType blockType;
    private MaterialType material;
    private ModelInstance modelInstance;
    private btRigidBody body;
    private BoundingBox boundingBox;

    public Block(BlockType blockType, MaterialType material, ModelInstance modelInstance, btRigidBody body) {
        this.id = UUID.randomUUID();
        this.blockType = blockType;
        this.material = material;
        this.modelInstance = modelInstance;
        this.body = body;
        this.body.userData = this.id;
        this.boundingBox = new BoundingBox();
        this.modelInstance.calculateBoundingBox(this.boundingBox);
    }

}
