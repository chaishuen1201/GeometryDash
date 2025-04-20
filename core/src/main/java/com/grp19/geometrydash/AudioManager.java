package com.grp19.geometrydash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;

public class AudioManager {
    private static AudioManager instance;
    private Preferences pref;
    private float masterVolume;
    private Music currentMusic;
    private float pausedPosition = 0;

    private AudioManager() {
        // Load saved settings
        pref = Gdx.app.getPreferences("settings");
        masterVolume = pref.getFloat("masterVolume", 1.0f);
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public float getVolume() {
        return masterVolume;
    }

    public void setVolume(float volume) {
        masterVolume = volume;
        pref.putFloat("masterVolume", volume);
        pref.flush(); // Save settings to disk
        updateCurrentMusicVolume();
    }

    public void playMusic(Music music) {
        if (currentMusic != null) {
            currentMusic.stop(); // Stops previous background music before starting a new one
        }
        currentMusic = music;
        updateCurrentMusicVolume();
        currentMusic.setLooping(true);
        currentMusic.play();
    }

    public void stopMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }
    }

    public void pauseMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            pausedPosition = currentMusic.getPosition();
            currentMusic.pause();
        }
    }

    public void resumeMusic() {
        if (currentMusic != null && !currentMusic.isPlaying()) {
            currentMusic.setPosition(pausedPosition);
            currentMusic.play();
            currentMusic.setVolume(masterVolume);
        }
    }

    public void updateCurrentMusicVolume() {
        if (currentMusic != null) {
            currentMusic.setVolume(masterVolume);
        }
    }
}
