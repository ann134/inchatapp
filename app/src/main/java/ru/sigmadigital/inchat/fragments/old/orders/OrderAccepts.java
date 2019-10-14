package ru.sigmadigital.inchat.fragments.old.orders;

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
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.OrderAcceptResponse;
import ru.sigmadigital.inchat.models.OrderResponse;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class OrderAccepts extends BaseFragment implements OrdersAcceptsAdapter.OnAcceptItemClick {

    private MyRetrofit.ServiceManager service;


    OrdersAcceptsAdapter acceptsAdapter;

    OrderResponse orderResponse;

    public static OrderAccepts newInstance(OrderResponse orderResponse) {
        Bundle args = new Bundle();
        args.putSerializable("orderResponse", orderResponse);
        OrderAccepts fragment = new OrderAccepts();
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
        final View v = inflater.inflate(R.layout.fragment_order_accepts, container, false);

        service = MyRetrofit.getInstance();

        RecyclerView recyclerView = v.findViewById(R.id.rv_activity);
        acceptsAdapter = new OrdersAcceptsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
        recyclerView.setAdapter(acceptsAdapter);


        getOrdersAccepts();

        return v;
    }




    private void getOrdersAccepts() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                service.getOrdersAccepts(SettingsHelper.getToken().getAccess(), orderResponse.getId()).enqueue(new Callback<List<OrderAcceptResponse>>() {
                    @Override
                    public void onResponse(Call<List<OrderAcceptResponse>> call, Response<List<OrderAcceptResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (OrderAcceptResponse i : response.body()) {
                                Log.e("onResponse", " " + i.toJson());
                            }
                            showAccepts(response.body());

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
                        Toast.makeText(App.getAppContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showAccepts(List<OrderAcceptResponse> orderResponses){
        if (orderResponses != null && orderResponses.size() > 0){
            Log.e("accepts" , orderResponses.size()+ "");
            acceptsAdapter.setItemList(orderResponses, this);
            acceptsAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void OnAcceptClick(OrderAcceptResponse acceptResponse) {

    }
}