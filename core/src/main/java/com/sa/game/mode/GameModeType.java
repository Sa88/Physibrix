package com.sa.game.mode;
import com.sa.game.mode.creative.CreativeMode;
import com.sa.game.mode.mission.MissionMode;
import com.sa.game.mode.survival.SurvivalMode;
public enum GameModeType {
    CREATIVE("Construção livre, sem limites de recursos e sem inimigos."),
    MISSION("Objetivos específicos a serem cumpridos em cada missão."),
    SURVIVAL("Sobreviva coletando recursos e enfrentando perigos.");

    private final String description;

    GameModeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public GameMode createInstance() {
        return switch (this) {
            case CREATIVE -> new CreativeMode();
            case MISSION -> new MissionMode();
            case SURVIVAL -> new SurvivalMode();
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case CREATIVE -> "Creative";
            case MISSION -> "Mission";
            case SURVIVAL -> "Survival";
        };
    }
}

