package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.grp19.geometrydash.GeometryDash;
import com.grp19.geometrydash.actors.Player;

public class GameScreen implements Screen {
    private final GeometryDash game;
    private final int level;

    private SpriteBatch batch;
    private Texture background;
    private Texture ground;
    private Texture obstacle;
    private Player player;

    private float backgroundX = 0f;
    private float groundX = 0f;
    private float obstacleX;

    public GameScreen(GeometryDash game, int level) {
        this.game = game;
        this.level = level;
    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen", "Showing GameScreen for level " + level);
        batch = new SpriteBatch();

        try {
            background = new Texture(Gdx.files.internal("gameBackground.png"));
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Missing background, using fallback", e);
            background = new Texture(Gdx.files.internal("background.png"));
        }

        ground = new Texture(Gdx.files.internal("ground.png"));
        obstacle = new Texture(Gdx.files.internal("obstacle.png"));
        obstacleX = Gdx.graphics.getWidth(); // Start obstacle off-screen

        player = new Player();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
            game.setScreen(new GameOverScreen(game, level));
            dispose();
            return;
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

        // Draw level text
        game.font.draw(batch, "Level " + level, 50, Gdx.graphics.getHeight() - 50);

        // Draw player
        player.render(batch);

        batch.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        ground.dispose();
        obstacle.dispose();
        player.dispose();
    }
}

// Pause menu thingy
// Load skin if not already
Skin skin = new Skin(Gdx.files.internal("uiskin.json")); // or your custom skin
this.skin = skin; // if you're storing it

// Create pause button
TextButton pauseButton = new TextButton("II", skin); // "II" for pause symbol
pauseButton.setPosition(Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 60);
pauseButton.setSize(80, 50);

pauseButton.addListener(new ClickListener() {
    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (!isPaused) {
            togglePause();
        }
    }
});

// Add to stage
stage.addActor(pauseButton);
