package ru.sigmadigital.inchat.fragments.newW.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.activity.LoginGoogleActivity;
import ru.sigmadigital.inchat.activity.LoginVkActivity;
import ru.sigmadigital.inchat.activity.MainActivity;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.fragments.old.login.RestorePasswordFragment;
import ru.sigmadigital.inchat.models.AuthEmail;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.Registration;
import ru.sigmadigital.inchat.models.Token;
import ru.sigmadigital.inchat.models.UserResponse;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.utils.HashUtil;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.SettingsHelper;


public class LoginFragment extends BaseFragment implements View.OnClickListener {

    private MyRetrofit.ServiceManager service;

    public static final int MODE_LOG_IN = 1;
    public static final int MODE_SIGN_IN = 2;

    private int mode;

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText password2;

    private TextView actionButton;
    private TextView indicator;
    private TextView restorePasswordButton;
    private TextView title;
    private TextView changeModeButton;


    public static LoginFragment newInstance(int mode) {
        LoginFragment fragment = new LoginFragment();
        fragment.setMode(mode);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_login, container, false);

        service = MyRetrofit.getInstance();

        name = v.findViewById(R.id.et_name);
        email = v.findViewById(R.id.et_mail);
        password = v.findViewById(R.id.et_password);
        password2 = v.findViewById(R.id.et_password_again);
        actionButton = v.findViewById(R.id.tv_action_button);
        changeModeButton = v.findViewById(R.id.tv_change_mode_button);
        title = v.findViewById(R.id.tv_title);
        restorePasswordButton = v.findViewById(R.id.tv_restore_password);
        indicator = v.findViewById(R.id.tv_indicator);

        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        actionButton.setOnClickListener(this);
        restorePasswordButton.setOnClickListener(this);
        changeModeButton.setOnClickListener(this);
        indicator.setOnClickListener(this);
        v.findViewById(R.id.ll_vk).setOnClickListener(this);
        v.findViewById(R.id.ll_google).setOnClickListener(this);

        initViewSettings(v, getMode());

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_action_button: {
                login();
                break;
            }
            case R.id.tv_restore_password: {
                replaceCurrentFragmentWith(getFragmentManager(),
                        R.id.fragment_container,
                        RestorePasswordFragment.newInstance(),
                        true);
                break;
            }
            case R.id.ll_vk: {
                if (getActivity() != null) {
                    startActivity(new Intent(getActivity(), LoginVkActivity.class));
                    getActivity().finish();
                }
                break;
            }
            case R.id.ll_google: {
                if (getActivity() != null) {
                    startActivity(new Intent(getActivity(), LoginGoogleActivity.class));
                    getActivity().finish();
                }
                break;
            }

            case R.id.tv_change_mode_button:
                if (mode == MODE_LOG_IN) {
                    setMode(MODE_SIGN_IN);
                    initViewSettings(v.getRootView(), MODE_SIGN_IN);
                    break;
                }

                if (mode == MODE_SIGN_IN) {
                    setMode(MODE_LOG_IN);
                    initViewSettings(v.getRootView(), MODE_LOG_IN);
                    break;
                }
                break;

            case R.id.tv_indicator:
                if (indicator.getText().equals("A")) {
                    indicator.setText("*");
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    indicator.setText("A");
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                break;
        }
    }


    private void login() {

        if (mode == MODE_SIGN_IN && !password.getText().toString().equals(password2.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.passes_not_equals), Toast.LENGTH_SHORT).show();
            return;
        }

        final Registration registration = new Registration(name.getText().toString(), email.getText().toString(), new AuthEmail(email.getText().toString(), HashUtil.sha256(password.getText().toString())));
        Log.e("send", registration.toJson());

        service.registration(registration).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Log.e("onResponseCode", response.code() + " ");

                if (response.body() != null) {
                    Log.e("onResponse", " " + response.body().toJson());
                    SettingsHelper.setToken(response.body());
                    SettingsHelper.setRegistration(registration);
                    setUserToPreferencesAndSubscribe(SettingsHelper.getToken().getAccess());
                    startMainActivity();
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
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e("onFailure", t + "");
                Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void startMainActivity() {
        if (getActivity() != null) {
            getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    private void initViewSettings(View v, int mode) {
        if (mode == MODE_LOG_IN) {
            v.findViewById(R.id.ll_1).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.ll_4).setVisibility(View.INVISIBLE);
            restorePasswordButton.setVisibility(View.VISIBLE);
            title.setText(R.string.input);
            changeModeButton.setText(R.string.registration);
            actionButton.setText(R.string.enter);
        }

        if (mode == MODE_SIGN_IN) {
            v.findViewById(R.id.ll_1).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ll_4).setVisibility(View.VISIBLE);
            restorePasswordButton.setVisibility(View.INVISIBLE);
            title.setText(R.string.registration);
            changeModeButton.setText(R.string.enter);
            actionButton.setText(R.string.register);
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    private void setUserToPreferencesAndSubscribe(String token){
        service.usersMe(token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                SettingsHelper.setUserId(response.body().getId());
                FirebaseMessaging.getInstance().subscribeToTopic("user_" + response.body().getId());
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
    }
}
