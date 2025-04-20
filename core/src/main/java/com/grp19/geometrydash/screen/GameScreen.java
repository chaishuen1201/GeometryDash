package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.grp19.geometrydash.AudioManager;
import com.grp19.geometrydash.GeometryDash;
import com.grp19.geometrydash.actors.Player;

public class GameScreen implements Screen {
    private final GeometryDash game;
    private final int level;
    private Music levelMusic;
    private SpriteBatch batch;
    private Texture background, ground, obstacle, pauseButton;
    private Player player;
    private float backgroundX, groundX, obstacleX;
    private float pauseButtonX, pauseButtonY, pauseButtonWidth, pauseButtonHeight;
    private final int NUM_LEVELS = 5;
    private boolean isPaused = false;

    public GameScreen(GeometryDash game, int level) {
        this.game = game;
        this.level = level;
    }

    public int getLevel()
    {
        return level;
    }

    public void setPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    @Override
    public void show() {
        // Play background music
        levelMusic = Gdx.audio.newMusic(Gdx.files.internal("musics/level1.mp3"));
        AudioManager.getInstance().playMusic(levelMusic);

        batch = new SpriteBatch();

        background = new Texture(Gdx.files.internal("background.png"));
        ground = new Texture(Gdx.files.internal("ground.png"));
        obstacle = new Texture(Gdx.files.internal("obstacle.png"));
        pauseButton = new Texture(Gdx.files.internal("pause.png"));

        player = new Player();

        obstacleX = Gdx.graphics.getWidth();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isPaused) {
            // Update positions for parallax effect
            backgroundX -= 20 * delta;
            if (backgroundX <= -background.getWidth()) {
                backgroundX = 0;
            }
            groundX -= 150 * delta;
            if (groundX <= -ground.getWidth()) {
                groundX = 0;
            }
            obstacleX -= 200 * delta;
            if (obstacleX < -obstacle.getWidth()) {
                obstacleX = Gdx.graphics.getWidth() + 200;
            }
            player.update(delta);

            // Collision detection
            Rectangle playerRect = player.getBounds();
            Rectangle obstacleRect = new Rectangle(obstacleX, ground.getHeight(), obstacle.getWidth(), obstacle.getHeight());
            if (playerRect.overlaps(obstacleRect)) {
                AudioManager.getInstance().stopMusic();
                game.setScreen(new GameOverScreen(game, level));
                dispose();
                return;
            }
        }

        batch.begin();

        // Draw background
        batch.draw(background, backgroundX, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background, backgroundX + background.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw ground
        int screenWidth = Gdx.graphics.getWidth();
        int groundWidth = ground.getWidth();
        for (int x = 0; x < screenWidth + groundWidth; x += groundWidth) {
            batch.draw(ground, groundX + x, 0, groundWidth, ground.getHeight());
        }

        // Draw obstacle
        batch.draw(obstacle, obstacleX, ground.getHeight());

        // Draw pause button
        pauseButtonWidth = 200;
        pauseButtonHeight = 200;
        pauseButtonX = Gdx.graphics.getWidth() - pauseButtonWidth - 60;
        pauseButtonY = Gdx.graphics.getHeight() - pauseButtonHeight - 30;;
        batch.draw(pauseButton, pauseButtonX, pauseButtonY, pauseButtonWidth, pauseButtonHeight);

        // Draw level text
        game.font.draw(batch, "Level " + level, 50, Gdx.graphics.getHeight() - 50);

        // Draw player
        player.render(batch);

        batch.end();

        // Handle touch input
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Pause button
            if (touchX >= pauseButtonX && touchX <= pauseButtonX + pauseButtonWidth &&
                touchY >= pauseButtonY && touchY <= pauseButtonY + pauseButtonHeight) {
                AudioManager.getInstance().pauseMusic();
                Gdx.app.postRunnable(() -> {
                    isPaused = true;
                    game.setScreen(new PauseMenu(game, this)); // Pass the current GameScreen to PauseMenu
                });

            }
        }
    }

    @Override
    public void dispose() {
        if (!isPaused) {
            batch.dispose();
            background.dispose();
            ground.dispose();
            obstacle.dispose();
            pauseButton.dispose();
            player.dispose();
            if (levelMusic != null) levelMusic.dispose();
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
