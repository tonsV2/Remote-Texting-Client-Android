package dk.fitfit.remotetexting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import dk.fitfit.remotetexting.service.BackendService;
import dk.fitfit.remotetexting.utils.SharedStorage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SmsReceive extends BroadcastReceiver implements Callback<ResponseBody> {
    private final String TAG = this.getClass().getName();
    private final String format = "3gpp";
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Object[] pdus = (Object[]) extras.get("pdus");
            // https://android.googlesource.com/platform/frameworks/opt/telephony/+/master/src/java/com/android/internal/telephony/SmsMessageBase.java#171
            long timestampProvider = 0;
            long timestampReceived = System.currentTimeMillis();
            String sender = "";
            String content = "";
            if (pdus != null) {
                for (Object smsMessage : pdus) {
                    SmsMessage message;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        message = SmsMessage.createFromPdu((byte[]) smsMessage, format);
                    } else {
                        message = SmsMessage.createFromPdu((byte[]) smsMessage);
                    }
                    timestampProvider = message.getTimestampMillis();
                    sender = message.getOriginatingAddress();
                    content += message.getMessageBody();
                }
            } else {
                // TODO
            }
            Log.d(TAG, String.format("From: %s", sender));
            Log.d(TAG, String.format("Content: %s", content));
            Log.d(TAG, String.format("timestampProvider: %s", timestampProvider));
            Log.d(TAG, String.format("timestampReceived: %s", timestampReceived));
            // TODO: Ensure idToken is valid... if backendService.postMessage fails
            String idToken = SharedStorage.load(context, SharedStorage.STORAGE_KEY_ID_TOKEN);
            Log.d(TAG, String.format("idToken: %s", idToken));

            BackendService backendService = new BackendService();
            Call<ResponseBody> call = backendService.postMessage(sender, null, content, timestampProvider, timestampReceived, idToken);
            call.enqueue(this);
        }
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        int code = response.code();
        if (code == 401) {
            // YODOHandle unauthorized
        }
        toast("Response.code: " + code);
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        // TODO: handle conditions such as unavailable network
        t.printStackTrace();
    }

    private void toast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
