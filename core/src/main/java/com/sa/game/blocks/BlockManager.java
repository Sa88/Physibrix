package com.sa.game.blocks;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.sa.game.MaterialType;
import com.sa.game.ModelFactory;
import com.sa.game.physics.PhysicsProperties;

import java.util.ArrayList;
import java.util.List;
public class BlockManager {
    private final List<Block> blocks = new ArrayList<>();

    public Block createBlock(Vector3 position, MaterialType currentMaterial, BlockType currentBlockType, BlockShapeType currentShapeType) {
        Model model = ModelFactory.createBlockModel(currentMaterial, currentBlockType, currentShapeType);
        ModelInstance instance = new ModelInstance(model);
        instance.transform.setToTranslation(position);

        btCollisionShape shape = PhysicsProperties.getShape(currentBlockType, currentShapeType);

        float mass = PhysicsProperties.getMass(currentMaterial);

        // Definir propriedades físicas
        btDefaultMotionState motionState = new btDefaultMotionState(instance.transform);
        Vector3 inertia = new Vector3();
        shape.calculateLocalInertia(mass, inertia);

        btRigidBody.btRigidBodyConstructionInfo bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, inertia);
        btRigidBody body = new btRigidBody(bodyInfo);
        bodyInfo.dispose();

        // Configurar propriedades físicas específicas do material
        body.setFriction(PhysicsProperties.getFriction(currentMaterial));
        body.setRestitution(PhysicsProperties.getRestitution(currentMaterial));
        var damping = PhysicsProperties.getDamping(currentMaterial);
        body.setDamping(damping, damping);

        // Aplica a posição inicial do bloco no mundo
        body.proceedToTransform(instance.transform);

        // Criação do bloco
        return new Block(currentBlockType, currentMaterial, instance, body);
    }

    public void addBlock(Block block) {
        blocks.add(block);
    }

    public void removeBlock(Block block) {
        blocks.remove(block);
        // ... disposal logic ...
    }

    public List<Block> getBlocks() {
        return blocks;
    }
}
