package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.grp19.geometrydash.GeometryDash;
import com.grp19.geometrydash.actors.Player;
import com.grp19.geometrydash.AudioManager;

public class GameScreen implements Screen {
    private final GeometryDash game;
    private final int level;

    private static final float BASE_BACKGROUND_SPEED = 100f;
    private static final float BASE_GROUND_SPEED = 400f;
    private static final float BASE_OBSTACLE_SPEED = 480f;
    private static final float BASE_GAME_SPEED_MULTIPLIER = 1.8f;
    private static final float BASE_LEVEL_DURATION = 35f;

    // The actual speeds used during gameplay, modified by level
    private float backgroundSpeed;
    private float groundSpeed;
    private float obstacleSpeed;
    private float gameSpeedMultiplier;
    private float levelDuration;

    // Safe zone tracking
    private float lastUpperObstacleEnd = -1000f;
    private float lastBlockEnd = -1000f;
    private static final float MIN_SAFE_ZONE = 200f; // Minimum space between obstacles

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer; // For drawing the progress bar
    private Texture background, ground, obstacle, upperObstacle, smallBlock, finishLine;
    private Player player;
    private Music music;
    private Sound deathSound;

    // Progress Bar Properties
    private static final int PROGRESS_BAR_HEIGHT = 8;
    private static final int PROGRESS_BAR_Y = Gdx.graphics.getHeight() - PROGRESS_BAR_HEIGHT - 10;
    private static final int PROGRESS_BAR_MARGIN = 20;
    private static final Color PROGRESS_BAR_BG_COLOR = new Color(0.2f, 0.2f, 0.2f, 0.7f);
    private static final Color PROGRESS_BAR_FG_COLOR = new Color(0.2f, 0.8f, 1.0f, 1.0f);

    private float backgroundX = 0f, groundX = 0f;
    private Array<Rectangle> obstacles = new Array<>();
    private Array<Rectangle> upperObstacles = new Array<>();
    private Array<Rectangle> smallBlocks = new Array<>();
    private Rectangle finishLineBounds;

    private float gameTime = 0f;
    private boolean gameOverTriggered = false;
    private boolean levelCompleted = false;
    private int nextEventIndex = 0;
    private int attemptCount = 0; // Track attempts for this level

    private float[][] levelEvents;
    private Texture pauseButtonTexture;
    private Rectangle pauseButtonBounds;
    private boolean isPaused = false;

    // Player metrics for obstacle placement decisions
    private static final float PLAYER_JUMP_HEIGHT = 200f; // Approximate max jump height
    private static final float PLAYER_JUMP_DISTANCE = 300f; // Approximate max jump distance

    public int getLevel() {
        return level;
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    public GameScreen(GeometryDash game, int level) {
        this(game, level, 0); // Default to 0 attempts for new level starts
    }

    public GameScreen(GeometryDash game, int level, int initialAttempts) {
        this.game = game;
        this.level = Math.min(level, 5); // Cap at level 5
        this.attemptCount = initialAttempts; // Initialize with the provided attempt count

        // Calculate difficulty based on level
        initializeLevelParameters();
    }

    private void initializeLevelParameters() {
        // Increase speed and difficulty with each level
        float speedIncreaseFactor = 1.0f + (level - 1) * 0.15f;

        backgroundSpeed = BASE_BACKGROUND_SPEED * speedIncreaseFactor;
        groundSpeed = BASE_GROUND_SPEED * speedIncreaseFactor;
        obstacleSpeed = BASE_OBSTACLE_SPEED * speedIncreaseFactor;
        gameSpeedMultiplier = BASE_GAME_SPEED_MULTIPLIER * (1.0f + (level - 1) * 0.1f);

        // Duration increases with level
        levelDuration = BASE_LEVEL_DURATION + (level - 1) * 10f; // Each level is 10 seconds longer

        // Generate level events based on current level's difficulty and duration
        levelEvents = generateLevelEvents(level, levelDuration);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        background = new Texture(Gdx.files.internal("background.png"));
        ground = new Texture(Gdx.files.internal("ground.png"));
        obstacle = new Texture(Gdx.files.internal("obstacle.png"));
        upperObstacle = new Texture(Gdx.files.internal("upperObstacle.png"));
        smallBlock = new Texture(Gdx.files.internal("smallBlock.png"));
        finishLine = new Texture(Gdx.files.internal("finish.png"));
        player = new Player();

        // Place finish line further back after all obstacles
        float finishLineX = (levelDuration * obstacleSpeed) + Gdx.graphics.getWidth() + 500f; // Added 500f extra distance
        finishLineBounds = new Rectangle(
            finishLineX,
            ground.getHeight(),
            finishLine.getWidth(),
            Gdx.graphics.getHeight() - ground.getHeight());

        pauseButtonTexture = new Texture(Gdx.files.internal("pause.png"));
        pauseButtonBounds = new Rectangle(Gdx.graphics.getWidth() - pauseButtonTexture.getWidth() - 10,
            Gdx.graphics.getHeight() - pauseButtonTexture.getHeight() - 10,
            pauseButtonTexture.getWidth(),
            pauseButtonTexture.getHeight());

        // Choose music based on level
        String musicFile = "level" + level + ".mp3";
        music = Gdx.audio.newMusic(Gdx.files.internal(musicFile));
        music.setLooping(false);
        AudioManager.getInstance().playMusic(music);
        
        // Load death sound
        deathSound = Gdx.audio.newSound(Gdx.files.internal("geometry-dash-death-sound-effect.mp3"));
    }

    @Override
    public void render(float delta) {
        if (isPaused) {
            game.setScreen(new PauseMenu(game, this));
            return;
        }

        if (gameOverTriggered || levelCompleted) return;

        delta *= gameSpeedMultiplier;
        gameTime += delta;

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update background and ground positions
        backgroundX -= backgroundSpeed * delta;
        if (backgroundX <= -background.getWidth()) backgroundX = 0;
        groundX -= groundSpeed * delta;
        if (groundX <= -ground.getWidth()) groundX = 0;

        // Spawn level events
        while (nextEventIndex < levelEvents.length && levelEvents[nextEventIndex][0] <= gameTime) {
            int type = (int) levelEvents[nextEventIndex][1];
            switch (type) {
                case 0: spawnLowerObstacle(); break;
                case 1: spawnUpperObstacle(); break;
                case 2: spawnLowerAndUpperObstacle(); break;
                case 3: spawnSmallBlockCluster(); break;
                case 4: spawnSmallBlockStep(); break;
                case 5: spawnObstacleGap(); break;
                case 6: spawnTripleObstacle(); break;
                case 7: spawnZigZagPattern(); break;
            }
            nextEventIndex++;
        }

        updateObstacles(delta);
        player.update(delta);
        checkCollisions();

        // Draw everything with SpriteBatch
        batch.begin();
        drawBackground();
        drawGround();
        drawObstacles();
        drawFinishLine();
        player.render(batch);
        game.font.draw(batch, "Level " + level, 50, Gdx.graphics.getHeight() - 50);
        batch.draw(pauseButtonTexture, pauseButtonBounds.x, pauseButtonBounds.y);
        batch.end();

        // Draw progress bar with ShapeRenderer
        drawProgressBar();

        // Handle pause button
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (pauseButtonBounds.contains(touchX, touchY)) {
                isPaused = true;
                AudioManager.getInstance().pauseMusic();
            }
        }
    }

    private void drawProgressBar() {
        int screenWidth = Gdx.graphics.getWidth();
        int barWidth = screenWidth - (PROGRESS_BAR_MARGIN * 2);
        float progress = Math.min(gameTime / levelDuration, 1.0f);
        int progressWidth = (int)(barWidth * progress);

        // Enable transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw background of progress bar
        shapeRenderer.setColor(PROGRESS_BAR_BG_COLOR);
        shapeRenderer.rect(PROGRESS_BAR_MARGIN, PROGRESS_BAR_Y, barWidth, PROGRESS_BAR_HEIGHT);

        // Draw filled portion of progress bar
        shapeRenderer.setColor(PROGRESS_BAR_FG_COLOR);
        shapeRenderer.rect(PROGRESS_BAR_MARGIN, PROGRESS_BAR_Y, progressWidth, PROGRESS_BAR_HEIGHT);

        shapeRenderer.end();

        // Disable blending
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void updateObstacles(float delta) {
        // Update regular obstacles
        for (int i = obstacles.size - 1; i >= 0; i--) {
            Rectangle obs = obstacles.get(i);
            obs.x -= obstacleSpeed * delta;
            if (obs.x + obs.width < 0) obstacles.removeIndex(i);
        }

        // Update upper obstacles
        for (int i = upperObstacles.size - 1; i >= 0; i--) {
            Rectangle obs = upperObstacles.get(i);
            obs.x -= obstacleSpeed * delta;
            if (obs.x + obs.width < 0) upperObstacles.removeIndex(i);
        }

        // Update small blocks
        for (int i = smallBlocks.size - 1; i >= 0; i--) {
            Rectangle block = smallBlocks.get(i);
            block.x -= obstacleSpeed * delta;
            if (block.x + block.width < 0) smallBlocks.removeIndex(i);
        }

        // Update finish line position
        finishLineBounds.x -= obstacleSpeed * delta;
    }

    private void checkCollisions() {
        Rectangle playerRect = player.getBounds();

        // Check collision with obstacles
        for (Rectangle obs : obstacles) {
            if (playerRect.overlaps(obs)) {
                // Play death sound
                deathSound.play();
                triggerGameOver();
                return;
            }
        }

        // Check collision with upper obstacles
        for (Rectangle obs : upperObstacles) {
            if (playerRect.overlaps(obs)) {
                // Play death sound
                deathSound.play();
                triggerGameOver();
                return;
            }
        }

        // Check collision with finish line
        if (playerRect.overlaps(finishLineBounds) && !levelCompleted) {
            triggerLevelComplete();
            return;
        }

        // Check collision with platforms
        boolean isOnPlatform = false;
        for (Rectangle block : smallBlocks) {
            if (playerRect.overlaps(block)) {
                if (playerRect.y > block.y + block.height / 2f) {
                    player.landOnPlatform(block.y + block.height);
                    isOnPlatform = true;
                    break;
                } else {
                    // Play death sound
                    deathSound.play();
                    triggerGameOver();
                    return;
                }
            }
        }

        if (!isOnPlatform && player.isOnPlatform()) {
            player.fallOffPlatform();
        }
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
        for (Rectangle obs : obstacles) {
            batch.draw(obstacle, obs.x, obs.y, obs.width, obs.height);
        }
        for (Rectangle obs : upperObstacles) {
            batch.draw(upperObstacle, obs.x, obs.y, obs.width, obs.height);
        }
        for (Rectangle block : smallBlocks) {
            batch.draw(smallBlock, block.x, block.y, block.width, block.height);
        }
    }

    private void drawFinishLine() {
        if (finishLineBounds.x + finishLineBounds.width > 0 &&
            finishLineBounds.x < Gdx.graphics.getWidth()) {
            // Draw the regular finish line image
            batch.draw(finishLine, finishLineBounds.x, finishLineBounds.y);

            // Draw a vertical line to indicate the finish
            float lineHeight = Gdx.graphics.getHeight() - ground.getHeight();
            // Use a part of the finish texture to create a vertical line
            batch.draw(finishLine,
                finishLineBounds.x, ground.getHeight(),
                20, // Width of the line
                lineHeight); // Height of the line
        }
    }

    // Helper method to check if a new obstacle would create an impossible situation
    private boolean isPathBlocked(float x, float y, boolean isUpper) {
        float screenRight = Gdx.graphics.getWidth();

        // If this is an upper obstacle, check if there are blocks right before it
        if (isUpper) {
            // Don't place upper obstacles too close after blocks, especially in level 2
            float requiredClearance;
            if (level == 2) {
                requiredClearance = MIN_SAFE_ZONE * 2.7f; // Significantly more space for level 2
            } else if (level < 2) {
                requiredClearance = MIN_SAFE_ZONE * 1.5f; // Keep current spacing for level 1
            } else {
                requiredClearance = MIN_SAFE_ZONE * 1.2f; // Keep current spacing for levels 3+
            }
            return (x - lastBlockEnd < requiredClearance);
        }

        // For lower obstacles, check if there are upper obstacles right after it
        return false; // Lower obstacles don't cause path blocking
    }

    // Basic obstacle spawners with safe zone checks
    private void spawnLowerObstacle() {
        float x = Gdx.graphics.getWidth();
        float y = ground.getHeight();
        obstacles.add(new Rectangle(x, y, obstacle.getWidth(), obstacle.getHeight()));
    }

    private void spawnUpperObstacle() {
        float x = Gdx.graphics.getWidth();
        float y = Gdx.graphics.getHeight() - upperObstacle.getHeight();

        // Check if placing this obstacle would block the player's path
        if (isPathBlocked(x, y, true)) {
            // Skip this obstacle or delay it
            x += MIN_SAFE_ZONE; // Push it further to create a safe zone
        }

        upperObstacles.add(new Rectangle(x, y, upperObstacle.getWidth(), upperObstacle.getHeight()));
        lastUpperObstacleEnd = x + upperObstacle.getWidth();
    }

    private void spawnLowerAndUpperObstacle() {
        // For level 2, make sure they're offset to create a passage
        if (level == 2) {
            float screenWidth = Gdx.graphics.getWidth();

            // First spawn lower obstacle
            float x = screenWidth;
            float y = ground.getHeight();
            obstacles.add(new Rectangle(x, y, obstacle.getWidth(), obstacle.getHeight()));

            // Then spawn upper obstacle with offset
            float upperX = x + obstacle.getWidth() + 200f; // Create generous gap in level 2
            float upperY = Gdx.graphics.getHeight() - upperObstacle.getHeight();
            upperObstacles.add(new Rectangle(upperX, upperY, upperObstacle.getWidth(), upperObstacle.getHeight()));
            lastUpperObstacleEnd = upperX + upperObstacle.getWidth();
        } else {
            // For other levels, can be more challenging
            float gap = 150f + (level - 2) * 20f; // Gap decreases with level
            spawnLowerObstacle();

            // Delay the upper obstacle just a bit
            float upperX = Gdx.graphics.getWidth() + gap;
            float upperY = Gdx.graphics.getHeight() - upperObstacle.getHeight();
            upperObstacles.add(new Rectangle(upperX, upperY, upperObstacle.getWidth(), upperObstacle.getHeight()));
            lastUpperObstacleEnd = upperX + upperObstacle.getWidth();
        }
    }

    private void spawnSmallBlockCluster() {
        float blockWidth = smallBlock.getWidth();
        float blockHeight = smallBlock.getHeight();
        float y = ground.getHeight();
        float startX = Gdx.graphics.getWidth();

        // Check if we need to add extra space after upper obstacles
        if (startX - lastUpperObstacleEnd < MIN_SAFE_ZONE) {
            startX = lastUpperObstacleEnd + MIN_SAFE_ZONE;
        }

        // More blocks for higher levels
        int blockCount = 3 + (int)(Math.random() * (1 + level / 2));
        for (int i = 0; i < blockCount; i++) {
            smallBlocks.add(new Rectangle(startX + i * blockWidth, y, blockWidth, blockHeight));
        }

        lastBlockEnd = startX + blockCount * blockWidth;
    }

    private void spawnSmallBlockStep() {
        float blockWidth = smallBlock.getWidth();
        float blockHeight = smallBlock.getHeight();
        float baseY = ground.getHeight();
        float startX = Gdx.graphics.getWidth();

        // Check if we need to add extra space after upper obstacles
        if (startX - lastUpperObstacleEnd < MIN_SAFE_ZONE) {
            startX = lastUpperObstacleEnd + MIN_SAFE_ZONE;
        }

        // Higher steps for higher levels but capped for level 2
        int maxHeight = level == 2 ? 2 : Math.min(level, 3);
        int stepCount = level == 2 ? 3 : 3 + level/2;

        float lastX = startX;
        for (int i = 0; i < stepCount; i++) {
            int stepHeight = Math.min(i, maxHeight);

            // Space factor determines gap between blocks
            // Make it more generous for level 2
            float spaceFactor = level == 2 ? 1.3f : Math.max(0.8f, 1.2f - (level * 0.05f));

            for (int j = 0; j <= stepHeight; j++) {
                float blockX = startX + i * (blockWidth * spaceFactor);
                float blockY = baseY + j * blockHeight;
                smallBlocks.add(new Rectangle(blockX, blockY, blockWidth, blockHeight));
                lastX = blockX + blockWidth;
            }
        }

        lastBlockEnd = lastX;
    }

    private void spawnObstacleGap() {
        float x = Gdx.graphics.getWidth();
        float y = ground.getHeight();

        obstacles.add(new Rectangle(x, y, obstacle.getWidth(), obstacle.getHeight()));

        // Gap width more generous for level 2
        float gapWidthFactor = level == 2 ? 2.5f : 2.5f - (level * 0.2f);
        if (gapWidthFactor < 1.5f) gapWidthFactor = 1.5f;

        float gapWidth = obstacle.getWidth() * gapWidthFactor;
        float upperX = x + obstacle.getWidth() + gapWidth;

        upperObstacles.add(new Rectangle(upperX,
            Gdx.graphics.getHeight() - upperObstacle.getHeight(),
            upperObstacle.getWidth(), upperObstacle.getHeight()));

        lastUpperObstacleEnd = upperX + upperObstacle.getWidth();
    }

    // New obstacle types for higher levels
    private void spawnTripleObstacle() {
        // Don't use this pattern in level 2 - too difficult
        if (level <= 2) {
            spawnLowerObstacle(); // Fallback to a simpler obstacle
            return;
        }

        float x = Gdx.graphics.getWidth();
        float y = ground.getHeight();

        // Spacing increases in lower levels
        float spacing = obstacle.getWidth() * (2.5f - level * 0.2f);
        if (spacing < obstacle.getWidth() * 1.2f) {
            spacing = obstacle.getWidth() * 1.2f; // Minimum spacing
        }

        // Triple lower obstacles with adjusted spacing
        obstacles.add(new Rectangle(x, y, obstacle.getWidth(), obstacle.getHeight()));
        obstacles.add(new Rectangle(x + spacing, y, obstacle.getWidth(), obstacle.getHeight()));
        obstacles.add(new Rectangle(x + spacing * 2, y, obstacle.getWidth(), obstacle.getHeight()));
    }

    private void spawnZigZagPattern() {
        // For level 2, make a very easy zigzag
        float blockWidth = smallBlock.getWidth();
        float blockHeight = smallBlock.getHeight();
        float baseY = ground.getHeight();
        float startX = Gdx.graphics.getWidth();

        // Check if we need to add extra space after upper obstacles
        if (startX - lastUpperObstacleEnd < MIN_SAFE_ZONE) {
            startX = lastUpperObstacleEnd + MIN_SAFE_ZONE;
        }

        // Create a zigzag pattern that requires precise jumping
        int steps = level == 2 ? 3 : 4 + level/2;
        float spacing = level == 2 ? 1.5f : 1.2f;
        float maxHeight = level == 2 ? 1 : 2;

        float lastX = startX;
        for (int i = 0; i < steps; i++) {
            float height = i % 2 == 0 ? 0 : maxHeight;
            float x = startX + i * blockWidth * spacing;
            float y = baseY + height * blockHeight;
            smallBlocks.add(new Rectangle(x, y, blockWidth, blockHeight));
            lastX = x + blockWidth;
        }

        lastBlockEnd = lastX;
    }

    private void triggerGameOver() {
        gameOverTriggered = true;
        attemptCount++; // Increment attempt count when player fails
        AudioManager.getInstance().stopMusic();
        Gdx.app.postRunnable(() -> {
            game.setScreen(new GameOverScreen(game, level, attemptCount));
            dispose();
        });
    }

    private void triggerLevelComplete() {
        levelCompleted = true;
        attemptCount++; // Increment attempt count for successful completion
        AudioManager.getInstance().stopMusic();
        Gdx.app.postRunnable(() -> {
            game.setScreen(new WinScreen(game, level, attemptCount));
            dispose();
        });
    }

    private static float[][] generateLevelEvents(int level, float duration) {
        Array<float[]> events = new Array<>();

        // Divide the level into 5 sections
        float sectionDuration = duration / 5f;

        // Section 1: Introduction - simple obstacles
        addEventSection(events, 0f, sectionDuration, getSection1Patterns(level),
            getBaseDifficulty(level, 1));

        // Section 2: Upper obstacles introduction
        addEventSection(events, sectionDuration, sectionDuration * 2,
            getSection2Patterns(level), getBaseDifficulty(level, 2));

        // Section 3: Mixed obstacles
        addEventSection(events, sectionDuration * 2, sectionDuration * 3,
            getSection3Patterns(level), getBaseDifficulty(level, 3));

        // Section 4: Platform section
        addEventSection(events, sectionDuration * 3, sectionDuration * 4,
            getSection4Patterns(level), getBaseDifficulty(level, 4));

        // Section 5: Final challenge - intense section
        addEventSection(events, sectionDuration * 4, duration,
            getSection5Patterns(level), getBaseDifficulty(level, 5));

        float[][] result = new float[events.size][2];
        for (int i = 0; i < events.size; i++) {
            result[i][0] = events.get(i)[0];
            result[i][1] = events.get(i)[1];
        }
        return result;
    }

    // Pattern generators for each section, difficulty increases with level
    private static int[] getSection1Patterns(int level) {
        switch (level) {
            case 1: return new int[]{0};
            case 2: return new int[]{0, 0}; // Simplified for level 2
            case 3: return new int[]{0, 0, 3};
            case 4: return new int[]{0, 0, 3, 0};
            default: return new int[]{0, 6, 0, 3};
        }
    }

    private static int[] getSection2Patterns(int level) {
        switch (level) {
            case 1: return new int[]{1, 0, 1};
            case 2: return new int[]{1, 0, 1}; // Reduced complexity for level 2
            case 3: return new int[]{1, 0, 1, 0, 1};
            case 4: return new int[]{1, 0, 2, 1, 0};
            default: return new int[]{1, 2, 1, 6, 1};
        }
    }

    private static int[] getSection3Patterns(int level) {
        switch (level) {
            case 1: return new int[]{0, 1, 2, 0};
            case 2: return new int[]{0, 1, 0, 1}; // Simplified for level 2
            case 3: return new int[]{0, 1, 2, 5, 0};
            case 4: return new int[]{2, 0, 5, 2, 6};
            default: return new int[]{2, 6, 5, 2, 7};
        }
    }

    private static int[] getSection4Patterns(int level) {
        switch (level) {
            case 1: return new int[]{3, 3, 4, 3};
            case 2: return new int[]{3, 4, 3}; // Fewer patterns for level 2
            case 3: return new int[]{3, 4, 7, 3};
            case 4: return new int[]{4, 7, 3, 4, 7};
            default: return new int[]{7, 4, 7, 3, 7};
        }
    }

    private static int[] getSection5Patterns(int level) {
        switch (level) {
            case 1: return new int[]{5, 2, 4, 5, 2};
            case 2: return new int[]{5, 0, 4, 5}; // Easiest final section for level 2
            case 3: return new int[]{5, 6, 4, 5, 7};
            case 4: return new int[]{6, 5, 7, 2, 6};
            default: return new int[]{6, 7, 6, 2, 7, 6};
        }
    }

    // Calculate time between obstacles based on level and section
    private static float getBaseDifficulty(int level, int section) {
        // Level 2 gets special treatment
        if (level == 2) {
            return 2.5f - (section - 1) * 0.1f; // More lenient timing for level 2
        }

        // Base time between obstacles decreases with level but with a minimum threshold
        float baseInterval = 2.5f - (level - 1) * 0.15f;
        
        // Ensure minimum interval doesn't go below 1.5 seconds
        baseInterval = Math.max(baseInterval, 1.5f);

        // Sections get progressively harder but maintain minimum spacing
        baseInterval -= (section - 1) * 0.15f;
        
        // Final safety check for minimum interval
        return Math.max(baseInterval, 1.2f);
    }

    private static void addEventSection(Array<float[]> events, float startTime, float endTime,
                                        int[] patternTypes, float baseInterval) {
        float time = startTime;
        int patternIndex = 0;
        float lastEventTime = -1000f; // Initialize to a large negative value

        while (time < endTime) {
            // Ensure minimum spacing between events
            if (time - lastEventTime < baseInterval * 0.8f) {
                time = lastEventTime + baseInterval * 0.8f;
            }

            int pattern = patternTypes[patternIndex % patternTypes.length];
            events.add(new float[]{time, pattern});

            // More controlled variation in timing
            float maxVariation = Math.max(0.2f, 0.4f - (patternIndex / 10f) * 0.05f);
            float variation = (float)(Math.random() * maxVariation);
            
            lastEventTime = time;
            time += baseInterval + variation;
            patternIndex++;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        background.dispose();
        ground.dispose();
        obstacle.dispose();
        upperObstacle.dispose();
        smallBlock.dispose();
        finishLine.dispose();
        pauseButtonTexture.dispose();
        player.dispose();
        if (deathSound != null) {
            deathSound.dispose();
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
