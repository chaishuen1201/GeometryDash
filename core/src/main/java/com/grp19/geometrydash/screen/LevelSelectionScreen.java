package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.grp19.geometrydash.GeometryDash;
import com.badlogic.gdx.graphics.GL20;

public class LevelSelectionScreen implements Screen {
    private final GeometryDash game;
    private SpriteBatch batch;
    private Texture background;
    private Texture titleImage;
    private Texture[] levelTextures;
    private Texture lockTexture;
    private Texture backButton;
    private Sound levelSelectedSound;

    private final int NUM_LEVELS = 5;
    private int unlockedLevels;

    public LevelSelectionScreen(GeometryDash game) {
        this.game = game;
        this.unlockedLevels = game.prefs.getUnlockedLevel();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture("background.png");
        titleImage = new Texture("Select Level.png");
        backButton = new Texture("back.png");
        lockTexture = new Texture("lock.png");
        
        // Load level selected sound
        levelSelectedSound = Gdx.audio.newSound(Gdx.files.internal("geometry-dash-level-selected.mp3"));

        levelTextures = new Texture[NUM_LEVELS];
        for (int i = 0; i < NUM_LEVELS; i++) {
            try {
                levelTextures[i] = new Texture("level" + (i + 1) + ".png");
            } catch (Exception e) {
                Gdx.app.error("LevelSelection", "Error loading level texture " + (i + 1), e);
                levelTextures[i] = new Texture("player.png");
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Draw background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw title
        float titleWidth = 1200;
        float titleHeight = 300;
        float titleX = (Gdx.graphics.getWidth() - titleWidth) / 2f;
        float titleY = Gdx.graphics.getHeight() - titleHeight - 40;
        batch.draw(titleImage, titleX, titleY, titleWidth, titleHeight);

        // Back button
        float backButtonSize = 150;
        float margin = 60;
        float backButtonX = margin;
        float backButtonY = Gdx.graphics.getHeight() - backButtonSize - margin;
        batch.draw(backButton, backButtonX, backButtonY, backButtonSize, backButtonSize);

        // Icon layout
        float iconSize = 250;
        float spacing = 100;
        float startY1 = titleY - iconSize - 60;
        float startY2 = startY1 - iconSize - spacing;

        // First row (3 levels)
        float startX1 = (Gdx.graphics.getWidth() - (3 * iconSize + 2 * spacing)) / 2f;
        for (int i = 0; i < 3 && i < NUM_LEVELS; i++) {
            float x = startX1 + i * (iconSize + spacing);
            float y = startY1;
            batch.draw(i < unlockedLevels ? levelTextures[i] : lockTexture, x, y, iconSize, iconSize);
        }

        // Second row (2 levels)
        float startX2 = (Gdx.graphics.getWidth() - (2 * iconSize + spacing)) / 2f;
        for (int i = 3; i < NUM_LEVELS; i++) {
            float x = startX2 + (i - 3) * (iconSize + spacing);
            float y = startY2;
            batch.draw(i < unlockedLevels ? levelTextures[i] : lockTexture, x, y, iconSize, iconSize);
        }

        batch.end();

        // Handle input
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Check back button
            if (touchX >= backButtonX && touchX <= backButtonX + backButtonSize &&
                touchY >= backButtonY && touchY <= backButtonY + backButtonSize) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
                return;
            }

            // Check level icons
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
                    // Play level selected sound
                    levelSelectedSound.play();
                    
                    final int selectedLevel = i + 1;
                    game.setScreen(new GameScreen(game, selectedLevel));
                    dispose();
                    return;
                }
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
        titleImage.dispose();
        backButton.dispose();
        lockTexture.dispose();
        for (Texture tex : levelTextures) {
            tex.dispose();
        }
        if (levelSelectedSound != null) {
            levelSelectedSound.dispose();
        }
    }
}
