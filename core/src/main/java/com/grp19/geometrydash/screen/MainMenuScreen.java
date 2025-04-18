package com.grp19.geometrydash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.grp19.geometrydash.GeometryDash;

public class MainMenuScreen implements Screen {
    private final GeometryDash game;
    private final Stage stage;
    private final Skin skin;

    public MainMenuScreen(GeometryDash game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton playButton = new TextButton("Play", skin);
        TextButton settingsButton = new TextButton("Settings", skin);
        TextButton leaderboardButton = new TextButton("Leaderboard", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new LevelSelectionScreen(game));
            }
        });

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new SettingsScreen(game));
            }
        });

        leaderboardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new LeaderboardScreen(game));
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        table.add(playButton).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(settingsButton).fillX().uniformX();
        table.row();
        table.add(leaderboardButton).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(exitButton).fillX().uniformX();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
