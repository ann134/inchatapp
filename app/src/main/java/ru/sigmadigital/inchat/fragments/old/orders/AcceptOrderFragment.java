package ru.sigmadigital.inchat.fragments.old.orders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.Done;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.OrderAcceptRequest;
import ru.sigmadigital.inchat.models.OrderResponse;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class AcceptOrderFragment extends BaseFragment  implements View.OnClickListener  {

    private MyRetrofit.ServiceManager service;

    OrderResponse orderResponse;

    EditText text;
    EditText price;
    EditText days;

    public static AcceptOrderFragment newInstance(OrderResponse orderResponse) {
        Bundle args = new Bundle();
        args.putSerializable("orderResponse", orderResponse);
        AcceptOrderFragment fragment = new AcceptOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey("orderResponse")) {
                orderResponse = (OrderResponse)getArguments().getSerializable("orderResponse");
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_order_accept, container, false);

        service = MyRetrofit.getInstance();

        text = v.findViewById(R.id.text);
        price = v.findViewById(R.id.price);
        days = v.findViewById(R.id.days);




        v.findViewById(R.id.btn_accept).setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept: {
                acceptOrder();
                break;
            }
        }
    }




    private void acceptOrder() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                service.acceptOrder(SettingsHelper.getToken().getAccess(), orderResponse.getId(), new OrderAcceptRequest(text.getText().toString(), Integer.parseInt(price.getText().toString()), Integer.parseInt(days.getText().toString()))).enqueue(new Callback<Done>() {
                    @Override
                    public void onResponse(Call<Done> call, Response<Done> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());

                            FirebaseMessaging.getInstance().subscribeToTopic("order_exec_" + orderResponse.getId());

                            getActivity().onBackPressed();

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
