package ru.sigmadigital.inchat.fragments.newW.menu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.activity.LoginActivity;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.UserResponse;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.utils.PicassoUtil;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;
import ru.sigmadigital.inchat.views.MenuView;

public class MenuMainFragment extends BaseFragment implements View.OnClickListener  {

    private MyRetrofit.ServiceManager service;

    private TextView name;
    private CircleImageView avatar;
    private TextView karmaValue;
    private TextView profiValue;


    public static MenuMainFragment newInstance() {
        return new MenuMainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main_menu, container, false);

        service = MyRetrofit.getInstance();

        init(v);
        getData();


        return v;
    }

    private void init(View v){

        name = v.findViewById(R.id.tv_name);
        avatar = v.findViewById(R.id.imv_avatar);
        karmaValue = v.findViewById(R.id.tv_karma_value);
        profiValue = v.findViewById(R.id.tv_profi_value);
        v.findViewById(R.id.profile_container).setOnClickListener(this);

        MenuView subscribes = v.findViewById(R.id.subscribes);
        subscribes.setTitle(getString(R.string.subscribes));
        subscribes.setIcon(R.drawable.ic_user);
        subscribes.setOnClickListener(this);

        MenuView money = v.findViewById(R.id.money);
        money.setTitle(getString(R.string.money));
        money.setIcon(R.drawable.ic_credit_card);
        money.setOnClickListener(this);

        MenuView settings = v.findViewById(R.id.settings);
        settings.setTitle(getString(R.string.settings));
        settings.setIcon(R.drawable.ic_settings);
        settings.setOnClickListener(this);

        MenuView about = v.findViewById(R.id.about);
        about.setTitle(getString(R.string.about));
        about.setIcon(R.drawable.ic_info);
        about.setOnClickListener(this);
    }



    private void getData(){
        getMyProfile();
        getMyAvatar();

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
                .load(MyRetrofit.BASE_URL +  "users/me/photo")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.common_google_signin_btn_icon_dark)
                .into(avatar);
    }

    private void setData(UserResponse user) {
        name.setText(user.getName());
        karmaValue.setText(user.getExecutorRate().getKarma()+"");
        profiValue.setText(user.getExecutorRate().getProfi() + user.getCustomerRate().getProfi()+"");
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_container: {
                showFragment(ProfileFragment.newInstance());
                break;
            }
            case R.id.subscribes: {

                break;
            }
            case R.id.money: {

                break;
            }
            case R.id.settings: {

                break;
            }
            case R.id.about: {
                logout();
                break;
            }
        }
    }

    private void showFragment(Fragment fragment){
        replaceCurrentFragmentWith(getFragmentManager(), getParentContainer(this.getView()), fragment, true);
    }

    private void logout(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic("user_" + SettingsHelper.getUser());
        SettingsHelper.clear();
        if (getActivity() != null) {
            Intent intent = new Intent(App.getAppContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

}
