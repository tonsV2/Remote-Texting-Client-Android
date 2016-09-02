package dk.fitfit.remotetexting.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import static dk.fitfit.remotetexting.MainActivity.TAG;

// TODO: See if the user has enabled reports... https://developer.android.com/reference/android/telephony/SmsManager.html#MMS_CONFIG_MMS_DELIVERY_REPORT_ENABLED
public class SmsService {
    private Context context;

    public SmsService(Context context) {
        this.context = context;
    }

    public void send(String messageId) {

        String callbackId = "hash of to and message";
        String SENT_ACTION = "SENT" + callbackId;
        String DELIVERED_ACTION = "DELIVERED" + callbackId;

        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(SENT_ACTION), 0);
        PendingIntent deliveryIntent = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED_ACTION), 0);

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "SMS sent intent received.");
            }
        }, new IntentFilter(SENT_ACTION));

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "SMS delivered intent received.");
            }
        }, new IntentFilter(DELIVERED_ACTION));

        SmsManager smsManager = SmsManager.getDefault();
//        smsManager.sendTextMessage(to, null, message, sentIntent, deliveryIntent);
    }
}
