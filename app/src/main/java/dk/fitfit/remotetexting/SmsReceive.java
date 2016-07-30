package dk.fitfit.remotetexting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;


public class SmsReceive extends BroadcastReceiver {
    private final String format = "3gpp";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Object[] pdus = (Object[])extras.get("pdus");
            String msg = "";
            for (Object smsMessage : pdus) {
                SmsMessage message;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    message = SmsMessage.createFromPdu((byte[]) smsMessage, format);
                } else {
                    message = SmsMessage.createFromPdu((byte[]) smsMessage);
                }
                msg += message.getMessageBody();
            }
            toast(context, msg);
        }
    }

    private void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
