package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.grp19.geometrydash.GeometryDash;
import com.grp19.geometrydash.AudioManager;

public class SettingsScreen implements Screen {
    private final GeometryDash game;
    private final SpriteBatch batch;
    private Texture background;
    private Texture backButton;
    private Texture sliderBar;
    private Texture sliderKnob;
    private Texture settingsBorder;

    private Rectangle sliderBarBounds;
    private Rectangle sliderKnobBounds;
    private boolean dragging = false;
    private float volume;

    public SettingsScreen(GeometryDash game) {
        this.game = game;
        this.batch = game.batch;

        // Initialize volume from AudioManager
        this.volume = AudioManager.getInstance().getVolume();
    }

    @Override
    public void show() {
        background = new Texture(Gdx.files.internal("background.png"));
        backButton = new Texture(Gdx.files.internal("back.png"));
        sliderBar = new Texture(Gdx.files.internal("slider_bar.png"));
        sliderKnob = new Texture(Gdx.files.internal("slider_knob.png"));
        settingsBorder = new Texture(Gdx.files.internal("settings_border.png"));

        // Initialize slider position based on current volume
        initSlider();
    }

    private void initSlider() {
        // Reduced width to 40% of screen width
        float barWidth = Gdx.graphics.getWidth() * 0.4f;
        float barHeight = 30;

        // Center the slider horizontally
        float barX = Gdx.graphics.getWidth() / 2 - barWidth / 2;
        // Position slider further down from the volume text
        float barY = Gdx.graphics.getHeight() / 2 - 30; // Added a 30px gap

        sliderBarBounds = new Rectangle(barX, barY, barWidth, barHeight);

        // Initialize knob position based on current volume
        float knobWidth = 50;
        float knobHeight = 50;
        float knobX = barX + (barWidth - knobWidth) * volume;
        float knobY = barY - (knobHeight - barHeight) / 2;

        sliderKnobBounds = new Rectangle(knobX, knobY, knobWidth, knobHeight);
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw back button (top-left corner)
        float backBtnSize = 150;
        batch.draw(backButton, 20, Gdx.graphics.getHeight() - backBtnSize - 20, backBtnSize, backBtnSize);

        // Draw settings title
        game.font.draw(batch, "SETTINGS",
            Gdx.graphics.getWidth()/2 - 100,
            Gdx.graphics.getHeight() - 100);

        // Draw settings border
        float borderPadding = 50;
        float borderWidth = Gdx.graphics.getWidth() - 2 * borderPadding;
        float borderHeight = Gdx.graphics.getHeight() - 2 * borderPadding;
        batch.draw(settingsBorder, borderPadding, borderPadding, borderWidth, borderHeight);

        // Draw volume text with greater separation from slider
        game.font.draw(batch, "VOLUME:",
            Gdx.graphics.getWidth()/2 - 100,
            sliderBarBounds.y + 80); // Positioned 80px above the slider

        // Draw slider
        batch.draw(sliderBar, sliderBarBounds.x, sliderBarBounds.y, sliderBarBounds.width, sliderBarBounds.height);
        batch.draw(sliderKnob, sliderKnobBounds.x, sliderKnobBounds.y, sliderKnobBounds.width, sliderKnobBounds.height);

        // Draw volume percentage
        int volumePercentage = (int)(volume * 100);
        game.font.draw(batch, volumePercentage + "%",
            sliderBarBounds.x + sliderBarBounds.width + 20,
            sliderBarBounds.y + sliderBarBounds.height/2 + 5);

        batch.end();

        handleInput();
    }

    private void handleInput() {
        Vector2 touchPos = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

        // Handle back button click
        if (Gdx.input.justTouched()) {
            float backBtnSize = 150;
            float backBtnX = 20;
            float backBtnY = Gdx.graphics.getHeight() - backBtnSize - 20;

            if (touchPos.x >= backBtnX && touchPos.x <= backBtnX + backBtnSize &&
                touchPos.y >= backBtnY && touchPos.y <= backBtnY + backBtnSize) {
                game.setScreen(new MainMenuScreen(game));
                return;
            }

            // Check if slider knob was touched
            if (sliderKnobBounds.contains(touchPos)) {
                dragging = true;
            }
        }

        // Handle slider dragging
        if (Gdx.input.isTouched() && dragging) {
            // Clamp the knob position within the slider bar bounds
            float newX = Math.max(sliderBarBounds.x,
                Math.min(touchPos.x - sliderKnobBounds.width/2,
                    sliderBarBounds.x + sliderBarBounds.width - sliderKnobBounds.width));

            sliderKnobBounds.x = newX;

            // Update volume based on knob position (0.0 to 1.0)
            volume = (newX - sliderBarBounds.x) / (sliderBarBounds.width - sliderKnobBounds.width);

            // Update the game's audio volume through AudioManager
            // This will save the setting to preferences and update any playing music
            AudioManager.getInstance().setVolume(volume);
        } else {
            dragging = false;
        }

        // Handle touch release
        if (!Gdx.input.isTouched()) {
            dragging = false;
        }
    }

    @Override
    public void dispose() {
        background.dispose();
        backButton.dispose();
        sliderBar.dispose();
        sliderKnob.dispose();
        settingsBorder.dispose();
    }

    @Override public void resize(int width, int height) {
        // Reinitialize slider when screen is resized
        initSlider();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
