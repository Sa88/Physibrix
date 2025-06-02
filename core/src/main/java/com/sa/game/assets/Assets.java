package com.sa.game.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable {

    private static Assets instance;

    public static Assets getInstance() {
        if (instance == null) {
            instance = new Assets();
        }
        return instance;
    }

    private final AssetManager assetManager = new AssetManager();

    // Textures carregadas
    public Texture concrete;
    public Texture glass;
    public Texture steel;
    public Texture wood;
    public Texture brick;
    public Texture stone;

    public Texture warningGreen;
    public Texture warningRed;
    public Texture warningYellow;

    public Texture materials;
    public Texture exit;
    public Texture cleanup;
    public Texture stabilityWarning;
    public Texture confirmBlock;
    public Texture cancelBlock;
    public Texture undo;

    private Assets() {
        // Construtor privado
    }

    // Inicia o carregamento assíncrono
    public void loadAllAsync() {
        loadTextures();
        loadIcons();
    }

    // Atualiza o carregamento (true = terminou)
    public boolean update() {
        return assetManager.update();
    }

    // Retorna progresso de 0.0 a 1.0
    public float getProgress() {
        return assetManager.getProgress();
    }

    // Atribui as texturas após tudo estar carregado
    public void assignTexturesIfLoaded() {
        concrete = assetManager.get(AssetPaths.CONCRETE, Texture.class);
        glass = assetManager.get(AssetPaths.GLASS, Texture.class);
        steel = assetManager.get(AssetPaths.STEEL, Texture.class);
        wood = assetManager.get(AssetPaths.WOOD, Texture.class);
        brick = assetManager.get(AssetPaths.BRICK, Texture.class);
        stone = assetManager.get(AssetPaths.STONE, Texture.class);

        warningGreen = assetManager.get(AssetPaths.WARNING_GREEN, Texture.class);
        warningRed = assetManager.get(AssetPaths.WARNING_RED, Texture.class);
        warningYellow = assetManager.get(AssetPaths.WARNING_YELLOW, Texture.class);
        materials = assetManager.get(AssetPaths.MATERIALS, Texture.class);
        exit = assetManager.get(AssetPaths.EXIT, Texture.class);
        cleanup = assetManager.get(AssetPaths.CLEAN_UP, Texture.class);
        stabilityWarning = assetManager.get(AssetPaths.STABILITY_WARNING, Texture.class);
        confirmBlock = assetManager.get(AssetPaths.CONFIRM_BLOCK, Texture.class);
        cancelBlock = assetManager.get(AssetPaths.CANCEL_BLOCK, Texture.class);
        undo = assetManager.get(AssetPaths.UNDO, Texture.class);
    }

    private void loadTextures() {
        assetManager.load(AssetPaths.CONCRETE, Texture.class);
        assetManager.load(AssetPaths.GLASS, Texture.class);
        assetManager.load(AssetPaths.STEEL, Texture.class);
        assetManager.load(AssetPaths.WOOD, Texture.class);
        assetManager.load(AssetPaths.BRICK, Texture.class);
        assetManager.load(AssetPaths.STONE, Texture.class);
    }

    private void loadIcons() {
        assetManager.load(AssetPaths.WARNING_GREEN, Texture.class);
        assetManager.load(AssetPaths.WARNING_RED, Texture.class);
        assetManager.load(AssetPaths.WARNING_YELLOW, Texture.class);
        assetManager.load(AssetPaths.MATERIALS, Texture.class);
        assetManager.load(AssetPaths.EXIT, Texture.class);
        assetManager.load(AssetPaths.CLEAN_UP, Texture.class);
        assetManager.load(AssetPaths.STABILITY_WARNING, Texture.class);
        assetManager.load(AssetPaths.CONFIRM_BLOCK, Texture.class);
        assetManager.load(AssetPaths.CANCEL_BLOCK, Texture.class);
        assetManager.load(AssetPaths.UNDO, Texture.class);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
