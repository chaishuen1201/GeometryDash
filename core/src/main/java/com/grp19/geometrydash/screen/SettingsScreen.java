package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.grp19.geometrydash.AudioManager;
import com.grp19.geometrydash.GeometryDash;

public class SettingsScreen implements Screen {
    private final GeometryDash game;
    private float masterVolume = 1.0f;
    private SpriteBatch batch;
    private Texture background, settingsBorder, sliderBar, sliderKnob, backButton;
    private float backButtonX, backButtonY, backButtonWidth, backButtonHeight;
    private Stage stage;
    private Slider volumeSlider;
    private Label volumeLabel;
    private BitmapFont customFont;

    public SettingsScreen(GeometryDash game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Get volume from audio manager
        AudioManager am = AudioManager.getInstance();
        masterVolume = am.getVolume();

        // Create sprite batch
        batch = new SpriteBatch();

        // Add textures
        background = new Texture(Gdx.files.internal("background.png"));
        settingsBorder = new Texture(Gdx.files.internal("settings_border.png"));
        sliderBar = new Texture(Gdx.files.internal("slider_bar.png"));
        sliderKnob = new Texture(Gdx.files.internal("slider_knob.png"));
        backButton = new Texture(Gdx.files.internal("back.png"));

        // Create stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Add volume slider
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(new TextureRegion(sliderBar));
        sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(sliderKnob));
        volumeSlider = new Slider(0f, 1f, 0.01f, false, sliderStyle);
        volumeSlider.setValue(masterVolume);
        volumeSlider.setPosition(775, 380);
        volumeSlider.setSize(800, 100);

        // Get custom font for volume label
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pusab.otf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 80;
        parameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        parameter.borderWidth = 6;
        parameter.borderColor = com.badlogic.gdx.graphics.Color.BLACK;
        customFont = generator.generateFont(parameter);
        generator.dispose();

        // Add volume label
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = customFont;
        volumeLabel = new Label("Master Volume:\n\n" + (int)(masterVolume * 100), labelStyle);
        volumeLabel.setPosition(920, 530);
        volumeLabel.setSize(500, 250);
        volumeLabel.setAlignment(Align.center);

        // Add volume slider listener
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                masterVolume = volumeSlider.getValue();
                volumeLabel.setText("Master Volume:\n\n" + (int)(masterVolume * 100));
                am.setVolume(masterVolume);
            }
        });

        // Add volume label and slider to stage
        stage.addActor(volumeLabel);
        stage.addActor(volumeSlider);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Draw background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw settings border
        batch.draw(settingsBorder, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw back button
        backButtonWidth = 150;
        backButtonHeight = 150;
        backButtonX = 20;
        backButtonY = Gdx.graphics.getHeight() - backButtonHeight - 20;
        batch.draw(backButton, backButtonX, backButtonY, backButtonWidth, backButtonHeight);

        batch.end();

        // Draw stage (volume label and slider)
        stage.act();
        stage.draw();

        // Handle touch input
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // If back button is pressed, return to main menu
            if (touchX >= backButtonX && touchX <= backButtonX + backButtonWidth &&
                touchY >= backButtonY && touchY <= backButtonY + backButtonHeight) {
                Gdx.app.postRunnable(() -> {
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                });
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        background.dispose();
        settingsBorder.dispose();
        sliderBar.dispose();
        sliderKnob.dispose();
        backButton.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
