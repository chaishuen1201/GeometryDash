package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.grp19.geometrydash.GeometryDash;
import com.grp19.geometrydash.GameData;

public class WinScreen implements Screen {
    private final GeometryDash game;
    private final int level;

    private Texture background;
    private Texture winBorder;
    private Texture winTitle;
    private Texture restartButton;
    private Texture levelSelectButton;

    private Rectangle restartBounds;
    private Rectangle levelSelectBounds;

    private SpriteBatch batch;

    public WinScreen(GeometryDash game, int level) {
        this.game = game;
        this.level = level;
    }

    @Override
    public void show() {
        GameData.getInstance().unlockNextLevel(level);

        batch = new SpriteBatch();

        // Load textures
        background = new Texture(Gdx.files.internal("background.png"));
        winBorder = new Texture(Gdx.files.internal("WinBorder.png"));
        winTitle = new Texture(Gdx.files.internal("CompleteTitle.png"));
        restartButton = new Texture(Gdx.files.internal("restart.png"));
        levelSelectButton = new Texture(Gdx.files.internal("main_menu.png")); // Changed from main_menu.png

        // Calculate positions
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float buttonSpacing = 50f; // Space between buttons

        // Calculate total width of both buttons plus spacing
        float totalWidth = restartButton.getWidth() + levelSelectButton.getWidth() + buttonSpacing;
        float startX = centerX - (totalWidth / 2);

        // Button bounds for click detection
        restartBounds = new Rectangle(
            startX,
            centerY - 150,
            restartButton.getWidth(),
            restartButton.getHeight()
        );

        levelSelectBounds = new Rectangle(
            startX + restartButton.getWidth() + buttonSpacing,
            centerY - 150,
            levelSelectButton.getWidth(),
            levelSelectButton.getHeight()
        );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Draw background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Calculate center positions
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        // Draw win border (centered)
        batch.draw(winBorder,
            centerX - winBorder.getWidth() / 2,
            centerY - winBorder.getHeight() / 2);

        // Draw win title (centered above buttons)
        batch.draw(winTitle,
            centerX - winTitle.getWidth() / 2,
            centerY + 100);

        // Draw buttons horizontally
        batch.draw(restartButton, restartBounds.x, restartBounds.y);
        batch.draw(levelSelectButton, levelSelectBounds.x, levelSelectBounds.y);

        batch.end();

        // Handle button clicks
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (restartBounds.contains(touchX, touchY)) {
                game.setScreen(new GameScreen(game, level));
                dispose();
            } else if (levelSelectBounds.contains(touchX, touchY)) {
                game.setScreen(new LevelSelectionScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Recalculate positions if window is resized
        show();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        winBorder.dispose();
        winTitle.dispose();
        restartButton.dispose();
        levelSelectButton.dispose();
    }
}
