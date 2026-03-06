package com.jarvis.assistant.automation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class CallManager {

    private Context context;

    public CallManager(Context context) {
        this.context = context;
    }

    // Direct phone number se call
    public void callNumber(String phoneNumber) {

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    // Dialer open karega (safer method)
    public void openDialer(String phoneNumber) {

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
}