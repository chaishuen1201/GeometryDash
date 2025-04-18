package com.grp19.geometrydash;

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
    private boolean isGrounded = true;

    public Player() {
        texture = new Texture(Gdx.files.internal("player.png"));
        bounds = new Rectangle(100, 100, texture.getWidth(), texture.getHeight());
    }

    public void update(float delta) {
        // Apply gravity
        yVelocity += GRAVITY * delta;
        bounds.y += yVelocity * delta;

        // Ground collision
        if (bounds.y < 100) {
            bounds.y = 100;
            yVelocity = 0;
            isGrounded = true;
        } else {
            isGrounded = false;
        }

        // Jump input
        if (Gdx.input.justTouched() && isGrounded) {
            yVelocity = JUMP_FORCE;
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
