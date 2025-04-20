package com.grp19.geometrydash.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Obstacle {
    protected Vector2 position;
    protected Texture texture;
    protected Rectangle bounds;

    public Obstacle(Texture texture, float x, float y) {
        this.texture = texture;
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public void update(float delta) {
        // Movement logic if any (optional)
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean collides(Rectangle playerBounds) {
        return bounds.overlaps(playerBounds);
    }

    public void dispose() {
        texture.dispose();
    }
}
