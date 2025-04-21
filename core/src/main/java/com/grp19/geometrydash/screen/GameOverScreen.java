package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.grp19.geometrydash.GeometryDash;

public class GameOverScreen implements Screen {
    private final GeometryDash game;
    private final int level;
    private SpriteBatch batch;
    private Texture background;
    private Texture gameOverText;
    private Texture retryButton;
    private Texture backButton;

    public GameOverScreen(GeometryDash game, int level) {
        this.game = game;
        this.level = level;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("background.png"));
        gameOverText = new Texture(Gdx.files.internal("gameOver.png"));
        retryButton = new Texture(Gdx.files.internal("retry.png"));
        backButton = new Texture(Gdx.files.internal("main_menu.png"));
    }

    @Override
    public void render(float delta) {
        batch.begin();

        // Draw background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw "Game Over" text
        float gameOverWidth = 1000;
        float gameOverHeight = 250;
        float gameOverX = (Gdx.graphics.getWidth() - gameOverWidth) / 2f;
        float gameOverY = Gdx.graphics.getHeight() / 2f + 100;
        batch.draw(gameOverText, gameOverX, gameOverY, gameOverWidth, gameOverHeight);

        // Button sizes
        float buttonWidth = 300;
        float buttonHeight = 300;
        float spacing = 80;

        // Total width of both buttons + spacing between them
        float totalButtonsWidth = 2 * buttonWidth + spacing;

        // Start X so the whole group is centered
        float startX = (Gdx.graphics.getWidth() - totalButtonsWidth) / 2f;
        float buttonY = gameOverY - buttonHeight - 50;

        // Back button position
        float backX = startX;

        // Retry button position
        float retryX = startX + buttonWidth + spacing;

        // Draw buttons
        batch.draw(backButton, backX, buttonY, buttonWidth, buttonHeight);
        batch.draw(retryButton, retryX, buttonY, buttonWidth, buttonHeight);

        batch.end();

        // Handle touch input
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Retry button click
            if (touchX >= retryX && touchX <= retryX + buttonWidth &&
                touchY >= buttonY && touchY <= buttonY + buttonHeight) {

                game.setScreen(new GameScreen(game, level));
                dispose();
                return;
            }

            // Back button click
            if (touchX >= backX && touchX <= backX + buttonWidth &&
                touchY >= buttonY && touchY <= buttonY + buttonHeight) {

                game.setScreen(new LevelSelectionScreen(game));
                dispose();
                return;
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        gameOverText.dispose();
        retryButton.dispose();
        backButton.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
