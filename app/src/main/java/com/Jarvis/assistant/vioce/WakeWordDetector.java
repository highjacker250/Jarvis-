package com.jarvis.assistant.voice;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import com.jarvis.assistant.R;

import java.util.ArrayList;
import java.util.Locale;

public class WakeWordDetector {

    private Context context;
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private WakeWordListener listener;

    private String wakeWord = "jarvis";

    public interface WakeWordListener {
        void onWakeWordDetected();
    }

    public WakeWordDetector(Context context, WakeWordListener listener) {

        this.context = context;
        this.listener = listener;

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        recognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
        );

        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onResults(Bundle results) {

                ArrayList<String> matches =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null) {

                    for (String text : matches) {

                        if (text.toLowerCase().contains(wakeWord)) {

                            playWakeSound();

                            if (listener != null) {
                                listener.onWakeWordDetected();
                            }

                            break;
                        }
                    }
                }

                restartListening();
            }

            @Override
            public void onError(int error) {
                restartListening();
            }

            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }

    public void startListening() {
        speechRecognizer.startListening(recognizerIntent);
    }

    private void restartListening() {

        try {
            speechRecognizer.stopListening();
            speechRecognizer.startListening(recognizerIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopListening() {

        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    public void destroy() {

        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    private void playWakeSound() {

        try {

            MediaPlayer mp = MediaPlayer.create(context, R.raw.wake_sound);
            mp.start();

            mp.setOnCompletionListener(mediaPlayer -> {
                mediaPlayer.release();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}