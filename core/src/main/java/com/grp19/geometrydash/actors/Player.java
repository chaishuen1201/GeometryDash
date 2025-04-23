package com.grp19.geometrydash.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;

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

    // Rotation properties
    private float rotation = 0f;
    private final float ROTATION_SPEED = 180f; // Reduced from 360f to 180f (half rotation per second)
    private final float FALL_ROTATION_SPEED = 90f; // Reduced from 180f to 90f (quarter rotation per second)

    public Player() {
        texture = new Texture(Gdx.files.internal("player.png"));
        bounds = new Rectangle(100, groundY, texture.getWidth(), texture.getHeight());
    }

    public void update(float delta) {
        yVelocity += GRAVITY * delta;
        bounds.y += yVelocity * delta;

        // Update rotation based on vertical movement
        if (yVelocity > 0) {
            // Spinning faster when going up
            rotation += ROTATION_SPEED * delta;
        } else if (yVelocity < 0) {
            // Spinning slower when falling
            rotation += FALL_ROTATION_SPEED * delta;
        }

        // If below ground
        if (bounds.y < groundY) {
            bounds.y = groundY;
            yVelocity = 0;
            jumpCount = 0;
            isJumping = false;
            onPlatform = false;
            inJumpGracePeriod = false;
            rotation = 0f; // Reset rotation when landing
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
            rotation = 0f; // Reset rotation when landing on platform
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
        // Save the current transformation matrix
        batch.end();
        batch.begin();
        
        // Calculate the center of the player for rotation
        float centerX = bounds.x + bounds.width / 2;
        float centerY = bounds.y + bounds.height / 2;
        
        // Draw the player with rotation
        batch.draw(texture,
            bounds.x, bounds.y,
            bounds.width / 2, bounds.height / 2, // Origin for rotation
            bounds.width, bounds.height,
            1f, 1f,
            rotation,
            0, 0,
            texture.getWidth(), texture.getHeight(),
            false, false);
    }

    public void dispose() {
        texture.dispose();
    }
}
