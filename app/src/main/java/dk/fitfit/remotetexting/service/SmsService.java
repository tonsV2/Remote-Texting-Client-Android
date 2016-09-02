package dk.fitfit.remotetexting.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import dk.fitfit.remotetexting.resource.MessageResource;
import dk.fitfit.remotetexting.utils.SharedStorage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static dk.fitfit.remotetexting.MainActivity.TAG;

// TODO: See if the user has enabled reports... https://developer.android.com/reference/android/telephony/SmsManager.html#MMS_CONFIG_MMS_DELIVERY_REPORT_ENABLED
public class SmsService implements Callback<MessageResource> {
    private final Context context;
    private final BackendService backendService;
    private String messageId;

    public SmsService(Context context) {
        this.context = context;
        backendService = new BackendService();
    }

    public void send(String messageId) {
        this.messageId = messageId;
        String idToken = SharedStorage.load(context, SharedStorage.STORAGE_KEY_ID_TOKEN);
        Call<MessageResource> call = backendService.getMessage(messageId, idToken);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<MessageResource> call, Response<MessageResource> response) {
        MessageResource messageResource = response.body();
        String to = messageResource.getTo().getNumber();
        String message = messageResource.getContent();

        String callbackId = messageId;
        String SENT_ACTION = "SENT" + callbackId;
        String DELIVERED_ACTION = "DELIVERED" + callbackId;

        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(SENT_ACTION), 0);
        PendingIntent deliveryIntent = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED_ACTION), 0);

        // TODO: Better error handling http://stackoverflow.com/a/8222244/672009
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "SMS sent intent received(" + messageId + ")");
            }
        }, new IntentFilter(SENT_ACTION));

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "SMS delivered intent received(" + messageId + ")");
            }
        }, new IntentFilter(DELIVERED_ACTION));

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(to, null, message, sentIntent, deliveryIntent);
    }

    @Override
    public void onFailure(Call<MessageResource> call, Throwable t) {

    }
}
