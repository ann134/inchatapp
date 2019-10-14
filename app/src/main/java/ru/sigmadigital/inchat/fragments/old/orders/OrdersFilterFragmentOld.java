package ru.sigmadigital.inchat.fragments.old.orders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.Category;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.OrderFilterRequest;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;


public class OrdersFilterFragmentOld extends BaseFragment implements View.OnClickListener {

    private MyRetrofit.ServiceManager service;

    TextView categories;

    private EditText category;
    private EditText page;
    private EditText type;
    private EditText minPrice;
    private EditText maxPrice;

    public static OrdersFilterFragmentOld newInstance() {
        return new OrdersFilterFragmentOld();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_orders_filter_old, container, false);

        service = MyRetrofit.getInstance();

        categories = v.findViewById(R.id.tv_categories);

        category = v.findViewById(R.id.et_category);
        page = v.findViewById(R.id.et_page);
        type = v.findViewById(R.id.et_type);
        minPrice = v.findViewById(R.id.et_minPrice);
        maxPrice = v.findViewById(R.id.et_maxPrice);

        v.findViewById(R.id.btn_get_orders).setOnClickListener(this);
        getOrdersCategories();

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_orders: {
                getOrders();
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
                            String s = "";
                            for (Category i : response.body()) {
                                Log.e("onResponse", " " + i.toJson());
                                s = s + i.toJson() + "\n";
                            }
                            categories.setText(s);

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
        OrderFilterRequest orderFilterRequest = new OrderFilterRequest();
        replaceCurrentFragmentWith(getFragmentManager(),
                R.id.fragment_container,
                OrdersFragment.newInstance(orderFilterRequest),
                true);
    }


}
