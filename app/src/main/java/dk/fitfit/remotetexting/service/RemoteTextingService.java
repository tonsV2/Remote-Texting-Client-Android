package dk.fitfit.remotetexting.service;


import dk.fitfit.remotetexting.resource.MessageResource;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

interface RemoteTextingService {
    @POST("/api/messages")
    Call<ResponseBody> postMessage(@Body MessageResource message, @Query("idToken") String idToken);

    @PUT("/api/users/fcmToken")
    Call<ResponseBody> putFcmToken(@Query("fcmToken") String fcmToken, @Query("idToken") String idToken);
}
