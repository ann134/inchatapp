package ru.sigmadigital.inchat.fragments.old;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.adapters.SubsFragmentAdapter;
import ru.sigmadigital.inchat.fragments.BaseFragment;

public class SubscriptionTabsFragment extends BaseFragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static SubscriptionTabsFragment newInstance(){
        return new SubscriptionTabsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_subscription_pager, null);
        viewPager = v.findViewById(R.id.view_pager);
        tabLayout = v.findViewById(R.id.tab_layout);

        SubsFragmentAdapter fragmentAdapter = new SubsFragmentAdapter(getChildFragmentManager());
        fragmentAdapter.addFragment(SubsRvFragment.newInstance(1),"Пользователи");
        fragmentAdapter.addFragment(SubsRvFragment.newInstance(2),"Подписки");
        fragmentAdapter.addFragment(SubsRvFragment.newInstance(3),"Подписчики");
        fragmentAdapter.addFragment(SubsRvFragment.newInstance(4),"Заявки");
        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return v;

    }

    @Override
    public void onStart() {
        super.onStart();


    }
}
