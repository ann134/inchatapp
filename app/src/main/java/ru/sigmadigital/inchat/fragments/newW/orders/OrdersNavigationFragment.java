package ru.sigmadigital.inchat.fragments.newW.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseNavigationFragment;
import ru.sigmadigital.inchat.utils.SettingsHelper;

public class OrdersNavigationFragment  extends BaseNavigationFragment {

    public static OrdersNavigationFragment newInstance() {
        return new OrdersNavigationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigator_main, container, false);

        replaceCurrentFragmentWith(getFragmentManager(), R.id.main__nav_container, OrdersMainFragment.newInstance(), false);

        return view;
    }
}
