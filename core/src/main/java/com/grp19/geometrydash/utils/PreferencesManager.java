package com.grp19.geometrydash.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
public class PreferencesManager {
    private static final String PREFS_NAME = "geometrydash_prefs";
    private static final String UNLOCKED_LEVEL_KEY = "unlocked_level";
    private static final String HIGH_SCORE_KEY_PREFIX = "high_score_level_";
    private static final String ATTEMPTS_KEY_PREFIX = "attempts_level_";

    private final Preferences prefs;

    public PreferencesManager() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    // --- Level Unlocking ---
    public int getUnlockedLevel() {
        return prefs.getInteger(UNLOCKED_LEVEL_KEY, 1);
    }

    public void unlockLevel(int level) {
        if (level > getUnlockedLevel()) {
            prefs.putInteger(UNLOCKED_LEVEL_KEY, level);
            prefs.flush();
        }
    }

    // --- High Scores ---
    public int getHighScore(int level) {
        return prefs.getInteger(HIGH_SCORE_KEY_PREFIX + level, 0);
    }

    public void setHighScore(int level, int score) {
        if (score > getHighScore(level)) {
            prefs.putInteger(HIGH_SCORE_KEY_PREFIX + level, score);
            prefs.flush();
        }
    }

    // --- Attempt Tracking ---
    public int getAttempts(int level) {
        return prefs.getInteger(ATTEMPTS_KEY_PREFIX + level, 0);
    }

    public void incrementAttempts(int level) {
        int current = getAttempts(level);
        prefs.putInteger(ATTEMPTS_KEY_PREFIX + level, current + 1);
        prefs.flush();
    }

    public void resetAllData() {
        prefs.clear();
        prefs.flush();
    }
}
