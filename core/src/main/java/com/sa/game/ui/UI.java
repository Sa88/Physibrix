package com.sa.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sa.game.BlockSelectionListener;
import com.sa.game.blocks.BlockShapeType;
import com.sa.game.blocks.BlockType;
import com.sa.game.GameCommandListener;
import com.sa.game.MaterialType;
import com.sa.game.assets.Assets;

import static com.sa.game.blocks.BlockShapeType.BOX;
import static com.sa.game.blocks.BlockType.BASE;
import static com.sa.game.blocks.BlockType.PILLAR;
import static com.sa.game.blocks.BlockType.ROOF;
import static com.sa.game.blocks.BlockType.STEP;
import static com.sa.game.MaterialType.BRICK;
import static com.sa.game.MaterialType.CONCRETE;
import static com.sa.game.MaterialType.GLASS;
import static com.sa.game.MaterialType.STEEL;
import static com.sa.game.MaterialType.STONE;
import static com.sa.game.MaterialType.WOOD;

public class UI {
    private Stage stage;
    private Skin skin;
    private MaterialType currentMaterial = CONCRETE;
    private BlockType currentBlockType = BASE;
    private BlockShapeType currentShapeType = BOX;
    private final BlockSelectionListener selectionListener;
    private final GameCommandListener commandListener;

    private Table materialMenu;

    private static final float BUTTON_WIDTH = 80f;
    private static final float BUTTON_HEIGHT = 80f;
    private static final float PADDING = 20f;

    private ImageButton confirmButton;
    private ImageButton cancelButton;


    public UI(BlockSelectionListener selectionListener, GameCommandListener commandListener) {

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        stage = new Stage(new ScreenViewport());

        this.selectionListener = selectionListener;
        this.commandListener = commandListener;
        Gdx.input.setInputProcessor(stage);

        // Materials

        Table materialTable = new Table();
        materialTable.top();
        materialTable.setFillParent(true);
        stage.addActor(materialTable);

        TextureRegionDrawable materialsDrawable = new TextureRegionDrawable(new TextureRegion(Assets.getInstance().materials));
        ImageButton materialsButton = new ImageButton(materialsDrawable);
        materialTable.add(materialsButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        materialsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (materialMenu != null && materialMenu.hasParent()) {
                    materialMenu.remove(); // collapse
                } else {
                    openMaterialsMenu(materialTable); // expand
                }
            }
        });


        Table blockTable = new Table();
        blockTable.top().left();
        blockTable.setFillParent(true);
        stage.addActor(blockTable);


        addBlockCategoryMenu(blockTable, "Pillar", PILLAR);
        addBlockCategoryMenu(blockTable, "Base", BASE);
        addBlockCategoryMenu(blockTable, "Step", STEP);
        addBlockCategoryMenu(blockTable, "Roof", ROOF);

        Table bottomTable = new Table();
        bottomTable.bottom().left();
        bottomTable.setFillParent(true);
        bottomTable.add(createExitButton()).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(PADDING).padLeft(PADDING);
        bottomTable.add(createCleanUpButton()).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(PADDING).padLeft(PADDING);
        bottomTable.add(createStabilityWarningButton()).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(PADDING).padLeft(PADDING);
        bottomTable.add(createUndoButton()).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(PADDING).padLeft(PADDING);
        bottomTable.add(createCancelButton()).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(PADDING).padLeft(PADDING);
        bottomTable.add(createConfirmButton()).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(PADDING).padLeft(PADDING);

        stage.addActor(bottomTable);
    }

    private void openMaterialsMenu(Table materialTable) {
        materialMenu = new Table();

        materialMenu.add(createMaterialButton(Assets.getInstance().concrete, CONCRETE)).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(PADDING);
        materialMenu.add(createMaterialButton(Assets.getInstance().steel, STEEL)).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(PADDING);
        materialMenu.add(createMaterialButton(Assets.getInstance().wood, WOOD)).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(PADDING);
        materialMenu.add(createMaterialButton(Assets.getInstance().glass, GLASS)).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(PADDING);
        materialMenu.add(createMaterialButton(Assets.getInstance().brick, BRICK)).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(PADDING);
        materialMenu.add(createMaterialButton(Assets.getInstance().stone, STONE)).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(PADDING);

        materialTable.row();
        materialTable.add(materialMenu);
    }

    private ImageButton createExitButton() {
        ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(Assets.getInstance().exit)));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        return button;
    }

    private ImageButton createCleanUpButton() {
        ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(Assets.getInstance().cleanup)));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                commandListener.onClearBlocks();
            }
        });
        return button;
    }

    private ImageButton createStabilityWarningButton() {
        ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(Assets.getInstance().stabilityWarning)));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                commandListener.onToggleWarnings();
            }
        });
        return button;
    }

    private ImageButton createUndoButton() {
        ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(Assets.getInstance().undo)));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                commandListener.onUndo();
            }
        });
        return button;
    }

    private ImageButton createConfirmButton() {
        confirmButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(Assets.getInstance().confirmBlock)));
        confirmButton.setVisible(false);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectionListener.confirmPlacement();
            }
        });
        return confirmButton;
    }
    private ImageButton createCancelButton() {
        cancelButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(Assets.getInstance().cancelBlock)));
        cancelButton.setVisible(false);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectionListener.cancelPlacement();
            }
        });
        return cancelButton;
    }

    private ImageButton createMaterialButton(Texture texture, MaterialType type) {
        ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(texture)));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentMaterial = type;
                materialMenu.remove(); // Close menu
            }
        });
        return button;
    }

    private void addBlockCategoryMenu(Table parentTable, String label, BlockType blockType) {
        final TextButton categoryButton = new TextButton(label + " ▼", skin, "toggle");
        final Table submenu = new Table();
        submenu.setVisible(false);

        categoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean visible = !submenu.isVisible();
                submenu.setVisible(visible);
                categoryButton.setText(label + (visible ? " ▲" : " ▼"));
            }
        });

        parentTable.add(categoryButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(PADDING);

        for (BlockShapeType shape : BlockShapeType.values()) {

            TextButton shapeButton = new TextButton(shape.name(), skin);
            shapeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    currentBlockType = blockType;
                    currentShapeType = shape;
                    submenu.setVisible(false);
                    selectionListener.onBlockSelected(currentMaterial, currentBlockType, currentShapeType);
                }
            });
            submenu.add(shapeButton).width(BUTTON_WIDTH).height(50).padLeft(PADDING).padBottom(PADDING);
        }

        parentTable.add(submenu).left().row();
        stage.addActor(parentTable);
    }

    public void render() {

        boolean shouldShow = selectionListener.isReadyToConfirm();
        if (confirmButton != null) confirmButton.setVisible(shouldShow);
        if (cancelButton != null) cancelButton.setVisible(shouldShow);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public Stage getStage() {
        return stage;
    }
}

