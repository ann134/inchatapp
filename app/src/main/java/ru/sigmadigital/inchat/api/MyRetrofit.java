package ru.sigmadigital.inchat.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import ru.sigmadigital.inchat.models.Category;
import ru.sigmadigital.inchat.models.ChatMessagesFilter;
import ru.sigmadigital.inchat.models.ChatMessageResponse;
import ru.sigmadigital.inchat.models.ChatRequest;
import ru.sigmadigital.inchat.models.ChatResponse;
import ru.sigmadigital.inchat.models.Done;
import ru.sigmadigital.inchat.models.IdResponse;
import ru.sigmadigital.inchat.models.Location;
import ru.sigmadigital.inchat.models.ChatNewMessageRequest;
import ru.sigmadigital.inchat.models.OrderAcceptRequest;
import ru.sigmadigital.inchat.models.OrderAcceptResponse;
import ru.sigmadigital.inchat.models.OrderFilterRequest;
import ru.sigmadigital.inchat.models.OrderMessageRequest;
import ru.sigmadigital.inchat.models.OrderRequest;
import ru.sigmadigital.inchat.models.OrderResponse;
import ru.sigmadigital.inchat.models.PortfolioItemRequestUpdate;
import ru.sigmadigital.inchat.models.PortfolioItemResponse;
import ru.sigmadigital.inchat.models.RateRequest;
import ru.sigmadigital.inchat.models.RateResponse;
import ru.sigmadigital.inchat.models.Registration;
import ru.sigmadigital.inchat.models.RestorePassword;
import ru.sigmadigital.inchat.models.RestorePasswordRestore;
import ru.sigmadigital.inchat.models.Token;
import ru.sigmadigital.inchat.models.UserRequest;
import ru.sigmadigital.inchat.models.UserResponse;
import ru.sigmadigital.inchat.models.UsersFilter;

public class MyRetrofit {

    private static ServiceManager service;
    public static String BASE_URL = "http://192.168.2.102:8090/api/";
//    public static String BASE_URL = "http://185.87.50.80:8090/api/";
    //public static String BASE_URL = "http://192.168.0.102:8090/api/";

    public static ServiceManager getInstance() {

        if (service == null) {
            Retrofit.Builder builder = new Retrofit.Builder();
            builder.baseUrl(BASE_URL);

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            builder.addConverterFactory(GsonConverterFactory.create(gson));

            Retrofit retrofit = builder.build();
            service = retrofit.create(ServiceManager.class);
        }
        return service;
    }


    public interface ServiceManager {

        //token
        @POST("registration")
        Call<Token> registration(@Body Registration body);

        @GET("token/refresh")
        @Headers("Content-Type: application/json")
        Call<Token> refresh(@Header("refresh") String refresh);


        //password
        @POST("restorepassword")
        Call<Done> restorePassword(@Body RestorePassword body);

        @POST("restorepassword/restore")
        Call<Done> restorePasswordRestore(@Body RestorePasswordRestore body);


        //users
        @GET("users/me")
        Call<UserResponse> usersMe(@Header("token") String token);

        @GET("users/{id}")
        Call<UserResponse> usersId(@Path("id") long id, @Header("token") String token);

        @POST("users")
        Call<List<UserResponse>> getUsers(@Header("token") String token, @Body UsersFilter body);

        @POST("users/update")
        Call<Done> update(@Header("token") String token, @Body UserRequest body);

        @POST("users/update/photo")
        @Streaming
        @Headers("Content-Type: image/*")
        Call<Done> uploadUserPhoto(@Header("token") String token, @Body RequestBody body);

        @POST("users/update/location")
        Call<Done> location(@Header("token") String token, @Body Location body);


        //portfolio
        @GET("users/me/portfolio")
        Call<List<PortfolioItemResponse>> portfolioMe(@Header("token") String token);

        @GET("users/{id}/portfolio")
        Call<List<PortfolioItemResponse>> portfolioId(@Path("id") long id, @Header("token") String token);

        @POST("users/update/portfolio")
        @Multipart
        Call<IdResponse> updatePortfolio(@Header("token") String token, @Part("description") RequestBody description, @Part MultipartBody.Part file);

        @POST("users/update/portfolio/{id}")
        Call<Done> updatePortfolioItem(@Path("id") long id, @Header("token") String token, @Body PortfolioItemRequestUpdate body);

        @GET("users/update/portfolio/{id}/delete")
        Call<Done> deletePortfolioItem(@Path("id") long id, @Header("token") String token);

        @GET("users/portfolio/{id}/file")
        Call<ResponseBody> getFilePortfolio(@Header("token") String token, @Path("id") long id);


        //stickerphoto
        @GET("users/me/stickerphoto")
        Call<List<IdResponse>> stickerPhotoMe(@Header("token") String token);

        @GET("users/{id}/stickerphoto")
        Call<List<IdResponse>> stickerPhotoId(@Path("id") long id, @Header("token") String token);

        @POST("users/update/stickerphoto")
        @Streaming
        @Headers("Content-Type: image/*")
        Call<IdResponse> uploadStickerPhoto(@Header("token") String token, @Body RequestBody body);

        @GET("users/update/stickerphoto/{id}/delete")
        Call<Done> deleteStickerPhoto(@Path("id") long id, @Header("token") String token);


        //orders
        @GET("orders/categories")
        Call<List<Category>> getOrdersCategories(@Header("token") String token);

        @POST("orders")
        Call<List<OrderResponse>> getOrders(@Header("token") String token, @Body OrderFilterRequest orderFilterRequest);

        @GET("orders/{id}")
        Call<OrderResponse> getOrderId(@Header("token") String token, @Path("id") long id);

        @POST("orders/add")
        Call<IdResponse> addOrder(@Header("token") String token, @Body OrderRequest orderRequest);

        @POST("orders/update/{id}")
        Call<Done> updateOrder(@Header("token") String token, @Path("id") long id, @Body OrderRequest orderRequest);

        @GET("orders/update/{id}/delete")
        Call<Done> deleteOrder(@Header("token") String token, @Path("id") long id);

        @POST("orders/update/{id}/addfile")
        @Multipart
        Call<IdResponse> addFileOrder(@Header("token") String token, @Path("id") long id, @Part("description") RequestBody description, @Part MultipartBody.Part file);

        @GET("orders/update/{id}/deletefile/{fileId}")
        Call<Done> deleteFileOrder(@Header("token") String token, @Path("id") long id, @Path("fileId") long fileId);

        @GET("orders/me")
        Call<List<OrderResponse>> getOrdersMe(@Header("token") String token);

        @GET("orders/{id}/files/{fileId}")
        @Streaming
        Call<ResponseBody> getFileOrder(@Header("token") String token, @Path("id") long id, @Path("fileId") long fileId);

        @GET("orders/{id}/files/archive")
        @Streaming
        Call<ResponseBody> getAllOrderFilesAsArchive(@Header("token") String token, @Path("id") long id);


        //accept
        @POST("orders/{id}/accept")
        Call<Done> acceptOrder(@Header("token") String token, @Path("id") long id, @Body OrderAcceptRequest orderAcceptRequest);

        @GET("orders/{id}/accepts")
        Call<List<OrderAcceptResponse>> getOrdersAccepts(@Header("token") String token, @Path("id") long id);

        @POST("orders/{id}/message")
        Call<Done> messageOrder(@Header("token") String token, @Path("id") long id, @Body OrderMessageRequest orderMessageRequest);

        @POST("orders/{id}/message")
        Call<Done> messageOrder(@Header("token") String token, @Path("id") long id, @Body OrderMessageRequest orderMessageRequest, @Query("acceptId") long acceptId);

        @GET("orders/{id}/accept/{userId}")
        Call<Done> markAccepter(@Header("token") String token, @Path("id") long id, @Path("userId") int userId);

        @GET("orders/{id}/close")
        Call<Done> close(@Header("token") String token, @Path("id") long id);

        @POST("orders/{id}/rate")
        Call<Done> sendRate(@Header("token") String token, @Path("id") long id, @Body RateRequest rateRequest);


        //reviews
        @GET("users/me/reviews")
        Call<List<RateResponse>> reviewsMe(@Header("token") String token);

        @GET("users/{id}/reviews")
        Call<List<RateResponse>> reviewsId(@Header("token") String token, @Path("id") long id);


        //subscribe
        @GET("users/{id}/subscribe")
        Call<Done> subscribeToUser(@Header("token") String token, @Path("id") long id);

        @GET("users/me/subscription/requests")
        Call<List<UserResponse>> getMySubscriptionsRequests(@Header("token") String token);

        @GET("users/me/subscriptions")
        Call<List<UserResponse>> getMySubscriptions(@Header("token") String token);

        @GET("users/me/subscriptions/wait")
        Call<List<UserResponse>> getMySubscriptionsWait(@Header("token") String token);

        @GET("users/me/subscribes")
        Call<List<UserResponse>> getMySubscribes(@Header("token") String token);

        @GET("users/{id}/subscribe/accept")
        Call<Done> acceptSubscribe(@Header("token") String token, @Path("id") long id);

        @GET("users/{id}/subscribe/reject")
        Call<Done> rejectSubscribe(@Header("token") String token, @Path("id") long id);


        //chats
        @GET("chats")
        Call<List<ChatResponse>> getChats(@Header("token") String token);

        @POST("chats/create")
        Call<IdResponse> createChat(@Header("token") String token, @Body ChatRequest chatRequest);

        @POST("chats/{id}")
        Call<List<ChatMessageResponse>> getMessages(@Header("token") String token, @Path("id") long id, @Body ChatMessagesFilter chatMessagesFilter);

        @POST("chats/{id}/newmessage")
        Call<ChatMessageResponse> newMessage(@Header("token") String token, @Path("id") long id, @Body ChatNewMessageRequest chatNewMessageRequest);

        @POST("chats/{id}/newfile")
        @Multipart
        Call<IdResponse> addFileChat(@Header("token") String token, @Path("id") long id, @Part("description") RequestBody description, @Part MultipartBody.Part file);

        @GET("chats/{id}/files/{fileId}")
        @Streaming
        Call<ResponseBody> getFileChat(@Header("token") String token, @Path("id") long id, @Path("fileId") int fileId);


    }
}
