package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.grp19.geometrydash.GeometryDash;
import com.grp19.geometrydash.utils.PreferencesManager;

public class WinScreen implements Screen {
    private final GeometryDash game;
    private final int level;
    private final int attempts;
    private SpriteBatch batch;
    private Texture background, completeTitle, restartButton, mainMenuButton;
    private BitmapFont font;
    private PreferencesManager prefs;
    private GlyphLayout layout;
    private Sound levelCompleteSound;

    public WinScreen(GeometryDash game, int level, int attempts) {
        this.game = game;
        this.level = level;
        this.attempts = attempts;
        this.prefs = game.prefs;
        this.layout = new GlyphLayout();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("background.png"));
        completeTitle = new Texture(Gdx.files.internal("CompleteTitle.png"));
        restartButton = new Texture(Gdx.files.internal("retry.png"));
        mainMenuButton = new Texture(Gdx.files.internal("main_menu.png"));
        
        // Load and play level complete sound
        levelCompleteSound = Gdx.audio.newSound(Gdx.files.internal("level-complete-geometry-dash.mp3"));
        levelCompleteSound.play(1.0f); // Play at full volume
        
        // Create custom font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pusab.otf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 80;
        parameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        parameter.borderWidth = 6;
        parameter.borderColor = com.badlogic.gdx.graphics.Color.BLACK;
        font = generator.generateFont(parameter);
        generator.dispose();

        // Save best attempt count if it's better than the previous best
        int bestAttempts = prefs.getHighScore(level);
        if (bestAttempts == 0 || attempts < bestAttempts) {
            prefs.setHighScore(level, attempts);
        }

        // Unlock next level
        prefs.unlockLevel(level + 1);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        // Draw background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw "Level Complete" text
        float titleWidth = 1200;
        float titleHeight = 275;
        float titleX = (Gdx.graphics.getWidth() - titleWidth) / 2f;
        float titleY = Gdx.graphics.getHeight() / 2f + 100;
        batch.draw(completeTitle, titleX, titleY, titleWidth, titleHeight);

        // Draw attempt count with more spacing and centered
        String attemptText = "Attempts: " + attempts;
        font.setColor(1, 1, 1, 1);
        layout.setText(font, attemptText);
        float attemptX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float attemptY = titleY - 50; // Moved higher
        font.draw(batch, attemptText, attemptX, attemptY);

        // Draw best attempts if available with more spacing and centered
        int bestAttempts = prefs.getHighScore(level);
        if (bestAttempts > 0) {
            String bestText = "Best: " + bestAttempts;
            layout.setText(font, bestText);
            float bestX = (Gdx.graphics.getWidth() - layout.width) / 2f;
            float bestY = attemptY - 70; // Moved higher
            font.draw(batch, bestText, bestX, bestY);
        }

        // Draw restart button - moved lower
        float buttonWidth = 300;
        float buttonHeight = 300;
        float restartButtonX = (Gdx.graphics.getWidth() - buttonWidth) / 2f - 200;
        float buttonY = (Gdx.graphics.getHeight() - buttonHeight) / 2f - 250; // Moved lower
        batch.draw(restartButton, restartButtonX, buttonY, buttonWidth, buttonHeight);

        // Draw main menu button - moved lower
        float mainMenuButtonX = (Gdx.graphics.getWidth() - buttonWidth) / 2f + 200;
        batch.draw(mainMenuButton, mainMenuButtonX, buttonY, buttonWidth, buttonHeight);
        batch.end();

        // Handle touch input
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Restart button
            if (touchX >= restartButtonX && touchX <= restartButtonX + buttonWidth &&
                touchY >= buttonY && touchY <= buttonY + buttonHeight) {
                game.setScreen(new GameScreen(game, level));
                dispose();
            }

            // Main menu button
            if (touchX >= mainMenuButtonX && touchX <= mainMenuButtonX + buttonWidth &&
                touchY >= buttonY && touchY <= buttonY + buttonHeight) {
                game.setScreen(new LevelSelectionScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        completeTitle.dispose();
        restartButton.dispose();
        mainMenuButton.dispose();
        font.dispose();
        if (levelCompleteSound != null) {
            levelCompleteSound.dispose();
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
