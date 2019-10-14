package ru.sigmadigital.inchat.fragments.old.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.fragments.newW.login.LoginFragment;
import ru.sigmadigital.inchat.models.Done;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.RestorePasswordRestore;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.utils.HashUtil;
import ru.sigmadigital.inchat.api.MyRetrofit;

public class RestorePasswordRestoreFragment extends BaseFragment implements View.OnClickListener {

    private MyRetrofit.ServiceManager service;

    String email;

    private EditText code;
    private EditText password;

    public static RestorePasswordRestoreFragment newInstance(String email) {
        Bundle args = new Bundle();
        args.putString("email", email);
        RestorePasswordRestoreFragment fragment = new RestorePasswordRestoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey("email")) {
                email = getArguments().getString("email");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_restore_password_restore, container, false);

        service = MyRetrofit.getInstance();

        code = v.findViewById(R.id.et_code);
        password = v.findViewById(R.id.et_pass);
        v.findViewById(R.id.btn_restore).setOnClickListener(this);


        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_restore: {
                restorePasswordRestore();
                break;
            }
        }
    }


    private void restorePasswordRestore() {

        RestorePasswordRestore r = new RestorePasswordRestore(code.getText().toString(), HashUtil.sha256(password.getText().toString()), email);
        Log.e("send", " " + r.toJson());

        service.restorePasswordRestore(r).enqueue(new Callback<Done>() {

            @Override
            public void onResponse(Call<Done> call, Response<Done> response) {
                Log.e("onResponseCode", response.code() + " ");

                if (response.body() != null) {
                    Log.e("onResponse", " " + response.body().toJson());

                    replaceCurrentFragmentWith(getFragmentManager(),
                            R.id.fragment_container,
                            LoginFragment.newInstance(LoginFragment.MODE_LOG_IN),
                            true);

                } else if (response.errorBody() != null) {
                    try {
                        String responseError = response.errorBody().string();
                        Log.e("onResponseError", " " + responseError);
                        Error error = JsonParser.fromJson(responseError, Error.class);
                        if (error != null) {
                            Log.e("Error", " " + error.getDescription() + " " + error.getCode());
                            ErrorHandler.catchError(error, getActivity());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Done> call, Throwable t) {
                Log.e("onFailure", t + "");
                Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }


}


