package com.jarvis.assistant.automation;

import android.bluetooth.BluetoothAdapter;
import android.content.*;
import android.hardware.camera2.*;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.provider.Settings;
import android.util.Log;
import com.jarvis.assistant.services.JarvisAccessibilityService;

public class SystemController {
    private static final String TAG = "SystemCtrl";
    private final Context context;
    private CameraManager cameraManager;
    private String cameraId;

    public SystemController(Context context) {
        this.context = context;
        try {
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            if (cameraManager != null) {
                String[] ids = cameraManager.getCameraIdList();
                if (ids.length > 0) cameraId = ids[0];
            }
        } catch (Exception e) {
            Log.e(TAG, "Camera init error: " + e.getMessage());
        }
    }

    public void setWifi(boolean enable) {
        try {
            WifiManager wifi = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                wifi.setWifiEnabled(enable);
            }
        } catch (Exception e) {
            Log.e(TAG, "WiFi error: " + e.getMessage());
        }
    }

    public void setBluetooth(boolean enable) {
        try {
            BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
            if (bt != null) {
                if (enable) bt.enable();
                else bt.disable();
            }
        } catch (Exception e) {
            Log.e(TAG, "BT error: " + e.getMessage());
        }
    }

    public void setFlashlight(boolean on) {
        try {
            if (cameraManager != null && cameraId != null) {
                cameraManager.setTorchMode(cameraId, on);
            }
        } catch (Exception e) {
            Log.e(TAG, "Torch error: " + e.getMessage());
        }
    }

    public void setBrightness(int value) {
        try {
            // Must have WRITE_SETTINGS permission
            if (Settings.System.canWrite(context)) {
                Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, value);
                // Also set manual mode
                Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Exception e) {
            Log.e(TAG, "Brightness error: " + e.getMessage());
        }
    }

    public void adjustVolume(boolean increase) {
        try {
            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audio != null) {
                audio.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        increase ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_SHOW_UI
                );
            }
        } catch (Exception e) {
            Log.e(TAG, "Volume error: " + e.getMessage());
        }
    }

    public void setMute(boolean mute) {
        try {
            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audio != null) {
                audio.setRingerMode(mute
                        ? AudioManager.RINGER_MODE_SILENT
                        : AudioManager.RINGER_MODE_NORMAL);
            }
        } catch (Exception e) {
            Log.e(TAG, "Mute error: " + e.getMessage());
        }
    }

    public int getBatteryLevel() {
        try {
            Intent batteryIntent = context.registerReceiver(null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (batteryIntent != null) {
                int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (level >= 0 && scale > 0) return (int) ((level / (float) scale) * 100);
            }
        } catch (Exception e) {
            Log.e(TAG, "Battery error: " + e.getMessage());
        }
        return -1;
    }

    public boolean isCharging() {
        try {
            Intent batteryIntent = context.registerReceiver(null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (batteryIntent != null) {
                int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                return status == BatteryManager.BATTERY_STATUS_CHARGING
                        || status == BatteryManager.BATTERY_STATUS_FULL;
            }
        } catch (Exception e) {
            Log.e(TAG, "Charging check error: " + e.getMessage());
        }
        return false;
    }

    public boolean isInternetConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo info = cm.getActiveNetworkInfo();
                return info != null && info.isConnected();
            }
        } catch (Exception e) {
            Log.e(TAG, "Network check error: " + e.getMessage());
        }
        return false;
    }

    public void takeScreenshot() {
        // Uses Accessibility Service
        JarvisAccessibilityService svc = JarvisAccessibilityService.getInstance();
        if (svc != null) {
            svc.performScreenshot();
        }
    }

    /**
     * Execute device action string (from AI response)
     */
    public void execute(String action) {
        if (action == null) return;
        switch (action.toUpperCase().trim()) {
            case "WIFI_ON":        setWifi(true);        break;
            case "WIFI_OFF":       setWifi(false);       break;
            case "BLUETOOTH_ON":   setBluetooth(true);   break;
            case "BLUETOOTH_OFF":  setBluetooth(false);  break;
            case "FLASHLIGHT_ON":  setFlashlight(true);  break;
            case "FLASHLIGHT_OFF": setFlashlight(false); break;
            case "VOLUME_UP":      adjustVolume(true);   break;
            case "VOLUME_DOWN":    adjustVolume(false);  break;
            case "MUTE":           setMute(true);        break;
            case "UNMUTE":         setMute(false);       break;
            case "SCREENSHOT":     takeScreenshot();      break;
            case "BRIGHTNESS_MAX": setBrightness(255);   break;
            case "BRIGHTNESS_MIN": setBrightness(60);    break;
            default:
                Log.w(TAG, "Unknown action: " + action);
        }
    }
}
