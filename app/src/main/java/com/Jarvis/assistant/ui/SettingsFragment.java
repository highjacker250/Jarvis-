package com.jarvis.assistant.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.SharedPreferences;
import android.widget.Switch;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.jarvis.assistant.R;

public class SettingsFragment extends Fragment {

    private Switch voiceSwitch;
    private Spinner languageSpinner;
    private SharedPreferences preferences;

    public SettingsFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        voiceSwitch = view.findViewById(R.id.voiceSwitch);
        languageSpinner = view.findViewById(R.id.languageSpinner);

        preferences = requireActivity()
                .getSharedPreferences("JarvisSettings", getContext().MODE_PRIVATE);

        loadSettings();

        voiceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("voice_enabled", isChecked).apply();
        });

        return view;
    }

    private void loadSettings() {

        boolean voiceEnabled = preferences.getBoolean("voice_enabled", true);

        voiceSwitch.setChecked(voiceEnabled);
    }
}