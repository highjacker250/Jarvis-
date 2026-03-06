package com.jarvis.assistant.voice;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import java.util.Locale;
import java.util.Set;

/**
 * FREE Android built-in TTS
 * Supports Hindi + English
 */
public class TextToSpeechEngine implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private boolean isReady = false;
    private String gender = "male";
    private float speechRate = 1.0f;
    private float pitch = 1.0f;
    private String pendingText = null;

    public TextToSpeechEngine(Context context) {
        tts = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            isReady = true;
            // Set Hindi + English
            tts.setLanguage(new Locale("hi", "IN"));
            applyVoiceSettings();
            
            if (pendingText != null) {
                speak(pendingText);
                pendingText = null;
            }
        }
    }

    public void speak(String text) {
        if (!isReady) {
            pendingText = text;
            return;
        }
        // Detect language and switch
        if (isHindi(text)) {
            tts.setLanguage(new Locale("hi", "IN"));
        } else {
            tts.setLanguage(Locale.US);
        }
        tts.setSpeechRate(speechRate);
        tts.setPitch(pitch);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis_" + System.currentTimeMillis());
    }

    public void setGender(String gender) {
        this.gender = gender;
        // Male: lower pitch, Female: higher pitch
        if (gender.equals("female")) {
            this.pitch = 1.3f;
            this.speechRate = 1.1f;
        } else {
            this.pitch = 0.8f;
            this.speechRate = 0.95f;
        }
        applyVoiceSettings();
    }

    private void applyVoiceSettings() {
        if (!isReady) return;
        // Try to find gender-specific voice
        Set<Voice> voices = tts.getVoices();
        if (voices != null) {
            for (Voice voice : voices) {
                if (voice.getLocale().getLanguage().equals("hi") || 
                    voice.getLocale().getLanguage().equals("en")) {
                    String voiceName = voice.getName().toLowerCase();
                    if (gender.equals("female") && (voiceName.contains("female") || 
                        voiceName.contains("woman") || voiceName.contains("f-"))) {
                        tts.setVoice(voice);
                        return;
                    } else if (gender.equals("male") && (voiceName.contains("male") || 
                               voiceName.contains("man") || voiceName.contains("m-"))) {
                        tts.setVoice(voice);
                        return;
                    }
                }
            }
        }
    }

    private boolean isHindi(String text) {
        for (char c : text.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.DEVANAGARI) {
                return true;
            }
        }
        // Check Roman Hindi keywords
        String lower = text.toLowerCase();
        return lower.contains("hai") || lower.contains("karo") || lower.contains("kar") ||
               lower.contains("aap") || lower.contains("mera") || lower.contains("hoon");
    }

    public void stop() {
        if (tts != null) tts.stop();
    }

    public void shutdown() {
        if (tts != null) tts.shutdown();
    }
}