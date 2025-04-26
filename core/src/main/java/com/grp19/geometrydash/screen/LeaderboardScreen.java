package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.BitmapFont;
import com.grp19.geometrydash.GeometryDash;

public class LeaderboardScreen implements Screen {
    private final GeometryDash game;
    private SpriteBatch batch;
    private Texture background, backButton;
    private BitmapFont font;

    // Preferences for saving best attempts
    private Preferences prefs;

    public LeaderboardScreen(GeometryDash game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("background.png"));
        backButton = new Texture(Gdx.files.internal("back.png"));
        font = new BitmapFont(); // You can customize the font here if needed

        // Load preferences to get best attempts
        prefs = Gdx.app.getPreferences("LeaderboardPrefs");
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

        // Display best attempts for each level
        float textY = Gdx.graphics.getHeight() - 100; // Starting Y position for the first level

        // Iterate over levels and display best attempts
        for (int i = 1; i <= 5; i++) { // Assuming you have 5 levels
            String levelName = "Level " + i;
            int bestAttempt = prefs.getInteger("best_attempt_" + i, 0); // Default to 0 if no attempt is saved
            String bestAttemptText = levelName + ": Best Attempt - " + bestAttempt;

            font.draw(batch, bestAttemptText, 20, textY);
            textY -= 30; // Adjust the Y position for the next line of text
        }

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
        font.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
