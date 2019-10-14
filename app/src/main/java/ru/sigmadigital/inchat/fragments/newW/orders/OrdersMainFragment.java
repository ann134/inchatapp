package ru.sigmadigital.inchat.fragments.newW.orders;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import ru.sigmadigital.inchat.activity.MainActivity;
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

public class OrdersMainFragment extends BaseFragment implements OrderAdapter.OnOrderItemClick, View.OnClickListener {

    private OrderFilterRequest filterRequest;

    private OrderAdapter orderAdapter;

    private View createOrderButton;

    private ImageView sort;
    private ImageView filter;
    private EditText search;
    private RecyclerView recyclerView;

    public static OrdersMainFragment newInstance() {
        return new OrdersMainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main_orders, container, false);
        createOrderButton = v.findViewById(R.id.ll_create_order_button);
        sort = v.findViewById(R.id.imv_sort);
        filter = v.findViewById(R.id.imv_filter);
        search = v.findViewById(R.id.et_search);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(filterRequest == null){
                    filterRequest = new OrderFilterRequest();
                }
                if(!s.toString().equals("")){
                    filterRequest.setSearchSequence(s.toString());
                    getOrders(filterRequest);
                } else {
                    filterRequest.setSearchSequence("-1");
                    getOrders(filterRequest);
                }
            }
        });



        recyclerView = v.findViewById(R.id.rv_orders);
        orderAdapter = new OrderAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
        recyclerView.setAdapter(orderAdapter);

        if (filterRequest != null) {
            getOrders(filterRequest);
        } else {
            getOrders(new OrderFilterRequest());
        }

        createOrderButton.setOnClickListener(this);
        sort.setOnClickListener(this);
        filter.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        search.clearFocus();

    }

    private void getOrders(final OrderFilterRequest filterRequest) {

        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                App.getRetrofitService().getOrders(SettingsHelper.getToken().getAccess(), filterRequest).enqueue(new Callback<List<OrderResponse>>() {

                    @Override
                    public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (OrderResponse i : response.body()) {
                                Log.e("onResponse", " " + i.toJson());
                                Log.e("FilesCount", i.getFiles().size() + "");
                            }
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

    private void showOrders(List<OrderResponse> orderResponses) {
        if (orderResponses != null ) {
            Log.e("ListSize", orderResponses.size() + "");
            orderAdapter.addItems(orderResponses);
            orderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void OnOrderClick(OrderResponse orderResponse) {
        replaceCurrentFragmentWith(getFragmentManager(),
                getParentContainer(OrdersMainFragment.this.getView()),
                OrderInfoFragment.newInstance(orderResponse, SettingsHelper.getUser() == orderResponse.getCreator().getId()),
                true);
        Log.e("ItemClick", "CliCK!");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_create_order_button:
                replaceCurrentFragmentWith(getFragmentManager(), getParentContainer(OrdersMainFragment.this.getView()), CreateOrderFragment.newInstance(), true);
                break;
            case R.id.imv_filter:
                replaceCurrentFragmentWith(getFragmentManager(), getParentContainer(OrdersMainFragment.this.getView()), OrdersFilterFragment.newInstance(filterRequest = (filterRequest != null) ? filterRequest: new OrderFilterRequest(), new OrdersFilterFragment.OnFilterApply() {
                    @Override
                    public void filterApply(OrderFilterRequest filterRequest) {
                        OrdersMainFragment.this.filterRequest = filterRequest;
                    }
                }), true);
                break;

            case R.id.imv_sort:
                getFragmentManager().beginTransaction().add(getParentContainer(OrdersMainFragment.this.getView()), OrdersSortFragment.newInstance(new OrdersSortFragment.OnSortItemClick() {
                    @Override
                    public void onSortClick(int sortType) {
                        if (filterRequest != null) {
                            filterRequest.setSortType(sortType);
                            getOrders(filterRequest);
                            getFragmentManager().popBackStack();
                        } else {
                            filterRequest = new OrderFilterRequest();
                            filterRequest.setSortType(sortType);
                            getOrders(filterRequest);
                            getFragmentManager().popBackStack();
                        }
                    }

                    @Override
                    public void onFragmentShow() {
                        setViewAndChildrenEnabled(OrdersMainFragment.this.getView(), false);
                    }

                    @Override
                    public void onFragmentDismiss() {
                        setViewAndChildrenEnabled(OrdersMainFragment.this.getView(), true);
                    }
                })).addToBackStack("sort").commit();
                break;
        }
    }

    private void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
        if (enabled == false) {
            recyclerView.setLayoutManager(new StopScrollLinearLayoutManager(App.getAppContext()));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
        }
    }

    private static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private class StopScrollLinearLayoutManager extends LinearLayoutManager {

        public StopScrollLinearLayoutManager(Context context) {
            super(context);
        }

        public StopScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public StopScrollLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public boolean canScrollVertically() {
            return false;
        }
    }
}
