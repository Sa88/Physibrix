package com.sa.game.mode.creative;
import com.sa.game.World;
import com.sa.game.mode.GameModeUIStrategy;
import com.sa.game.ui.UI;
public class CreativeUIStrategy implements GameModeUIStrategy {
    @Override
    public void initialize(World world, UI ui) {
        // Creative-specific UI setup
    }

    @Override
    public void render(float delta) {
        // Show creative mode HUD
    }

    @Override
    public void dispose() {
        // Cleanup
    }
}
