package com.sa.game.mode;
import com.sa.game.World;
import com.sa.game.ui.UI;
public interface GameModeUIStrategy {
    void initialize(World world, UI ui);
    void render(float delta);
    void dispose();
}
