package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.grp19.geometrydash.GeometryDash;
import com.grp19.geometrydash.AudioManager;

public class MainMenuScreen implements Screen {
    private final GeometryDash game;
    private SpriteBatch batch;
    private Texture background;
    private Texture playButton;
    private Texture settingButton;
    private Texture leaderboardButton;
    private Texture titleImage;

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

        // Music - using AudioManager
        AudioManager am = AudioManager.getInstance();
        am.playMusic(Gdx.audio.newMusic(Gdx.files.internal("mainMenu.mp3")));

        // Set element sizes
        titleWidth = 1200;
        titleHeight = 250;

        playButtonWidth = 300;
        playButtonHeight = 300;

        settingButtonWidth = 250;
        settingButtonHeight = 250;

        leaderboardButtonWidth = 250;
        leaderboardButtonHeight = 250;

        float spacing = 60;
        float totalButtonWidth = leaderboardButtonWidth + spacing + playButtonWidth + spacing + settingButtonWidth;
        float totalButtonHeight = playButtonHeight;

        float totalLayoutHeight = titleHeight + 80 + totalButtonHeight;

        float centerY = (Gdx.graphics.getHeight() - totalLayoutHeight) / 2f;
        float centerX = (Gdx.graphics.getWidth() - totalButtonWidth) / 2f;

        // Position title centered at top of layout
        titleX = (Gdx.graphics.getWidth() - titleWidth) / 2f;
        titleY = centerY + totalButtonHeight + 80;

        // Position buttons below title
        leaderboardButtonX = centerX;
        leaderboardButtonY = centerY + (totalButtonHeight - leaderboardButtonHeight) / 2f;

        playButtonX = leaderboardButtonX + leaderboardButtonWidth + spacing;
        playButtonY = centerY;

        settingButtonX = playButtonX + playButtonWidth + spacing;
        settingButtonY = centerY + (totalButtonHeight - settingButtonHeight) / 2f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(titleImage, titleX, titleY, titleWidth, titleHeight);
        batch.draw(leaderboardButton, leaderboardButtonX, leaderboardButtonY, leaderboardButtonWidth, leaderboardButtonHeight);
        batch.draw(playButton, playButtonX, playButtonY, playButtonWidth, playButtonHeight);
        batch.draw(settingButton, settingButtonX, settingButtonY, settingButtonWidth, settingButtonHeight);
        batch.end();

        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Handle Play button click
            if (x >= playButtonX && x <= playButtonX + playButtonWidth &&
                y >= playButtonY && y <= playButtonY + playButtonHeight) {
                AudioManager.getInstance().stopMusic();
                Gdx.app.postRunnable(() -> {
                    game.setScreen(new LevelSelectionScreen(game));
                    dispose();
                });
            }

            // Handle Settings button click
            else if (x >= settingButtonX && x <= settingButtonX + settingButtonWidth &&
                y >= settingButtonY && y <= settingButtonY + settingButtonHeight) {
                Gdx.app.postRunnable(() -> {
                    game.setScreen(new SettingsScreen(game));
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
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
