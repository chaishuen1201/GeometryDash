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
    // Increased gravity to make jumps feel more responsive
    private final float GRAVITY = -800f;
    // Increased jump force to jump higher
    private final float JUMP_FORCE = 500f;
    // Increased max jumps to 2 (double jump)
    private final int MAX_JUMPS = 2;

    private int jumpCount = 0;
    private float jumpCooldown = 0.1f; // Slightly reduced for better responsiveness
    private float timeSinceLastJump = 0f;

    private boolean isJumping = false;
    private boolean onPlatform = false;

    // Added a jump grace period to make platform jumps more forgiving
    private float jumpGracePeriod = 0.1f;
    private float timeSinceLeftPlatform = 0f;
    private boolean inJumpGracePeriod = false;

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
            inJumpGracePeriod = false;
        }

        timeSinceLastJump += delta;

        // Update grace period timer
        if (inJumpGracePeriod) {
            timeSinceLeftPlatform += delta;
            if (timeSinceLeftPlatform > jumpGracePeriod) {
                inJumpGracePeriod = false;
            }
        }

        // Jump input
        if ((Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.SPACE))
            && (jumpCount < MAX_JUMPS || inJumpGracePeriod)
            && timeSinceLastJump >= jumpCooldown) {

            // Allow jump if in grace period (just fell off platform)
            if (inJumpGracePeriod) {
                jumpCount = 1; // Count this as first jump
                inJumpGracePeriod = false;
            }

            yVelocity = JUMP_FORCE;
            jumpCount++;
            timeSinceLastJump = 0;
            isJumping = true;
            onPlatform = false;
        }
    }

    public void landOnPlatform(float platformTopY) {
        // Made landing detection more forgiving
        if (bounds.y > platformTopY - 25 && yVelocity <= 0) {
            bounds.y = platformTopY;
            yVelocity = 0;
            jumpCount = 0;
            isJumping = false;
            onPlatform = true;
            inJumpGracePeriod = false;
        }
    }

    public void fallOffPlatform() {
        onPlatform = false;
        // Start grace period when falling off platform
        inJumpGracePeriod = true;
        timeSinceLeftPlatform = 0f;
    }

    public boolean isOnPlatform() {
        return onPlatform;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y);
    }

    public void dispose() {
        texture.dispose();
    }
}
