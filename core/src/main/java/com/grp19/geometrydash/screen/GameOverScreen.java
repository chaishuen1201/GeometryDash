package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.grp19.geometrydash.GeometryDash;

public class GameOverScreen implements Screen {
    private final GeometryDash game;
    private final int level;
    private final int attempts;
    private SpriteBatch batch;
    private Texture background, gameOverImage, retryButton, mainMenuButton;
    private BitmapFont font;
    private GlyphLayout layout;

    public GameOverScreen(GeometryDash game, int level, int attempts) {
        this.game = game;
        this.level = level;
        this.attempts = attempts;
        this.layout = new GlyphLayout();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("background.png"));
        gameOverImage = new Texture(Gdx.files.internal("gameOver.png"));
        retryButton = new Texture(Gdx.files.internal("retry.png"));
        mainMenuButton = new Texture(Gdx.files.internal("main_menu.png"));
        
        // Create custom font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pusab.otf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 80;
        parameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        parameter.borderWidth = 6;
        parameter.borderColor = com.badlogic.gdx.graphics.Color.BLACK;
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        // Draw background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw "Game Over" text
        float titleWidth = 1200;
        float titleHeight = 275;
        float titleX = (Gdx.graphics.getWidth() - titleWidth) / 2f;
        float titleY = Gdx.graphics.getHeight() / 2f + 100;
        batch.draw(gameOverImage, titleX, titleY, titleWidth, titleHeight);

        // Draw attempt count with more spacing and centered
        String attemptText = "Attempts: " + attempts;
        font.setColor(1, 1, 1, 1);
        layout.setText(font, attemptText);
        float attemptX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float attemptY = titleY - 50;
        font.draw(batch, attemptText, attemptX, attemptY);

        // Draw retry button - moved lower
        float buttonWidth = 300;
        float buttonHeight = 300;
        float retryButtonX = (Gdx.graphics.getWidth() - buttonWidth) / 2f - 200;
        float buttonY = (Gdx.graphics.getHeight() - buttonHeight) / 2f - 250; // Moved lower
        batch.draw(retryButton, retryButtonX, buttonY, buttonWidth, buttonHeight);

        // Draw main menu button - moved lower
        float mainMenuButtonX = (Gdx.graphics.getWidth() - buttonWidth) / 2f + 200;
        batch.draw(mainMenuButton, mainMenuButtonX, buttonY, buttonWidth, buttonHeight);
        batch.end();

        // Handle touch input
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Retry button
            if (touchX >= retryButtonX && touchX <= retryButtonX + buttonWidth &&
                touchY >= buttonY && touchY <= buttonY + buttonHeight) {
                game.setScreen(new GameScreen(game, level, attempts));
                dispose();
            }

            // Main menu button
            if (touchX >= mainMenuButtonX && touchX <= mainMenuButtonX + buttonWidth &&
                touchY >= buttonY && touchY <= buttonY + buttonHeight) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        gameOverImage.dispose();
        retryButton.dispose();
        mainMenuButton.dispose();
        font.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
