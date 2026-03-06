package com.jarvis.assistant.ai;

import android.content.Context;
import android.content.SharedPreferences;

public class LearningEngine {

    private static final String PREF_NAME = "JarvisLearning";
    private SharedPreferences preferences;

    public LearningEngine(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Save user preference
    public void savePreference(String key, String value) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Get user preference
    public String getPreference(String key) {

        return preferences.getString(key, null);
    }

    // Save command usage count
    public void increaseCommandCount(String command) {

        int count = preferences.getInt(command, 0);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(command, count + 1);
        editor.apply();
    }

    // Get command usage count
    public int getCommandCount(String command) {

        return preferences.getInt(command, 0);
    }

    // Reset learning data
    public void resetLearning() {

        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
