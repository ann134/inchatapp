package ru.sigmadigital.inchat.fragments.newW.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.fragments.newW.chats.ChatsMainFragment;

public class NewsMainFragment extends BaseFragment {

    private MyRetrofit.ServiceManager service;

    public static NewsMainFragment newInstance() {
        return new NewsMainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main_news, container, false);

        service = MyRetrofit.getInstance();

        return v;
    }

}
