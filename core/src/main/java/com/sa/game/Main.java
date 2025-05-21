package com.sa.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.physics.bullet.Bullet;

public class Main extends Game {

    @Override
    public void create() {

        Bullet.init();
        setScreen(new LoadingScreen(this));

    }
}
