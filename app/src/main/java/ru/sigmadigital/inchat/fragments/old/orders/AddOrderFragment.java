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
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.IdResponse;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.OrderRequest;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class AddOrderFragment extends BaseFragment implements View.OnClickListener {


    private MyRetrofit.ServiceManager service;

    //OrderResponse orderResponse;


    EditText orderType;
    EditText priceType;
    EditText price;
    EditText category;
    EditText title;
    EditText description;
    EditText actuality;


    public static AddOrderFragment newInstance() {
           /* Bundle args = new Bundle();
            args.putSerializable("orderResponse", orderResponse);*/
        AddOrderFragment fragment = new AddOrderFragment();
        /* fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey("orderResponse")) {
                //orderResponse = (OrderResponse) getArguments().getSerializable("orderResponse");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_add_new_order, container, false);

        service = MyRetrofit.getInstance();


        title = v.findViewById(R.id.tv_title);
        description = v.findViewById(R.id.description);
        orderType = v.findViewById(R.id.orderType);
        priceType = v.findViewById(R.id.priceType);
        price = v.findViewById(R.id.price);
        actuality = v.findViewById(R.id.actuality);
        category = v.findViewById(R.id.category);

        v.findViewById(R.id.btn_add_order).setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_order: {
                addOrder();
                break;
            }
        }
    }


    private void addOrder() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                OrderRequest orderRequest = new OrderRequest();
                orderRequest.setTitle(title.getText().toString());
                orderRequest.setDescription(description.getText().toString());
                orderRequest.setCategory(Integer.parseInt(category.getText().toString()));
                orderRequest.setOrderType(Integer.parseInt(orderType.getText().toString()));
                orderRequest.setPriceType(Integer.parseInt(priceType.getText().toString()));
                orderRequest.setPrice(Integer.parseInt(price.getText().toString()));
//                orderRequest.setActuality(actuality.getText().toString());

                Log.e("send", orderRequest.toJson());

                service.addOrder(SettingsHelper.getToken().getAccess(), orderRequest).enqueue(new Callback<IdResponse>() {

                    @Override
                    public void onResponse(Call<IdResponse> call, Response<IdResponse> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());

                            FirebaseMessaging.getInstance().subscribeToTopic("order_cust_" + response.body().getId());

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
                    public void onFailure(Call<IdResponse> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
