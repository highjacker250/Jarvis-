package com.jarvis.assistant.core;

import android.content.Context;
import android.util.Log;

import com.jarvis.assistant.ai.LocalAIProcessor;
import com.jarvis.assistant.automation.DeviceController;
import com.jarvis.assistant.voice.TextToSpeechEngine;
import com.jarvis.assistant.utils.LanguageDetector;

public class JarvisBrain {

    private static final String TAG = "JarvisBrain";

    private Context context;
    private CommandProcessor commandProcessor;
    private MemoryManager memoryManager;
    private TextToSpeechEngine ttsEngine;
    private LocalAIProcessor aiProcessor;
    private DeviceController deviceController;

    // Assistant settings
    private String assistantName = "Jarvis";
    private String voiceGender = "male";
    private String personality = "professional";
    private String responseLanguage = "auto";

    public JarvisBrain(Context context) {

        this.context = context;

        commandProcessor = new CommandProcessor(context);
        memoryManager = new MemoryManager(context);
        ttsEngine = new TextToSpeechEngine(context);
        aiProcessor = new LocalAIProcessor(context);
        deviceController = new DeviceController(context);

        loadPersonalitySettings();
    }

    /**
     * Main brain method
     */
    public void processInput(String input, BrainCallback callback) {

        Log.d(TAG, "Processing input: " + input);

        // Save interaction
        memoryManager.saveMemory("last_input", input);

        // Detect language
        LanguageDetector detector = new LanguageDetector();
        LanguageDetector.Language lang = detector.detectLanguage(input);
        String language = lang.name().toLowerCase();

        // Try fast local command
        String localResult = commandProcessor.tryLocalCommand(input, language);

        if (localResult != null) {
            respond(localResult, callback);
            return;
        }

        // Use AI processor
        aiProcessor.processQuery(input, language, new LocalAIProcessor.AICallback() {

            @Override
            public void onResponse(String response) {

                String action = extractDeviceAction(response);

                if (action != null) {
                    deviceController.execute(action);
                }

                respond(response, callback);
            }

            @Override
            public void onError(String error) {
                respond(getFallbackResponse(language), callback);
            }
        });
    }

    private void respond(String text, BrainCallback callback) {

        text = applyPersonality(text);

        ttsEngine.speak(text);

        if (callback != null) {
            callback.onResponse(text);
        }
    }

    private String applyPersonality(String response) {

        switch (personality) {

            case "casual":
                return response;

            case "friendly":
                return response;

            default:
                return response;
        }
    }

    private String getFallbackResponse(String lang) {

        if (lang.equals("hindi") || lang.equals("bhojpuri")) {
            return "Maafi chahta hoon, main samajh nahi paya. Dobara boliye.";
        }

        return "Sorry, I didn't understand that. Please repeat.";
    }

    private String extractDeviceAction(String aiResponse) {

        if (aiResponse.contains("[ACTION:")) {

            int start = aiResponse.indexOf("[ACTION:") + 8;
            int end = aiResponse.indexOf("]", start);

            if (end > start) {
                return aiResponse.substring(start, end);
            }
        }

        return null;
    }

    private void loadPersonalitySettings() {

        android.content.SharedPreferences prefs =
                context.getSharedPreferences("jarvis_settings", Context.MODE_PRIVATE);

        assistantName = prefs.getString("assistant_name", "Jarvis");
        voiceGender = prefs.getString("voice_gender", "male");
        personality = prefs.getString("personality", "professional");
    }

    public void updateSettings(String name, String gender, String pers) {

        assistantName = name;
        voiceGender = gender;
        personality = pers;

        ttsEngine.setGender(gender);

        android.content.SharedPreferences.Editor editor =
                context.getSharedPreferences("jarvis_settings", Context.MODE_PRIVATE).edit();

        editor.putString("assistant_name", name);
        editor.putString("voice_gender", gender);
        editor.putString("personality", pers);

        editor.apply();
    }

    public interface BrainCallback {
        void onResponse(String response);
        void onError(String error);
    }
}