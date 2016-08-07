package dk.fitfit.remotetexting.service;

import java.io.IOException;

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

    public Call<ResponseBody> postIdToken(String idToken) throws IOException {
        return service.postIdToken(idToken);
    }

    public Call<ResponseBody> postMessage(String sender, String content, long timestampProvider, long timestampReceived, String idToken) {
        PhoneNumberResource phoneNumberResource = new PhoneNumberResource();
        phoneNumberResource.setNumber(sender);
        MessageResource messageResource = new MessageResource();
        messageResource.setFrom(phoneNumberResource);
        messageResource.setContent(content);
        messageResource.setTimestampProvider(timestampProvider);
        messageResource.setTimestampReceived(timestampReceived);
        return service.postMessage(messageResource, idToken);
    }
}
