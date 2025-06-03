package com.sa.game.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import java.util.EnumMap;

public class Assets implements Disposable {

    private static Assets instance;

    public static Assets getInstance() {
        if (instance == null) {
            instance = new Assets();
        }
        return instance;
    }

    private final AssetManager assetManager = new AssetManager();
    private final EnumMap<TextureType, Texture> textures = new EnumMap<>(TextureType.class);

    private Assets() {}

    public void loadAllAsync() {
        for (TextureType type : TextureType.values()) {
            assetManager.load(type.getPath(), Texture.class);
        }
    }

    public boolean update() {
        return assetManager.update();
    }

    public float getProgress() {
        return assetManager.getProgress();
    }

    public void assignTexturesIfLoaded() {
        for (TextureType type : TextureType.values()) {
            textures.put(type, assetManager.get(type.getPath(), Texture.class));
        }
    }

    public Texture get(TextureType type) {
        return textures.get(type);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        textures.clear();
    }
}
