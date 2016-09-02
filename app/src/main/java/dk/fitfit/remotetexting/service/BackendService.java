package dk.fitfit.remotetexting.service;

import dk.fitfit.remotetexting.resource.MessageResource;
import dk.fitfit.remotetexting.resource.PhoneNumberResource;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// TODO: do not return call objects!!! Retrofit specific stuff should be contained within this service
public class BackendService {
    private static final String TAG = BackendService.class.getSimpleName();
    private final String baseUrl = "http://192.168.0.18:8080";
    private final RemoteTextingService service;

    public BackendService() {
        // TODO: Only log if we are in debug mode
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RemoteTextingService.class);
    }

    public Call<ResponseBody> postMessage(String sender, String recipient, String content, long timestampProvider, long timestampReceived, String idToken) {
        MessageResource messageResource = new MessageResource();
        messageResource.setFrom(new PhoneNumberResource(sender));
        messageResource.setTo(new PhoneNumberResource(recipient));
        messageResource.setContent(content);
        messageResource.setTimestampProvider(timestampProvider);
        messageResource.setTimestampReceived(timestampReceived);
        return service.postMessage(messageResource, idToken);
    }

    public Call<ResponseBody> putFcmToken(String fcmToken, String idToken) {
        return service.putFcmToken(fcmToken, idToken);
    }

    public Call<MessageResource> getMessage(String id, String idToken) {
        return service.getMessage(id, idToken);
    }
}
