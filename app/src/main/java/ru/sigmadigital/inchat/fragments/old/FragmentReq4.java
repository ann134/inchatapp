package ru.sigmadigital.inchat.fragments.old;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.ChatRequest;
import ru.sigmadigital.inchat.models.ChatResponse;
import ru.sigmadigital.inchat.models.Done;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.IdResponse;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.UserResponse;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class FragmentReq4 extends BaseFragment implements View.OnClickListener {

    MyRetrofit.ServiceManager service;

    public static FragmentReq4 newInstance() {
        return new FragmentReq4();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_req4, container, false);

        service = MyRetrofit.getInstance();

        v.findViewById(R.id.bt_1).setOnClickListener(this);
        v.findViewById(R.id.bt_2).setOnClickListener(this);
        v.findViewById(R.id.bt_3).setOnClickListener(this);
        v.findViewById(R.id.bt_4).setOnClickListener(this);
        v.findViewById(R.id.bt_5).setOnClickListener(this);
        v.findViewById(R.id.bt_6).setOnClickListener(this);
        v.findViewById(R.id.bt_7).setOnClickListener(this);
        v.findViewById(R.id.bt_8).setOnClickListener(this);


        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_1: {
                subscribeToUser();
                break;
            }
            case R.id.bt_2: {
                getMySubscriptionsRequests();
                break;
            }
            case R.id.bt_3: {
                getMySubscriptions();
                break;
            }
            case R.id.bt_4: {
                getMySubscriptionsWait();
                break;
            }
            case R.id.bt_5: {
                acceptSubscribe();
                break;
            }
            case R.id.bt_6: {
                rejectSubscribe();
                break;
            }
            case R.id.bt_7: {
                createChat();
                break;
            }
            case R.id.bt_8: {
                getChats();
                break;
            }

        }
    }

    private void subscribeToUser() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                service.subscribeToUser(SettingsHelper.getToken().getAccess(), 5).enqueue(new Callback<Done>() {

                    @Override
                    public void onResponse(Call<Done> call, Response<Done> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());

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
        });
    }


    private void getMySubscriptionsRequests() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.getMySubscriptionsRequests(SettingsHelper.getToken().getAccess()).enqueue(new Callback<List<UserResponse>>() {

                    @Override
                    public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (UserResponse i : response.body()) {
                                Log.e("onResponse", " " + i.toJson());
                            }

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
                    public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void getMySubscriptions() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.getMySubscriptions(SettingsHelper.getToken().getAccess()).enqueue(new Callback<List<UserResponse>>() {

                    @Override
                    public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (UserResponse i : response.body()) {
                                Log.e("onResponse", " " + i.toJson());
                            }

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
                    public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void getMySubscriptionsWait() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.getMySubscriptionsWait(SettingsHelper.getToken().getAccess()).enqueue(new Callback<List<UserResponse>>() {

                    @Override
                    public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (UserResponse i : response.body()) {
                                Log.e("onResponse", " " + i.toJson());
                            }

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
                    public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void acceptSubscribe() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.acceptSubscribe(SettingsHelper.getToken().getAccess(), 1).enqueue(new Callback<Done>() {

                    @Override
                    public void onResponse(Call<Done> call, Response<Done> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());

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
        });
    }

    private void rejectSubscribe() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.rejectSubscribe(SettingsHelper.getToken().getAccess(), 1).enqueue(new Callback<Done>() {

                    @Override
                    public void onResponse(Call<Done> call, Response<Done> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());

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
        });
    }


    private void createChat() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                ChatRequest chatRequest = new ChatRequest(null, new int[]{1, 5});
                service.createChat(SettingsHelper.getToken().getAccess(), chatRequest).enqueue(new Callback<IdResponse>() {
                    @Override
                    public void onResponse(Call<IdResponse> call, Response<IdResponse> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());

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
                    public void onFailure(Call<IdResponse> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void getChats() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.getChats(SettingsHelper.getToken().getAccess()).enqueue(new Callback<List<ChatResponse>>() {

                    @Override
                    public void onResponse(Call<List<ChatResponse>> call, Response<List<ChatResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (ChatResponse i : response.body()) {
                                Log.e("onResponse", " " + i.toJson());
                            }

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
                    public void onFailure(Call<List<ChatResponse>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
