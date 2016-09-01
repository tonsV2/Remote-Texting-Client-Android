package dk.fitfit.remotetexting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import dk.fitfit.remotetexting.service.BackendService;
import dk.fitfit.remotetexting.utils.SharedStorage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "RemoteTextingClient";
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private SmsObserver smsObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureGoogleApiClient();
        silentSignin();
        registerSmsObserver();
        postFcmToken();
    }

    private void postFcmToken() {
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        String idToken = SharedStorage.load(this, SharedStorage.STORAGE_KEY_ID_TOKEN);
        BackendService backendService = new BackendService();
        Call<ResponseBody> call = backendService.putFcmToken(fcmToken, idToken);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                toast("FMC Token success!");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                toast("FMC Token ConnectionFailed!!!");
            }
        });
    }

    private void registerSmsObserver() {
        Handler handler = new Handler();
        smsObserver = new SmsObserver(handler, getApplicationContext());
        getContentResolver().registerContentObserver(SmsObserver.URI, true, smsObserver);
    }

    private void silentSignin() {
        // https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInApi#silentSignIn(com.google.android.gms.common.api.GoogleApiClient)
        OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (pendingResult.isDone()) {
            // There's immediate result available.
            updateUI(pendingResult.get());
        } else {
            // There's no immediate result ready, displays some progress indicator and waits for the
            // async callback.
//            showProgressIndicator();
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    updateUI(result);
//                    hideProgressIndicator();
                }
            });
        }
    }

    private void updateUI(GoogleSignInResult result) {
        View signInButton = findViewById(R.id.sign_in_button);
        View signedInTextView = findViewById(R.id.signed_in);
        if (result.isSuccess()) {
            signInButton.setVisibility(GONE);
            signedInTextView.setVisibility(VISIBLE);
            handleIdToken(result);
        } else {
            signedInTextView.setVisibility(GONE);
            signInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });
        }
    }

    private void configureGoogleApiClient() {
        InputStream inputStream = getResources().openRawResource(R.raw.client_secret);
        GoogleClientSecrets clientSecrets = null;
        try {
            clientSecrets = GoogleClientSecrets.load(new GsonFactory(), new InputStreamReader(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GoogleClientSecrets.Details details = clientSecrets.getDetails();

        String serverClientId = details.getClientId();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(serverClientId)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            handleIdToken(result);
        } else {
            toast("Sign in failed!!!");
        }
    }

    private void handleIdToken(GoogleSignInResult result) {
        GoogleSignInAccount acct = result.getSignInAccount();
        String idToken = acct.getIdToken();
        // TODO: Handle error... eg. empty idToken
        SharedStorage.save(this, SharedStorage.STORAGE_KEY_ID_TOKEN, idToken);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        toast("ConnectionFailed!!!");
    }
}
