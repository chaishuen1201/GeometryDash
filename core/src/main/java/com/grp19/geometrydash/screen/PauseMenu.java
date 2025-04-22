package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.grp19.geometrydash.AudioManager;
import com.grp19.geometrydash.GeometryDash;

public class PauseMenu implements Screen {
    private final GeometryDash game;
    private final GameScreen gameScreen;
    private SpriteBatch batch;
    private Texture background, titleImage, restartButton, resumeButton, mainMenuButton;

    public PauseMenu(GeometryDash game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        // Pause background music
        AudioManager.getInstance().pauseMusic();

        batch = new SpriteBatch();

        background = new Texture(Gdx.files.internal("background.png"));
        titleImage = new Texture(Gdx.files.internal("pause_title.png"));
        resumeButton = new Texture(Gdx.files.internal("resume.png"));
        restartButton = new Texture(Gdx.files.internal("restart.png"));
        mainMenuButton = new Texture(Gdx.files.internal("main_menu.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Draw background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw "Pause" title
        float titleWidth = 800;
        float titleHeight = 275;
        float titleX = (Gdx.graphics.getWidth() - titleWidth) / 2f;
        float titleY = Gdx.graphics.getHeight() / 2f + 100;
        batch.draw(titleImage, titleX, titleY, titleWidth, titleHeight);

        // Draw resume button
        float resumeButtonWidth = 350;
        float resumeButtonHeight = 350;
        float resumeButtonX = (Gdx.graphics.getWidth() - resumeButtonWidth) / 2f;
        float resumeButtonY = (Gdx.graphics.getHeight() - resumeButtonHeight) / 2f - 150;
        batch.draw(resumeButton, resumeButtonX, resumeButtonY, resumeButtonWidth, resumeButtonHeight);

        // Draw restart button
        float restartButtonWidth = 300;
        float restartButtonHeight = 300;
        float restartButtonX = resumeButtonX - 380;
        float restartButtonY = resumeButtonY + (resumeButtonHeight - restartButtonHeight) / 2f;
        batch.draw(restartButton, restartButtonX, restartButtonY, restartButtonWidth, restartButtonHeight);

        // Draw main menu button
        float mainMenuButtonWidth = 300;
        float mainMenuButtonHeight = 300;
        float mainMenuButtonX = resumeButtonX + 430;
        float mainMenuButtonY = resumeButtonY + (resumeButtonHeight - mainMenuButtonHeight) / 2f;
        batch.draw(mainMenuButton, mainMenuButtonX, mainMenuButtonY, mainMenuButtonWidth, mainMenuButtonHeight);
        batch.end();

        // Handle touch input
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Resume button
            if (touchX >= resumeButtonX && touchX <= resumeButtonX + resumeButtonWidth &&
                touchY >= resumeButtonY && touchY <= resumeButtonY + resumeButtonHeight) {
                AudioManager.getInstance().resumeMusic();
                game.setScreen(gameScreen);
                dispose();
            }

            // Restart button
            if (touchX >= restartButtonX && touchX <= restartButtonX + restartButtonWidth &&
                touchY >= restartButtonY && touchY <= restartButtonY + restartButtonHeight) {
                gameScreen.dispose();
                AudioManager.getInstance().stopMusic();
                game.setScreen(new GameScreen(game, gameScreen.getLevel()));
                dispose();
            }

            // Main menu button
            if (touchX >= mainMenuButtonX && touchX <= mainMenuButtonX + mainMenuButtonWidth &&
                touchY >= mainMenuButtonY && touchY <= mainMenuButtonY + mainMenuButtonHeight) {
                gameScreen.dispose();
                AudioManager.getInstance().stopMusic();
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
        resumeButton.dispose();
        restartButton.dispose();
        mainMenuButton.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
