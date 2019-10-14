package ru.sigmadigital.inchat.fragments.newW.menu;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.adapters.PhotosAdapter;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.Done;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.IdResponse;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.UserRequest;
import ru.sigmadigital.inchat.models.UserResponse;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class ProfileEditFragment extends BaseFragment implements View.OnClickListener {

    private MyRetrofit.ServiceManager service;

    private UserResponse user;

    private CircleImageView avatar;
    private TextView name;
    private TextView userId;
    private TextView karmaValue;
    private TextView profiValue;

    private EditText dateOfBirth;
    private Date dateOfBD;
    private EditText city;
    private EditText email;

    private RecyclerView recyclerPhotos;
    private PhotosAdapter photosAdapter;


    public static ProfileEditFragment newInstance(UserResponse userResponse) {
        ProfileEditFragment fragment = new ProfileEditFragment();
        fragment.setUser(userResponse);
        return fragment;
    }

    private void setUser(UserResponse userResponse){
        user = userResponse;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_profile_edit2, container, false);

        service = MyRetrofit.getInstance();
        init(v);

        return v;
    }

    private void init(View v) {
        name = v.findViewById(R.id.tv_name);
        userId = v.findViewById(R.id.tv_userID_value);
        avatar = v.findViewById(R.id.imv_avatar);
        karmaValue = v.findViewById(R.id.tv_karma_value);
        profiValue = v.findViewById(R.id.tv_profi_value);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImageDialog.build(new PickSetup()).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult pickResult) {
                        Log.e("ImagePath", pickResult.getPath());
                        uploadAvatar(pickResult.getPath());
                    }
                }).show(getFragmentManager());
            }
        });

        name.setText(user.getName());
        userId.setText(user.getId()+"");
        karmaValue.setText(user.getExecutorRate().getKarma()+"");
        profiValue.setText(user.getExecutorRate().getProfi() + user.getCustomerRate().getProfi()+"");

        dateOfBirth =  v.findViewById(R.id.et_date_of_birth);
        city =  v.findViewById(R.id.et_city);
        email =  v.findViewById(R.id.et_email);

        if(user.getDateOfBirth() != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(user.getDateOfBirth());
            setDayOfBirth(calendar);
        }

        city.setText(user.getCity());
        email.setText(user.getMail());
        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(i,i1,i2);
                        setDayOfBirth(calendar);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        v.findViewById(R.id.bt_save_changes).setOnClickListener(this);
        v.findViewById(R.id.bt_delete_all_fotos).setOnClickListener(this);

        recyclerPhotos = v.findViewById(R.id.recycler_fotos);
        photosAdapter = new PhotosAdapter(PhotosAdapter.PHOTOS_EDIT, getFragmentManager(), new PhotosAdapter.PhotoUploader() {
            @Override
            public void uploadPhoto(String path) {
                uploadPhotosticker(path);
            }

            @Override
            public void deletePhoto(long photoId) {
                deleteStickerPhoto(photoId);
            }
        });
        recyclerPhotos.setLayoutManager(new GridLayoutManager(App.getAppContext(), 3));
        recyclerPhotos.setAdapter(photosAdapter);

        getMyAvatar();
        getMyStickerPhoto();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save_changes: {
                updateProfileInfo();
                break;
            }
            case R.id.bt_delete_all_fotos: {

                break;
            }
        }
    }





    //-----------------------------------------------------

    private void getMyAvatar() {
        Glide.with(App.getAppContext())
                .load(MyRetrofit.BASE_URL +  "users/me/photo")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.common_google_signin_btn_icon_dark)
                .into(avatar);
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

                            if(response.body().isDone()){
                                getMyAvatar();
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
                    public void onFailure(Call<Done> call, Throwable t) {
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
                userRequest.setCity(city.getText().toString());
                userRequest.setMail(email.getText().toString());
                userRequest.setDateOfBirth(dateOfBD);

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

                            List<IdResponse> list = response.body();

                            if (list.size() > 9){
                                photosAdapter.setItemList(list.subList(0, 8));
                            } else {
                                photosAdapter.setItemList(list);
                            }
                            photosAdapter.notifyDataSetChanged();

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
                        Toast.makeText(App.getAppContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void uploadPhotosticker(final String path) {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {


                File file = new File(path);

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

                service.uploadStickerPhoto(SettingsHelper.getToken().

                        getAccess(), requestFile).

                        enqueue(new Callback<IdResponse>() {
                            @Override
                            public void onResponse(Call<IdResponse> call, Response<IdResponse> response) {
                                Log.e("onResponseCode", response.code() + " ");

                                if (response.body() != null) {
                                    Log.e("onResponse", " " + response.body().toJson());

                                    getMyStickerPhoto();


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

    private void deleteStickerPhoto(final long photoId) {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.deleteStickerPhoto(photoId, SettingsHelper.getToken().getAccess()).enqueue(new Callback<Done>() {

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

    private void setDayOfBirth(Calendar calendar){
        dateOfBirth.setText(String.format("%02d.%02d.%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)));
        dateOfBD = calendar.getTime();
    }


}
