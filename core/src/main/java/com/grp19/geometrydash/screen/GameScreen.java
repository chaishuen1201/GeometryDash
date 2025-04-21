package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.grp19.geometrydash.GeometryDash;
import com.grp19.geometrydash.actors.Player;
import com.grp19.geometrydash.AudioManager;

public class GameScreen implements Screen {
    private final GeometryDash game;
    private final int level;

    private static final float BACKGROUND_SPEED = 100f;
    private static final float GROUND_SPEED = 400f;
    private static final float OBSTACLE_SPEED = 480f;
    private static final float GAME_SPEED_MULTIPLIER = 1.8f; // Reduced from 2.2f for better playability

    private SpriteBatch batch;
    private Texture background, ground, obstacle, upperObstacle, smallBlock;
    private Player player;
    private Music music;

    private float backgroundX = 0f, groundX = 0f;
    private Array<Rectangle> obstacles = new Array<>();
    private Array<Rectangle> upperObstacles = new Array<>();
    private Array<Rectangle> smallBlocks = new Array<>();

    private float gameTime = 0f;
    private boolean gameOverTriggered = false;
    private int nextEventIndex = 0;

    // Changed from 198f to 35f seconds
    private static final float LEVEL_DURATION = 35f;
    private static final float[][] levelEvents = generateLevelEvents();
    private Texture pauseButtonTexture;
    private Rectangle pauseButtonBounds;
    private boolean isPaused = false;

    public int getLevel() {
        return level;
    }

    public GameScreen(GeometryDash game, int level) {
        this.game = game;
        this.level = level;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("background.png"));
        ground = new Texture(Gdx.files.internal("ground.png"));
        obstacle = new Texture(Gdx.files.internal("obstacle.png"));
        upperObstacle = new Texture(Gdx.files.internal("upperObstacle.png"));
        smallBlock = new Texture(Gdx.files.internal("smallBlock.png"));
        player = new Player();
        pauseButtonTexture = new Texture(Gdx.files.internal("pause.png"));
        pauseButtonBounds = new Rectangle(Gdx.graphics.getWidth() - pauseButtonTexture.getWidth() - 10,
            Gdx.graphics.getHeight() - pauseButtonTexture.getHeight() - 10,
            pauseButtonTexture.getWidth(),
            pauseButtonTexture.getHeight());

        // Load the music
        music = Gdx.audio.newMusic(Gdx.files.internal("level1.mp3"));
        music.setLooping(false);

        // Use AudioManager to play the music with proper volume
        AudioManager.getInstance().playMusic(music);
    }

    @Override
    public void render(float delta) {
        if (isPaused) {
            game.setScreen(new PauseMenu(game, this));
            return;
        }

        if (gameOverTriggered) return;
        delta *= GAME_SPEED_MULTIPLIER;
        gameTime += delta;

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        backgroundX -= BACKGROUND_SPEED * delta;
        if (backgroundX <= -background.getWidth()) backgroundX = 0;
        groundX -= GROUND_SPEED * delta;
        if (groundX <= -ground.getWidth()) groundX = 0;

        while (nextEventIndex < levelEvents.length && levelEvents[nextEventIndex][0] <= gameTime) {
            int type = (int) levelEvents[nextEventIndex][1];
            switch (type) {
                case 0: spawnLowerObstacle(); break;
                case 1: spawnUpperObstacle(); break;
                case 2: spawnLowerObstacle(); spawnUpperObstacle(); break;
                case 3: spawnSmallBlockCluster(); break;
                case 4: spawnSmallBlockStep(); break;
                case 5: spawnObstacleGap(); break;
            }
            nextEventIndex++;
        }

        updateObstacles(delta);
        player.update(delta);
        checkCollisions();

        batch.begin();
        drawBackground();
        drawGround();
        drawObstacles();
        player.render(batch);
        game.font.draw(batch, "Level " + level, 50, Gdx.graphics.getHeight() - 50);
        batch.draw(pauseButtonTexture, pauseButtonBounds.x, pauseButtonBounds.y);
        batch.end();

        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
            System.out.println("Touch coordinates: " + touchX + ", " + touchY);
            if (pauseButtonBounds.contains(touchX, touchY)) {
                isPaused = !isPaused;
                if (isPaused) {
                    AudioManager.getInstance().pauseMusic();
                } else {
                    AudioManager.getInstance().resumeMusic();
                }
            }
        }
    }

    private void updateObstacles(float delta) {
        for (int i = obstacles.size - 1; i >= 0; i--) {
            Rectangle obs = obstacles.get(i);
            obs.x -= OBSTACLE_SPEED * delta;
            if (obs.x + obs.width < 0) obstacles.removeIndex(i);
        }
        for (int i = upperObstacles.size - 1; i >= 0; i--) {
            Rectangle obs = upperObstacles.get(i);
            obs.x -= OBSTACLE_SPEED * delta;
            if (obs.x + obs.width < 0) upperObstacles.removeIndex(i);
        }
        for (int i = smallBlocks.size - 1; i >= 0; i--) {
            Rectangle block = smallBlocks.get(i);
            block.x -= OBSTACLE_SPEED * delta;
            if (block.x + block.width < 0) smallBlocks.removeIndex(i);
        }
    }

    private void checkCollisions() {
        Rectangle playerRect = player.getBounds();
        for (Rectangle obs : obstacles)
            if (playerRect.overlaps(obs)) {
                triggerGameOver();
                return;
            }
        for (Rectangle obs : upperObstacles)
            if (playerRect.overlaps(obs)) {
                triggerGameOver();
                return;
            }

        boolean isOnPlatform = false;
        for (Rectangle block : smallBlocks) {
            if (playerRect.overlaps(block)) {
                if (playerRect.y > block.y + block.height / 2f) {
                    player.landOnPlatform(block.y + block.height);
                    isOnPlatform = true;
                    break;
                } else {
                    triggerGameOver();
                    return;
                }
            }
        }
        if (!isOnPlatform && player.isOnPlatform()) player.fallOffPlatform();
    }

    private void drawBackground() {
        batch.draw(background, backgroundX, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background, backgroundX + background.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void drawGround() {
        int screenWidth = Gdx.graphics.getWidth();
        int groundWidth = ground.getWidth();
        for (int x = 0; x < screenWidth + groundWidth; x += groundWidth) {
            batch.draw(ground, groundX + x, 0, groundWidth, ground.getHeight());
        }
    }

    private void drawObstacles() {
        for (Rectangle obs : obstacles)
            batch.draw(obstacle, obs.x, obs.y, obs.width, obs.height);
        for (Rectangle obs : upperObstacles)
            batch.draw(upperObstacle, obs.x, obs.y, obs.width, obs.height);
        for (Rectangle block : smallBlocks)
            batch.draw(smallBlock, block.x, block.y, block.width, block.height);
    }

    private void spawnLowerObstacle() {
        float x = Gdx.graphics.getWidth();
        float y = ground.getHeight();
        obstacles.add(new Rectangle(x, y, obstacle.getWidth(), obstacle.getHeight()));
    }

    private void spawnUpperObstacle() {
        float x = Gdx.graphics.getWidth();
        float y = Gdx.graphics.getHeight() - upperObstacle.getHeight();
        upperObstacles.add(new Rectangle(x, y, upperObstacle.getWidth(), upperObstacle.getHeight()));
    }

    private void spawnSmallBlockCluster() {
        float blockWidth = smallBlock.getWidth();
        float blockHeight = smallBlock.getHeight();
        float y = ground.getHeight();
        float startX = Gdx.graphics.getWidth();

        // Create a platform of 3-5 blocks
        int blockCount = 3 + (int)(Math.random() * 3);
        for (int i = 0; i < blockCount; i++) {
            smallBlocks.add(new Rectangle(startX + i * blockWidth, y, blockWidth, blockHeight));
        }
    }

    private void spawnSmallBlockStep() {
        float blockWidth = smallBlock.getWidth();
        float blockHeight = smallBlock.getHeight();
        float baseY = ground.getHeight();
        float startX = Gdx.graphics.getWidth();

        // Create a small staircase of blocks (1-2-3 blocks in height)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j <= i; j++) {
                float blockX = startX + i * blockWidth;
                float blockY = baseY + j * blockHeight;
                smallBlocks.add(new Rectangle(blockX, blockY, blockWidth, blockHeight));
            }
        }
    }

    private void spawnObstacleGap() {
        float x = Gdx.graphics.getWidth();
        float y = ground.getHeight();

        // Spawn a lower obstacle followed by an upper obstacle with a gap between them
        obstacles.add(new Rectangle(x, y, obstacle.getWidth(), obstacle.getHeight()));

        // Increased space for player to jump through
        float gapWidth = obstacle.getWidth() * 2.5f; // Increased from 1.5f for better playability

        upperObstacles.add(new Rectangle(x + obstacle.getWidth() + gapWidth,
            Gdx.graphics.getHeight() - upperObstacle.getHeight(),
            upperObstacle.getWidth(), upperObstacle.getHeight()));
    }

    private void triggerGameOver() {
        gameOverTriggered = true;
        AudioManager.getInstance().stopMusic();
        Gdx.app.postRunnable(() -> {
            game.setScreen(new GameOverScreen(game, level));
            dispose();
        });
    }

    private static float[][] generateLevelEvents() {
        Array<float[]> events = new Array<>();

        // Define patterns for a more logical level design
        // Each section is roughly 7 seconds of gameplay

        // Section 1: Introduction - simple obstacles (0-7s)
        addEventSection(events, 0f, 7f, new int[]{0}, 2.0f); // Increased from 1.4f

        // Section 2: Upper obstacles introduction (7-14s)
        addEventSection(events, 7f, 14f, new int[]{1, 0, 1}, 1.8f); // Increased from 1.2f

        // Section 3: Mixed obstacles (14-21s)
        addEventSection(events, 14f, 21f, new int[]{0, 1, 2, 0}, 1.6f); // Increased from 1.0f

        // Section 4: Platform section (21-28s)
        addEventSection(events, 21f, 28f, new int[]{3, 3, 4, 3}, 1.7f); // Increased from 1.2f

        // Section 5: Final challenge - intense section (28-35s)
        addEventSection(events, 28f, 35f, new int[]{5, 2, 4, 5, 2}, 1.4f); // Increased from 0.8f

        // Convert to array
        float[][] result = new float[events.size][2];
        for (int i = 0; i < events.size; i++) {
            result[i][0] = events.get(i)[0];
            result[i][1] = events.get(i)[1];
        }
        return result;
    }

    // Helper method to add events for a section of the level
    private static void addEventSection(Array<float[]> events, float startTime, float endTime,
                                        int[] patternTypes, float baseInterval) {
        float time = startTime;
        int patternIndex = 0;

        while (time < endTime) {
            int pattern = patternTypes[patternIndex % patternTypes.length];
            events.add(new float[]{time, pattern});

            // Vary interval slightly for more natural timing, but only add positive variation
            // to prevent obstacles from being too close together
            float variation = (float)(Math.random() * 0.3f); // Only positive variation
            time += baseInterval + variation;
            patternIndex++;
        }
    }

    @Override public void dispose() {
        batch.dispose();
        background.dispose();
        ground.dispose();
        obstacle.dispose();
        upperObstacle.dispose();
        smallBlock.dispose();
        pauseButtonTexture.dispose();
        player.dispose();
        // Don't dispose music here since AudioManager manages it
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
