package ru.sigmadigital.inchat.fragments.old;

import android.content.Intent;
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
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.activity.LoginActivity;
import ru.sigmadigital.inchat.fragments.BaseFragment;

import ru.sigmadigital.inchat.fragments.old.chat.ChatsListFragment;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.UserResponse;
import ru.sigmadigital.inchat.models.UsersFilter;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class MainFragment extends BaseFragment implements View.OnClickListener {

    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main, container, false);

        v.findViewById(R.id.bt_drag_and_drop).setOnClickListener(this);
        v.findViewById(R.id.req_frag).setOnClickListener(this);
        v.findViewById(R.id.bt_camera).setOnClickListener(this);
        v.findViewById(R.id.req_frag2).setOnClickListener(this);
        v.findViewById(R.id.req_frag3).setOnClickListener(this);
        v.findViewById(R.id.req_frag4).setOnClickListener(this);
        v.findViewById(R.id.bt_drag_and_drop2).setOnClickListener(this);
        v.findViewById(R.id.bt_log_out).setOnClickListener(this);
        v.findViewById(R.id.chat).setOnClickListener(this);
        v.findViewById(R.id.btn_profile).setOnClickListener(this);
        v.findViewById(R.id.btn_subs).setOnClickListener(this);


        v.findViewById(R.id.bt_orders).setOnClickListener(this);
        v.findViewById(R.id.bt_add_orders).setOnClickListener(this);


        users();


        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.req_frag: {
                replaceCurrentFragmentWith(getFragmentManager(),
                        R.id.fragment_container,
                        FragmentReq.newInstance(),
                        true);
                break;
            }
            case R.id.req_frag2: {
                replaceCurrentFragmentWith(getFragmentManager(),
                        R.id.fragment_container,
                        FragmentReq2.newInstance(),
                        true);
                break;
            }
            case R.id.req_frag3: {
                replaceCurrentFragmentWith(getFragmentManager(),
                        R.id.fragment_container,
                        FragmentReq3.newInstance(),
                        true);
                break;
            }
            case R.id.req_frag4: {
                replaceCurrentFragmentWith(getFragmentManager(),
                        R.id.fragment_container,
                        FragmentReq4.newInstance(),
                        true);
                break;
            }
            case R.id.chat: {
                replaceCurrentFragmentWith(getFragmentManager(),
                        R.id.fragment_container,
                        ChatsListFragment.newInstance(),
                        true);
                break;
            }
            case R.id.bt_camera: {
                replaceCurrentFragmentWith(getFragmentManager(),
                        R.id.fragment_container,
                        FotoapparatFragment.newInstance(),
                        true);
                break;
            }
            case R.id.bt_drag_and_drop: {
                replaceCurrentFragmentWith(getFragmentManager(),
                        R.id.fragment_container,
                        DragAndDrop.newInstance(),
                        true);
                break;
            }
            case R.id.bt_drag_and_drop2: {
//                replaceCurrentFragmentWith(getFragmentManager(),
//                        R.id.fragment_container,
//                        DragAndDrop2.newInstance(null),
//                        true);
                break;
            }
            case R.id.bt_log_out: {
                SettingsHelper.clear();
                if (getActivity() != null) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                }
                break;
            }

            case R.id.bt_orders: {
                /*replaceCurrentFragmentWith(getFragmentManager(),
                        R.id.fragment_container,
                        OrdersFilterFragmentOld.newInstance(),
                        true);*/
                break;
            }

            case R.id.bt_add_orders: {
                /*replaceCurrentFragmentWith(getFragmentManager(),
                        R.id.fragment_container,
                        AddOrderFragment.newInstance(),
                        true);*/
                break;
            }

            case R.id.btn_profile: {
                replaceCurrentFragmentWith(getFragmentManager(),
                        R.id.fragment_container,
                        ProfileFragment.newInstance(),
                        true);
                break;
            }
            case R.id.btn_subs: {
                replaceCurrentFragmentWith(getFragmentManager(),
                        R.id.fragment_container,
                        SubscriptionTabsFragment.newInstance(),
                        true);
                break;
            }
        }
    }

    private void users() {

        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                UsersFilter usersFilter = new UsersFilter();
                usersFilter.setKarmaMax(6);
               // usersFilter.setCategories(new int[]{5, 8});
               // usersFilter.setName("mira");
                //usersFilter.setDistance(new Distance(87, 87, 9));
                usersFilter.setKarmaMin(3);
               // usersFilter.setProfileType(0);
               // usersFilter.setUniversity("ИТМО");
               // usersFilter.setKarmaMax(0);

                Log.e("usersFilter", usersFilter.toJson() + " ");


                MyRetrofit.getInstance().getUsers(SettingsHelper.getToken().getAccess(), usersFilter).enqueue(new Callback<List<UserResponse>>() {
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
                        Toast.makeText(App.getAppContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


}
