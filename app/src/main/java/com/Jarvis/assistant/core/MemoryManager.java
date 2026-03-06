package com.jarvis.assistant.core;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.*;

public class MemoryManager {
    private static SharedPreferences prefs;
    private static final Gson gson = new Gson();
    private static List<String> conversationHistory = new ArrayList<>();
    private static final int MAX_HISTORY = 100;

    public static void init(Context context) {
        prefs = context.getSharedPreferences("jarvis_memory", Context.MODE_PRIVATE);
        loadHistory();
    }

    // ===== USER PROFILE =====
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

    public static boolean isFirstRun() {
        return prefs.getBoolean("first_run", true);
    }

    public static void setFirstRunDone() {
        prefs.edit().putBoolean("first_run", false).apply();
    }

    // ===== CONVERSATION MEMORY =====
    public static void saveInteraction(String text, long timestamp) {
        conversationHistory.add("[" + formatTime(timestamp) + "] Aap: " + text);
        trimHistory();
        saveHistory();
    }

    public static void saveResponse(String text, long timestamp) {
        conversationHistory.add("[" + formatTime(timestamp) + "] Jarvis: " + text);
        trimHistory();
        saveHistory();
    }

    public static String getRecentContext(int pairCount) {
        int size = conversationHistory.size();
        int start = Math.max(0, size - pairCount * 2);
        List<String> recent = conversationHistory.subList(start, size);
        return String.join("\n", recent);
    }

    public static List<String> getFullHistory() {
        return new ArrayList<>(conversationHistory);
    }

    public static void clearHistory() {
        conversationHistory.clear();
        prefs.edit().remove("conv_history").apply();
    }

    // ===== REMINDERS =====
    public static void addReminder(String title, long timeMs) {
        List<Map<String, String>> reminders = getReminders();
        Map<String, String> r = new HashMap<>();
        r.put("title", title);
        r.put("time", String.valueOf(timeMs));
        r.put("id", String.valueOf(System.currentTimeMillis()));
        reminders.add(r);
        prefs.edit().putString("reminders", gson.toJson(reminders)).apply();
    }

    public static List<Map<String, String>> getReminders() {
        String json = prefs.getString("reminders", "[]");
        List<Map<String, String>> list = gson.fromJson(json,
                new TypeToken<List<Map<String, String>>>() {}.getType());
        return list != null ? list : new ArrayList<>();
    }

    public static void removeReminder(String id) {
        List<Map<String, String>> reminders = getReminders();
        reminders.removeIf(r -> id.equals(r.get("id")));
        prefs.edit().putString("reminders", gson.toJson(reminders)).apply();
    }

    // ===== FACE DATA =====
    public static void saveFaceData(float[] features) {
        prefs.edit().putString("face_data", gson.toJson(features)).apply();
    }

    public static float[] getFaceData() {
        String data = prefs.getString("face_data", null);
        if (data == null) return null;
        return gson.fromJson(data, float[].class);
    }

    public static boolean hasFaceRegistered() {
        return prefs.contains("face_data");
    }

    public static void clearFaceData() {
        prefs.edit().remove("face_data").apply();
    }

    // ===== QUICK FACTS (things Jarvis remembers about you) =====
    public static void rememberFact(String key, String value) {
        prefs.edit().putString("fact_" + key.toLowerCase(), value).apply();
    }

    public static String recallFact(String key) {
        return prefs.getString("fact_" + key.toLowerCase(), null);
    }

    // ===== HELPERS =====
    private static void trimHistory() {
        while (conversationHistory.size() > MAX_HISTORY) {
            conversationHistory.remove(0);
        }
    }

    private static void saveHistory() {
        prefs.edit().putString("conv_history", gson.toJson(conversationHistory)).apply();
    }

    private static void loadHistory() {
        String json = prefs.getString("conv_history", "[]");
        conversationHistory = gson.fromJson(json, new TypeToken<List<String>>() {}.getType());
        if (conversationHistory == null) conversationHistory = new ArrayList<>();
    }

    private static String formatTime(long ms) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(ms));
    }
}
