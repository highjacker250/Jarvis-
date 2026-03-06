package com.jarvis.assistant.automation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import java.util.HashMap;
import java.util.Map;

public class AppLauncher {
    private Context context;
    private Map<String, String> appMap;

    public AppLauncher(Context context) {
        this.context = context;
        buildAppMap();
    }

    private void buildAppMap() {
        appMap = new HashMap<>();
        // English names
        appMap.put("whatsapp", "com.whatsapp");
        appMap.put("youtube", "com.google.android.youtube");
        appMap.put("chrome", "com.android.chrome");
        appMap.put("camera", "com.android.camera2");
        appMap.put("gallery", "com.android.gallery3d");
        appMap.put("maps", "com.google.android.apps.maps");
        appMap.put("google maps", "com.google.android.apps.maps");
        appMap.put("phone", "com.android.dialer");
        appMap.put("messages", "com.google.android.apps.messaging");
        appMap.put("calculator", "com.android.calculator2");
        appMap.put("calendar", "com.android.calendar");
        appMap.put("clock", "com.android.deskclock");
        appMap.put("contacts", "com.android.contacts");
        appMap.put("gmail", "com.google.android.gm");
        appMap.put("facebook", "com.facebook.katana");
        appMap.put("instagram", "com.instagram.android");
        appMap.put("twitter", "com.twitter.android");
        appMap.put("netflix", "com.netflix.mediaclient");
        appMap.put("spotify", "com.spotify.music");
        appMap.put("settings", "com.android.settings");
        appMap.put("playstore", "com.android.vending");
        appMap.put("play store", "com.android.vending");
        
        // Hindi names
        appMap.put("whatsapp kholo", "com.whatsapp");
        appMap.put("youtube kholo", "com.google.android.youtube");
        appMap.put("camera kholo", "com.android.camera2");
        appMap.put("nakshe", "com.google.android.apps.maps");
        appMap.put("calculator", "com.android.calculator2");
    }

    public boolean launchApp(String appName) {
        String lower = appName.toLowerCase().trim();
        
        // Check known app map
        String packageName = appMap.get(lower);
        if (packageName != null) {
            return launchPackage(packageName);
        }
        
        // Try fuzzy search installed apps
        PackageManager pm = context.getPackageManager();
        for (Map.Entry<String, String> entry : appMap.entrySet()) {
            if (lower.contains(entry.getKey()) || entry.getKey().contains(lower)) {
                return launchPackage(entry.getValue());
            }
        }
        
        // Last resort: search Play Store
        searchPlayStore(appName);
        return false;
    }

    private boolean launchPackage(String packageName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }
        } catch (Exception e) { /* not installed */ }
        return false;
    }

    public void searchGoogle(String query) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra("query", query);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void searchPlayStore(String appName) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
            Uri.parse("market://search?q=" + appName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}