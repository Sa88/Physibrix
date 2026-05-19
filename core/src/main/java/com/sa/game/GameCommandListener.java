package com.sa.game;
public interface GameCommandListener {
    void onClearBlocks();
    void onToggleWarnings();
    void onToggleWindSystem();
    void onUndo();
}
