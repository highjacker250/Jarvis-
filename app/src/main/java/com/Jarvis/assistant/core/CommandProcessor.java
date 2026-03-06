package com.jarvis.assistant.core;

import android.content.Context;
import com.jarvis.assistant.automation.*;

public class CommandProcessor {
    private Context context;
    private AppLauncher appLauncher;
    private CallManager callManager;
    private MessageManager messageManager;
    private SystemController systemController;

    public CommandProcessor(Context context) {
        this.context = context;
        this.appLauncher = new AppLauncher(context);
        this.callManager = new CallManager(context);
        this.messageManager = new MessageManager(context);
        this.systemController = new SystemController(context);
    }

    /**
     * Pattern-based fast command matching
     * Supports: English, Hindi, Bhojpuri
     */
    public String tryLocalCommand(String input, String lang) {
        String lower = input.toLowerCase().trim();

        // ===== APP LAUNCH =====
        if (matches(lower, "open ", "kholo ", "chalu kar ", "chalao ")) {
            String appName = extractAppName(lower);
            boolean success = appLauncher.launchApp(appName);
            if (success) {
                return lang.equals("hindi") ? appName + " khol diya!" : "Opening " + appName;
            }
        }

        // ===== CALL =====
        if (matches(lower, "call ", "phone kar ", "call karo ", "ring karo ")) {
            String contact = extractContact(lower);
            callManager.makeCall(contact);
            return lang.equals("hindi") ? contact + " ko call kar raha hoon" : "Calling " + contact;
        }

        // ===== SMS =====
        if (matches(lower, "message ", "sms ", "message bhejo ", "message karo ")) {
            // Extract contact and message
            messageManager.promptAndSend(lower);
            return lang.equals("hindi") ? "Message bhej raha hoon" : "Sending message...";
        }

        // ===== WIFI =====
        if (matches(lower, "wifi on", "wifi chalu", "wifi band karo off", "wifi off", 
                          "wifi band", "turn on wifi", "turn off wifi")) {
            boolean turnOn = !lower.contains("off") && !lower.contains("band");
            systemController.setWifi(turnOn);
            return lang.equals("hindi") ? 
                "WiFi " + (turnOn ? "chalu" : "band") + " kar diya" : 
                "WiFi " + (turnOn ? "enabled" : "disabled");
        }

        // ===== BLUETOOTH =====
        if (matches(lower, "bluetooth on", "bluetooth off", "bluetooth chalu", "bluetooth band")) {
            boolean turnOn = lower.contains("on") || lower.contains("chalu");
            systemController.setBluetooth(turnOn);
            return "Bluetooth " + (turnOn ? "on" : "off") + " kar diya";
        }

        // ===== FLASHLIGHT =====
        if (matches(lower, "torch on", "flashlight on", "torch chalu", "light on",
                          "torch off", "flashlight off", "torch band")) {
            boolean on = lower.contains("on") || lower.contains("chalu");
            systemController.setFlashlight(on);
            return "Torch " + (on ? "chalu" : "band") + " kar diya!";
        }

        // ===== BRIGHTNESS =====
        if (lower.contains("brightness") || lower.contains("roshan")) {
            if (lower.contains("max") || lower.contains("full") || lower.contains("zyada")) {
                systemController.setBrightness(255);
                return "Brightness maximum kar diya!";
            } else if (lower.contains("min") || lower.contains("low") || lower.contains("kam")) {
                systemController.setBrightness(50);
                return "Brightness kam kar diya!";
            }
        }

        // ===== VOLUME =====
        if (lower.contains("volume") || lower.contains("awaaz")) {
            if (lower.contains("up") || lower.contains("badhao") || lower.contains("zyada")) {
                systemController.adjustVolume(true);
                return "Volume badha diya!";
            } else if (lower.contains("down") || lower.contains("kam") || lower.contains("ghata")) {
                systemController.adjustVolume(false);
                return "Volume ghata diya!";
            } else if (lower.contains("mute") || lower.contains("silent") || lower.contains("band")) {
                systemController.setMute(true);
                return "Phone silent kar diya!";
            }
        }

        // ===== TIME/DATE =====
        if (matches(lower, "time", "kitne baje", "time batao", "kya time hai")) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a", 
                java.util.Locale.getDefault());
            String time = sdf.format(new java.util.Date());
            return lang.equals("hindi") ? "Abhi " + time + " baj rahe hain" : "Current time is " + time;
        }

        if (matches(lower, "date", "aaj ki date", "kya date hai", "today")) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMMM yyyy", 
                java.util.Locale.getDefault());
            String date = sdf.format(new java.util.Date());
            return lang.equals("hindi") ? "Aaj ki taareekh hai " + date : "Today's date is " + date;
        }

        // ===== BATTERY =====
        if (matches(lower, "battery", "charge kitna", "battery kitni")) {
            int battery = systemController.getBatteryLevel();
            return lang.equals("hindi") ? 
                "Battery " + battery + " percent hai" : 
                "Battery is at " + battery + "%";
        }

        // ===== SCREENSHOT =====
        if (matches(lower, "screenshot", "screen capture", "screenshot lo")) {
            systemController.takeScreenshot();
            return "Screenshot le liya!";
        }

        // ===== REMINDER =====
        if (matches(lower, "remind me", "reminder", "yaad dilao", "alarm")) {
            // Pass to reminder parser
            return null; // Let AI handle complex reminder parsing
        }

        // ===== SEARCH =====
        if (matches(lower, "search", "google", "dhundo", "kya hai")) {
            String query = extractSearchQuery(lower);
            appLauncher.searchGoogle(query);
            return lang.equals("hindi") ? 
                query + " search kar raha hoon" : "Searching for " + query;
        }

        // ===== SETTINGS =====
        if (matches(lower, "settings", "setting", "setting kholo")) {
            appLauncher.openSettings();
            return "Settings khol diya!";
        }

        // Not a local command
        return null;
    }

    private boolean matches(String input, String... patterns) {
        for (String pattern : patterns) {
            if (input.contains(pattern)) return true;
        }
        return false;
    }

    private String extractAppName(String input) {
        String[] prefixes = {"open ", "kholo ", "chalu kar ", "chalao "};
        for (String prefix : prefixes) {
            if (input.contains(prefix)) {
                return input.substring(input.indexOf(prefix) + prefix.length()).trim();
            }
        }
        return input;
    }

    private String extractContact(String input) {
        String[] prefixes = {"call ", "phone kar ", "call karo ", "ring karo "};
        for (String prefix : prefixes) {
            if (input.contains(prefix)) {
                return input.substring(input.indexOf(prefix) + prefix.length()).trim();
            }
        }
        return input;
    }

    private String extractSearchQuery(String input) {
        String[] prefixes = {"search for ", "search ", "google ", "dhundo "};
        for (String prefix : prefixes) {
            if (input.contains(prefix)) {
                return input.substring(input.indexOf(prefix) + prefix.length()).trim();
            }
        }
        return input;
    }
}