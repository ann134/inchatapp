package ru.sigmadigital.inchat.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.activity.LoginActivity;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.Registration;
import ru.sigmadigital.inchat.models.Token;


public class ValidateToken {


    public static void validate(final Activity activity, final IValid listener) {
        if (isValidAccess()) {
            Log.e("isValid", "ok");
            listener.OnValid();
        } else {
            refresh(activity, listener);
        }
    }

    public static void refresh(final Activity activity, final IValid listener) {
        isValidRefresh(new ValidCallback() {
            @Override
            public void onSuccess() {
                Log.e("isValidRefresh", "ok");
                listener.OnValid();
            }

            @Override
            public void onFail() {

                canLogin(new ValidCallback() {
                    @Override
                    public void onSuccess() {
                        Log.e("canLogin", "ok");
                        listener.OnValid();
                    }

                    @Override
                    public void onFail() {
                        Log.e("isValid", "fail");
                        startLoginActivity(activity);
                    }
                });
            }
        });
    }


    private static boolean isValidAccess() {
        Token token = SettingsHelper.getToken();
        if (token == null) {
            return false;
        }
        Date currentDate = new Date();
        return currentDate.getTime() < token.getAccessExp().getTime();
    }


    private static void isValidRefresh(ValidCallback callback) {
        Token token = SettingsHelper.getToken();
        if (token == null) {
            callback.onFail();
            return;
        }
        Date currentDate = new Date();
        if (currentDate.getTime() < token.getRefreshExp().getTime()) {
            updateToken(callback);
        } else {
            callback.onFail();
        }
    }

    private static void updateToken(final ValidCallback callback) {
        MyRetrofit.getInstance().refresh(SettingsHelper.getToken().getRefresh()).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Log.e("onResponseCode", response.code() + " ");

                if (response.body() != null) {
                    Log.e("onResponse", " " + response.body().toJson());
                    SettingsHelper.setToken(response.body());
                    callback.onSuccess();

                } else if (response.errorBody() != null) {
                    try {
                        callback.onFail();
                        String responseError = response.errorBody().string();
                        Log.e("onResponseError", " " + responseError);
                        Error error = JsonParser.fromJson(responseError, Error.class);
                        if (error != null) {
                            Log.e("Error", " " + error.getDescription() + " " + error.getCode());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e("onFailure", t + "");
                callback.onFail();
            }
        });
    }

    private static void canLogin(ValidCallback callback) {
        if (SettingsHelper.getRegistration() != null) {
            login(callback);
        } else {
            callback.onFail();
        }
    }

    private static void login(final ValidCallback callback) {
        Registration user = SettingsHelper.getRegistration();

        MyRetrofit.getInstance().registration(user).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Log.e("onResponseCode", response.code() + " ");

                if (response.body() != null) {
                    Log.e("onResponse", " " + response.body().toJson());
                    SettingsHelper.setToken(response.body());
                    callback.onSuccess();
                } else if (response.errorBody() != null) {
                    try {
                        callback.onFail();
                        String responseError = response.errorBody().string();
                        Log.e("onResponseError", " " + responseError);
                        Error error = JsonParser.fromJson(responseError, Error.class);
                        if (error != null) {
                            Log.e("Error", " " + error.getDescription() + " " + error.getCode());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e("onFailure", t + "");
                callback.onFail();
            }
        });
    }

    private static void startLoginActivity(Activity activity) {
        if (activity != null) {
            activity.startActivity(new Intent(activity, LoginActivity.class));
            activity.finish();
        }
    }


    public interface IValid {
        void OnValid();
    }

    public interface ValidCallback {
        void onSuccess();

        void onFail();
    }
}
