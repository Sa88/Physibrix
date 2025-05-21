package com.sa.game;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Block {
    public UUID id;
    public BlockType blockType;
    public MaterialType material;
    public ModelInstance modelInstance;
    public Vector3 position;
    public btRigidBody body;
    public BoundingBox boundingBox;

    public Block(BlockType blockType, MaterialType material, ModelInstance modelInstance, Vector3 position, btRigidBody body) {
        this.id = UUID.randomUUID();
        this.blockType = blockType;
        this.material = material;
        this.modelInstance = modelInstance;
        this.position = position;
        this.body = body;
        this.body.userData = this.id;
        this.boundingBox = new BoundingBox();
        this.modelInstance.calculateBoundingBox(this.boundingBox);
    }
}
