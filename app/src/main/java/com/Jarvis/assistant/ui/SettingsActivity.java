package com.jarvis.assistant.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.jarvis.assistant.JarvisApplication;
import com.jarvis.assistant.R;
import com.jarvis.assistant.core.MemoryManager;

public class SettingsActivity extends AppCompatActivity {

    private EditText etName, etAssistantName, etApiKey;
    private RadioGroup rgGender, rgPersonality;
    private SeekBar sbSpeechRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        etName = findViewById(R.id.et_user_name);
        etAssistantName = findViewById(R.id.et_assistant_name);
        etApiKey = findViewById(R.id.et_api_key);
        rgGender = findViewById(R.id.rg_voice_gender);
        rgPersonality = findViewById(R.id.rg_personality);
        sbSpeechRate = findViewById(R.id.sb_speech_rate);

        loadCurrentSettings();

        // Toolbar back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Jarvis Settings");
        }

        findViewById(R.id.btn_save_settings).setOnClickListener(v -> saveSettings());
        findViewById(R.id.btn_clear_memory).setOnClickListener(v -> clearMemory());
        findViewById(R.id.btn_clear_face).setOnClickListener(v -> clearFace());
        findViewById(R.id.btn_reset_setup).setOnClickListener(v -> resetSetup());
    }

    private void loadCurrentSettings() {
        SharedPreferences prefs = getSharedPreferences("jarvis_settings", MODE_PRIVATE);

        // User name
        etName.setText(MemoryManager.getUserName());

        // Assistant name
        etAssistantName.setText(prefs.getString("assistant_name", "Jarvis"));

        // API Key (masked)
        String key = prefs.getString("openrouter_key", "");
        if (!key.isEmpty()) {
            etApiKey.setHint("Key saved (tap to change)");
        }

        // Voice gender
        String gender = prefs.getString("voice_gender", "male");
        if ("female".equals(gender)) {
            ((RadioButton) rgGender.getChildAt(1)).setChecked(true);
        } else {
            ((RadioButton) rgGender.getChildAt(0)).setChecked(true);
        }

        // Personality
        String pers = prefs.getString("personality", "professional");
        switch (pers) {
            case "casual": ((RadioButton) rgPersonality.getChildAt(1)).setChecked(true); break;
            case "funny":  ((RadioButton) rgPersonality.getChildAt(2)).setChecked(true); break;
            default:       ((RadioButton) rgPersonality.getChildAt(0)).setChecked(true);
        }
    }

    private void saveSettings() {
        String userName = etName.getText().toString().trim();
        String assistantName = etAssistantName.getText().toString().trim();
        String apiKey = etApiKey.getText().toString().trim();

        if (assistantName.isEmpty()) assistantName = "Jarvis";

        // Save user name
        if (!userName.isEmpty()) MemoryManager.setUserName(userName);

        // Save API key
        if (!apiKey.isEmpty()) {
            getSharedPreferences("jarvis_settings", MODE_PRIVATE)
                    .edit().putString("openrouter_key", apiKey).apply();
        }

        // Gender
        int genderId = rgGender.getCheckedRadioButtonId();
        String gender = (genderId == R.id.rb_female) ? "female" : "male";

        // Personality
        int persId = rgPersonality.getCheckedRadioButtonId();
        String personality = "professional";
        if (persId == R.id.rb_casual) personality = "casual";
        else if (persId == R.id.rb_funny) personality = "funny";

        // Apply to brain
        JarvisApplication.brain.updateSettings(assistantName, gender, personality);

        Toast.makeText(this, "Settings saved! ✓", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void clearMemory() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Memory Clear?")
                .setMessage("Sari conversation history delete ho jayegi. Sure ho?")
                .setPositiveButton("Haan, delete karo", (d, w) -> {
                    MemoryManager.clearHistory();
                    Toast.makeText(this, "Memory cleared!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearFace() {
        MemoryManager.clearFaceData();
        Toast.makeText(this, "Face data cleared!", Toast.LENGTH_SHORT).show();
    }

    private void resetSetup() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Full Reset?")
                .setMessage("Sab kuch reset ho jayega including naam, settings aur history.")
                .setPositiveButton("Reset karo", (d, w) -> {
                    MemoryManager.clearHistory();
                    MemoryManager.clearFaceData();
                    getSharedPreferences("jarvis_settings", MODE_PRIVATE).edit().clear().apply();
                    getSharedPreferences("jarvis_memory", MODE_PRIVATE).edit().clear().apply();
                    Toast.makeText(this, "Reset complete! App restart karein.", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
