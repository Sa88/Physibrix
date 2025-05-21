package com.sa.game.mode.mission;
public class Mission {
    String description = "Construa uma torre de pelo menos 10m que resista ao vento";
    float minHeight = 10f;
    boolean requireStability = true;

    float structureHeight = 10f;

    public boolean isCompleted() {
        return structureHeight >= minHeight && requireStability;
    }
}
