package com.jarvis.assistant.automation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppLauncher {
    private static final String TAG = "AppLauncher";
    private final Context context;
    private final Map<String, String> appMap = new HashMap<>();

    public AppLauncher(Context context) {
        this.context = context;
        buildAppMap();
    }

    private void buildAppMap() {
        // English names
        appMap.put("whatsapp", "com.whatsapp");
        appMap.put("youtube", "com.google.android.youtube");
        appMap.put("chrome", "com.android.chrome");
        appMap.put("google chrome", "com.android.chrome");
        appMap.put("camera", "com.android.camera2");
        appMap.put("gallery", "com.google.android.apps.photos");
        appMap.put("photos", "com.google.android.apps.photos");
        appMap.put("maps", "com.google.android.apps.maps");
        appMap.put("google maps", "com.google.android.apps.maps");
        appMap.put("phone", "com.google.android.dialer");
        appMap.put("dialer", "com.google.android.dialer");
        appMap.put("messages", "com.google.android.apps.messaging");
        appMap.put("sms", "com.google.android.apps.messaging");
        appMap.put("calculator", "com.google.android.calculator");
        appMap.put("calendar", "com.google.android.calendar");
        appMap.put("clock", "com.google.android.deskclock");
        appMap.put("contacts", "com.google.android.contacts");
        appMap.put("gmail", "com.google.android.gm");
        appMap.put("email", "com.google.android.gm");
        appMap.put("facebook", "com.facebook.katana");
        appMap.put("instagram", "com.instagram.android");
        appMap.put("twitter", "com.twitter.android");
        appMap.put("x", "com.twitter.android");
        appMap.put("netflix", "com.netflix.mediaclient");
        appMap.put("spotify", "com.spotify.music");
        appMap.put("amazon", "com.amazon.mShop.android.shopping");
        appMap.put("flipkart", "com.flipkart.android");
        appMap.put("paytm", "net.one97.paytm");
        appMap.put("gpay", "com.google.android.apps.nbu.paisa.user");
        appMap.put("google pay", "com.google.android.apps.nbu.paisa.user");
        appMap.put("phonepe", "com.phonepe.app");
        appMap.put("settings", "com.android.settings");
        appMap.put("play store", "com.android.vending");
        appMap.put("playstore", "com.android.vending");
        appMap.put("drive", "com.google.android.apps.docs");
        appMap.put("google drive", "com.google.android.apps.docs");
        appMap.put("docs", "com.google.android.apps.docs.editors.docs");
        appMap.put("sheets", "com.google.android.apps.docs.editors.sheets");
        appMap.put("meet", "com.google.android.apps.tachyon");
        appMap.put("google meet", "com.google.android.apps.tachyon");
        appMap.put("zoom", "us.zoom.videomeetings");
        appMap.put("telegram", "org.telegram.messenger");
        appMap.put("snapchat", "com.snapchat.android");
        appMap.put("linkedin", "com.linkedin.android");
        appMap.put("hotstar", "in.startv.hotstar");
        appMap.put("disney", "in.startv.hotstar");
        appMap.put("myntra", "com.myntra.android");
        appMap.put("swiggy", "in.swiggy.android");
        appMap.put("zomato", "com.application.zomato");
        appMap.put("uber", "com.ubercab");
        appMap.put("ola", "com.olacabs.customer");
        appMap.put("chrome browser", "com.android.chrome");
        appMap.put("firefox", "org.mozilla.firefox");
        appMap.put("brave", "com.brave.browser");

        // Hindi names
        appMap.put("whatsapp kholo", "com.whatsapp");
        appMap.put("youtube kholo", "com.google.android.youtube");
        appMap.put("nakshe", "com.google.android.apps.maps");
        appMap.put("hisab", "com.google.android.calculator");
        appMap.put("sandesh", "com.google.android.apps.messaging");
        appMap.put("galary", "com.google.android.apps.photos");
    }

    public boolean launchApp(String appName) {
        if (appName == null || appName.trim().isEmpty()) return false;
        String lower = appName.toLowerCase().trim();

        // Direct map lookup
        String pkg = appMap.get(lower);
        if (pkg != null) return launchPackage(pkg);

        // Partial match in our map
        for (Map.Entry<String, String> entry : appMap.entrySet()) {
            if (lower.contains(entry.getKey()) || entry.getKey().contains(lower)) {
                if (launchPackage(entry.getValue())) return true;
            }
        }

        // Search installed apps by label
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo app : apps) {
            String label = pm.getApplicationLabel(app).toString().toLowerCase();
            if (label.contains(lower) || lower.contains(label)) {
                if (launchPackage(app.packageName)) return true;
            }
        }

        return false;
    }

    private boolean launchPackage(String packageName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Log.d(TAG, "Launched: " + packageName);
                return true;
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to launch " + packageName + ": " + e.getMessage());
        }
        return false;
    }

    public void searchGoogle(String query) {
        try {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra("query", query);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            // Fallback: open browser directly
            openUrl("https://www.google.com/search?q=" + Uri.encode(query));
        }
    }

    public void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "URL open error: " + e.getMessage());
        }
    }

    public void openSettings() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
