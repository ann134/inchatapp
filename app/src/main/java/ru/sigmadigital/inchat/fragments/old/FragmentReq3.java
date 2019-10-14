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

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.OrderFilterRequest;
import ru.sigmadigital.inchat.models.OrderRequest;
import ru.sigmadigital.inchat.models.Category;
import ru.sigmadigital.inchat.models.Done;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.IdResponse;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.OrderResponse;
import ru.sigmadigital.inchat.models.OrderAcceptResponse;
import ru.sigmadigital.inchat.models.OrderFileDescriptionRequest;
import ru.sigmadigital.inchat.models.OrderMessageRequest;
import ru.sigmadigital.inchat.models.RateRequest;
import ru.sigmadigital.inchat.models.RateResponse;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class FragmentReq3 extends BaseFragment implements View.OnClickListener {

    MyRetrofit.ServiceManager service;

    public static FragmentReq3 newInstance() {
        return new FragmentReq3();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_req3, container, false);

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
        v.findViewById(R.id.bt_14).setOnClickListener(this);
        v.findViewById(R.id.bt_15).setOnClickListener(this);
        v.findViewById(R.id.bt_16).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_1: {
                getOrdersCategories();
                break;
            }
            case R.id.bt_2: {
                getOrders();
                break;
            }
            case R.id.bt_3: {
                addOrder();
                break;
            }
            case R.id.bt_4: {
                updateOrder();
                break;
            }
            case R.id.bt_5: {
                deleteOrder();
                break;
            }
            case R.id.bt_6: {
                addFileOrder();
                break;
            }
            case R.id.bt_7: {
                deleteFileOrder();
                break;
            }
            case R.id.bt_8: {
                acceptOrder();
                break;
            }
            case R.id.bt_9: {
                messageOrder();
                break;
            }
            case R.id.bt_10: {
                getOrdersAccepts();
                break;
            }
            case R.id.bt_11: {
                markAccepter();
                break;
            }
            case R.id.bt_12: {
                close();
                break;
            }
            case R.id.bt_13: {
                sendRate();
                break;
            }
            case R.id.bt_14: {
                reviewsMe();
                break;
            }
            case R.id.bt_15: {
                reviewsId();
                break;
            }
            case R.id.bt_16: {
                messageOrderId();
                break;
            }
        }
    }


    private void getOrdersCategories() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                service.getOrdersCategories(SettingsHelper.getToken().getAccess()).enqueue(new Callback<List<Category>>() {

                    @Override
                    public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (Category i : response.body()) {
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
                    public void onFailure(Call<List<Category>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void getOrders() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                OrderFilterRequest orderFilterRequest = new OrderFilterRequest();

                service.getOrders(SettingsHelper.getToken().getAccess(), orderFilterRequest).enqueue(new Callback<List<OrderResponse>>() {

                    @Override
                    public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (OrderResponse i : response.body()) {
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
                        } else {
                            //список пуст
                        }
                    }

                    @Override
                    public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void addOrder() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                OrderRequest orderRequest = new OrderRequest();
                orderRequest.setCategory(1);
                orderRequest.setDescription("jhyguyg");
                orderRequest.setOrderType(OrderResponse.TypeOrders.quick_task);
                orderRequest.setTitle("fdthgdrt");
                Log.e("send", orderRequest.toJson());

                service.addOrder(SettingsHelper.getToken().getAccess(), orderRequest).enqueue(new Callback<IdResponse>() {

                    @Override
                    public void onResponse(Call<IdResponse> call, Response<IdResponse> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());

                            FirebaseMessaging.getInstance().subscribeToTopic("order_cust_" + response.body().getId());

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

    private void updateOrder() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                OrderRequest orderRequest = new OrderRequest();
                orderRequest.setCategory(1);
                orderRequest.setDescription("jhyguyg");
                orderRequest.setOrderType(OrderResponse.TypeOrders.quick_task);
                orderRequest.setTitle("fdthgdrt");
                Log.e("send", orderRequest.toJson());

                service.updateOrder(SettingsHelper.getToken().getAccess(), 159, orderRequest).enqueue(new Callback<Done>() {

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

    private void deleteOrder() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.deleteOrder(SettingsHelper.getToken().getAccess(), 148).enqueue(new Callback<Done>() {

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

    private void addFileOrder() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {


                String fileUri = "/sdcard/DCIM/Camera/pizza.jpg";
                File file = new File(fileUri);

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                OrderFileDescriptionRequest d = new OrderFileDescriptionRequest("namename");
                RequestBody description = RequestBody.create(MediaType.parse("application/json"), d.toJson());

                Log.e("body", body.headers().toString());
                Log.e("body", body.body().contentType().toString());

                service.addFileOrder(SettingsHelper.getToken().getAccess(), 283, description, body).enqueue(new Callback<IdResponse>() {
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

    private void deleteFileOrder() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.deleteFileOrder(SettingsHelper.getToken().getAccess(), 148, 150).enqueue(new Callback<Done>() {
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


    private void acceptOrder() {
        /*ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                final int id = 223;

                service.acceptOrder(SettingsHelper.getToken().getAccess(), id, new OrderAcceptRequest("juygyug", 45, 45)).enqueue(new Callback<Done>() {
                    @Override
                    public void onResponse(Call<Done> call, Response<Done> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());

                            FirebaseMessaging.getInstance().subscribeToTopic("order_exec_" + id);

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
        });*/
    }


    private void messageOrder() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                Log.e("messageOrder", "messageOrder");
                service.messageOrder(SettingsHelper.getToken().getAccess(), 213, new OrderMessageRequest("juygbjhguyug")).enqueue(new Callback<Done>() {
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

    private void messageOrderId() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                Log.e("messageOrder", "messageOrder");
                service.messageOrder(SettingsHelper.getToken().getAccess(), 213, new OrderMessageRequest("juygbjhguyug"), 214).enqueue(new Callback<Done>() {
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

    private void getOrdersAccepts() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.getOrdersAccepts(SettingsHelper.getToken().getAccess(), 213).enqueue(new Callback<List<OrderAcceptResponse>>() {
                    @Override
                    public void onResponse(Call<List<OrderAcceptResponse>> call, Response<List<OrderAcceptResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (OrderAcceptResponse i : response.body()) {
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
                    public void onFailure(Call<List<OrderAcceptResponse>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void markAccepter() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.markAccepter(SettingsHelper.getToken().getAccess(), 223, 5).enqueue(new Callback<Done>() {
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

    private void close() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.close(SettingsHelper.getToken().getAccess(), 161).enqueue(new Callback<Done>() {
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

    private void sendRate() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                RateRequest rateRequest = new RateRequest(RateResponse.RateType.negative, "uhbuyyutytf");
                Log.e("rate", rateRequest.toJson());

                service.sendRate(SettingsHelper.getToken().getAccess(), 223, rateRequest).enqueue(new Callback<Done>() {
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

    private void reviewsId() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.reviewsId(SettingsHelper.getToken().getAccess(), 1).enqueue(new Callback<List<RateResponse>>() {
                    @Override
                    public void onResponse(Call<List<RateResponse>> call, Response<List<RateResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (RateResponse i : response.body()) {
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
                    public void onFailure(Call<List<RateResponse>> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
