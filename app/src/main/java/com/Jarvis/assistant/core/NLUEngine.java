package com.jarvis.assistant.core;

import java.util.Locale;

public class NLUEngine {

    public String processCommand(String input) {

        if (input == null) {
            return "UNKNOWN";
        }

        String command = input.toLowerCase(Locale.ROOT);

        // Open YouTube
        if (command.contains("youtube")) {
            return "OPEN_YOUTUBE";
        }

        // Open WhatsApp
        if (command.contains("whatsapp")) {
            return "OPEN_WHATSAPP";
        }

        // WiFi On
        if (command.contains("wifi on") || command.contains("wifi chalu") || command.contains("wifi chalao")) {
            return "TURN_ON_WIFI";
        }

        // WiFi Off
        if (command.contains("wifi off") || command.contains("wifi band") || command.contains("wifi bandh")) {
            return "TURN_OFF_WIFI";
        }

        // Flashlight On
        if (command.contains("flashlight on") || command.contains("torch on") || command.contains("torch chalu")) {
            return "FLASH_ON";
        }

        // Flashlight Off
        if (command.contains("flashlight off") || command.contains("torch off") || command.contains("torch band")) {
            return "FLASH_OFF";
        }

        // Call command
        if (command.contains("call")) {
            return "CALL_CONTACT";
        }

        // Message command
        if (command.contains("message")) {
            return "SEND_MESSAGE";
        }

        // Internet search
        if (command.contains("search") || command.contains("kya hai") || command.contains("kaun hai")) {
            return "INTERNET_SEARCH";
        }

        return "UNKNOWN";
    }
}