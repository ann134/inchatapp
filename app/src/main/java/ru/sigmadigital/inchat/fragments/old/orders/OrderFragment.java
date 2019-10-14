package ru.sigmadigital.inchat.fragments.old.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.OrderResponse;
import ru.sigmadigital.inchat.api.MyRetrofit;

public class OrderFragment extends BaseFragment implements View.OnClickListener {

    private MyRetrofit.ServiceManager service;

    OrderResponse orderResponse;

    TextView id;
    TextView title;
    TextView description;
    TextView orderType;
    TextView priceType;
    TextView price;
    TextView actuality;
    TextView files;
    TextView creator;
    TextView createdAt;
    TextView updatedAt;
    TextView responsesCount;
    TextView viewsCount;


    public static OrderFragment newInstance(OrderResponse orderResponse) {
        Bundle args = new Bundle();
        args.putSerializable("orderResponse", orderResponse);
        OrderFragment fragment = new OrderFragment();
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
        final View v = inflater.inflate(R.layout.fragment_order, container, false);

        service = MyRetrofit.getInstance();

        id = v.findViewById(R.id.id);
        title = v.findViewById(R.id.tv_title);
        description = v.findViewById(R.id.description);
        orderType = v.findViewById(R.id.orderType);
        priceType = v.findViewById(R.id.priceType);
        price = v.findViewById(R.id.price);
        actuality = v.findViewById(R.id.actuality);
        files = v.findViewById(R.id.files);
        creator = v.findViewById(R.id.creator);
        createdAt = v.findViewById(R.id.createdAt);
        updatedAt = v.findViewById(R.id.updatedAt);
        responsesCount = v.findViewById(R.id.responsesCount);
        viewsCount = v.findViewById(R.id.viewsCount);

        id.setText("id: " + orderResponse.getId());
        title.setText("title: " + orderResponse.getTitle());
        description.setText("description: " + orderResponse.getDescription());
        orderType.setText("orderType: " + orderResponse.getOrderType());
        priceType.setText("priceType: " + orderResponse.getPriceType());
        price.setText("price: " + orderResponse.getPrice());
        actuality.setText("actuality: " + orderResponse.getActuality());
        files.setText("files: " + orderResponse.getFiles().size());
        creator.setText("creator: " + orderResponse.getCreator().getName());
        createdAt.setText("createdAt: " + orderResponse.getCreatedAt());
        updatedAt.setText("updatedAt: " + orderResponse.getUpdatedAt());
        responsesCount.setText("responsesCount: " + orderResponse.getResponsesCount());
        viewsCount.setText("viewsCount: " + orderResponse.getViewsCount());


        v.findViewById(R.id.btn_accept_order).setOnClickListener(this);

        v.findViewById(R.id.btn_accepts).setOnClickListener(this);



        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept_order: {
                acceptOrder();
                break;
            }


            case R.id.btn_accepts: {
                replaceCurrentFragmentWith(getFragmentManager(),
                        R.id.fragment_container,
                        OrderAccepts.newInstance(orderResponse),
                        true);
                break;
            }
        }
    }




    private void acceptOrder() {
        replaceCurrentFragmentWith(getFragmentManager(),
                R.id.fragment_container,
                AcceptOrderFragment.newInstance(orderResponse),
                true);
    }




}
