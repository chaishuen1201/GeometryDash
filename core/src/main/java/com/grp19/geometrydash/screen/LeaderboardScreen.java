package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Gdx;
import com.grp19.geometrydash.GeometryDash;

public class LeaderboardScreen implements Screen {
    private final GeometryDash game;

    public LeaderboardScreen(GeometryDash game) {
        this.game = game;
    }

    @Override public void show() {}
    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
