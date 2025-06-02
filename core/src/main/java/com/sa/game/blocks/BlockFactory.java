package com.sa.game.blocks;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.sa.game.MaterialType;
import com.sa.game.ModelFactory;
import com.sa.game.physics.PhysicsProperties;
public class BlockFactory {
    public static Block createBlock(Vector3 position, MaterialType material, BlockType blockType, BlockShapeType shapeType) {
        var model = ModelFactory.createBlockModel(material, blockType, shapeType);
        var instance = new ModelInstance(model);
        instance.transform.setToTranslation(position);

        var shape = PhysicsProperties.getShape(blockType, shapeType);
        float mass = PhysicsProperties.getMass(material);

        var motionState = PhysicsProperties.createMotionState(instance.transform);
        var inertia = PhysicsProperties.calculateInertia(shape, mass);
        var bodyInfo = PhysicsProperties.createBodyInfo(mass, motionState, shape, inertia);
        var body = new btRigidBody(bodyInfo);
        bodyInfo.dispose();

        PhysicsProperties.configureBody(body, material, instance.transform);

        return new Block(blockType, material, instance, body);
    }
}
