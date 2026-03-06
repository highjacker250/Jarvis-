package com.jarvis.assistant.services;

import android.app.*;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.jarvis.assistant.JarvisApplication;
import com.jarvis.assistant.voice.VoiceRecognizer;

/**
 * Foreground service — keeps Jarvis alive even when screen is off
 * Wake word detection: "Hey Jarvis" / "Jarvis" / "Ok Jarvis"
 */
public class JarvisService extends Service {
    private static final String CHANNEL_ID = "jarvis_channel";
    private VoiceRecognizer wakeWordListener;
    private boolean listening = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForeground(1, buildNotification("Jarvis is ready..."));
        startWakeWordDetection();
        return START_STICKY; // Restart if killed
    }

    private void startWakeWordDetection() {
        wakeWordListener = new VoiceRecognizer(this);
        listenForWakeWord();
    }

    private void listenForWakeWord() {
        wakeWordListener.startListening(new VoiceRecognizer.VoiceCallback() {
            @Override
            public void onResult(String text) {
                String lower = text.toLowerCase();
                if (lower.contains("jarvis") || lower.contains("hey jarvis") || 
                    lower.contains("ok jarvis") || lower.contains("hello jarvis")) {
                    // Wake word detected! Launch main activity
                    Intent i = new Intent(JarvisService.this, 
                        com.jarvis.assistant.MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("auto_listen", true);
                    startActivity(i);
                    updateNotification("Listening...");
                } else {
                    // Continue listening
                    listenForWakeWord();
                }
            }
            @Override public void onError(String error) { listenForWakeWord(); }
            @Override public void onReadyForSpeech() {}
            @Override public void onEndOfSpeech() {}
        });
    }

    private Notification buildNotification(String text) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Jarvis AI")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();
    }

    private void updateNotification(String text) {
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.notify(1, buildNotification(text));
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID, "Jarvis AI", NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}