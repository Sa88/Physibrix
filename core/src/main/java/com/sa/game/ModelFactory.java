package com.sa.game;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.sa.game.assets.Assets;

import java.util.HashMap;
import java.util.Map;

public class ModelFactory {

    private static final Map<BlockModelKey, Model> modelCache = new HashMap<>();

    private static final int VERTEX_ATTRIBUTES = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;

    public static Model createGroundModel() {
        ModelBuilder modelBuilder = new ModelBuilder();
        return modelBuilder.createBox(50f, 1f, 50f, new Material(TextureAttribute.createDiffuse(Assets.getInstance().concrete)),VERTEX_ATTRIBUTES);
    }

    public static Model createPillarModel(Material material, BlockShapeType shapeType) {
        ModelBuilder modelBuilder = new ModelBuilder();

        return switch (shapeType) {
            case BOX -> modelBuilder.createBox(0.5f, 2f, 0.5f, material, VERTEX_ATTRIBUTES);
            case CYLINDER -> modelBuilder.createCylinder(0.5f, 2f, 0.5f, 16, material, VERTEX_ATTRIBUTES);
            case CONE -> modelBuilder.createCone(0.5f, 2f, 0.5f, 16, material, VERTEX_ATTRIBUTES);
        };
    }

    public static Model createBaseModel(Material material) {
        ModelBuilder modelBuilder = new ModelBuilder();
        return modelBuilder.createBox(4f, 0.5f, 4f, material, VERTEX_ATTRIBUTES);
    }

    public static Model createRoofModel(Material material, BlockShapeType shapeType) {
        ModelBuilder modelBuilder = new ModelBuilder();
        return switch (shapeType) {
            case BOX -> modelBuilder.createBox(1f, 2f, 1f, material, VERTEX_ATTRIBUTES);
            default -> modelBuilder.createCone(1f, 2f, 1f, 16, material, VERTEX_ATTRIBUTES);
        };
    }

    public static Model createStepModel(Material material) {
        ModelBuilder modelBuilder = new ModelBuilder();
        return modelBuilder.createBox(2f, 0.5f, 2f, material, VERTEX_ATTRIBUTES);
    }

    public static Model createBlockModel(MaterialType currentMaterial, BlockType blockType, BlockShapeType blockShapeType) {

        var modelKey = new BlockModelKey(currentMaterial, blockType, blockShapeType);

        if (modelCache.containsKey(modelKey)) return modelCache.get(modelKey);


        // Seleciona o material baseado no tipo atual
        Material material = switch (currentMaterial) {
            case STEEL -> new Material(TextureAttribute.createDiffuse(Assets.getInstance().steel));
            case WOOD -> new Material(TextureAttribute.createDiffuse(Assets.getInstance().wood));
            case GLASS -> new Material(TextureAttribute.createDiffuse(Assets.getInstance().glass));
            case BRICK -> new Material(TextureAttribute.createDiffuse(Assets.getInstance().brick));
            case STONE -> new Material(TextureAttribute.createDiffuse(Assets.getInstance().stone));
            default -> new Material(TextureAttribute.createDiffuse(Assets.getInstance().concrete));
        };

        Model model = switch (blockType) {
            case PILLAR -> createPillarModel(material, blockShapeType);
            case BASE -> createBaseModel(material);
            case STEP -> createStepModel(material);
            case ROOF -> createRoofModel(material, blockShapeType);
        };

        modelCache.put(modelKey, model);
        return model;
    }

    public static void disposeAllModels() {
        for (Model model : modelCache.values()) {
            model.dispose();
        }
        modelCache.clear();
    }

}
