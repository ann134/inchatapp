package ru.sigmadigital.inchat.fragments.old.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.adapters.ChatAdapterOld;
import ru.sigmadigital.inchat.api.FireService;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.ChatNewMessageRequest;
import ru.sigmadigital.inchat.models.ChatResponse;
import ru.sigmadigital.inchat.models.realm.ChatMessageRealm;
import ru.sigmadigital.inchat.utils.ChatProvider;
import ru.sigmadigital.inchat.api.MyRetrofit;

public class ChatFragmentOld extends BaseFragment implements View.OnClickListener, FilePickerFragment.OnFileSelected, ChatAdapterOld.OnFileChatClick, ChatProvider.OnGetChatMessages/*, ChatAdapterOld.OnFileChatClick */ {

    private MyRetrofit.ServiceManager service;
    private ChatResponse chatResponse;

    boolean previous = true;
    private int count = 3;

    private EditText editText;
    private ChatAdapterOld chatAdapter;
    private RecyclerView recyclerView;
    private LinearLayout filesContainer;

    //files
    private List<File> selectedFiles = new ArrayList<>();
    private List<Long> filesIds = new ArrayList<>();

    public static ChatFragmentOld newInstance(ChatResponse chatResponse) {
        ChatFragmentOld fragment = new ChatFragmentOld();
        fragment.setChatResponse(chatResponse);
        return fragment;
    }

    private void setChatResponse(ChatResponse chatResponse) {
        this.chatResponse = chatResponse;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_chat_old, container, false);

        service = MyRetrofit.getInstance();

        v.findViewById(R.id.send).setOnClickListener(this);
        v.findViewById(R.id.file).setOnClickListener(this);
        v.findViewById(R.id.next).setOnClickListener(this);
        v.findViewById(R.id.previous).setOnClickListener(this);
        editText = v.findViewById(R.id.edit_text);

        filesContainer = v.findViewById(R.id.files_container);
        for (File file : selectedFiles) {
            addChildFile(file);
        }
        recyclerView = v.findViewById(R.id.rv_activity);
        chatAdapter = new ChatAdapterOld(chatResponse.getId(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
        recyclerView.setAdapter(chatAdapter);


        subscrubeToFireServise();
        if(chatResponse.getLastMessage() != null){
            getMessages(chatResponse.getLastMessage().getId(), count, true);
        }



        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send: {
                sendMessage();
                break;
            }
            case R.id.file: {
                replaceCurrentFragmentWith(getFragmentManager(),
                        R.id.fragment_container,
                        FilePickerFragment.newInstance(this),
                        true);
                break;
            }
            case R.id.next: {
                getMessages(chatAdapter.getLastMessageDown(), count, false);
                break;
            }
            case R.id.previous: {
                getMessages(chatAdapter.getLastMessageUp(), count, true);
                break;
            }
        }
    }


    private void subscrubeToFireServise() {
        LiveData<String> liveData = FireService.getLiveData();
        liveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String responce) {
                if (responce != null) {
                    getMessages(-1, count, true);
                }
            }
        });
    }

    private void getMessages(long lastMessageId, int count, boolean previous) {
        ChatProvider.getChatMessages(chatResponse.getId(), lastMessageId, count, previous, this, getActivity());
    }

    @Override
    public void OnChatMessages(List<ChatMessageRealm> chatMessages, boolean previous) {
        chatAdapter.addItemList(chatMessages, previous);
        Log.e("OnChatMessages", chatMessages.size() + " " + previous);
    }

    @Override
    public void OnChatMessage(ChatMessageRealm chatMessage, boolean previous) {
        Log.e("OnNewChatMessage", " ");
        editText.setText("");
        filesIds.clear();
        chatAdapter.addItemList(chatMessage);
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
    }


    //send message
    private void sendMessage() {
        if (selectedFiles.size() > 0) {
            addFileChat();
        } else {
            newMessage();
        }
    }

    private void newMessage() {
        filesContainer.removeAllViews();
        ChatNewMessageRequest chatNewMessageRequest = new ChatNewMessageRequest(editText.getText().toString(), filesIds);
        Log.e("mess", chatNewMessageRequest.toJson());
        ChatProvider.sendNewMessage(getActivity(), chatNewMessageRequest, chatResponse.getId(), this);
    }



    //files picker
    @Override
    public void onFileSelected(File file) {
        selectedFiles.add(file);
        //addChildFile(file);
    }

    private void addChildFile(File file) {
        @SuppressLint("InflateParams") View childView = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_selected_file, null);
        TextView name = childView.findViewById(R.id.file_name);
        name.setText(file.getName());
        filesContainer.addView(childView);
        Log.e("childs", filesContainer.getChildCount() + "");
    }


    //send file
    private void addFileChat() {
        File file = selectedFiles.get(0);
        selectedFiles.remove(0);
        ChatProvider.sendFile(getActivity(), file, chatResponse.getId(), this);
    }

    @Override
    public void OnFileResponseId(long id) {
        filesIds.add(id);
        sendMessage();
    }


    //show file
    @Override
    public void OnFileClick(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Uri uri = FileProvider.getUriForFile(App.getAppContext(), App.getAppContext().getPackageName() + ".provider", file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, mimetype);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(mimetype, "Не получается обработать намерение!");
        }

//        Intent myIntent = new Intent(Intent.ACTION_VIEW);
////        myIntent.setData(Uri.fromFile(file));
//        myIntent.setData(FileProvider.getUriForFile(App.getAppContext(), App.getAppContext().getPackageName() + ".provider", file));
//        Intent j = Intent.createChooser(myIntent, "Choose an application to open with:");
//        j.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        startActivity(j);
    }

}
