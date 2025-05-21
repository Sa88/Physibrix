package com.sa.game.physics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btFixedConstraint;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btTypedConstraint;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;
import com.badlogic.gdx.utils.Disposable;
import com.sa.game.Block;

import java.util.ArrayList;
import java.util.List;
public class PhysicsSystem implements Disposable {


    private btDiscreteDynamicsWorld dynamicsWorld;
    private btBroadphaseInterface broadphase;
    private final btDefaultCollisionConfiguration collisionConfig;
    private final btCollisionDispatcher dispatcher;
    private final btSequentialImpulseConstraintSolver solver;

    private List<btTypedConstraint> constraintList;

//    private DebugDrawer debugDrawer;


    public PhysicsSystem() {
        broadphase = new btDbvtBroadphase();
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        solver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -9.8f, 0));

//        debugDrawer = new DebugDrawer();
//        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawAabb);
//        dynamicsWorld.setDebugDrawer(debugDrawer);
        constraintList = new ArrayList<btTypedConstraint>();
    }

    public void update(float delta) {
        dynamicsWorld.stepSimulation(delta, 5, 1f / 60f);
    }

/*    public void render(Camera camera) {
        debugDrawer.begin(camera);
        dynamicsWorld.debugDrawWorld();
        debugDrawer.end();
    }*/

    public void addRigidBody(btRigidBody rigidBody) {
        dynamicsWorld.addRigidBody(rigidBody);
    }

    public void removeRigidBody(btRigidBody rigidBody) {
        dynamicsWorld.removeRigidBody(rigidBody);
    }

    public void addCollisionObject(btCollisionObject collisionObject) {
        dynamicsWorld.addCollisionObject(collisionObject);
    }

    public void removeCollisionObject(btCollisionObject collisionObject) {
        dynamicsWorld.removeCollisionObject(collisionObject);
    }

    public void addConstraint(Block blockA, Block blockB) {
        btRigidBody bodyA = blockA.body;
        btRigidBody bodyB = blockB.body;

        // Obter as transformações globais
        Matrix4 matrixA = blockA.modelInstance.transform;
        Matrix4 matrixB = blockB.modelInstance.transform;

        // Calcular as transformações relativas
        Matrix4 frameInA = new Matrix4().idt(); // Identidade se blocos estiverem alinhados
        Matrix4 inverseA = new Matrix4(matrixA).inv(); // inversa de A
        Matrix4 frameInB = new Matrix4(inverseA).mul(matrixB); // transform B relativo a A

        // Criar a constraint
        btFixedConstraint constraint = new btFixedConstraint(bodyA, bodyB, frameInA, frameInB);
        constraint.setBreakingImpulseThreshold(1e8f);

        dynamicsWorld.addConstraint(constraint, true);
        constraintList.add(constraint);
    }

    public boolean connectIfTouching(Block blockA, Block blockB) {
        btRigidBody bodyA = blockA.body;
        btRigidBody bodyB = blockB.body;

        int numManifolds = dynamicsWorld.getDispatcher().getNumManifolds();

        System.out.println("numManifolds: " + numManifolds);

        for (int i = 0; i < numManifolds; i++) {
            btPersistentManifold manifold = dynamicsWorld.getDispatcher().getManifoldByIndexInternal(i);
            btCollisionObject objA = manifold.getBody0();
            btCollisionObject objB = manifold.getBody1();

            System.out.println("Manifold ContactPoint: " + manifold.getContactPoint(i));
            System.out.println("Manifold ContactPointConst: " + manifold.getContactPointConst(i));

            System.out.println("objA: " + objA);
            System.out.println("objB: " + objB);
            System.out.println("bodyA: " + bodyA);
            System.out.println("bodyB: " + bodyB);

            // Verifica se os dois blocos estão envolvidos
            //if ((objA == bodyA && objB == bodyB) || (objA == bodyB && objB == bodyA)) {
            if (objB == bodyB) {

                int numContacts = manifold.getNumContacts();
                if (numContacts > 0) {
                    // Usa o primeiro ponto de contato
                    btManifoldPoint point = manifold.getContactPoint(0);
                    Vector3 contactPointWorld = new Vector3();
                    point.getPositionWorldOnA(contactPointWorld);

                    // Transforma para coordenadas locais dos corpos
                    Matrix4 invTransformA = new Matrix4(blockA.modelInstance.transform).inv();
                    Matrix4 invTransformB = new Matrix4(blockB.modelInstance.transform).inv();

                    Vector3 localA = contactPointWorld.cpy().mul(invTransformA);
                    Vector3 localB = contactPointWorld.cpy().mul(invTransformB);

                    Matrix4 frameInA = new Matrix4().setToTranslation(localA);
                    Matrix4 frameInB = new Matrix4().setToTranslation(localB);

                    btFixedConstraint constraint = new btFixedConstraint(bodyA, bodyB, frameInA, frameInB);
                    constraint.setBreakingImpulseThreshold(1e8f);

                    dynamicsWorld.addConstraint(constraint, true);
                    constraintList.add(constraint);
                    return true; // sucesso
                }
            }
        }
        return false; // não encontrou contato
    }

    public void removeConstraint(btTypedConstraint constraint) {
        dynamicsWorld.removeConstraint(constraint);
        constraintList.remove(constraint);
        constraint.dispose();
    }

    public void removeAllConstraints() {
        for (btTypedConstraint constraint : constraintList) {
            dynamicsWorld.removeConstraint(constraint);
            constraint.dispose();
        }
        constraintList.clear();
    }

    public btDispatcher getDispatcher() {
        return dispatcher;
    }

    @Override
    public void dispose() {
        removeAllConstraints();
        dynamicsWorld.dispose();
        broadphase.dispose();
        collisionConfig.dispose();
        dispatcher.dispose();
        solver.dispose();
//        debugDrawer.dispose();
    }
}
