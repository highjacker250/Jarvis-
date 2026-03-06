package com.jarvis.assistant.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Uses Android's built-in FREE SpeechRecognizer
 * Supports Hindi, English, Bhojpuri (via Hindi model)
 * No API key needed!
 */
public class VoiceRecognizer {
    private Context context;
    private SpeechRecognizer speechRecognizer;
    private VoiceCallback callback;
    private boolean isListening = false;

    public interface VoiceCallback {
        void onResult(String text);
        void onError(String error);
        void onReadyForSpeech();
        void onEndOfSpeech();
    }

    public VoiceRecognizer(Context context) {
        this.context = context;
        initRecognizer();
    }

    private void initRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    isListening = true;
                    if (callback != null) callback.onReadyForSpeech();
                }

                @Override
                public void onResults(Bundle results) {
                    isListening = false;
                    ArrayList<String> matches = results.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        if (callback != null) callback.onResult(matches.get(0));
                    }
                }

                @Override
                public void onError(int error) {
                    isListening = false;
                    String msg = getErrorMessage(error);
                    if (callback != null) callback.onError(msg);
                }

                @Override public void onBeginningOfSpeech() {}
                @Override public void onRmsChanged(float rmsdB) {}
                @Override public void onBufferReceived(byte[] buffer) {}
                @Override public void onEndOfSpeech() {
                    if (callback != null) callback.onEndOfSpeech();
                }
                @Override public void onPartialResults(Bundle partialResults) {
                    // Show real-time partial results
                    ArrayList<String> partial = partialResults.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION);
                    if (partial != null && !partial.isEmpty() && callback != null) {
                        // Optional: show partial text in UI
                    }
                }
                @Override public void onEvent(int eventType, Bundle params) {}
            });
        }
    }

    public void startListening(VoiceCallback cb) {
        this.callback = cb;
        if (speechRecognizer == null) initRecognizer();
        
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Multi-language: Hindi + English
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, 
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN"); // Hindi primary
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN,en-IN,en-US");
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        
        speechRecognizer.startListening(intent);
    }

    public void stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            isListening = false;
        }
    }

    public boolean isListening() { return isListening; }

    private String getErrorMessage(int error) {
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO: return "Audio recording error";
            case SpeechRecognizer.ERROR_CLIENT: return "Client side error";
            case SpeechRecognizer.ERROR_NETWORK: return "Network error - try offline mode";
            case SpeechRecognizer.ERROR_NO_MATCH: return "No speech detected";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT: return "No speech input";
            default: return "Unknown error: " + error;
        }
    }

    public void destroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}