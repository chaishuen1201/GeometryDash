package com.grp19.geometrydash;

public class GameData {
    private static GameData instance;
    private int unlockedLevels = 1; // Start with level 1 unlocked

    private GameData() {}

    public static GameData getInstance() {
        if (instance == null) {
            instance = new GameData();
        }
        return instance;
    }

    public int getUnlockedLevels() {
        return unlockedLevels;
    }

    public void unlockNextLevel(int currentLevel) {
        if (currentLevel >= unlockedLevels) {
            unlockedLevels = currentLevel + 1;
            // Here you could save to preferences if you want persistence
        }
    }
}
