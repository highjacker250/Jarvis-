package com.jarvis.assistant.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import java.util.ArrayList;

public class VoiceRecognizer {
    private static final String TAG = "VoiceRecognizer";

    private final Context context;
    private SpeechRecognizer speechRecognizer;
    private VoiceCallback callback;
    private boolean isListening = false;

    public interface VoiceCallback {
        void onResult(String text);
        void onPartialResult(String text);
        void onError(String error);
        void onReadyForSpeech();
        void onEndOfSpeech();
    }

    public VoiceRecognizer(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            if (speechRecognizer != null) {
                speechRecognizer.destroy();
            }
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {

                @Override
                public void onReadyForSpeech(Bundle params) {
                    isListening = true;
                    if (callback != null) callback.onReadyForSpeech();
                }

                @Override
                public void onBeginningOfSpeech() {
                    Log.d(TAG, "Speech started");
                }

                @Override
                public void onRmsChanged(float rmsdB) {}

                @Override
                public void onBufferReceived(byte[] buffer) {}

                @Override
                public void onEndOfSpeech() {
                    isListening = false;
                    if (callback != null) callback.onEndOfSpeech();
                }

                @Override
                public void onError(int error) {
                    isListening = false;
                    String msg = getErrorText(error);
                    Log.e(TAG, "Error: " + msg);
                    if (callback != null) callback.onError(msg);
                    // Reinitialize recognizer after error
                    init();
                }

                @Override
                public void onResults(Bundle results) {
                    isListening = false;
                    ArrayList<String> matches = results.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String best = matches.get(0);
                        Log.d(TAG, "Result: " + best);
                        if (callback != null) callback.onResult(best);
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {
                    ArrayList<String> partial = partialResults.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION);
                    if (partial != null && !partial.isEmpty() && callback != null) {
                        callback.onPartialResult(partial.get(0));
                    }
                }

                @Override
                public void onEvent(int eventType, Bundle params) {}
            });
        } else {
            Log.e(TAG, "Speech recognition not available on this device!");
        }
    }

    public void startListening(VoiceCallback cb) {
        this.callback = cb;
        if (speechRecognizer == null) init();

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Primary: Hindi, also accepts English
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN");
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000L);

        try {
            speechRecognizer.startListening(intent);
        } catch (Exception e) {
            Log.e(TAG, "Start listening error: " + e.getMessage());
            init();
        }
    }

    public void stopListening() {
        if (speechRecognizer != null) {
            try {
                speechRecognizer.stopListening();
            } catch (Exception e) {
                Log.e(TAG, "Stop error: " + e.getMessage());
            }
        }
        isListening = false;
    }

    public boolean isListening() {
        return isListening;
    }

    public void destroy() {
        if (speechRecognizer != null) {
            try {
                speechRecognizer.destroy();
            } catch (Exception e) {
                Log.e(TAG, "Destroy error: " + e.getMessage());
            }
            speechRecognizer = null;
        }
    }

    private String getErrorText(int error) {
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO: return "Audio error. Check microphone.";
            case SpeechRecognizer.ERROR_CLIENT: return "Client error.";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS: return "Microphone permission denied!";
            case SpeechRecognizer.ERROR_NETWORK: return "Network error. Check internet.";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT: return "Network timeout.";
            case SpeechRecognizer.ERROR_NO_MATCH: return "Kuch samajh nahi aaya. Dobara bolein.";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY: return "Recognizer busy. Please wait.";
            case SpeechRecognizer.ERROR_SERVER: return "Server error.";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT: return "No speech detected.";
            default: return "Unknown error: " + error;
        }
    }
}
