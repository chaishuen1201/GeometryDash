package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.grp19.geometrydash.GeometryDash;
import com.grp19.geometrydash.screen.GameScreen;
import com.badlogic.gdx.graphics.GL20;
public class LevelSelectionScreen implements Screen {
    private final GeometryDash game;
    private SpriteBatch batch;
    private Texture background;
    private Texture titleImage;
    private Texture[] levelTextures;
    private Texture lockTexture;

    private final int NUM_LEVELS = 5;
    private int unlockedLevels = 1; // Start with only level 1 unlocked

    public LevelSelectionScreen(GeometryDash game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("background.png"));
        titleImage = new Texture(Gdx.files.internal("Select Level.png"));

        levelTextures = new Texture[NUM_LEVELS];
        for (int i = 0; i < NUM_LEVELS; i++) {
            try {
                levelTextures[i] = new Texture(Gdx.files.internal("level" + (i + 1) + ".png"));
            } catch (Exception e) {
                Gdx.app.error("LevelSelection", "Error loading level texture " + (i + 1), e);
                levelTextures[i] = new Texture(Gdx.files.internal("player.png"));
            }
        }
        lockTexture = new Texture(Gdx.files.internal("lock.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw title
        float titleWidth = 1000;
        float titleHeight = 300;
        float titleX = (Gdx.graphics.getWidth() - titleWidth) / 2f;
        float titleY = Gdx.graphics.getHeight() - titleHeight - 40;
        batch.draw(titleImage, titleX, titleY, titleWidth, titleHeight);

        // Icon display layout
        float iconSize = 250;
        float spacing = 100;

        float startY1 = titleY - iconSize - 60;
        float startY2 = startY1 - iconSize - spacing;

        // First row: 3 icons
        float startX1 = (Gdx.graphics.getWidth() - (3 * iconSize + 2 * spacing)) / 2f;
        for (int i = 0; i < 3 && i < NUM_LEVELS; i++) {
            float x = startX1 + i * (iconSize + spacing);
            float y = startY1;

            if (i < unlockedLevels) {
                batch.draw(levelTextures[i], x, y, iconSize, iconSize);
            } else {
                batch.draw(lockTexture, x, y, iconSize, iconSize);
            }
        }

        // Second row: 2 icons
        float startX2 = (Gdx.graphics.getWidth() - (2 * iconSize + spacing)) / 2f;
        for (int i = 3; i < NUM_LEVELS; i++) {
            float x = startX2 + (i - 3) * (iconSize + spacing);
            float y = startY2;

            if (i < unlockedLevels) {
                batch.draw(levelTextures[i], x, y, iconSize, iconSize);
            } else {
                batch.draw(lockTexture, x, y, iconSize, iconSize);
            }
        }

        batch.end();

        // Handle touch input
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            for (int i = 0; i < NUM_LEVELS; i++) {
                float x, y;
                if (i < 3) {
                    x = startX1 + i * (iconSize + spacing);
                    y = startY1;
                } else {
                    x = startX2 + (i - 3) * (iconSize + spacing);
                    y = startY2;
                }

                if (i < unlockedLevels &&
                    touchX >= x && touchX <= x + iconSize &&
                    touchY >= y && touchY <= y + iconSize) {

                    final int selectedLevel = i + 1;
                    Gdx.app.log("LevelSelection", "Selected level: " + selectedLevel);

                    Gdx.app.postRunnable(() -> {
                        game.setScreen(new GameScreen(game, selectedLevel));
                        dispose();
                    });
                    return;
                }
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        titleImage.dispose();
        for (Texture tex : levelTextures) {
            if (tex != null) tex.dispose();
        }
        lockTexture.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
