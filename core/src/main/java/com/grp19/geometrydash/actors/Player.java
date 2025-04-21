package com.grp19.geometrydash.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    private Texture texture;
    private Rectangle bounds;

    private float yVelocity = 0;
    private final float GRAVITY = -600f;
    private final float JUMP_FORCE = 350f;
    private final int MAX_JUMPS = 2;

    private int jumpCount = 0;
    private float jumpCooldown = 0.15f;
    private float timeSinceLastJump = 0f;

    private boolean isJumping = false;
    private boolean onPlatform = false;

    private final float groundY = 235f;

    public Player() {
        texture = new Texture(Gdx.files.internal("player.png"));
        bounds = new Rectangle(100, groundY, texture.getWidth(), texture.getHeight());
    }

    public void update(float delta) {
        yVelocity += GRAVITY * delta;
        bounds.y += yVelocity * delta;

        // If below ground
        if (bounds.y < groundY) {
            bounds.y = groundY;
            yVelocity = 0;
            jumpCount = 0;
            isJumping = false;
            onPlatform = false;
        }

        timeSinceLastJump += delta;

        // Jump input
        if ((Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.SPACE))
            && jumpCount < MAX_JUMPS
            && timeSinceLastJump >= jumpCooldown) {
            yVelocity = JUMP_FORCE;
            jumpCount++;
            timeSinceLastJump = 0;
            isJumping = true;
            onPlatform = false;
        }
    }

    public void landOnPlatform(float platformTopY) {
        if (bounds.y > platformTopY - 20 && yVelocity <= 0) {
            bounds.y = platformTopY;
            yVelocity = 0;
            jumpCount = 0;
            isJumping = false;
            onPlatform = true;
        }
    }

    public void fallOffPlatform() {
        onPlatform = false;
    }

    public boolean isOnPlatform() {
        return onPlatform;
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
