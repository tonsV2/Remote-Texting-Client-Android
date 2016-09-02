package dk.fitfit.remotetexting.service;


import dk.fitfit.remotetexting.resource.MessageResource;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface RemoteTextingService {
    @POST("/api/messages")
    Call<ResponseBody> postMessage(@Body MessageResource message, @Query("idToken") String idToken);

    @PUT("/api/users/fcmToken")
    Call<ResponseBody> putFcmToken(@Query("fcmToken") String fcmToken, @Query("idToken") String idToken);

    @GET("/api/messages/{id}/msg")
    Call<MessageResource> getMessage(@Path("id") String id, @Query("idToken") String idToken);

    @PUT("/api/messages/{id}/sent/{ts}")
    Call<ResponseBody> messageSent(@Path("id") String id, @Path("ts") long ts, @Query("idToken") String idToken);

    @PUT("/api/messages/{id}/delivered/{ts}")
    Call<ResponseBody> messageDelivered(@Path("id") String id, @Path("ts") long ts, @Query("idToken") String idToken);
}
