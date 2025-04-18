//GeometryDash.java
package com.grp19.geometrydash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.grp19.geometrydash.screen.MainMenuScreen;
import com.grp19.geometrydash.util.PreferencesManager;
import com.badlogic.gdx.Gdx;




public class GeometryDash extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public PreferencesManager prefs;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // Uses Arial default font
        font.getData().setScale(2);
        prefs = new PreferencesManager();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}


