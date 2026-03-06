package com.jarvis.assistant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.*;
import android.widget.*;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jarvis.assistant.core.JarvisBrain;
import com.jarvis.assistant.core.MemoryManager;
import com.jarvis.assistant.services.JarvisService;
import com.jarvis.assistant.voice.VoiceRecognizer;

public class MainActivity extends AppCompatActivity {

    private VoiceRecognizer voiceRecognizer;
    private TextView tvResponse, tvTranscript;
    private ImageButton btnMic;

    private boolean isListening = false;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private MemoryManager memoryManager;

    private static final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memoryManager = new MemoryManager(this);

        initViews();
        checkPermissions();
        startJarvisService();

        // Greeting
        String name = memoryManager.getMemory("user_name");
        if (name == null) name = "";

        String greeting = name.isEmpty() ?
                "Hello! How can I help you?" :
                "Hello " + name + "! Ready to assist!";

        tvResponse.setText(greeting);
    }

    private void initViews() {

        tvResponse = findViewById(R.id.tv_response);
        tvTranscript = findViewById(R.id.tv_transcript);
        btnMic = findViewById(R.id.btn_mic);

        voiceRecognizer = new VoiceRecognizer(this);

        btnMic.setOnClickListener(v -> {
            if (isListening) stopListening();
            else startListening();
        });

        // Settings button
        findViewById(R.id.btn_settings).setOnClickListener(v ->
                startActivity(new Intent(this, com.jarvis.assistant.ui.SettingsActivity.class))
        );
    }

    private void startListening() {

        isListening = true;

        btnMic.setImageResource(R.drawable.ic_mic_active);
        tvTranscript.setText("Listening...");

        voiceRecognizer.startListening(new VoiceRecognizer.VoiceCallback() {

            @Override
            public void onResult(String text) {

                isListening = false;

                mainHandler.post(() -> {

                    tvTranscript.setText("You said: " + text);
                    btnMic.setImageResource(R.drawable.ic_mic);
                    tvResponse.setText("Processing...");

                    processVoiceCommand(text);
                });
            }

            @Override
            public void onError(String error) {

                isListening = false;

                mainHandler.post(() -> {
                    tvTranscript.setText("Try again: " + error);
                    btnMic.setImageResource(R.drawable.ic_mic);
                });
            }

            @Override
            public void onReadyForSpeech() {
                mainHandler.post(() -> tvTranscript.setText("🎙️ Speak now..."));
            }

            @Override
            public void onEndOfSpeech() {
                mainHandler.post(() -> tvTranscript.setText("Processing..."));
            }
        });
    }

    private void stopListening() {

        isListening = false;
        voiceRecognizer.stopListening();

        btnMic.setImageResource(R.drawable.ic_mic);
        tvTranscript.setText("Tap mic to speak");
    }

    private void processVoiceCommand(String text) {

        JarvisBrain brain = JarvisApplication.brain;

        if (brain == null) {
            tvResponse.setText("Jarvis not initialized");
            return;
        }

        brain.processInput(text, new JarvisBrain.BrainCallback() {

            @Override
            public void onResponse(String response) {

                mainHandler.post(() -> tvResponse.setText(response));

                memoryManager.saveMemory("last_response", response);
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> tvResponse.setText("Error: " + error));
            }
        });
    }

    private void checkPermissions() {

        for (String permission : PERMISSIONS) {

            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, PERMISSIONS, 100);
                return;
            }
        }
    }

    private void startJarvisService() {

        Intent serviceIntent = new Intent(this, JarvisService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
}