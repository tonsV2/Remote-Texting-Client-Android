package dk.fitfit.remotetexting.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static dk.fitfit.remotetexting.MainActivity.TAG;


public class MyFcmListenerService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Log.d(TAG, "FCM From: " + from);
        Map<String, String> data = message.getData();
//        for (Map.Entry<String, String> entry : data.entrySet()) {
//            Log.d(TAG, entry.getKey() + ": " + entry.getValue());
//        }

        switch (data.get("command")) {
            case "sendMessage":
                SmsService smsService = new SmsService(getApplicationContext());
                // Get to and content from server by messageId
                smsService.send(data.get("messageId"));
                smsService.send(data.get("to"), data.get("message"));
                break;
        }
    }
}
