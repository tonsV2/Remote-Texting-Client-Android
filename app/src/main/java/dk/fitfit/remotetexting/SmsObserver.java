package dk.fitfit.remotetexting;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import dk.fitfit.remotetexting.service.BackendService;
import dk.fitfit.remotetexting.utils.SharedStorage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// Inspiration: https://stackoverflow.com/questions/19219401/detecting-outgoing-sms-android
// https://stackoverflow.com/questions/5787894/android-is-there-any-way-to-listen-outgoing-sms
// https://stackoverflow.com/questions/17938726/access-sms-sent-message
class SmsObserver extends ContentObserver implements Callback<ResponseBody> {
    private final String TAG = this.getClass().getName();
    static final Uri URI = Uri.parse("content://sms/sent");
    private final String MESSAGE_TYPE_SENT = "2";
    private Context context;

    SmsObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        toast("onChange");
        Cursor cursor = context.getContentResolver().query(URI, null, null, null, null);
        String address = null;
        String body = null;
        long date = 0;
        if (cursor != null && cursor.moveToFirst()) {
            try {
                // TODO: See what types we are actually getting instead of just using getString
                String type = cursor.getString(cursor.getColumnIndex("type"));
                toast("type: " + type);
                if (type.equals(MESSAGE_TYPE_SENT)) {
                    address = cursor.getString(cursor.getColumnIndex("address"));
                    body = cursor.getString(cursor.getColumnIndex("body"));
                    toast("body: " + body);
                    date = cursor.getLong(cursor.getColumnIndex("date"));
                }
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(TAG, "SMSHelper: outgoingSMS", e);
            } finally {
                cursor.close();
            }
            Log.i(TAG, address);
            Log.i(TAG, body);
            // TODO: lookup sms by hash(address, body, date)
            // if not found
            // post to server
            // else return

            String idToken = SharedStorage.load(context, SharedStorage.STORAGE_KEY_ID_TOKEN);
            BackendService backendService = new BackendService();
            Call<ResponseBody> call = backendService.postMessage(null, address, body, date, 0, idToken);
            call.enqueue(this);
        }
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        int code = response.code();
        if (code == 401) {
            // TODO: Handle unauthorized
        }
        toast("Response.code: " + code);

    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }

    private void toast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
