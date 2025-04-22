package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.grp19.geometrydash.AudioManager;
import com.grp19.geometrydash.GeometryDash;

public class MainMenuScreen implements Screen {
    private final GeometryDash game;
    private SpriteBatch batch;
    private Texture background, titleImage, playButton, settingButton, leaderboardButton;

    public MainMenuScreen(GeometryDash game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Play background music
        Music menuMusic = Gdx.audio.newMusic(Gdx.files.internal("main_menu.mp3"));
        AudioManager.getInstance().playMusic(menuMusic);

        batch = new SpriteBatch();

        background = new Texture(Gdx.files.internal("background.png"));
        titleImage = new Texture(Gdx.files.internal("game_title.png"));
        playButton = new Texture(Gdx.files.internal("play.png"));
        settingButton = new Texture(Gdx.files.internal("settings.png"));
        leaderboardButton = new Texture(Gdx.files.internal("leaderboard.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Draw background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw title
        float titleWidth = 1500;
        float titleHeight = 300;
        float titleX = (Gdx.graphics.getWidth() - titleWidth) / 2f;
        float titleY = Gdx.graphics.getHeight() / 2f + 100;
        batch.draw(titleImage, titleX, titleY, titleWidth, titleHeight);

        // Draw play button
        float playButtonWidth = 470;
        float playButtonHeight = 400;
        float playButtonX = (Gdx.graphics.getWidth() - playButtonWidth) / 2f;
        float playButtonY = (Gdx.graphics.getHeight() - playButtonHeight) / 2f - 150;
        batch.draw(playButton, playButtonX, playButtonY, playButtonWidth, playButtonHeight);

        // Draw settings button
        float settingButtonWidth = 365;
        float settingButtonHeight = 350;
        float settingButtonX = (Gdx.graphics.getWidth() - playButtonWidth) / 2f + 480;
        float settingButtonY = playButtonY + (playButtonHeight - settingButtonHeight) / 2f;
        batch.draw(settingButton, settingButtonX, settingButtonY, settingButtonWidth, settingButtonHeight);

        // Draw leaderboard button
        float leaderboardButtonWidth = 300;
        float leaderboardButtonHeight = 310;
        float leaderboardButtonX = (Gdx.graphics.getWidth() - playButtonWidth) / 2f - 340;
        float leaderboardButtonY = playButtonY + (playButtonHeight - leaderboardButtonHeight) / 2f;
        batch.draw(leaderboardButton, leaderboardButtonX, leaderboardButtonY, leaderboardButtonWidth, leaderboardButtonHeight);

        batch.end();

        // Handle touch input
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // If play button is pressed, go to level selection screen
            if (touchX >= playButtonX && touchX <= playButtonX + playButtonWidth &&
                touchY >= playButtonY && touchY <= playButtonY + playButtonHeight) {
                Gdx.app.postRunnable(() -> {
                    game.setScreen(new LevelSelectionScreen(game));
                    dispose();
                });
            }

            // If settings button is pressed, go to settings screen
            else if (touchX >= settingButtonX && touchX <= settingButtonX + settingButtonWidth &&
                touchY >= settingButtonY && touchY <= settingButtonY + settingButtonHeight) {
                Gdx.app.postRunnable(() -> {
                    game.setScreen(new SettingsScreen(game));
                    dispose();
                });
            }

            // If leaderboard button is pressed, go to leaderboard screen
            else if (touchX >= leaderboardButtonX && touchX <= leaderboardButtonX + leaderboardButtonWidth &&
                touchY >= leaderboardButtonY && touchY <= leaderboardButtonY + leaderboardButtonHeight) {
                Gdx.app.postRunnable(() -> {
                    game.setScreen(new LeaderboardScreen(game));
                    dispose();
                });
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        titleImage.dispose();
        playButton.dispose();
        settingButton.dispose();
        leaderboardButton.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
