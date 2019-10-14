package ru.sigmadigital.inchat.fragments.newW.chats;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.activity.ChatActivity;
import ru.sigmadigital.inchat.adapters.ChatsListAdapter;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.fragments.old.chat.ChatFragmentOld;
import ru.sigmadigital.inchat.models.ChatResponse;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class ChatList2Fragment extends BaseFragment implements View.OnClickListener, ChatsListAdapter.OnChatItemClick {

    private MyRetrofit.ServiceManager service;

    private ChatsListAdapter chatAdapter;

    public static ChatList2Fragment newInstance() {
        return new ChatList2Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_chat_list, container, false);

        service = MyRetrofit.getInstance();

        init(v);
        getChats();

        return v;
    }

    private void init (View v) {
        RecyclerView recyclerView = v.findViewById(R.id.recycler_chats);
        chatAdapter = new ChatsListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
        recyclerView.setAdapter(chatAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_createChat: {
                break;
            }
        }
    }

    private void getChats() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                service.getChats(SettingsHelper.getToken().getAccess()).enqueue(new Callback<List<ChatResponse>>() {

                    @Override
                    public void onResponse(Call<List<ChatResponse>> call, Response<List<ChatResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (ChatResponse i : response.body()) {
                                Log.e("onResponse", " " + i.toJson());
                            }
                            showChats(response.body());

                        } else if (response.errorBody() != null) {
                            try {
                                String responseError = response.errorBody().string();
                                Log.e("onResponseError", " " + responseError);
                                Error error = JsonParser.fromJson(responseError, Error.class);
                                if (error != null) {
                                    Log.e("Error", " " + error.getDescription() + " " + error.getCode());
                                    ErrorHandler.catchError(error, getActivity());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ChatResponse>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(App.getAppContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showChats(List<ChatResponse> chatResponses){
        if (chatResponses.size() > 0){
            chatAdapter.setItemList(chatResponses);
        }
    }

    @Override
    public void OnChatClick(ChatResponse chatResponse) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("chatResponse", chatResponse);
        startActivity(intent);

    }
}