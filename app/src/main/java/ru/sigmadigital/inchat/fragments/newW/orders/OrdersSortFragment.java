package ru.sigmadigital.inchat.fragments.newW.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.activity.MainActivity;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.OrderFilterRequest;

public class OrdersSortFragment extends BaseFragment implements View.OnClickListener {

    private OnSortItemClick listener;

    private TextView myOrders;
    private TextView newOrders;
    private TextView increasePrice;
    private TextView reductionPrice;

    public static OrdersSortFragment newInstance(OnSortItemClick listener){
        OrdersSortFragment fragment = new OrdersSortFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sort_orders, null);
        myOrders = v.findViewById(R.id.tv_my_orders);
        newOrders = v.findViewById(R.id.tv_new_orders);
        increasePrice = v.findViewById(R.id.tv_increase_price);
        reductionPrice = v.findViewById(R.id.tv_reduction_price);

        myOrders.setOnClickListener(this);
        newOrders.setOnClickListener(this);
        increasePrice.setOnClickListener(this);
        reductionPrice.setOnClickListener(this);
        return v;
    }



    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).hideBottomNavigationBar();
        listener.onFragmentShow();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).showBottomNavigationBar();
        listener.onFragmentDismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_my_orders:
                listener.onSortClick(OrderFilterRequest.SORT_MY_ORDERS);
                break;
            case R.id.tv_new_orders:
                listener.onSortClick(OrderFilterRequest.SORT_NEW);
                break;
            case R.id.tv_increase_price:
                listener.onSortClick(OrderFilterRequest.SORT_INCREASE_PRICE);
                break;
            case R.id.tv_reduction_price:
                listener.onSortClick(OrderFilterRequest.SORT_REDUCTION_PRICE);
                break;
        }
    }



    interface OnSortItemClick{
        void onSortClick(int sortType);
        void onFragmentDismiss();
        void onFragmentShow();
    }
}
