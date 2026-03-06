package com.jarvis.assistant.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.jarvis.assistant.JarvisApplication;
import com.jarvis.assistant.MainActivity;
import com.jarvis.assistant.R;
import com.jarvis.assistant.core.MemoryManager;

public class SetupActivity extends AppCompatActivity {
    private int currentStep = 0;
    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        showStep(0);
    }

    private void showStep(int step) {
        LinearLayout container = findViewById(R.id.setup_container);
        container.removeAllViews();
        currentStep = step;

        switch (step) {
            case 0: showWelcomeStep(container); break;
            case 1: showNameStep(container); break;
            case 2: showApiKeyStep(container); break;
            case 3: showPersonalityStep(container); break;
            case 4: finishSetup(); break;
        }
    }

    private void showWelcomeStep(LinearLayout container) {
        TextView title = makeTitle("🤖 Jarvis AI mein aapka swagat hai!");
        TextView desc = makeDesc("Main aapka personal AI assistant hoon.\nHindi, English, aur Bhojpuri mein baat kar sakte hain.\n\nChalo setup karte hain!");
        Button btn = makeButton("Shuru Karein →", v -> showStep(1));
        container.addView(title);
        container.addView(desc);
        container.addView(btn);
    }

    private void showNameStep(LinearLayout container) {
        TextView title = makeTitle("Aapka naam kya hai?");
        TextView desc = makeDesc("Main aapko is naam se yaad rakhunga.");
        EditText etName = new EditText(this);
        etName.setHint("Apna naam likhein (e.g. Rahul)");
        etName.setTextSize(18);
        etName.setPadding(16, 16, 16, 16);
        etName.setTextColor(0xFFFFFFFF);
        etName.setHintTextColor(0x88FFFFFF);
        Button btn = makeButton("Aage →", v -> {
            userName = etName.getText().toString().trim();
            if (!userName.isEmpty()) {
                MemoryManager.setUserName(userName);
                showStep(2);
            } else {
                Toast.makeText(this, "Kripya naam likhein", Toast.LENGTH_SHORT).show();
            }
        });
        Button skip = makeSkipButton("Skip", v -> showStep(2));
        container.addView(title);
        container.addView(desc);
        container.addView(etName);
        container.addView(btn);
        container.addView(skip);
    }

    private void showApiKeyStep(LinearLayout container) {
        TextView title = makeTitle("AI Key (Optional)");
        TextView desc = makeDesc("Behtar AI responses ke liye OpenRouter.ai se FREE key lo:\n\n" +
                "1. openrouter.ai par jaao\n" +
                "2. Free signup karo\n" +
                "3. API key copy karo\n" +
                "4. Neeche paste karo\n\n" +
                "Key ke bina bhi kaam karega (limited).");
        EditText etKey = new EditText(this);
        etKey.setHint("sk-or-... (optional)");
        etKey.setTextSize(14);
        etKey.setPadding(16, 16, 16, 16);
        etKey.setTextColor(0xFFFFFFFF);
        etKey.setHintTextColor(0x88FFFFFF);
        Button btn = makeButton("Save & Aage →", v -> {
            String key = etKey.getText().toString().trim();
            if (!key.isEmpty()) {
                getSharedPreferences("jarvis_settings", MODE_PRIVATE)
                        .edit().putString("openrouter_key", key).apply();
                JarvisApplication.brain.processInput("__RELOAD_KEY__", null);
            }
            showStep(3);
        });
        Button skip = makeSkipButton("Skip karo", v -> showStep(3));
        container.addView(title);
        container.addView(desc);
        container.addView(etKey);
        container.addView(btn);
        container.addView(skip);
    }

    private void showPersonalityStep(LinearLayout container) {
        TextView title = makeTitle("Jarvis ki personality?");
        TextView desc = makeDesc("Aap kaise conversation prefer karte hain?");

        String[] options = {"Professional (Default)", "Friendly & Casual", "Funny & Witty"};
        String[] values = {"professional", "casual", "funny"};
        final String[] selected = {values[0]};

        RadioGroup rg = new RadioGroup(this);
        rg.setPadding(16, 16, 16, 16);
        for (int i = 0; i < options.length; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(options[i]);
            rb.setTextColor(0xFFFFFFFF);
            rb.setTextSize(16);
            final String val = values[i];
            rb.setOnClickListener(v -> selected[0] = val);
            if (i == 0) rb.setChecked(true);
            rg.addView(rb);
        }

        Button btn = makeButton("Setup Complete! ✓", v -> {
            getSharedPreferences("jarvis_settings", MODE_PRIVATE)
                    .edit().putString("personality", selected[0]).apply();
            showStep(4);
        });
        container.addView(title);
        container.addView(desc);
        container.addView(rg);
        container.addView(btn);
    }

    private void finishSetup() {
        MemoryManager.setFirstRunDone();
        Toast.makeText(this, "Setup complete! Jarvis ready hai!", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // ===== UI HELPERS =====
    private TextView makeTitle(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(22);
        tv.setTextColor(0xFF00D4FF);
        tv.setPadding(16, 32, 16, 16);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        return tv;
    }

    private TextView makeDesc(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(15);
        tv.setTextColor(0xCCFFFFFF);
        tv.setPadding(16, 8, 16, 24);
        tv.setLineSpacing(8, 1.0f);
        return tv;
    }

    private Button makeButton(String label, android.view.View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(label);
        btn.setOnClickListener(listener);
        btn.setBackgroundColor(0xFF00D4FF);
        btn.setTextColor(0xFF000000);
        btn.setTextSize(16);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 16, 16, 8);
        btn.setLayoutParams(params);
        return btn;
    }

    private Button makeSkipButton(String label, android.view.View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(label);
        btn.setOnClickListener(listener);
        btn.setBackgroundColor(0x33FFFFFF);
        btn.setTextColor(0x99FFFFFF);
        btn.setTextSize(14);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 0, 16, 8);
        btn.setLayoutParams(params);
        return btn;
    }
}
