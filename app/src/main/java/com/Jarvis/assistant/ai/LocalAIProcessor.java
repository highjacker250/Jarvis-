package com.jarvis.assistant.ai;

import android.content.Context;
import android.util.Log;
import com.jarvis.assistant.core.MemoryManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.*;

/**
 * LOCAL FREE AI Processing
 * Uses: Ollama (local LLM) OR OpenRouter free tier OR rule-based fallback
 * Priority: Local Rules → Ollama (if installed) → OpenRouter Free → Fallback
 */
public class LocalAIProcessor {
    private static final String TAG = "LocalAI";
    
    // Ollama runs locally on Android via Termux (FREE!)
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    
    // OpenRouter FREE models (no cost, just signup)
    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
    private String openRouterKey = ""; // User adds their FREE key
    
    private Context context;
    private boolean ollamaAvailable = false;

    public interface AICallback {
        void onResponse(String response);
        void onError(String error);
    }

    public LocalAIProcessor(Context context) {
        this.context = context;
        checkOllama();
        loadApiKey();
    }

    private void loadApiKey() {
        android.content.SharedPreferences prefs = 
            context.getSharedPreferences("jarvis_settings", Context.MODE_PRIVATE);
        openRouterKey = prefs.getString("openrouter_key", "");
    }

    public void processQuery(String query, String language, AICallback callback) {
        // Build context from memory
        String memoryContext = MemoryManager.getRecentContext(5);
        String userName = MemoryManager.getUserName();
        
        String systemPrompt = buildSystemPrompt(userName, language, memoryContext);

        if (ollamaAvailable) {
            callOllama(systemPrompt, query, callback);
        } else if (!openRouterKey.isEmpty()) {
            callOpenRouter(systemPrompt, query, callback);
        } else {
            // Smart rule-based fallback
            callback.onResponse(ruleBasedResponse(query, language));
        }
    }

    private String buildSystemPrompt(String userName, String lang, String memory) {
        return "You are Jarvis, an advanced AI assistant like Iron Man's JARVIS. " +
               "User's name: " + (userName.isEmpty() ? "Sir" : userName) + ". " +
               "Recent context: " + memory + ". " +
               "Language: Respond in " + lang + ". " +
               "If the user asks to control device, respond with [ACTION:COMMAND] format. " +
               "Commands: WIFI_ON, WIFI_OFF, BLUETOOTH_ON, BLUETOOTH_OFF, FLASHLIGHT_ON, " +
               "FLASHLIGHT_OFF, SCREENSHOT, VOLUME_UP, VOLUME_DOWN, MUTE. " +
               "Be concise, helpful, and intelligent. " +
               "If Hindi/Bhojpuri, respond in Hindi (Roman or Devanagari).";
    }

    private void callOllama(String system, String query, AICallback callback) {
        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("model", "llama3.2:1b"); // Lightweight FREE model
                body.put("prompt", system + "\n\nUser: " + query + "\nJarvis:");
                body.put("stream", false);

                String response = httpPost(OLLAMA_URL, body.toString());
                JSONObject json = new JSONObject(response);
                callback.onResponse(json.getString("response").trim());
            } catch (Exception e) {
                Log.e(TAG, "Ollama error", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    private void callOpenRouter(String system, String query, AICallback callback) {
        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("model", "meta-llama/llama-3.2-3b-instruct:free"); // FREE model!
                
                JSONArray messages = new JSONArray();
                JSONObject sysMsg = new JSONObject();
                sysMsg.put("role", "system");
                sysMsg.put("content", system);
                messages.put(sysMsg);
                
                JSONObject userMsg = new JSONObject();
                userMsg.put("role", "user");
                userMsg.put("content", query);
                messages.put(userMsg);
                
                body.put("messages", messages);

                String response = httpPostWithAuth(OPENROUTER_URL, body.toString(), openRouterKey);
                JSONObject json = new JSONObject(response);
                String result = json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
                callback.onResponse(result.trim());
            } catch (Exception e) {
                Log.e(TAG, "OpenRouter error", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    private String ruleBasedResponse(String query, String lang) {
        // Intelligent fallback when no internet/AI
        String q = query.toLowerCase();
        boolean hi = lang.equals("hindi") || lang.equals("bhojpuri");
        
        if (q.contains("hello") || q.contains("hi") || q.contains("namaste") || q.contains("halo"))
            return hi ? "Namaskar! Main Jarvis hoon. Aap ki kya seva kar sakta hoon?" 
                      : "Hello! I'm Jarvis. How can I assist you?";
        
        if (q.contains("kaise ho") || q.contains("how are you"))
            return hi ? "Main bilkul theek hoon! Aap batao, kya kaam hai?" 
                      : "I'm functioning perfectly! What can I do for you?";
        
        if (q.contains("tera naam") || q.contains("your name") || q.contains("kaun ho"))
            return hi ? "Main Jarvis hoon, aapka personal AI assistant!" 
                      : "I am Jarvis, your personal AI assistant!";
        
        if (q.contains("joke") || q.contains("mazak"))
            return hi ? "Ek programmer ghar gaya aur bola: 'Darwaza kholo!' Darwaza bola: '403 Forbidden!'" 
                      : "Why do programmers prefer dark mode? Because light attracts bugs!";
        
        return hi ? "Yeh kaam main abhi seekh raha hoon. Internet connect karein better responses ke liye." 
                  : "I'm still learning this. Connect to internet for better AI responses.";
    }

    private void checkOllama() {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:11434/api/tags");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(1000);
                ollamaAvailable = conn.getResponseCode() == 200;
            } catch (Exception e) {
                ollamaAvailable = false;
            }
        }).start();
    }

    private String httpPost(String urlStr, String body) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes("UTF-8"));
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private String httpPostWithAuth(String urlStr, String body, String apiKey) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("HTTP-Referer", "com.jarvis.assistant");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes("UTF-8"));
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
}