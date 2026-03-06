package com.jarvis.assistant.voice;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import java.util.Locale;
import java.util.Set;

public class TextToSpeechEngine implements TextToSpeech.OnInitListener {
    private static final String TAG = "TTS";

    private TextToSpeech tts;
    private boolean isReady = false;
    private String pendingText = null;
    private String gender = "male";

    public TextToSpeechEngine(Context context) {
        tts = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            isReady = true;
            tts.setLanguage(new Locale("hi", "IN"));
            applyGender();
            if (pendingText != null) {
                speak(pendingText);
                pendingText = null;
            }
        } else {
            Log.e(TAG, "TTS initialization failed!");
        }
    }

    public void speak(String text) {
        if (text == null || text.trim().isEmpty()) return;
        if (!isReady) {
            pendingText = text;
            return;
        }
        // Auto-detect language and switch
        if (isHindiText(text)) {
            tts.setLanguage(new Locale("hi", "IN"));
        } else {
            tts.setLanguage(Locale.US);
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "j_" + System.currentTimeMillis());
    }

    public void setGender(String gender) {
        this.gender = gender;
        applyGender();
    }

    public void setSpeechRate(float rate) {
        if (isReady) tts.setSpeechRate(rate);
    }

    public void setPitch(float pitch) {
        if (isReady) tts.setPitch(pitch);
    }

    private void applyGender() {
        if (!isReady) return;
        // Male: lower pitch, Female: higher pitch
        if ("female".equals(gender)) {
            tts.setPitch(1.25f);
            tts.setSpeechRate(1.05f);
        } else {
            tts.setPitch(0.85f);
            tts.setSpeechRate(0.95f);
        }
        // Try to find a gender-matching voice
        Set<Voice> voices = tts.getVoices();
        if (voices != null) {
            for (Voice v : voices) {
                String lang = v.getLocale().getLanguage();
                if (!lang.equals("hi") && !lang.equals("en")) continue;
                String name = v.getName().toLowerCase();
                boolean isFemale = name.contains("female") || name.contains("woman") || name.contains("-f");
                boolean isMale = name.contains("male") || name.contains("man") || name.contains("-m");
                if ("female".equals(gender) && isFemale) { tts.setVoice(v); return; }
                if ("male".equals(gender) && isMale) { tts.setVoice(v); return; }
            }
        }
    }

    private boolean isHindiText(String text) {
        for (char c : text.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.DEVANAGARI) return true;
        }
        String lower = text.toLowerCase();
        return lower.contains(" hai") || lower.contains(" karo") || lower.contains("namaskar")
                || lower.contains("hoon") || lower.contains(" diya") || lower.contains("aapka");
    }

    public boolean isReady() { return isReady; }

    public void stop() {
        if (tts != null && isReady) tts.stop();
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            isReady = false;
        }
    }
}
