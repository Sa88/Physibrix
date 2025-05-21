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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btFixedConstraint;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.utils.Disposable;
import com.sa.game.assets.Assets;
import com.sa.game.camera.CameraController;
import com.sa.game.grid.GridRenderer;
import com.sa.game.physics.PhysicsProperties;
import com.sa.game.physics.PhysicsSystem;

import java.util.ArrayList;
import java.util.Iterator;

public class World implements GameCommandListener, Disposable {
    private CameraController cameraController;
    private ModelBatch modelBatch;
    private Environment environment;
    protected ArrayList<Block> blocks;
    private Model baseModel;
    private btRigidBody baseBody;
    private ModelInstance baseInstance;

    private GridRenderer gridRenderer;

    private boolean removeMode;
    private boolean renderWarnings;

    private StabilityMonitor stabilityMonitor;

    private SpriteBatch warningBatch;

    private PhysicsSystem physicsSystem;

    public World() {
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -0.8f, -0.2f));

        cameraController = new CameraController();

        gridRenderer = new GridRenderer();

        physicsSystem = new PhysicsSystem();

        blocks = new ArrayList<>();

        createBase();

        removeMode = false;
        renderWarnings = false;

        stabilityMonitor = new StabilityMonitor();

        warningBatch = new SpriteBatch();

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
        return new Block(currentBlockType, currentMaterial, instance, position, body);
    }

    public void addBlock(Vector3 position, MaterialType currentMaterial, BlockType currentBlockType, BlockShapeType currentShapeType) {

        // Criação do bloco
        Block block = createBlock(position, currentMaterial, currentBlockType, currentShapeType);
        blocks.add(block);

        // Adicionar o corpo rígido ao mundo físico
        physicsSystem.addRigidBody(block.body);

        for (Block otherBlock : blocks) {
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

/*    public Vector3 getRaycastSnapPosition(int screenX, int screenY, BlockType type) {
        Ray ray = cameraController.getCamera().getPickRay(screenX, screenY);

        Vector3 from = ray.origin;
        Vector3 to = new Vector3(ray.direction).scl(1000f).add(from); // distância longa

        ClosestRayResultCallback callback = new ClosestRayResultCallback(from, to);
        dynamicsWorld.rayTest(from, to, callback);

        if (callback.hasHit()) {
            Vector3 hitPoint = new Vector3();
            Vector3 hitNormal = new Vector3();
            callback.getHitPointWorld(hitPoint);
            callback.getHitNormalWorld(hitNormal);

            // Posição base para o novo bloco: um pouco afastado na direção da normal
            Vector3 placementPos = new Vector3(hitPoint).add(hitNormal.scl(BlockDimensions.getHalfExtents(type).y * 2));

            return GridUtils.snapToGrid(placementPos, type);
        } else {
            // Sem colisão — volta para o plano XZ (plano do chão)
            return GridUtils.snapToGrid(getWorldCoordinates(screenX, screenY), type);
        }
    }*/

    public void render(DragHandler dragHandler) {
        Gdx.gl.glClearColor(0.4f, 0.6f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        cameraController.update(Gdx.graphics.getDeltaTime());
        physicsSystem.update(Gdx.graphics.getDeltaTime());

        modelBatch.begin(cameraController.getCamera());

        dragHandler.update(modelBatch);

        for (Block block : blocks) {
            if (block.body != null) {
                block.body.getWorldTransform(block.modelInstance.transform);
                modelBatch.render(block.modelInstance, environment);
            }
        }

        // Renderizar a base
        modelBatch.render(baseInstance, environment);
        modelBatch.end();

        gridRenderer.render(cameraController.getCamera());

        stabilityMonitor.update(Gdx.graphics.getDeltaTime(), physicsSystem.getDispatcher());
        if(renderWarnings) {
            stabilityMonitor.renderWarnings(warningBatch, cameraController.getCamera());
        }

//        physicsSystem.render(cameraController.getCamera());

    }

    public boolean canPlaceBlock(Vector3 position, Model blockModel) {
        BoundingBox newBlockBounds = new BoundingBox();
        blockModel.calculateBoundingBox(newBlockBounds);
        float newBlockHeight = newBlockBounds.getHeight();
        float newBlockWidth = newBlockBounds.getWidth();
        float newBlockDepth = newBlockBounds.getDepth();

        // Posição do centro da base do novo bloco
        Vector3 baseCenter = new Vector3(position).sub(0, newBlockHeight / 2f, 0);

        if (!checkSupport(baseCenter, newBlockHeight)) {
            return false; // Se não tiver suporte, não pode colocar
        }

        if (checkCollision(position, newBlockWidth, newBlockHeight, newBlockDepth)) {
            return false; // Se colidir com outro bloco, não pode colocar
        }

        return true;
    }

    private boolean checkSupport(Vector3 baseCenter, float newBlockHeight) {

        // Considera o chão/base do mundo como suporte
        if (Math.abs(baseCenter.y) < 0.05f) {
            return true;
        }

        for (Block existingBlock : blocks) {
            Vector3 existingPos = existingBlock.modelInstance.transform.getTranslation(new Vector3());
            float existingHeight = existingBlock.boundingBox.getHeight();

            Vector3 topOfExisting = new Vector3(existingPos).add(0, existingHeight / 2f, 0);

            float verticalGap = Math.abs(topOfExisting.y - baseCenter.y);
            float horizontalDistance = topOfExisting.dst(new Vector3(baseCenter.x, topOfExisting.y, baseCenter.z));

            if (verticalGap < 0.1f && horizontalDistance < 0.6f) {
                return true; // Tem suporte
            }
        }

        return false; // Sem suporte
    }

    private boolean checkCollision(Vector3 position, float newBlockWidth, float newBlockHeight, float newBlockDepth) {
        for (Block existingBlock : blocks) {
            Vector3 existingPos = existingBlock.modelInstance.transform.getTranslation(position);
            float existingHeight = existingBlock.boundingBox.getHeight();
            float existingWidth = existingBlock.boundingBox.getWidth();
            float existingDepth = existingBlock.boundingBox.getDepth();

            // Verificação de colisão lateral
            float dx = Math.abs(position.x - existingPos.x);
            float dy = Math.abs(position.y - existingPos.y);
            float dz = Math.abs(position.z - existingPos.z);

            float overlapX = (newBlockWidth + existingWidth) / 2f;
            float overlapY = (newBlockHeight + existingHeight) / 2f;
            float overlapZ = (newBlockDepth + existingDepth) / 2f;

            if (dx < overlapX && dy < overlapY && dz < overlapZ) {
                return true; // Colisão detectada
            }
        }
        return false; // Sem colisão
    }

    public void clearBlocks() {
        physicsSystem.removeAllConstraints();
        for (Block block : blocks) {
            physicsSystem.removeRigidBody(block.body);
            block.body.dispose();
        }
        blocks.clear();
        stabilityMonitor.dispose();
        ModelFactory.disposeAllModels();
    }

    public boolean isRemoveMode() {
        return removeMode;
    }

    public boolean isRenderWarnings() {
        return renderWarnings;
    }

    public void setRemoveMode(boolean removeMode) {
        this.removeMode = removeMode;
    }

    public void setRenderWarnings(boolean renderWarnings) {
        this.renderWarnings = renderWarnings;
    }

    public void removeBlockAt(Vector3 position) {
        Iterator<Block> iterator = blocks.iterator();

        while (iterator.hasNext()) {
            Block block = iterator.next();
            // Verifica se a posição do bloco está suficientemente próxima
            float width = block.boundingBox.getWidth() / 2;
            float height = block.boundingBox.getHeight() / 2;
            float depth = block.boundingBox.getDepth() / 2;

            if (position.x >= width && position.y >= height && position.z >= depth) {
                // Remove da física
                if (block.body != null) {
                    physicsSystem.removeRigidBody(block.body);
                    block.body.dispose();
                }

                // Remove da lista
                iterator.remove();

                return; // Sai após remover o primeiro encontrado
            }
        }
    }

    @Override
    public void dispose() {
        warningBatch.dispose();
        stabilityMonitor.dispose();
        modelBatch.dispose();
        baseModel.dispose();
        for (Block block : blocks) {
            block.modelInstance.model.dispose();
            physicsSystem.removeRigidBody(block.body);
            block.body.dispose();
        }
        blocks.clear();
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
    public void setGridRenderer(GridRenderer gridRenderer) {
        this.gridRenderer = gridRenderer;
    }

    @Override
    public void onClearBlocks() {
        clearBlocks();
    }
    @Override
    public void onToggleRemoveMode() {
        setRemoveMode(!isRemoveMode());
    }
    @Override
    public void onToggleWarnings() {
        setRenderWarnings(!isRenderWarnings());
    }
}
