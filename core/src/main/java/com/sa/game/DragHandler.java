package com.sa.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.sa.game.blocks.Block;
import com.sa.game.blocks.BlockDimensions;
import com.sa.game.blocks.BlockShapeType;
import com.sa.game.blocks.BlockType;

import static com.sa.game.blocks.BlockShapeType.BOX;
import static com.sa.game.blocks.BlockType.BASE;
import static com.sa.game.MaterialType.CONCRETE;

public class DragHandler extends InputAdapter implements BlockSelectionListener {
    private final World world;

    private boolean draggingBlock = false;
    private Block tempBlock;

    private MaterialType currentMaterial = CONCRETE;
    private BlockType currentBlockType = BASE;

    private BlockShapeType currentShapeType = BOX;

    private Stage uiStage;

    public DragHandler(World world) {
        this.world = world;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ENTER) {
            confirmPlacement();
        } else if (keycode == Input.Keys.ESCAPE) {
            cancelPlacement();
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (draggingBlock) {
            updateTempBlockPosition(screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (isDraggingWithTempBlock()) {
            updateTempBlockPosition(screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (isDraggingWithTempBlock()) {
            updateTempBlockPosition(screenX, screenY);
            draggingBlock = false;
        }
        return true;
    }

    public void startDragging(MaterialType material, BlockType blockType, BlockShapeType shapeType) {
        draggingBlock = true;
        currentMaterial = material;
        currentBlockType = blockType;
        currentShapeType = shapeType;
        tempBlock = world.getBlockManager().createBlock(new Vector3(), material, currentBlockType, currentShapeType);
        world.getGridRenderer().setBuildMode(true);
    }

    private void updateTempBlockPosition(int screenX, int screenY) {

        Vector3 snappedCoords = WorldUtils.getSnappedWorldCoordinates(world, screenX, screenY, currentBlockType);

        Vector3 halfExtents = BlockDimensions.getHalfExtents(currentBlockType);
        // Ajusta a altura com base no que já existe no mundo
        float existingHeight = WorldUtils.getHeightAt(world, snappedCoords.x, snappedCoords.z, tempBlock);

        snappedCoords.y = existingHeight + halfExtents.y;

/*        if (!world.canPlaceBlock(snappedCoords, tempBlock.modelInstance.model)) {
            tempBlock.modelInstance.materials.get(0).set(RED);
        } else {
            tempBlock.modelInstance.materials.get(0).set(GREEN);
        }*/

        tempBlock.getModelInstance().transform.setToTranslation(snappedCoords);
    }

    @Override
    public void confirmPlacement() {
        if (hasTempBlock()) {
            Vector3 snappedCoords = new Vector3();
            tempBlock.getModelInstance().transform.getTranslation(snappedCoords);

            world.addBlock(snappedCoords, currentMaterial, currentBlockType, currentShapeType);

            draggingBlock = false;
            tempBlock = null;
            world.getGridRenderer().setBuildMode(false);
        }
    }

    @Override
    public void cancelPlacement() {
        if (hasTempBlock()) {
            tempBlock = null;
            draggingBlock = false;
            world.getGridRenderer().setBuildMode(false);
        }
    }

    @Override
    public boolean isReadyToConfirm() {
        return hasTempBlock();
    }

    public void setUIStage(Stage stage) {
        this.uiStage = stage;
    }

    private boolean isOverUI() {
        if (uiStage == null) return false;
        return uiStage.hit(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), true) != null;
    }



    public void update(ModelBatch modelBatch) {
        if (hasTempBlock()) {
            if (!isOverUI()) {
                updateTempBlockPosition(Gdx.input.getX(), Gdx.input.getY());
            }
            modelBatch.render(tempBlock.getModelInstance(), world.getEnvironment());
        }
    }
    @Override
    public void onBlockSelected(MaterialType material, BlockType type, BlockShapeType shape) {
        startDragging(material, type, shape);
    }

    public boolean hasTempBlock() {
        return tempBlock != null;
    }

    public boolean isDraggingWithTempBlock() {
        return draggingBlock && tempBlock != null;
    }

}
