package com.mygdx.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class PauseMenu extends Table {
    public PauseMenu(Skin skin, final Stage stage, final Runnable onResume, final Runnable onRestart, final Runnable onMainMenu) {
        super(skin); //i have no idea which skin to add here
        setFillParent(true);
        setBackground("default-round"); // Needs drawable in skin
        center();

        Label pauseLabel = new Label("Paused", skin);
        pauseLabel.setFontScale(2);
        pauseLabel.setColor(Color.WHITE);

        TextButton resumeButton = new TextButton("Resume", skin);
        TextButton restartButton = new TextButton("Restart", skin);
        TextButton mainMenuButton = new TextButton("Main Menu", skin);

        resumeButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                onResume.run();
                remove();
            }
        });

        restartButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                onRestart.run();
            }
        });

        mainMenuButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                onMainMenu.run();
            }
        });

        add(pauseLabel).pad(20);
        row();
        add(resumeButton).pad(10).width(200);
        row();
        add(restartButton).pad(10).width(200);
        row();
        add(mainMenuButton).pad(10).width(200);
    }
}
