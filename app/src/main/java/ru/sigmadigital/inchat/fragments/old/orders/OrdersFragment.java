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
import ru.sigmadigital.inchat.adapters.OrderAdapter;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.OrderFilterRequest;
import ru.sigmadigital.inchat.models.OrderResponse;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class OrdersFragment extends BaseFragment implements OrderAdapter.OnOrderItemClick {

    private MyRetrofit.ServiceManager service;

    OrderFilterRequest orderFilterRequest;
    OrderAdapter orderAdapter;

    public static OrdersFragment newInstance(OrderFilterRequest orderFilterRequest) {
        Bundle args = new Bundle();
        args.putSerializable("orderFilterRequest", orderFilterRequest);
        OrdersFragment fragment = new OrdersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey("orderFilterRequest")) {
                orderFilterRequest = (OrderFilterRequest)getArguments().getSerializable("orderFilterRequest");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_orders, container, false);

        service = MyRetrofit.getInstance();

        RecyclerView recyclerView = v.findViewById(R.id.rv_activity);
        orderAdapter = new OrderAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
        recyclerView.setAdapter(orderAdapter);


        getOrders();

        return v;
    }

    private void getOrders() {
        Log.e("orderfilter", orderFilterRequest.toJson());

        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                service.getOrders(SettingsHelper.getToken().getAccess(), orderFilterRequest).enqueue(new Callback<List<OrderResponse>>() {

                    @Override
                    public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (OrderResponse i : response.body()) {
                                Log.e("onResponse", " " + i.toJson());
                            }
                            //orderAdapter.setItemList(response.body());
                            showOrders(response.body());

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

    private void showOrders(List<OrderResponse> orderResponses){
      /*  if (orderResponses != null && orderResponses.size() > 0){
            Log.e("orders" , orderResponses.size()+ "");
            orderAdapter.setItemList(orderResponses);
            orderAdapter.notifyDataSetChanged();
        }*/
    }

    @Override
    public void OnOrderClick(OrderResponse orderResponse) {
        replaceCurrentFragmentWith(getFragmentManager(),
                R.id.fragment_container,
                OrderFragment.newInstance(orderResponse),
                true);
    }
}
