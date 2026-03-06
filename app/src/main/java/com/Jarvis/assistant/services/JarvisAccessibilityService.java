package com.jarvis.assistant.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class JarvisAccessibilityService extends AccessibilityService {
    private static final String TAG = "JarvisA11y";
    private static JarvisAccessibilityService instance;

    public static JarvisAccessibilityService getInstance() {
        return instance;
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        Log.d(TAG, "Accessibility Service connected!");

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
                | AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // We can monitor events here if needed
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        Log.d(TAG, "Accessibility service destroyed");
    }

    /**
     * Take a screenshot using Android's built-in screenshot action
     */
    public void performScreenshot() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
            Log.d(TAG, "Screenshot taken!");
        } else {
            Log.w(TAG, "Screenshot requires Android 9+");
        }
    }

    /**
     * Go back
     */
    public void pressBack() {
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    /**
     * Go home
     */
    public void pressHome() {
        performGlobalAction(GLOBAL_ACTION_HOME);
    }

    /**
     * Open recents / recent apps
     */
    public void pressRecents() {
        performGlobalAction(GLOBAL_ACTION_RECENTS);
    }

    /**
     * Pull down notification shade
     */
    public void openNotifications() {
        performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS);
    }

    /**
     * Open quick settings
     */
    public void openQuickSettings() {
        performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS);
    }
}
