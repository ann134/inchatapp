package ru.sigmadigital.inchat.fragments.newW.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.adapters.TabAdapter;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.fragments.old.chat.ChatsListFragment;
import ru.sigmadigital.inchat.api.MyRetrofit;

public class ChatsMainFragment extends BaseFragment {

    private MyRetrofit.ServiceManager service;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static ChatsMainFragment newInstance() {
        return new ChatsMainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main_chats, container, false);

        service = MyRetrofit.getInstance();

        viewPager = v.findViewById(R.id.view_pager);
        tabLayout = v.findViewById(R.id.tab_layout);

        TabAdapter adapter = new TabAdapter(getChildFragmentManager());

        adapter.addFragment(ChatList2Fragment.newInstance(), "рабочие");
        adapter.addFragment(ChatList2Fragment.newInstance(), "личные");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return v;
    }
}
