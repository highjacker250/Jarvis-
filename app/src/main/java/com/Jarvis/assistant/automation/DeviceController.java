package com.jarvis.assistant.automation;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraAccessException;

public class DeviceController {

    private Context context;
    private WifiManager wifiManager;
    private BluetoothAdapter bluetoothAdapter;
    private CameraManager cameraManager;
    private String cameraId;

    public DeviceController(Context context) {

        this.context = context;

        wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // WiFi ON
    public void turnOnWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    // WiFi OFF
    public void turnOffWifi() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    // Bluetooth ON
    public void turnOnBluetooth() {
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
    }

    // Bluetooth OFF
    public void turnOffBluetooth() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }

    // Flashlight ON
    public void turnOnFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // Flashlight OFF
    public void turnOffFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}