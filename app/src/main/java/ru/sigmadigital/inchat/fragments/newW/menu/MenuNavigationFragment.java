package ru.sigmadigital.inchat.fragments.newW.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseNavigationFragment;

public class MenuNavigationFragment extends BaseNavigationFragment {

    public static MenuNavigationFragment newInstance() {
        return new MenuNavigationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigator_main, container, false);

        replaceCurrentFragmentWith(getFragmentManager(), R.id.main__nav_container, MenuMainFragment.newInstance(), false);

        return view;
    }
}
