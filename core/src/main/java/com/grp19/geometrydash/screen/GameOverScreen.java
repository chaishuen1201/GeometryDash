//GameOverScreen.java

package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.grp19.geometrydash.GeometryDash;
import com.grp19.geometrydash.screen.GameScreen;

public class GameOverScreen implements Screen {
    private final GeometryDash game;
    private final int level;
    private SpriteBatch batch;
    private Texture background;
    private Texture gameOverText;
    private Texture retryButton;

    public GameOverScreen(GeometryDash game, int level) {
        this.game = game;
        this.level = level;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("gameBackground.png"));
        gameOverText = new Texture(Gdx.files.internal("gameOver.png"));
        retryButton = new Texture(Gdx.files.internal("retry.png"));
    }

    @Override
    public void render(float delta) {
        batch.begin();

        // Draw background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw "Game Over" text
        float gameOverWidth = 600;
        float gameOverHeight = 150;
        float gameOverX = (Gdx.graphics.getWidth() - gameOverWidth) / 2f;
        float gameOverY = Gdx.graphics.getHeight() / 2f + 100;
        batch.draw(gameOverText, gameOverX, gameOverY, gameOverWidth, gameOverHeight);

        // Draw retry button
        float retryWidth = 250;
        float retryHeight = 120;
        float retryX = (Gdx.graphics.getWidth() - retryWidth) / 2f;
        float retryY = gameOverY - retryHeight - 50;
        batch.draw(retryButton, retryX, retryY, retryWidth, retryHeight);

        batch.end();

        // Handle touch input for retry
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (touchX >= retryX && touchX <= retryX + retryWidth &&
                touchY >= retryY && touchY <= retryY + retryHeight) {

                game.setScreen(new GameScreen(game, level));
                dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        gameOverText.dispose();
        retryButton.dispose();
    }
}
