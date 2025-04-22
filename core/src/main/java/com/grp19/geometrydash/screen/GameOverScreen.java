package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.grp19.geometrydash.GeometryDash;

public class GameOverScreen implements Screen {
    private final GeometryDash game;
    private final int level;
    private SpriteBatch batch;
    private Texture background, titleImage, retryButton, mainMenuButton;

    public GameOverScreen(GeometryDash game, int level) {
        this.game = game;
        this.level = level;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        background = new Texture(Gdx.files.internal("background.png"));
        titleImage = new Texture(Gdx.files.internal("gameover_title.png"));
        retryButton = new Texture(Gdx.files.internal("restart.png"));
        mainMenuButton = new Texture(Gdx.files.internal("main_menu.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Draw background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw "Game Over" title
        float titleWidth = 1200;
        float titleHeight = 230;
        float titleX = (Gdx.graphics.getWidth() - titleWidth) / 2f;
        float titleY = Gdx.graphics.getHeight() / 2f + 100;
        batch.draw(titleImage, titleX, titleY, titleWidth, titleHeight);

        // Draw retry button
        float retryButtonWidth = 300;
        float retryButtonHeight = 300;
        float retryButtonX = (Gdx.graphics.getWidth() - retryButtonWidth) / 2f - 200;
        float retryButtonY = Gdx.graphics.getHeight() / 2f - 300;
        batch.draw(retryButton, retryButtonX, retryButtonY, retryButtonWidth, retryButtonHeight);

        // Draw main menu button
        float mainMenuButtonWidth = 300;
        float mainMenuButtonHeight = 300;
        float mainMenuButtonX = (Gdx.graphics.getWidth() - retryButtonWidth) / 2f + 200;
        float mainMenuButtonY = Gdx.graphics.getHeight() / 2f - 300;
        batch.draw(mainMenuButton, mainMenuButtonX, mainMenuButtonY, mainMenuButtonWidth, mainMenuButtonHeight);

        batch.end();

        // Handle touch input
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Retry button
            if (touchX >= retryButtonX && touchX <= retryButtonX + retryButtonWidth &&
                touchY >= retryButtonY && touchY <= retryButtonY + retryButtonHeight) {
                game.setScreen(new GameScreen(game, level));
                dispose();
            }

            // Main menu button
            if (touchX >= mainMenuButtonX && touchX <= mainMenuButtonX + mainMenuButtonWidth &&
                touchY >= mainMenuButtonY && touchY <= mainMenuButtonY + mainMenuButtonHeight) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        titleImage.dispose();
        retryButton.dispose();
        mainMenuButton.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
