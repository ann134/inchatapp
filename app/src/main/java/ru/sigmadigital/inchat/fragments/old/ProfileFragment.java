package ru.sigmadigital.inchat.fragments.old;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.adapters.PortfolioItemsAdapter;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.PortfolioItemResponse;
import ru.sigmadigital.inchat.models.UserResponse;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.PicassoUtil;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class ProfileFragment extends BaseFragment {

    private MyRetrofit.ServiceManager service;

    private TextView name;
    private TextView univer;
    private TextView vk;
    private TextView telegram;
    private TextView skype;
    private TextView whatsapp;
    private TextView facebook;
    private TextView execRate;
    private TextView custRate;
    private TextView city;
    private TextView country;
    private TextView userType;

    private RecyclerView recyclerView;
    private ImageView avatar;
    private Button btnChange;

    public static ProfileFragment newInstance(){
        return new ProfileFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, null);

        service = MyRetrofit.getInstance();

        avatar = v.findViewById(R.id.imv_avatar);
        btnChange = v.findViewById(R.id.btn_change);
        name = v.findViewById(R.id.tv_name);
        univer = v.findViewById(R.id.tv_university);
        vk = v.findViewById(R.id.tv_vk);
        telegram = v.findViewById(R.id.tv_telegram);
        skype = v.findViewById(R.id.tv_skype);
        whatsapp = v.findViewById(R.id.tv_whatsapp);
        facebook = v.findViewById(R.id.tv_facebook);
        execRate = v.findViewById(R.id.tv_exec_rate);
        custRate = v.findViewById(R.id.tv_cust_rate);
        city = v.findViewById(R.id.tv_city);
        country = v.findViewById(R.id.tv_country);
        userType = v.findViewById(R.id.tv_user_type);
        recyclerView = v.findViewById(R.id.rv_portfolio);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceCurrentFragmentWith(getFragmentManager(),R.id.fragment_container, ProfileEditFragment.newInstance(), true);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMyProfile();
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

    private void setInfoToFields(UserResponse user){
        name.setText(user.getName());
        univer.setText(user.getUniversity());
        vk.setText(user.getVk());
        telegram.setText(user.getTelegram());
        skype.setText(user.getSkype());
        whatsapp.setText(user.getWhatsapp());
        facebook.setText(user.getFacebook());
        execRate.setText("Карма: " + user.getExecutorRate().getKarma() + " / Профи: " + user.getExecutorRate().getProfi());
        custRate.setText("Профи: " + user.getCustomerRate().getProfi() );
        city.setText(user.getCity());
        country.setText(user.getCountry());
        userType.setText("Тип: " + user.getUserType());
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

}
