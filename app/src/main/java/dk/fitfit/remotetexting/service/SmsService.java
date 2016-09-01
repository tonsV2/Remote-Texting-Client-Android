package dk.fitfit.remotetexting.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import static dk.fitfit.remotetexting.MainActivity.TAG;

// TODO: See if the user has enabled reports... https://developer.android.com/reference/android/telephony/SmsManager.html#MMS_CONFIG_MMS_DELIVERY_REPORT_ENABLED
public class SmsService {
    private Context context;

    public SmsService(Context context) {
        this.context = context;
    }

    public void send(String to, String message) {
        Log.d(TAG, to + ": " + message);

        String callbackId = "hash of to and message";
        String intentAction = TAG + "-" + callbackId;  // callbackId is unique
        Intent intent = new Intent(intentAction);
        intent.putExtra("phoneNumber", to);
        intent.putExtra("callbackId", callbackId);
        intent.putExtra("message", message);

        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(to, null, message, sentIntent, null);
    }

    public void send(String messageId) {

    }
}
