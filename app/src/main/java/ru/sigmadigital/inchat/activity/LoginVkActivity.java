package ru.sigmadigital.inchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.models.AuthVk;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.Registration;
import ru.sigmadigital.inchat.models.Token;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.SettingsHelper;

public class LoginVkActivity extends BaseActivity {

    MyRetrofit.ServiceManager service;

    VKAccessToken vkAccessToken;
    String[] scope = new String[]{VKScope.EMAIL};

    String codeVk;
    String first;
    String last;
    String city;
    String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginvk);

        /*String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
        Log.e("Response", Arrays.asList(fingerprints).toString());*/

        VKSdk.login(this, scope);

        service = MyRetrofit.getInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Log.e("VKAccessToken", res.userId);
                vkAccessToken = res;
                getVkCode();
                // Пользователь успешно авторизовался
            }

            @Override
            public void onError(VKError error) {
                Log.e("VKAccessError", error.errorMessage + "");
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void getVkCode() {
        VKRequest request = new VKRequest("execute.getCode");
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    String code = (String) response.json.get("response");
                    Log.e("code", code);
                    codeVk = code;
                    getInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("VKRequestComplete", response.responseString);
                //Do complete stuff
            }

            @Override
            public void onError(VKError error) {
                Log.e("VKRequestError", error.errorMessage);
                //Do error stuff
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                Log.e("VKRequestFailed", attemptNumber + "");
                //I don't really believe in progress
            }
        });
    }

    private void getInfo() {
        VKRequest request = new VKRequest("account.getProfileInfo");
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    JSONObject profile = (JSONObject) response.json.get("response");
                    first = (String) profile.get("first_name");
                    last = (String) profile.get("last_name");
                    city = (String) ((JSONObject) profile.get("city")).get("title");
                    country = (String) ((JSONObject) profile.get("country")).get("title");
                    Log.e("city", city);


                    getUniversity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Log.e("VKRequestComplete", response.responseString);
                //Do complete stuff
            }

            @Override
            public void onError(VKError error) {
                Log.e("VKRequestError", error.errorMessage);
                //Do error stuff
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                Log.e("VKRequestFailed", attemptNumber + "");
                //I don't really believe in progress
            }
        });
    }

    private void getUniversity() {
        VKRequest request = VKApi.users().get();
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                loginVk();
                Log.e("VKRequestComplete", response.responseString);
                //Do complete stuff
            }

            @Override
            public void onError(VKError error) {
                Log.e("VKRequestError", error.errorMessage);
                //Do error stuff
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                Log.e("VKRequestFailed", attemptNumber + "");
                //I don't really believe in progress
            }
        });
    }


    private void loginVk() {
        final Registration registration = new Registration(/*first + " " + last, "nnn", */"", new AuthVk(vkAccessToken.userId, codeVk));
        Log.e("send", registration.toJson());

        service.registration(registration).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Log.e("onResponseCode", response.code() + " ");

                if (response.body() != null) {
                    Log.e("onResponse", " " + response.body().toJson());
                    SettingsHelper.setToken(response.body());
                    SettingsHelper.setRegistration(registration);
                    startMainActivity();
                } else if (response.errorBody() != null) {
                    try {
                        String responseError = response.errorBody().string();
                        Log.e("onResponseError",  " " + responseError);
                        Error error  = JsonParser.fromJson(responseError, Error.class);
                        if (error != null){
                            Log.e("Error",  " " + error.getDescription() + " " + error.getCode());
                            ErrorHandler.catchError(error, LoginVkActivity.this);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e("onFailure", t + "");
                Toast.makeText(getBaseContext(), "onFailure", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


}
