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
    private Texture background, titleImage, restartButton, resumeButton, mainmenuButton;
    private float titleX, titleY, titleWidth, titleHeight;
    private float resumeButtonX, resumeButtonY, resumeButtonWidth, resumeButtonHeight;
    private float restartButtonX, restartButtonY, restartButtonWidth, restartButtonHeight;
    private float mainmenuButtonX, mainmenuButtonY, mainmenuButtonWidth, mainmenuButtonHeight;

    public PauseMenu(GeometryDash game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("background.png"));
        titleImage = new Texture(Gdx.files.internal("pause_title.png"));
        resumeButton = new Texture(Gdx.files.internal("resume.png"));
        restartButton = new Texture(Gdx.files.internal("restart.png"));
        mainmenuButton = new Texture(Gdx.files.internal("main_menu.png"));

        AudioManager.getInstance().pauseMusic();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw title
        titleWidth = 800;
        titleHeight = 275;
        titleX = (Gdx.graphics.getWidth() - titleWidth) / 2f;
        titleY = Gdx.graphics.getHeight() / 2f + 100;
        batch.draw(titleImage, titleX, titleY, titleWidth, titleHeight);

        // Draw buttons
        resumeButtonWidth = 350;
        resumeButtonHeight = 350;
        resumeButtonX = (Gdx.graphics.getWidth() - resumeButtonWidth) / 2f;
        resumeButtonY = (Gdx.graphics.getHeight() - resumeButtonHeight) / 2f - 150;
        batch.draw(resumeButton, resumeButtonX, resumeButtonY, resumeButtonWidth, resumeButtonHeight);

        restartButtonWidth = 300;
        restartButtonHeight = 300;
        restartButtonX = resumeButtonX - 380;
        restartButtonY = resumeButtonY + (resumeButtonHeight - restartButtonHeight) / 2f;
        batch.draw(restartButton, restartButtonX, restartButtonY, restartButtonWidth, restartButtonHeight);

        mainmenuButtonWidth = 300;
        mainmenuButtonHeight = 300;
        mainmenuButtonX = resumeButtonX + 430;
        mainmenuButtonY = resumeButtonY + (resumeButtonHeight - mainmenuButtonHeight) / 2f;
        batch.draw(mainmenuButton, mainmenuButtonX, mainmenuButtonY, mainmenuButtonWidth, mainmenuButtonHeight);
        batch.end();

        // Handle input
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (touchX >= resumeButtonX && touchX <= resumeButtonX + resumeButtonWidth &&
                touchY >= resumeButtonY && touchY <= resumeButtonY + resumeButtonHeight) {
                AudioManager.getInstance().resumeMusic();
                game.setScreen(gameScreen);
                dispose();
            }


            if (touchX >= restartButtonX && touchX <= restartButtonX + restartButtonWidth &&
                touchY >= restartButtonY && touchY <= restartButtonY + restartButtonHeight) {
                gameScreen.dispose();
                AudioManager.getInstance().stopMusic();
                // Use the getLevel() method to restart at the correct level
                game.setScreen(new GameScreen(game, gameScreen.getLevel()));  // Restart at the correct level

                dispose();
            }

            if (touchX >= mainmenuButtonX && touchX <= mainmenuButtonX + mainmenuButtonWidth &&
                touchY >= mainmenuButtonY && touchY <= mainmenuButtonY + mainmenuButtonHeight) {
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
        mainmenuButton.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
