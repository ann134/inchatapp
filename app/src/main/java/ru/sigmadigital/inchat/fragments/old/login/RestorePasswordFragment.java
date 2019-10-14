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
import ru.sigmadigital.inchat.models.Done;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.RestorePassword;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;

public class RestorePasswordFragment extends BaseFragment implements View.OnClickListener {


    private MyRetrofit.ServiceManager service;

    private EditText login;


    public static RestorePasswordFragment newInstance() {
        return new RestorePasswordFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_restore, container, false);

        service = MyRetrofit.getInstance();

        login = v.findViewById(R.id.et_email);
        v.findViewById(R.id.btn_restore).setOnClickListener(this);


        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_restore: {
                restorePassword();
                break;
            }
        }
    }


    private void restorePassword() {

        RestorePassword r = new RestorePassword(login.getText().toString());
        Log.e("send", " " + r.toJson());
        service.restorePassword(r).enqueue(new Callback<Done>() {

            @Override
            public void onResponse(Call<Done> call, Response<Done> response) {
                Log.e("onResponseCode", response.code() + " ");

                if (response.body() != null) {
                    Log.e("onResponse", " " + response.body().toJson());

                    replaceCurrentFragmentWith(getFragmentManager(),
                            R.id.fragment_container,
                            RestorePasswordRestoreFragment.newInstance(login.getText().toString()),
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
