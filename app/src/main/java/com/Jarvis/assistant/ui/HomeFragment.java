package com.jarvis.assistant.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.jarvis.assistant.R;

public class HomeFragment extends Fragment {

    private ImageButton micButton;
    private TextView statusText;

    public HomeFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        micButton = view.findViewById(R.id.micButton);
        statusText = view.findViewById(R.id.statusText);

        micButton.setOnClickListener(v -> {
            startListening();
        });

        return view;
    }

    private void startListening() {

        statusText.setText("Jarvis Listening...");

        // Yaha VoiceRecognizer start hoga
        // Example:
        // voiceRecognizer.startListening();

    }
}