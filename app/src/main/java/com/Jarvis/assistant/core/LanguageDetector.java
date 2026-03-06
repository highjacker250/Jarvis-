package com.jarvis.assistant.core;

public class LanguageDetector {

    public static String detect(String text){

        text = text.toLowerCase();

        if(text.matches(".*[अ-ह].*"))
            return "hindi";

        return "english";
    }
}
