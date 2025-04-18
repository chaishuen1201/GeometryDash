package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.grp19.geometrydash.GeometryDash;
import com.grp19.geometrydash.Player;

public class GameScreen implements Screen {
    final GeometryDash game;
    private OrthographicCamera camera;
    private Texture background;
    private Player player;

    public GameScreen(GeometryDash game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        background = new Texture("background.png");
        player = new Player(); // make sure player.png exists
    }

    @Override
    public void render(float delta) {
        player.update(delta);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(background, 0, 0, 800, 480);
        player.render(game.batch);
        game.font.draw(game.batch, "Gameplay Running", 300, 400);
        game.batch.end();

        if (player.collides()) {
            game.setScreen(new GameOverScreen(game));
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        background.dispose();
    }
}
