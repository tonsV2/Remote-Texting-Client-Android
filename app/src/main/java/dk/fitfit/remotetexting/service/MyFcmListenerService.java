package dk.fitfit.remotetexting.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class MyFcmListenerService extends FirebaseMessagingService {
    private static final String TAG = MyFcmListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage message) {
//        String from = message.getFrom();
        Map<String, String> data = message.getData();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            Log.i(TAG, entry.getKey() + ": " + entry.getValue());
        }
    }
}
