package ru.sigmadigital.inchat.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.models.ChatFileDescriptionRequest;
import ru.sigmadigital.inchat.models.ChatMessageResponse;
import ru.sigmadigital.inchat.models.ChatMessagesFilter;
import ru.sigmadigital.inchat.models.ChatNewMessageRequest;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.IdResponse;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.PortfolioItemResponse;
import ru.sigmadigital.inchat.models.realm.ChatMessageRealm;

public class ChatProvider {

    public interface OnGetChatMessages {
        void OnChatMessages(List<ChatMessageRealm> chatMessages, boolean previous);
        void OnChatMessage(ChatMessageRealm chatMessage, boolean previous);
        void OnFileResponseId(long id);
    }


    //loadMessages
    public static void getChatMessages(long chatId, long lastMessageId, int count, boolean previous, OnGetChatMessages listener, Activity activity) {

        Log.e("getMessages", chatId + " " + lastMessageId + " " + count + " " + previous);

        List<ChatMessageRealm> listFromRealm = getFromRealm(chatId, lastMessageId, count, previous);

        if (listFromRealm != null && listFromRealm.size() > 0) {
            listener.OnChatMessages(listFromRealm, previous);
        } else {
            getFromServer(chatId, lastMessageId, count, previous, listener, activity);
        }
    }


    private static List<ChatMessageRealm> getFromRealm(long chatId, long lastMessageId, int count, boolean previous) {
        Realm realm = App.getRealm();
        if (realm != null) {

            Log.e("getFromRealm", chatId + " " + lastMessageId + " " + count + " " + previous);
            ChatMessageRealm lastMes = realm.where(ChatMessageRealm.class).equalTo("chatId", chatId).equalTo("id", lastMessageId).findFirst();
            RealmResults<ChatMessageRealm> results;
            List<ChatMessageRealm> list = new ArrayList<>();

            if (lastMes != null){
                if (previous){
                    results = realm.where(ChatMessageRealm.class).equalTo("chatId", chatId).lessThan("createdAtLong", lastMes.getCreatedAtLong()).findAll();
                    results = results.sort("createdAtLong", Sort.DESCENDING);

                } else {
                    results = realm.where(ChatMessageRealm.class).equalTo("chatId", chatId).greaterThan("createdAtLong", lastMes.getCreatedAtLong()).findAll();
                    results = results.sort("createdAtLong", Sort.ASCENDING);

                }
            } else {
                if (previous){
                    results = realm.where(ChatMessageRealm.class).equalTo("chatId", chatId).findAll();
                    results = results.sort("createdAtLong", Sort.DESCENDING);
                } else {
                    results = realm.where(ChatMessageRealm.class).equalTo("chatId", chatId).findAll();
                    results = results.sort("createdAtLong", Sort.ASCENDING);
                }
            }


            for (int i = 0; i < count; i++) {
                if (i < results.size())
                list.add(results.get(i));
            }


            //List<ChatMessageRealm> list = realm.copyFromRealm(results);
            Log.e("RealmResults", results.size() + " ");
            for (ChatMessageRealm i : list) {
                Log.e("ChatMessageRealm", " " + i.toJson());
            }
            return list;
        }

        return null;
    }


    private static void getFromServer(final long chatId, final long lastMessageId, final int count, final boolean previous, final OnGetChatMessages listener, final Activity activity) {
        ValidateToken.validate(activity, new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                ChatMessagesFilter chatMessagesFilter = new ChatMessagesFilter(lastMessageId, count, previous);
                MyRetrofit.getInstance().getMessages(SettingsHelper.getToken().getAccess(), chatId, chatMessagesFilter).enqueue(new Callback<List<ChatMessageResponse>>() {
                    @Override
                    public void onResponse(Call<List<ChatMessageResponse>> call, Response<List<ChatMessageResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            List<ChatMessageResponse> list = response.body();
                            if (previous) {
                                Collections.reverse(list);
                            }

                            for (ChatMessageResponse i : list) {
                                Log.e("onResponse", " " + i.toJson());
                            }


                            saveMessagesToRealm(list, chatId, previous, listener);
                            //listener.OnChatMessages(list, previous);

                        } else if (response.errorBody() != null) {
                            try {
                                String responseError = response.errorBody().string();
                                Log.e("onResponseError", " " + responseError);
                                Error error = JsonParser.fromJson(responseError, Error.class);
                                if (error != null) {
                                    Log.e("Error", " " + error.getDescription() + " " + error.getCode());
                                    ErrorHandler.catchError(error, activity);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ChatMessageResponse>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(App.getAppContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private static void saveMessagesToRealm(List<ChatMessageResponse> chatMessages, long chatId, boolean previous, final OnGetChatMessages listener) {
        Realm realm = App.getRealm();
        if (realm != null) {
            realm.beginTransaction();
            List<ChatMessageRealm> listRealm = new ArrayList<>();
            for (ChatMessageResponse messageResponse : chatMessages) {
                ChatMessageRealm messageRealm = new ChatMessageRealm(messageResponse, chatId);
                Log.e("messageRealm", messageRealm.getId() + " " + messageRealm.getCreatedAt() + " " + previous);
                listRealm.add(messageRealm);
                realm.insertOrUpdate(messageRealm);
            }
            realm.commitTransaction();
            listener.OnChatMessages(listRealm, previous);
        }
    }


    //new message
    public static void sendNewMessage(final Activity activity, final ChatNewMessageRequest chatNewMessageRequest, final long chatId, final OnGetChatMessages listener) {
        ValidateToken.validate(activity, new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                MyRetrofit.getInstance().newMessage(SettingsHelper.getToken().getAccess(), chatId, chatNewMessageRequest).enqueue(new Callback<ChatMessageResponse>() {
                    @Override
                    public void onResponse(Call<ChatMessageResponse> call, Response<ChatMessageResponse> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());

                            ChatProvider.saveMessageToRealm(response.body(), chatId, false, listener);


                        } else if (response.errorBody() != null) {
                            try {
                                String responseError = response.errorBody().string();
                                Log.e("onResponseError", " " + responseError);
                                Error error = JsonParser.fromJson(responseError, Error.class);
                                if (error != null) {
                                    Log.e("Error", " " + error.getDescription() + " " + error.getCode());
                                    ErrorHandler.catchError(error, activity);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatMessageResponse> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(App.getAppContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private static void saveMessageToRealm(ChatMessageResponse chatMessage, long chatId, boolean previous, final OnGetChatMessages listener) {
        Realm realm = App.getRealm();
        if (realm != null) {
            realm.beginTransaction();
            ChatMessageRealm messageRealm = new ChatMessageRealm(chatMessage, chatId);
            Log.e("messageRealm", messageRealm.getId() + " " + messageRealm.getCreatedAt() + " " + previous);
            realm.insertOrUpdate(messageRealm);
            realm.commitTransaction();
            listener.OnChatMessage(messageRealm, previous);
        }
    }


    //file
    public static void sendFile(final Activity activity, final File file, final long chatId, final OnGetChatMessages listener) {
        ValidateToken.validate(activity, new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                ChatFileDescriptionRequest d = new ChatFileDescriptionRequest(file.getName(), PortfolioItemResponse.FilesType.FILE);
                RequestBody description = RequestBody.create(MediaType.parse("application/json"), d.toJson());

                Log.e("body", body.headers().toString());
                Log.e("body", body.body().contentType().toString());

                MyRetrofit.getInstance().addFileChat(SettingsHelper.getToken().getAccess(), chatId, description, body).enqueue(new Callback<IdResponse>() {
                    @Override
                    public void onResponse(Call<IdResponse> call, Response<IdResponse> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());
                            listener.OnFileResponseId(response.body().getId());

                        } else if (response.errorBody() != null) {
                            try {
                                String responseError = response.errorBody().string();
                                Log.e("onResponseError", " " + responseError);
                                Error error = JsonParser.fromJson(responseError, Error.class);
                                if (error != null) {
                                    Log.e("Error", " " + error.getDescription() + " " + error.getCode());
                                    ErrorHandler.catchError(error, activity);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<IdResponse> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(App.getAppContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
