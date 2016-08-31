package dk.fitfit.remotetexting.service;

import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.google.firebase.iid.FirebaseInstanceId;

import dk.fitfit.remotetexting.utils.SharedStorage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyInstanceIDListenerService extends InstanceIDListenerService {
    private static final String TAG = MyInstanceIDListenerService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String fcmToken) {
        String idToken = SharedStorage.load(getApplicationContext(), SharedStorage.STORAGE_KEY_ID_TOKEN);
        BackendService backendService = new BackendService();
        Call<ResponseBody> call = backendService.putFcmToken(fcmToken, idToken);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
