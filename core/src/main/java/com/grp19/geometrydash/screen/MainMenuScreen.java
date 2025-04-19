package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.grp19.geometrydash.GeometryDash;

public class MainMenuScreen implements Screen {
    private final GeometryDash game;
    private SpriteBatch batch;
    private Texture background;
    private Texture playButton;
    private Texture settingButton;
    private Texture leaderboardButton;
    private Texture titleImage;
    private Music menuMusic;

    private float playButtonX, playButtonY, playButtonWidth, playButtonHeight;
    private float settingButtonX, settingButtonY, settingButtonWidth, settingButtonHeight;
    private float leaderboardButtonX, leaderboardButtonY, leaderboardButtonWidth, leaderboardButtonHeight;
    private float titleX, titleY, titleWidth, titleHeight;

    public MainMenuScreen(GeometryDash game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        background = new Texture(Gdx.files.internal("background.png"));
        playButton = new Texture(Gdx.files.internal("play.png"));
        settingButton = new Texture(Gdx.files.internal("setting.png"));
        leaderboardButton = new Texture(Gdx.files.internal("leaderboard.png"));
        titleImage = new Texture(Gdx.files.internal("Title.png"));

        // Play music
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("mainMenu.mp3"));
        menuMusic.setLooping(true);
        menuMusic.setVolume(0.8f); //set volume
        menuMusic.play();

        // Positioning
        playButtonWidth = 400;
        playButtonHeight = 400;
        playButtonX = (Gdx.graphics.getWidth() - playButtonWidth) / 2f;
        playButtonY = (Gdx.graphics.getHeight() - playButtonHeight) / 2f;

        settingButtonWidth = 350;
        settingButtonHeight = 350;
        settingButtonX = playButtonX + playButtonWidth + 20;
        settingButtonY = playButtonY + (playButtonHeight - settingButtonHeight) / 2f;

        leaderboardButtonWidth = 300;
        leaderboardButtonHeight = 300;
        leaderboardButtonX = playButtonX - leaderboardButtonWidth - 20;
        leaderboardButtonY = playButtonY + (playButtonHeight - leaderboardButtonHeight) / 2f;

        titleWidth = 1500;
        titleHeight = 300;
        titleX = (Gdx.graphics.getWidth() - titleWidth) / 2f;
        titleY = playButtonY + playButtonHeight + 40;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(titleImage, titleX, titleY, titleWidth, titleHeight);
        batch.draw(playButton, playButtonX, playButtonY, playButtonWidth, playButtonHeight);
        batch.draw(settingButton, settingButtonX, settingButtonY, settingButtonWidth, settingButtonHeight);
        batch.draw(leaderboardButton, leaderboardButtonX, leaderboardButtonY, leaderboardButtonWidth, leaderboardButtonHeight);
        batch.end();

        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (x >= playButtonX && x <= playButtonX + playButtonWidth &&
                y >= playButtonY && y <= playButtonY + playButtonHeight) {
                // Stop music and change screen
                if (menuMusic != null && menuMusic.isPlaying()) {
                    menuMusic.stop();
                }
                Gdx.app.postRunnable(() -> {
                    game.setScreen(new LevelSelectionScreen(game));
                    dispose();
                });
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        playButton.dispose();
        settingButton.dispose();
        leaderboardButton.dispose();
        titleImage.dispose();
        if (menuMusic != null) menuMusic.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
