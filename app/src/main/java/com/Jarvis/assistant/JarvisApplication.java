package com.jarvis.assistant;

import android.app.Application;
import com.jarvis.assistant.core.MemoryManager;
import com.jarvis.assistant.core.JarvisBrain;

public class JarvisApplication extends Application {
    private static JarvisApplication instance;
    public static JarvisBrain brain;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Initialize core AI brain
        brain = new JarvisBrain(this);
        MemoryManager.init(this);
    }

    public static JarvisApplication getInstance() {
        return instance;
    }
