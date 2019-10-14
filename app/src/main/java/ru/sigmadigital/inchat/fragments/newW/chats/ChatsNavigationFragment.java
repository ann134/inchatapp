package ru.sigmadigital.inchat.fragments.newW.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseNavigationFragment;

public class ChatsNavigationFragment extends BaseNavigationFragment {

    public static ChatsNavigationFragment newInstance() {
        return new ChatsNavigationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigator_main, container, false);

        replaceCurrentFragmentWith(getFragmentManager(), R.id.main__nav_container, ChatsMainFragment.newInstance(), false);

        return view;
    }

}
