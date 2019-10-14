package ru.sigmadigital.inchat.fragments.newW.menu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.adapters.PhotosAdapter;
import ru.sigmadigital.inchat.adapters.RatesAdapter;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.IdResponse;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.RateResponse;
import ru.sigmadigital.inchat.models.UserResponse;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.PicassoUtil;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {

    private MyRetrofit.ServiceManager service;

    UserResponse userResponse;

    private TextView name;
    private TextView dateOfBirth;
    private TextView city;
    private TextView email;
    private TextView userId;
    private TextView karmaValue;
    private TextView profiValue;

    //statistic executor
    private TextView ordersDone;
    private TextView ordersOnCheck;
    private TextView ordersFailed;
    private TextView ordersInProcess;
    private TextView ordersFailedInProcess;
    private TextView ordersOnRework;

    //statistic customer
    private TextView ordersCreated;
    private TextView ordersFinished;
    private TextView ordersActive;
    private TextView ordersOnReworkCust;

    private CircleImageView avatar;
    private RecyclerView recyclerFotos;
    private PhotosAdapter photosAdapter;
    private RecyclerView recyclerRates;
    private RatesAdapter ratesAdapter;

    private Button btnEditProfile;
    private Button btnShowFotos;
    private Button btnShowRates;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_profile2, container, false);

        service = MyRetrofit.getInstance();
        init(v);
        getData();

        return v;
    }

    private void init(View v) {
        avatar = v.findViewById(R.id.imv_avatar);

        name = v.findViewById(R.id.tv_name);
        dateOfBirth = v.findViewById(R.id.tv_date_of_birth_value);
        city = v.findViewById(R.id.tv_city_value);
        email = v.findViewById(R.id.tv_email_value);
        userId = v.findViewById(R.id.tv_userID);
        karmaValue = v.findViewById(R.id.tv_karma_value);
        profiValue = v.findViewById(R.id.tv_profi_value);

        ordersDone = v.findViewById(R.id.tv_orders_done_value);
        ordersOnCheck = v.findViewById(R.id.tv_orders_onCheck_value);
        ordersFailed = v.findViewById(R.id.tv_orders_fail_value);
        ordersInProcess = v.findViewById(R.id.tv_orders_in_process_value);
        ordersFailedInProcess = v.findViewById(R.id.tv_orders_failed_in_process_value);
        ordersOnRework = v.findViewById(R.id.tv_orders_in_rework_value);

        ordersCreated = v.findViewById(R.id.tv_orders_created_value);
        ordersFinished = v.findViewById(R.id.tv_orders_finished_value);
        ordersActive = v.findViewById(R.id.tv_orders_active_value);
        ordersOnReworkCust = v.findViewById(R.id.tv_orders_ex_rework_value);

        recyclerFotos = v.findViewById(R.id.recycler_fotos);
        photosAdapter = new PhotosAdapter(PhotosAdapter.PHOTOS_REVIEW);
        recyclerFotos.setLayoutManager(new GridLayoutManager(App.getAppContext(), 3));
        recyclerFotos.setAdapter(photosAdapter);

        recyclerRates = v.findViewById(R.id.recycler_rates);
        ratesAdapter = new RatesAdapter();
        recyclerRates.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
        recyclerRates.setAdapter(ratesAdapter);


        btnEditProfile = v.findViewById(R.id.bt_edit_profile);
        btnShowFotos = v.findViewById(R.id.bt_foto);
        btnShowRates = v.findViewById(R.id.bt_rates);

        // btnSettings.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);
        btnShowFotos.setOnClickListener(this);
        btnShowRates.setOnClickListener(this);
    }


    private void getData() {
        getMyProfile();
        getMyAvatar();
        getMyStickerPhoto();
        reviewsMe();
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

                            setData(response.body());

                            //убрать это на старт
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

    private void getMyAvatar() {
        Glide.with(App.getAppContext())
                .load(MyRetrofit.BASE_URL + "users/me/photo")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.common_google_signin_btn_icon_dark)
                .into(avatar);
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

                            if (list.size() > 9) {
                                photosAdapter.setItemList(list.subList(0, 8));
                            } else {
                                photosAdapter.setItemList(list);
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
                        Toast.makeText(App.getAppContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void reviewsMe() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.reviewsMe(SettingsHelper.getToken().getAccess()).enqueue(new Callback<List<RateResponse>>() {
                    @Override
                    public void onResponse(Call<List<RateResponse>> call, Response<List<RateResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (RateResponse i : response.body()) {
                                Log.e("onResponse", " " + i.toJson());
                            }

                            List<RateResponse> list = response.body();

                            list.add(new RateResponse());
                            list.add(new RateResponse());
                            list.add(new RateResponse());
                            list.add(new RateResponse());
                            list.add(new RateResponse());
                            list.add(new RateResponse());
                            list.add(new RateResponse());
                            list.add(new RateResponse());
                            list.add(new RateResponse());
                            list.add(new RateResponse());

                            if (list.size() > 3) {
                                ratesAdapter.setItemList(list.subList(0, 3));
                            } else {
                                ratesAdapter.setItemList(list);
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
                    public void onFailure(Call<List<RateResponse>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setData(UserResponse user) {
        this.userResponse = user;

        name.setText(user.getName());
        if (user.getDateOfBirth() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(user.getDateOfBirth());
            dateOfBirth.setText(String.format("%02d.%02d.%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)));
        }
        city.setText(user.getCity());
        email.setText(user.getMail());
        userId.setText(user.getId() + "");
        karmaValue.setText(user.getExecutorRate().getKarma() + "");
        profiValue.setText(user.getExecutorRate().getProfi() + user.getCustomerRate().getProfi() + "");

        ordersDone.setText("---");
        ordersOnCheck.setText("---");
        ordersFailed.setText("---");
        ordersInProcess.setText("---");
        ordersFailedInProcess.setText("---");
        ordersOnRework.setText("---");

        ordersCreated.setText("---");
        ordersFinished.setText("---");
        ordersActive.setText("---");
        ordersOnReworkCust.setText("---");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_edit_profile: {
                showFragment(ProfileEditFragment.newInstance(userResponse));
                break;
            }
            case R.id.bt_foto: {

                break;
            }
            case R.id.bt_rates: {

                break;
            }
        }
    }

    private void showFragment(Fragment fragment) {
        replaceCurrentFragmentWith(getFragmentManager(), getParentContainer(this.getView()), fragment, true);
    }

}
