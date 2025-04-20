package com.grp19.geometrydash.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    private Texture texture;
    private Rectangle bounds;
    private float yVelocity = 0;
    private final float GRAVITY = -600f;
    private final float JUMP_FORCE = 350f;
    private final int MAX_JUMPS = 3;

    private int jumpCount = 0;
    private float jumpCooldown = 0.15f;
    private float timeSinceLastJump = 0f;

    public Player() {
        texture = new Texture(Gdx.files.internal("player.png"));
        bounds = new Rectangle(100, 235, texture.getWidth(), texture.getHeight());
    }

    public void update(float delta) {
        // Gravity
        yVelocity += GRAVITY * delta;
        bounds.y += yVelocity * delta;

        // Ground collision
        if (bounds.y < 235) {
            bounds.y = 235;
            yVelocity = 0;
            jumpCount = 0;
        }

        timeSinceLastJump += delta;

        // Jump input (hold or tap) with cooldown
        if (Gdx.input.isTouched() && jumpCount < MAX_JUMPS && timeSinceLastJump >= jumpCooldown) {
            yVelocity = JUMP_FORCE;
            jumpCount++;
            timeSinceLastJump = 0;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }
}
