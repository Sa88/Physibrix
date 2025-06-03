package com.sa.game.assets;
import lombok.Getter;
@Getter
public enum TextureType {
    CONCRETE(AssetPaths.CONCRETE),
    GLASS(AssetPaths.GLASS),
    STEEL(AssetPaths.STEEL),
    WOOD(AssetPaths.WOOD),
    BRICK(AssetPaths.BRICK),
    STONE(AssetPaths.STONE),

    WARNING_GREEN(AssetPaths.WARNING_GREEN),
    WARNING_RED(AssetPaths.WARNING_RED),
    WARNING_YELLOW(AssetPaths.WARNING_YELLOW),
    MATERIALS(AssetPaths.MATERIALS),
    EXIT(AssetPaths.EXIT),
    CLEAN_UP(AssetPaths.CLEAN_UP),
    STABILITY_WARNING(AssetPaths.STABILITY_WARNING),
    CONFIRM_BLOCK(AssetPaths.CONFIRM_BLOCK),
    CANCEL_BLOCK(AssetPaths.CANCEL_BLOCK),
    UNDO(AssetPaths.UNDO);

    private final String path;

    TextureType(String path) {
        this.path = path;
    }

}
