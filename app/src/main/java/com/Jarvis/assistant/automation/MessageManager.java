package com.jarvis.assistant.automation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

public class MessageManager {

    private Context context;

    public MessageManager(Context context) {
        this.context = context;
    }

    // Direct SMS send karega
    public void sendSMS(String phoneNumber, String message) {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    // Messaging app open karega
    public void openMessagingApp(String phoneNumber, String message) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("sms:" + phoneNumber));
        intent.putExtra("sms_body", message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
}