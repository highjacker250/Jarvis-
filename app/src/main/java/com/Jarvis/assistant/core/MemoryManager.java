package com.jarvis.assistant.core;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.*;

/**
 * Persistent memory - remembers user, preferences, conversation history
 * Uses SharedPreferences + Room DB (FREE, local storage)
 */
public class MemoryManager {
    private static SharedPreferences prefs;
    private static Gson gson = new Gson();
    private static List<String> conversationHistory = new ArrayList<>();
    private static final int MAX_HISTORY = 50;

    public static void init(Context context) {
        prefs = context.getSharedPreferences("jarvis_memory", Context.MODE_PRIVATE);
        loadHistory();
    }

    // User profile
    public static void setUserName(String name) {
        prefs.edit().putString("user_name", name).apply();
    }

    public static String getUserName() {
        return prefs.getString("user_name", "");
    }

    public static void setUserPreference(String key, String value) {
        prefs.edit().putString("pref_" + key, value).apply();
    }

    public static String getUserPreference(String key, String defaultValue) {
        return prefs.getString("pref_" + key, defaultValue);
    }

    // Interaction memory
    public static void saveInteraction(String text, long timestamp) {
        conversationHistory.add("[" + formatTime(timestamp) + "] User: " + text);
        if (conversationHistory.size() > MAX_HISTORY) {
            conversationHistory.remove(0);
        }
        saveHistory();
    }

    public static void saveResponse(String text, long timestamp) {
        conversationHistory.add("[" + formatTime(timestamp) + "] Jarvis: " + text);
        if (conversationHistory.size() > MAX_HISTORY) {
            conversationHistory.remove(0);
        }
        saveHistory();
    }

    public static String getRecentContext(int count) {
        int size = conversationHistory.size();
        int start = Math.max(0, size - count * 2);
        List<String> recent = conversationHistory.subList(start, size);
        return String.join("\n", recent);
    }

    public static List<String> getFullHistory() {
        return new ArrayList<>(conversationHistory);
    }

    public static void clearHistory() {
        conversationHistory.clear();
        prefs.edit().remove("conversation_history").apply();
    }

    // Reminders
    public static void addReminder(String title, long timeMs) {
        List<Map<String, String>> reminders = getReminders();
        Map<String, String> reminder = new HashMap<>();
        reminder.put("title", title);
        reminder.put("time", String.valueOf(timeMs));
        reminders.add(reminder);
        prefs.edit().putString("reminders", gson.toJson(reminders)).apply();
    }

    public static List<Map<String, String>> getReminders() {
        String json = prefs.getString("reminders", "[]");
        return gson.fromJson(json, new TypeToken<List<Map<String, String>>>(){}.getType());
    }

    // Face ID stored as feature hash
    public static void saveFaceData(float[] faceFeatures) {
        String data = gson.toJson(faceFeatures);
        prefs.edit().putString("face_data", data).apply();
    }

    public static float[] getFaceData() {
        String data = prefs.getString("face_data", null);
        if (data == null) return null;
        return gson.fromJson(data, float[].class);
    }

    private static void saveHistory() {
        prefs.edit().putString("conversation_history", gson.toJson(conversationHistory)).apply();
    }

    private static void loadHistory() {
        String json = prefs.getString("conversation_history", "[]");
        conversationHistory = gson.fromJson(json, new TypeToken<List<String>>(){}.getType());
        if (conversationHistory == null) conversationHistory = new ArrayList<>();
    }

    private static String formatTime(long ms) {
        return new java.text.SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(ms));
    }
}