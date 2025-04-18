//Player.java
package com.grp19.geometrydash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {
    private Texture texture;
    private float x, y, velocityY;
    private final float GRAVITY = -20;
    private final float JUMP_VELOCITY = 600;

    public Player() {
        texture = new Texture("player.png");
        x = 100;
        y = 300;
    }

    public void update(float delta) {
        if (Gdx.input.justTouched()) velocityY = JUMP_VELOCITY;
        velocityY += GRAVITY;
        y += velocityY * delta;
        if (y < 0) y = 0;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public boolean collides() {
        return y <= 0;
    }
}
