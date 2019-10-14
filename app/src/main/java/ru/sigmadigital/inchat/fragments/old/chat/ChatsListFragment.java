package ru.sigmadigital.inchat.fragments.old.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import ru.sigmadigital.inchat.adapters.ChatsListAdapter;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.ChatRequest;
import ru.sigmadigital.inchat.models.ChatResponse;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.IdResponse;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class ChatsListFragment  extends BaseFragment implements View.OnClickListener, ChatsListAdapter.OnChatItemClick {

    MyRetrofit.ServiceManager service;

    private EditText u1;
    private EditText u2;
    private EditText u3;

    ChatsListAdapter chatAdapter;

    RecyclerView recyclerView;

    public static ChatsListFragment newInstance() {
        return new ChatsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_chats_list_old, container, false);

        service = MyRetrofit.getInstance();

        v.findViewById(R.id.bt_createChat).setOnClickListener(this);

        u1 = v.findViewById(R.id.et_1);
        u2 = v.findViewById(R.id.et_2);
        u3 = v.findViewById(R.id.et_3);

        recyclerView = v.findViewById(R.id.rv_activity);
        chatAdapter = new ChatsListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
        recyclerView.setAdapter(chatAdapter);

        getChats();

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_createChat: {
                createChat();
                break;
            }


        }
    }

    private void createChat() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                int[] users;
                if (u3.getText().toString().equals("")){
                    users = new int[]{Integer.parseInt(u1.getText().toString()), Integer.parseInt(u2.getText().toString())};
                } else {
                    users = new int[]{Integer.parseInt(u1.getText().toString()), Integer.parseInt(u2.getText().toString()),Integer.parseInt(u3.getText().toString())};
                }
                Log.e("users", users.toString()+ " "+ users.length);

                ChatRequest chatRequest = new ChatRequest(null, users);
                service.createChat(SettingsHelper.getToken().getAccess(), chatRequest).enqueue(new Callback<IdResponse>() {
                    @Override
                    public void onResponse(Call<IdResponse> call, Response<IdResponse> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());

                            getChats();

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
                    public void onFailure(Call<IdResponse> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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
        if (chatResponses != null && chatResponses.size() > 0){
            Log.e("chatResponses" , chatResponses.size()+ "");
            chatAdapter.setItemList(chatResponses);
        }
    }


    @Override
    public void OnChatClick(ChatResponse orderResponse) {
        replaceCurrentFragmentWith(getFragmentManager(),
                R.id.fragment_container,
                ChatFragmentOld.newInstance(orderResponse),
                true);
    }
}
