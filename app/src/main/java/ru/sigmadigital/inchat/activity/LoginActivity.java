package ru.sigmadigital.inchat.activity;

import android.os.Bundle;

import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.newW.login.LoginFragment;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        replaceCurrentFragmentWith(getSupportFragmentManager(),
                R.id.fragment_container,
                LoginFragment.newInstance(LoginFragment.MODE_LOG_IN),
                false);

    }

}
