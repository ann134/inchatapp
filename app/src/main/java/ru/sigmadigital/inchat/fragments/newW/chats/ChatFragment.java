package ru.sigmadigital.inchat.fragments.newW.chats;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.adapters.ChatAdapter;
import ru.sigmadigital.inchat.api.FireService;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.ChatMessageResponse;
import ru.sigmadigital.inchat.models.ChatMessagesFilter;
import ru.sigmadigital.inchat.models.ChatNewMessageRequest;
import ru.sigmadigital.inchat.models.ChatResponse;
import ru.sigmadigital.inchat.utils.DpPxUtil;
import ru.sigmadigital.inchat.utils.RecyclerViewMargins;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class ChatFragment extends BaseFragment {

    private ChatResponse currChat;

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private ImageView attachedFiles;
    private ImageView sendMessage;
    private EditText message;

    public static ChatFragment newInstance(ChatResponse chatResponse) {
        ChatFragment fragment = new ChatFragment();
        fragment.currChat = chatResponse;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, null);
        recyclerView = v.findViewById(R.id.rv_chat);
        attachedFiles = v.findViewById(R.id.imv_attache);
        sendMessage = v.findViewById(R.id.imv_send_message);
        message = v.findViewById(R.id.et_message);

        LinearLayoutManager manager = new LinearLayoutManager(App.getAppContext());
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new RecyclerViewMargins(DpPxUtil.getPixelsFromDp(4)));
        adapter = new ChatAdapter();
        recyclerView.setAdapter(adapter);


        if (currChat.getLastMessage() != null) {
            loadFirstMessagesPageFromServer(currChat.getId(), currChat.getLastMessage().getId(), 20, true);

        } else {
            loadFirstMessagesPageFromServer(currChat.getId(), -1, 20, true);

        }

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!message.getText().toString().equals("")) {
                    ChatNewMessageRequest request = new ChatNewMessageRequest(message.getText().toString(), null);
                    sendNewMessage(request, currChat.getId());
                    message.setText("");
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(!recyclerView.canScrollVertically(-1)){
                    if(adapter.getItemList().size() > 0) {
                        loadMessagesPageFromServer(currChat.getId(), adapter.getItemList().get(adapter.getItemList().size() - 1).getId(), 20, true);

                    }
                }
            }
        });

        FireService.getLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                getLastMessageFromServer(currChat.getId());
            }
        });

        return v;
    }

    private void loadFirstMessagesPageFromServer(final long chatId, final long lastMessageId, final int count, final boolean previous) {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                ChatMessagesFilter chatMessagesFilter = new ChatMessagesFilter(lastMessageId, count, previous);
                MyRetrofit.getInstance().getMessages(SettingsHelper.getToken().getAccess(), chatId, chatMessagesFilter).enqueue(new Callback<List<ChatMessageResponse>>() {
                    @Override
                    public void onResponse(Call<List<ChatMessageResponse>> call, Response<List<ChatMessageResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            adapter.setItemList(response.body());
                            adapter.addNewMessage(currChat.getLastMessage());
                            adapter.notifyDataSetChanged();
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

    private void loadMessagesPageFromServer(final long chatId, final long lastMessageId, final int count, final boolean previous) {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                ChatMessagesFilter chatMessagesFilter = new ChatMessagesFilter(lastMessageId, count, previous);
                MyRetrofit.getInstance().getMessages(SettingsHelper.getToken().getAccess(), chatId, chatMessagesFilter).enqueue(new Callback<List<ChatMessageResponse>>() {
                    @Override
                    public void onResponse(Call<List<ChatMessageResponse>> call, Response<List<ChatMessageResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            adapter.addPageOfMessage(response.body());
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

    private void getLastMessageFromServer(final long chatId) {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                ChatMessagesFilter chatMessagesFilter = new ChatMessagesFilter(-1, 1, true);
                MyRetrofit.getInstance().getMessages(SettingsHelper.getToken().getAccess(), chatId, chatMessagesFilter).enqueue(new Callback<List<ChatMessageResponse>>() {
                    @Override
                    public void onResponse(Call<List<ChatMessageResponse>> call, Response<List<ChatMessageResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null && response.body().size() > 0) {
                            adapter.addNewMessage(response.body().get(0));
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

    public void sendNewMessage(final ChatNewMessageRequest chatNewMessageRequest, final long chatId) {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                MyRetrofit.getInstance().newMessage(SettingsHelper.getToken().getAccess(), chatId, chatNewMessageRequest).enqueue(new Callback<ChatMessageResponse>() {
                    @Override
                    public void onResponse(Call<ChatMessageResponse> call, Response<ChatMessageResponse> response) {
                        Log.e("onResponseCode", response.code() + " ");
                        if (response.body() != null) {
                            adapter.addNewMessage(response.body());
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

}
