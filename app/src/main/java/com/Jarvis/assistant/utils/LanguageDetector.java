package com.jarvis.assistant.utils;

public class LanguageDetector {

    public enum Language {
        HINDI,
        ENGLISH,
        BHOJPURI,
        UNKNOWN
    }

    public Language detectLanguage(String text) {

        if (text == null || text.isEmpty()) {
            return Language.UNKNOWN;
        }

        String input = text.toLowerCase();

        // Hindi keywords
        if (input.contains("karo") ||
            input.contains("chalu") ||
            input.contains("band") ||
            input.contains("kya") ||
            input.contains("kaun")) {

            return Language.HINDI;
        }

        // Bhojpuri keywords
        if (input.contains("ka") ||
            input.contains("ba") ||
            input.contains("ka ho") ||
            input.contains("khola") ||
            input.contains("band kara")) {

            return Language.BHOJPURI;
        }

        // English keywords
        if (input.contains("open") ||
            input.contains("turn on") ||
            input.contains("turn off") ||
            input.contains("call") ||
            input.contains("send")) {

            return Language.ENGLISH;
        }

        return Language.UNKNOWN;
    }
}