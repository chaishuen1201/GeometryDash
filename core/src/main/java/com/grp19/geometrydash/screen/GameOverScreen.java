//GameOverScreen.java

package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.grp19.geometrydash.GeometryDash;

public class GameOverScreen implements Screen {
    final GeometryDash game;

    public GameOverScreen(GeometryDash game) {
        this.game = game;
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.8f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.font.draw(game.batch, "Game Over", 300, 400);
        game.font.draw(game.batch, "Tap to Restart", 300, 350);
        game.batch.end();

        if (Gdx.input.justTouched()) {
            game.setScreen(new GameScreen(game));
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
