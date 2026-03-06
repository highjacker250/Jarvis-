package com.jarvis.assistant.ui;

import android.content.Intent;
import android.os.*;
import android.view.animation.*;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.jarvis.assistant.MainActivity;
import com.jarvis.assistant.R;
import com.jarvis.assistant.core.MemoryManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tvName = findViewById(R.id.tv_app_name);
        TextView tvTagline = findViewById(R.id.tv_tagline);
        LottieAnimationView lottie = findViewById(R.id.lottie_splash);

        // Animate text in
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeIn.setDuration(1000);
        tvName.startAnimation(fadeIn);
        tvTagline.startAnimation(fadeIn);

        // Navigate after 2.5 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (MemoryManager.isFirstRun()) {
                intent = new Intent(this, SetupActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2500);
    }
}
