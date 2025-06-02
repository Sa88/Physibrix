package com.sa.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.utils.Disposable;
import com.sa.game.assets.Assets;
import com.sa.game.blocks.Block;
import com.sa.game.blocks.BlockFactory;
import com.sa.game.blocks.BlockManager;
import com.sa.game.blocks.BlockShapeType;
import com.sa.game.blocks.BlockType;
import com.sa.game.camera.CameraController;
import com.sa.game.grid.GridRenderer;
import com.sa.game.physics.PhysicsSystem;

public class World implements GameCommandListener, Disposable {
    private CameraController cameraController;
    private ModelBatch modelBatch;
    private Environment environment;
    private Model baseModel;
    private btRigidBody baseBody;
    private ModelInstance baseInstance;
    private GridRenderer gridRenderer;
    private boolean renderWarnings;
    private StabilityMonitor stabilityMonitor;
    private SpriteBatch warningBatch;
    private PhysicsSystem physicsSystem;
    private BlockManager blockManager;

    public World() {
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -0.8f, -0.2f));

        cameraController = new CameraController();

        gridRenderer = new GridRenderer();

        physicsSystem = new PhysicsSystem();

        blockManager = new BlockManager();

        createBase();

        renderWarnings = false;

        stabilityMonitor = new StabilityMonitor();

        warningBatch = new SpriteBatch();

    }

    public BlockManager getBlockManager() {
        return blockManager;
    }

    private void createBase() {
        baseModel = ModelFactory.createGroundModel();
        baseInstance = new ModelInstance(baseModel);
        baseInstance.transform.setToTranslation(0, -0.5f, 0);

        btCollisionShape baseShape = new btBoxShape(new Vector3(25f, 0.5f, 25f));
        btDefaultMotionState baseMotionState = new btDefaultMotionState();
        baseMotionState.setWorldTransform(baseInstance.transform);
        btRigidBody.btRigidBodyConstructionInfo baseInfo = new btRigidBody.btRigidBodyConstructionInfo(0f, baseMotionState, baseShape, new Vector3());
        baseBody = new btRigidBody(baseInfo);
        baseInfo.dispose();

        physicsSystem.addRigidBody(baseBody);
    }

    public void addBlock(Vector3 position, MaterialType currentMaterial, BlockType currentBlockType, BlockShapeType currentShapeType) {

        Block block = BlockFactory.createBlock(position, currentMaterial, currentBlockType, currentShapeType);
        blockManager.addBlock(block);

        // Adicionar o corpo rígido ao mundo físico
        physicsSystem.addRigidBody(block.getBody());

        for (Block otherBlock : blockManager.getBlocks()) {
            if (otherBlock != block && WorldUtils.areAdjacent(block, otherBlock)) {


/*                boolean conectado = physicsSystem.connectIfTouching(block, otherBlock);
                if (conectado) {
                    System.out.println("Blocos conectados com sucesso.");
                } else {
                    System.out.println("Nenhum contato encontrado entre os blocos.");
                }*/
                stabilityMonitor.registerConnection(block, otherBlock); // força máxima segura
            }
        }
    }

    public Vector3 getWorldCoordinates(int screenX, int screenY) {
        Ray pickRay = cameraController.getCamera().getPickRay(screenX, screenY);
        float t = (0 - pickRay.origin.y) / pickRay.direction.y;
        return new Vector3(pickRay.origin).mulAdd(pickRay.direction, t); // x/z plano
    }


    public void render(DragHandler dragHandler) {
        Gdx.gl.glClearColor(0.4f, 0.6f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        cameraController.update(Gdx.graphics.getDeltaTime());
        physicsSystem.update(Gdx.graphics.getDeltaTime());

        modelBatch.begin(cameraController.getCamera());

        dragHandler.update(modelBatch);

        for (Block block : blockManager.getBlocks()) {
            if (block.getBody() != null) {
                block.getBody().getWorldTransform(block.getModelInstance().transform);
                modelBatch.render(block.getModelInstance(), environment);
            }
        }

        // Render base
        modelBatch.render(baseInstance, environment);
        modelBatch.end();

        gridRenderer.render(cameraController.getCamera());

        stabilityMonitor.update(Gdx.graphics.getDeltaTime(), physicsSystem.getDispatcher());
        if(renderWarnings) {
            stabilityMonitor.renderWarnings(warningBatch, cameraController.getCamera());
        }

        physicsSystem.render(cameraController.getCamera());

    }

    public void clearBlocks() {
        physicsSystem.removeAllConstraints();
        for (Block block : blockManager.getBlocks()) {
            physicsSystem.removeRigidBody(block.getBody());
            block.getBody().dispose();
        }
        blockManager.clearBlocks();
        stabilityMonitor.dispose();
        ModelFactory.disposeAllModels();
    }

    public boolean isRenderWarnings() {
        return renderWarnings;
    }


    public void setRenderWarnings(boolean renderWarnings) {
        this.renderWarnings = renderWarnings;
    }

    @Override
    public void dispose() {
        warningBatch.dispose();
        stabilityMonitor.dispose();
        modelBatch.dispose();
        baseModel.dispose();
        for (Block block : blockManager.getBlocks()) {
            block.getModelInstance().model.dispose();
            physicsSystem.removeRigidBody(block.getBody());
            block.getBody().dispose();
        }
        blockManager.clearBlocks();
        physicsSystem.removeRigidBody(baseBody);
        baseBody.dispose();
        physicsSystem.dispose();
        gridRenderer.dispose();
        Assets.getInstance().dispose();
    }
    public CameraController getCameraController() {
        return cameraController;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public GridRenderer getGridRenderer() {
        return gridRenderer;
    }

    @Override
    public void onClearBlocks() {
        clearBlocks();
    }

    @Override
    public void onToggleWarnings() {
        setRenderWarnings(!isRenderWarnings());
    }
    @Override
    public void onUndo() {
        var blockRemoved = blockManager.removeLastBlock();
        if(blockRemoved != null) {
            physicsSystem.removeRigidBody(blockRemoved.getBody());
            blockRemoved.getBody().dispose();
        }
    }
}
