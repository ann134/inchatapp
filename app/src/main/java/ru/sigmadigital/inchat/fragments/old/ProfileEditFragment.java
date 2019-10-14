package ru.sigmadigital.inchat.fragments.old;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.adapters.PortfolioItemsAdapter;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.Done;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.PortfolioItemResponse;
import ru.sigmadigital.inchat.models.UserRequest;
import ru.sigmadigital.inchat.models.UserResponse;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.PicassoUtil;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class ProfileEditFragment extends BaseFragment {

    private MyRetrofit.ServiceManager service;

    private String avatarPath = null;
    private EditText univer;
    private EditText vk;
    private EditText telegram;
    private EditText skype;
    private EditText whatsapp;
    private EditText facebook;

    private EditText city;
    private EditText country;


    private RecyclerView recyclerView;
    private ImageView avatar;
    private Button save;
    private Button addFile;

    private boolean profileWasLoaded = false;

    public static ProfileEditFragment newInstance(){
        return new ProfileEditFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_edit, null);

        service = MyRetrofit.getInstance();

        avatar = v.findViewById(R.id.imv_avatar);
        save = v.findViewById(R.id.btn_save);
        addFile = v.findViewById(R.id.btn_add_item);
        univer = v.findViewById(R.id.tv_university);
        vk = v.findViewById(R.id.tv_vk);
        telegram = v.findViewById(R.id.tv_telegram);
        skype = v.findViewById(R.id.tv_skype);
        whatsapp = v.findViewById(R.id.tv_whatsapp);
        facebook = v.findViewById(R.id.tv_facebook);

        city = v.findViewById(R.id.tv_city);
        country = v.findViewById(R.id.tv_country);
        recyclerView = v.findViewById(R.id.rv_portfolio);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileInfo();
                if(avatarPath != null){
                    uploadAvatar(avatarPath);
                }
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup()).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult pickResult) {
                        avatarPath = pickResult.getPath();
                        avatar.setImageBitmap(pickResult.getBitmap());
                    }
                }).setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {

                    }
                }).show(getFragmentManager());
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!profileWasLoaded){
            getMyProfile();
            profileWasLoaded = true;
        }
        getMyPortfolio();
    }

    private void getMyProfile() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.usersMe(SettingsHelper.getToken().getAccess()).enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());

                            setInfoToFields(response.body());
                            getMyAvatar(response.body().getId());


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

    private void setInfoToFields(UserResponse user){

        univer.setText(user.getUniversity());
        vk.setText(user.getVk());
        telegram.setText(user.getTelegram());
        skype.setText(user.getSkype());
        whatsapp.setText(user.getWhatsapp());
        facebook.setText(user.getFacebook());
        city.setText(user.getCity());
        country.setText(user.getCountry());
    }

    private void getMyAvatar(long userId) {
        PicassoUtil.getPicasso()
                .load(MyRetrofit.BASE_URL + "users/" + userId + "/photo").memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.common_google_signin_btn_icon_dark)
                .into(avatar);
    }

    private void getMyPortfolio() {
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

                            PortfolioItemsAdapter adapter = new PortfolioItemsAdapter();
                            adapter.setItemList(response.body());
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(App.getAppContext(), RecyclerView.HORIZONTAL, false);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);


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



    private void updateProfileInfo() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                UserRequest userRequest = new UserRequest();
                userRequest.setUniversity(univer.getText().toString());
                userRequest.setVk(vk.getText().toString());
                userRequest.setCity(city.getText().toString());
                userRequest.setFacebook(facebook.getText().toString());
                userRequest.setTelegram(telegram.getText().toString());
                userRequest.setSkype(skype.getText().toString());
                userRequest.setCountry(country.getText().toString());
                userRequest.setWhatsapp(whatsapp.getText().toString());
                userRequest.setCategories(new int[]{1, 2});
                Log.e("send", userRequest.toJson());

                service.update(SettingsHelper.getToken().getAccess(), userRequest).enqueue(new Callback<Done>() {

                    @Override
                    public void onResponse(Call<Done> call, Response<Done> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());
                            getFragmentManager().popBackStack();

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

    private void uploadAvatar(final String path) {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                File file = new File(path);

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





}
