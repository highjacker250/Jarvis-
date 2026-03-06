package com.jarvis.assistant.automation;

import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.view.WindowManager;
import android.app.Activity;

public class SystemController {

    private Context context;
    private AudioManager audioManager;

    public SystemController(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    // Volume Up
    public void increaseVolume() {
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    // Volume Down
    public void decreaseVolume() {
        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }

    // Mute Volume
    public void muteVolume() {
        audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI);
    }

    // Set Screen Brightness (0 - 255)
    public void setBrightness(Activity activity, int brightness) {

        WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
        layoutParams.screenBrightness = brightness / 255f;
        activity.getWindow().setAttributes(layoutParams);
    }

    // Auto Brightness On
    public void enableAutoBrightness() {

        Settings.System.putInt(
                context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        );
    }

    // Auto Brightness Off
    public void disableAutoBrightness() {

        Settings.System.putInt(
                context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        );
    }
}