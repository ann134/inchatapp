package ru.sigmadigital.inchat.fragments.old;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.Done;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.IdResponse;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.PortfolioItemRequestUpdate;
import ru.sigmadigital.inchat.models.PortfolioItemResponse;
import ru.sigmadigital.inchat.models.UserRequest;
import ru.sigmadigital.inchat.models.UserResponse;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class FragmentReq extends BaseFragment implements View.OnClickListener {


    MyRetrofit.ServiceManager service;

    public static FragmentReq newInstance() {
        return new FragmentReq();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_req, container, false);

        service = MyRetrofit.getInstance();

        v.findViewById(R.id.bt_1).setOnClickListener(this);
        v.findViewById(R.id.bt_2).setOnClickListener(this);
        v.findViewById(R.id.bt_3).setOnClickListener(this);
        v.findViewById(R.id.bt_4).setOnClickListener(this);
        v.findViewById(R.id.bt_5).setOnClickListener(this);
        v.findViewById(R.id.bt_6).setOnClickListener(this);
        v.findViewById(R.id.bt_7).setOnClickListener(this);
        v.findViewById(R.id.bt_8).setOnClickListener(this);
        v.findViewById(R.id.bt_9).setOnClickListener(this);
        v.findViewById(R.id.bt_10).setOnClickListener(this);
        v.findViewById(R.id.bt_11).setOnClickListener(this);
        v.findViewById(R.id.bt_12).setOnClickListener(this);
        v.findViewById(R.id.bt_13).setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_1: {
                //refresh();
                break;
            }
            case R.id.bt_2: {
                userMe();
                break;
            }
            case R.id.bt_3: {
                userId();
                break;
            }
            case R.id.bt_4: {
                update();
                break;
            }
            case R.id.bt_5: {
                portfolioMe();
                break;
            }
            case R.id.bt_6: {
                portfolioId();
                break;
            }
            case R.id.bt_7: {
                deletePortfolioItem();
                break;
            }
            case R.id.bt_8: {
                deleteStickerPhoto();
                break;
            }
            case R.id.bt_9: {
                getMyStickerPhoto();
                break;
            }
            case R.id.bt_10: {
                getIdStickerPhoto();
                break;
            }
            case R.id.bt_11: {
                updatePortfolioItem();
                break;
            }
            case R.id.bt_12: {
               // restorePassword();
                break;
            }
            case R.id.bt_13: {
               // restorePasswordRestore();
                break;
            }
        }
    }


    private void refresh() {
       /* Log.e("getRefresh", SettingsHelper.getToken().getRefresh() + " ");

        service.refresh(SettingsHelper.getToken().getRefresh()).enqueue(new Callback<Token>() {

            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Log.e("onResponseCode", response.code() + " ");

                if (response.body() != null) {
                    Log.e("onResponse", " " + response.body().toJson());
                    SettingsHelper.setToken(response.body());

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
        });*/
    }

    private void userMe() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.usersMe(SettingsHelper.getToken().getAccess()).enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());
                            FirebaseMessaging.getInstance().subscribeToTopic("user_" + response.body().getId());

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
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void userId() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                service.usersId(1, SettingsHelper.getToken().getAccess()).enqueue(new Callback<UserResponse>() {

                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
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
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void update() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                UserRequest userRequest = new UserRequest();
                userRequest.setCity("ftgfr");
                userRequest.setFacebook("ftgfr");
                userRequest.setTelegram("ftgfr");
                userRequest.setSkype("ftgfr");
                userRequest.setCategories(new int[]{1, 2});
                Log.e("send", userRequest.toJson());

                service.update(SettingsHelper.getToken().getAccess(), userRequest).enqueue(new Callback<Done>() {

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


    private void portfolioMe() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                service.portfolioMe(SettingsHelper.getToken().getAccess()).enqueue(new Callback<List<PortfolioItemResponse>>() {

                    @Override
                    public void onResponse(Call<List<PortfolioItemResponse>> call, Response<List<PortfolioItemResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (PortfolioItemResponse i : response.body()) {
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
                    public void onFailure(Call<List<PortfolioItemResponse>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void portfolioId() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                service.portfolioId(0, SettingsHelper.getToken().getAccess()).enqueue(new Callback<List<PortfolioItemResponse>>() {

                    @Override
                    public void onResponse(Call<List<PortfolioItemResponse>> call, Response<List<PortfolioItemResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (PortfolioItemResponse i : response.body()) {
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
                    public void onFailure(Call<List<PortfolioItemResponse>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void deletePortfolioItem() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.deletePortfolioItem(146, SettingsHelper.getToken().getAccess()).enqueue(new Callback<Done>() {

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


    private void deleteStickerPhoto() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.deleteStickerPhoto(2, SettingsHelper.getToken().getAccess()).enqueue(new Callback<Done>() {

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

    private void getMyStickerPhoto() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.stickerPhotoMe(SettingsHelper.getToken().getAccess()).enqueue(new Callback<List<IdResponse>>() {

                    @Override
                    public void onResponse(Call<List<IdResponse>> call, Response<List<IdResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (IdResponse i : response.body()) {
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
                    public void onFailure(Call<List<IdResponse>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void getIdStickerPhoto() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.stickerPhotoId(1, SettingsHelper.getToken().getAccess()).enqueue(new Callback<List<IdResponse>>() {

                    @Override
                    public void onResponse(Call<List<IdResponse>> call, Response<List<IdResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (IdResponse i : response.body()) {
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
                    public void onFailure(Call<List<IdResponse>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updatePortfolioItem() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.updatePortfolioItem(146, SettingsHelper.getToken().getAccess(), new PortfolioItemRequestUpdate("fgdg", "uiuhi")).enqueue(new Callback<Done>() {

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

    /*private void restorePassword() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                RestorePasswordFragment r = new RestorePasswordFragment("grind1122@mail.ru");
                Log.e("send", " " + r.toJson());
                service.restorePassword(r).enqueue(new Callback<Done>() {

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

    private void restorePasswordRestore() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                RestorePasswordRestore r = new RestorePasswordRestore(
                        "Ybud2u", "12345", "grind1122@mail.ru");
                Log.e("send", " " + r.toJson());

                service.restorePasswordRestore(r).enqueue(new Callback<Done>() {

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
*/

}
