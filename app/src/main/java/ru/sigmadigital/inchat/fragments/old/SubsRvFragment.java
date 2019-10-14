package ru.sigmadigital.inchat.fragments.old;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.adapters.SubsRvAdapter;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.Distance;
import ru.sigmadigital.inchat.models.Done;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.UserResponse;
import ru.sigmadigital.inchat.models.UsersFilter;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class SubsRvFragment extends BaseFragment {

    private int type;
    private RecyclerView recyclerView;
    private MyRetrofit.ServiceManager service;

    private SubsRvAdapter.OnSubsButtonListener listener = new SubsRvAdapter.OnSubsButtonListener() {
        @Override
        public void subscribe(final long userId) {
                ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
                    @Override
                    public void OnValid() {
                        service.subscribeToUser(SettingsHelper.getToken().getAccess(), userId).enqueue(new Callback<Done>() {

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

        @Override
        public void unsubscribe(final long userId) {
            ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
                @Override
                public void OnValid() {

                    service.rejectSubscribe(SettingsHelper.getToken().getAccess(), userId).enqueue(new Callback<Done>() {

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

        @Override
        public void deleteSubscriber(final long userId) {
            ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
                @Override
                public void OnValid() {

                    service.rejectSubscribe(SettingsHelper.getToken().getAccess(), userId).enqueue(new Callback<Done>() {

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

        @Override
        public void acceptSubRequest(final long userId) {
            ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
                @Override
                public void OnValid() {

                    service.acceptSubscribe(SettingsHelper.getToken().getAccess(), userId).enqueue(new Callback<Done>() {

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
    };


    public static SubsRvFragment newInstance(int type){
        SubsRvFragment fragment = new SubsRvFragment();
        fragment.setType(type);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_subscription_rv, null);
        recyclerView = v.findViewById(R.id.rv_subs);
        service = MyRetrofit.getInstance();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(type == 1){
            ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
                @Override
                public void OnValid() {
                    UsersFilter usersFilter = new UsersFilter();
                    usersFilter.setKarmaMax(8);
                    usersFilter.setCategories(new int[]{5, 8});
                    usersFilter.setName("ann");
                    usersFilter.setDistance(new Distance(87,87, 9));
                    usersFilter.setKarmaMin(98);
                    usersFilter.setProfileType(0);
                    usersFilter.setUniversity("bnvk");
                    usersFilter.setKarmaMax(0);


                    service.getUsers(SettingsHelper.getToken().getAccess(), new UsersFilter()).enqueue(new Callback<List<UserResponse>>() {
                        @Override
                        public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                            Log.e("onResponseCode", response.code() + " ");

                            if (response.body() != null) {
                                for (UserResponse i : response.body()) {
                                    Log.e("onResponse", " " + i.toJson());
                                }
                                SubsRvAdapter adapder = new SubsRvAdapter(1, listener);
                                adapder.setUsersList(response.body());
                                recyclerView.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
                                recyclerView.setAdapter(adapder);


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
        if(type == 2){
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
                                SubsRvAdapter adapder = new SubsRvAdapter(2, listener);
                                adapder.setUsersList(response.body());
                                recyclerView.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
                                recyclerView.setAdapter(adapder);


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

        if(type == 3){
            ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
                @Override
                public void OnValid() {

                    service.getMySubscribes(SettingsHelper.getToken().getAccess()).enqueue(new Callback<List<UserResponse>>() {
                        @Override
                        public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                            Log.e("onResponseCode", response.code() + " ");

                            if (response.body() != null) {
                                for (UserResponse i : response.body()) {
                                    Log.e("onResponse", " " + i.toJson());
                                }
                                SubsRvAdapter adapder = new SubsRvAdapter(3, listener);
                                adapder.setUsersList(response.body());
                                recyclerView.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
                                recyclerView.setAdapter(adapder);


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

        if(type == 4){
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
                                SubsRvAdapter adapder = new SubsRvAdapter(4, listener);
                                adapder.setUsersList(response.body());
                                recyclerView.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
                                recyclerView.setAdapter(adapder);


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



    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
