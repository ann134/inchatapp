package ru.sigmadigital.inchat.fragments.old;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.Done;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.IdResponse;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.PortfolioItemRequest;
import ru.sigmadigital.inchat.models.PortfolioItemResponse;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.PicassoUtil;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class FragmentReq2 extends BaseFragment implements View.OnClickListener {

    MyRetrofit.ServiceManager service;

    ImageView image1;
    ImageView image2;
    ImageView image3;


    public static FragmentReq2 newInstance() {
        return new FragmentReq2();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_req2, container, false);

        service = MyRetrofit.getInstance();

        image1 = v.findViewById(R.id.img_1);
        image2 = v.findViewById(R.id.img_2);
        image3 = v.findViewById(R.id.img_3);

        v.findViewById(R.id.img_1).setOnClickListener(this);
        v.findViewById(R.id.img_2).setOnClickListener(this);
        v.findViewById(R.id.bt_3).setOnClickListener(this);
        v.findViewById(R.id.bt_4).setOnClickListener(this);
        v.findViewById(R.id.bt_5).setOnClickListener(this);
        v.findViewById(R.id.img_3).setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_1: {
                getProfilePhoto();
                break;
            }
            case R.id.img_2: {
                getStickerPhoto();
                break;
            }
            case R.id.bt_3: {
                uploadProfile();
                break;
            }
            case R.id.bt_4: {
                uploadPhotosticker();
                break;
            }
            case R.id.bt_5: {
                sendMultipart();
                break;
            }
            case R.id.img_3: {
                getPortfolio();
                break;
            }
        }
    }


    private void getProfilePhoto() {
        PicassoUtil.getPicasso()
                //.load(MyRetrofit.BASE_URL + "users/me/photo")
                .load(MyRetrofit.BASE_URL + "users/" + 1 + "/photo")
                .error(R.drawable.common_google_signin_btn_icon_dark)
                .into(image1);
    }

    private void getStickerPhoto() {
        PicassoUtil.getPicasso()
                .load(MyRetrofit.BASE_URL + "users/stickerphoto/" + 2 + "/photo")
                .error(R.drawable.common_google_signin_btn_icon_dark)
                .into(image2);
    }

    private void getPortfolio() {
        PicassoUtil.getPicasso()
                .load(MyRetrofit.BASE_URL + "users/portfolio/" + 144 + "/file")
                .error(R.drawable.common_google_signin_btn_icon_dark)
                .into(image3);
    }


    private void uploadProfile() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                String fileUri = "/sdcard/DCIM/Camera/pizza.jpg";
                File file = new File(fileUri);

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

                service.uploadUserPhoto(SettingsHelper.getToken().getAccess(), requestFile).enqueue(new Callback<Done>() {
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


    private void uploadPhotosticker() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                String fileUri = "/sdcard/DCIM/Camera/pizza.jpg";
                File file = new File(fileUri);

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

                service.uploadStickerPhoto(SettingsHelper.getToken().

                        getAccess(), requestFile).

                        enqueue(new Callback<IdResponse>() {
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


    private void sendMultipart() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                String fileUri = "/sdcard/DCIM/Camera/pizza.jpg";
                File file = new File(fileUri);

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                PortfolioItemRequest portfolioItemResponse = new PortfolioItemRequest("descript", "titlee", PortfolioItemResponse.FilesType.IMAGE);
                RequestBody description = RequestBody.create(MediaType.parse("application/json"), portfolioItemResponse.toJson());

                Log.e("body", body.headers().toString());
                Log.e("body", body.body().contentType().toString());

                service.updatePortfolio(SettingsHelper.getToken().getAccess(), description, body).enqueue(new Callback<IdResponse>() {

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

}
