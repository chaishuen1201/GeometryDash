package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.grp19.geometrydash.GeometryDash;

public class LeaderboardScreen implements Screen {
    private final GeometryDash game;
    private SpriteBatch batch;
    private Texture background, backButton;

    public LeaderboardScreen(GeometryDash game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        background = new Texture(Gdx.files.internal("background.png"));
        backButton = new Texture(Gdx.files.internal("back.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Draw background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw back button
        float backButtonWidth = 150;
        float backButtonHeight = 150;
        float backButtonX = 20;
        float backButtonY = Gdx.graphics.getHeight() - backButtonHeight - 20;
        batch.draw(backButton, backButtonX, backButtonY, backButtonWidth, backButtonHeight);

        batch.end();

        // Handle touch input
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // If back button is pressed, return to main menu
            if (touchX >= backButtonX && touchX <= backButtonX + backButtonWidth &&
                touchY >= backButtonY && touchY <= backButtonY + backButtonHeight) {
                Gdx.app.postRunnable(() -> {
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                });
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        backButton.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
