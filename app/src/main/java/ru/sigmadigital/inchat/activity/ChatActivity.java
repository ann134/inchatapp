package ru.sigmadigital.inchat.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.newW.chats.ChatFragment;
import ru.sigmadigital.inchat.models.ChatResponse;

public class ChatActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if(getIntent().getSerializableExtra("chatResponse") != null) {
            replaceCurrentFragmentWith(getSupportFragmentManager(), R.id.chat_container, ChatFragment.newInstance((ChatResponse) getIntent().getSerializableExtra("chatResponse")), false);
        }
    }
}
