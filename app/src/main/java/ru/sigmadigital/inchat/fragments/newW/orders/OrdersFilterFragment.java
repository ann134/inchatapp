package ru.sigmadigital.inchat.fragments.newW.orders;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.customviews.IdCheckBox;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.Category;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.OrderFilterRequest;
import ru.sigmadigital.inchat.models.OrderResponse;
import ru.sigmadigital.inchat.utils.DpPxUtil;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class OrdersFilterFragment extends BaseFragment implements View.OnClickListener {

    public static final int FAST_TASK = 11;
    public static final int SIDE_JOB = 12;

    public static final int FIX_PRICE = 13;
    public static final int TREATY_PRICE = 14;
    public static final int FREE_PRICE = 15;

    private OnFilterApply listener;
    private OrderFilterRequest filterRequest;

    private int priceType = 13;
    private int orderType = 11;

    private TextView fastTask;
    private TextView sideJob;
    private TextView fixPrice;
    private TextView treatyPrice;
    private TextView freePrice;

    private TextView uncheckedAllButton;

    private LinearLayout categoriesContainer;

    private TextView dropFilterButton;
    private TextView saveFilterButton;

    private Switch attachFiles;

    public static OrdersFilterFragment newInstance(OrderFilterRequest oldFilterRequest, OnFilterApply listener){
        OrdersFilterFragment fragment = new OrdersFilterFragment();
        fragment.filterRequest = oldFilterRequest;
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_filter, null);

        fastTask = v.findViewById(R.id.tv_fast_task);
        sideJob = v.findViewById(R.id.tv_side_job);
        fixPrice = v.findViewById(R.id.tv_fix_price);
        treatyPrice = v.findViewById(R.id.tv_treaty_price);
        freePrice = v.findViewById(R.id.tv_free_price);
        uncheckedAllButton = v.findViewById(R.id.tv_unchecked_all);
        categoriesContainer = v.findViewById(R.id.ll_categories_container);
        attachFiles = v.findViewById(R.id.sw_attach_files);
        dropFilterButton = v.findViewById(R.id.tv_drop_filter);
        saveFilterButton = v.findViewById(R.id.tv_save_filter);

        fastTask.setOnClickListener(this);
        sideJob.setOnClickListener(this);
        fixPrice.setOnClickListener(this);
        treatyPrice.setOnClickListener(this);
        freePrice.setOnClickListener(this);
        uncheckedAllButton.setOnClickListener(this);
        dropFilterButton.setOnClickListener(this);
        saveFilterButton.setOnClickListener(this);

        getOrdersCategories();
        initViewsFromOldFilter(filterRequest);


        return v;
    }



    private void initViewsFromOldFilter(OrderFilterRequest request){
        if(request.getOrderType() == OrderResponse.TypeOrders.quick_task || request.getOrderType() == -1){
            sideJob.setBackground(new ColorDrawable(getResources().getColor(android.R.color.transparent, App.getAppContext().getTheme())));
            fastTask.setBackground(getResources().getDrawable(R.drawable.bg_round_corners_32, App.getAppContext().getTheme()));
            fastTask.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent, App.getAppContext().getTheme()), PorterDuff.Mode.ADD);
            fastTask.setTextColor(getResources().getColor(android.R.color.white, App.getAppContext().getTheme()));
            sideJob.setTextColor(getResources().getColor(android.R.color.black, App.getAppContext().getTheme()));
            orderType = FAST_TASK;
        } else {
            fastTask.setBackground(new ColorDrawable(getResources().getColor(android.R.color.transparent, App.getAppContext().getTheme())));
            sideJob.setBackground(getResources().getDrawable(R.drawable.bg_round_corners_32, App.getAppContext().getTheme()));
            sideJob.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent, App.getAppContext().getTheme()), PorterDuff.Mode.ADD);
            sideJob.setTextColor(getResources().getColor(android.R.color.white, App.getAppContext().getTheme()));
            fastTask.setTextColor(getResources().getColor(android.R.color.black, App.getAppContext().getTheme()));
            orderType = SIDE_JOB;
        }

        if(request.getPriceType() == OrderResponse.TypePayment.fix || request.getPriceType() == -1){
            pickPriceType(fixPrice);
            unpickPriceType(treatyPrice);
            unpickPriceType(freePrice);
            priceType = FIX_PRICE;
        } else if(request.getPriceType() == OrderResponse.TypePayment.treaty){
            pickPriceType(treatyPrice);
            unpickPriceType(fixPrice);
            unpickPriceType(freePrice);
            priceType = TREATY_PRICE;
        } else {
            pickPriceType(freePrice);
            unpickPriceType(treatyPrice);
            unpickPriceType(fixPrice);
            priceType = FREE_PRICE;
        }

        if(request.isAttachedFiles()){
            attachFiles.setChecked(true);
        } else {
            attachFiles.setChecked(false);
        }
    }

    private void initCategoriesViewFromOldFilter(OrderFilterRequest oldFilterRequest){
        for(Long categoryId : oldFilterRequest.getCategories()){
            for(int i = 0; i < categoriesContainer.getChildCount(); i++){
                IdCheckBox box = categoriesContainer.getChildAt(i).findViewById(R.id.cb_box);
                if(box.getServerId() == categoryId){
                    box.setChecked(true);
                }
            }
        }

    }

    private void getOrdersCategories() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                App.getRetrofitService().getOrdersCategories(SettingsHelper.getToken().getAccess()).enqueue(new Callback<List<Category>>() {

                    @Override
                    public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {


                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            for (Category category : response.body()) {
                                Log.e("onResponse", " " + category.toJson());
                                View idView = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_category_checkbox, null);
                                IdCheckBox box = idView.findViewById(R.id.cb_box);
                                TextView categoryName = idView.findViewById(R.id.tv_name);
                                box.setServerId(category.getId());
                                categoryName.setText(category.getName());
                                int states[][] = {{android.R.attr.state_checked}, {}};
                                int colors[] = {getResources().getColor(R.color.blue,App.getAppContext().getTheme()), getResources().getColor(R.color.text_gray,App.getAppContext().getTheme())};
                                box.setButtonTintList(new ColorStateList(states, colors));

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.bottomMargin = DpPxUtil.getPixelsFromDp(16);
                                categoriesContainer.addView(idView, params);

                            }
                            initCategoriesViewFromOldFilter(filterRequest);


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

    @Override
    public void onClick(View v) {
        fastTask.setOnClickListener(this);
        sideJob.setOnClickListener(this);
        fixPrice.setOnClickListener(this);
        treatyPrice.setOnClickListener(this);
        freePrice.setOnClickListener(this);
        uncheckedAllButton.setOnClickListener(this);
        dropFilterButton.setOnClickListener(this);
        saveFilterButton.setOnClickListener(this);


        switch (v.getId()){
            case R.id.tv_side_job :
                fastTask.setBackground(new ColorDrawable(getResources().getColor(android.R.color.transparent, App.getAppContext().getTheme())));
                sideJob.setBackground(getResources().getDrawable(R.drawable.bg_round_corners_32, App.getAppContext().getTheme()));
                sideJob.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent, App.getAppContext().getTheme()), PorterDuff.Mode.ADD);
                sideJob.setTextColor(getResources().getColor(android.R.color.white, App.getAppContext().getTheme()));
                fastTask.setTextColor(getResources().getColor(android.R.color.black, App.getAppContext().getTheme()));
                orderType = SIDE_JOB;
                break;

            case R.id.tv_fast_task :
                sideJob.setBackground(new ColorDrawable(getResources().getColor(android.R.color.transparent, App.getAppContext().getTheme())));
                fastTask.setBackground(getResources().getDrawable(R.drawable.bg_round_corners_32, App.getAppContext().getTheme()));
                fastTask.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent, App.getAppContext().getTheme()), PorterDuff.Mode.ADD);
                fastTask.setTextColor(getResources().getColor(android.R.color.white, App.getAppContext().getTheme()));
                sideJob.setTextColor(getResources().getColor(android.R.color.black, App.getAppContext().getTheme()));
                orderType = FAST_TASK;
                break;

            case R.id.tv_fix_price:
                pickPriceType(fixPrice);
                unpickPriceType(treatyPrice);
                unpickPriceType(freePrice);
                priceType = FIX_PRICE;
                break;

            case R.id.tv_treaty_price:
                pickPriceType(treatyPrice);
                unpickPriceType(fixPrice);
                unpickPriceType(freePrice);
                priceType = TREATY_PRICE;
                break;

            case R.id.tv_free_price:
                pickPriceType(freePrice);
                unpickPriceType(treatyPrice);
                unpickPriceType(fixPrice);
                priceType = FREE_PRICE;
                break;

            case R.id.tv_unchecked_all:
                for (int i = 0; i < categoriesContainer.getChildCount(); i++){
                    View view = categoriesContainer.getChildAt(i);
                    ((IdCheckBox)view.findViewById(R.id.cb_box)).setChecked(false);
                }
                break;

            case R.id.tv_drop_filter:
                listener.filterApply(new OrderFilterRequest());
                getFragmentManager().popBackStack();
                break;

            case R.id.tv_save_filter:
                OrderFilterRequest request = new OrderFilterRequest();
                if(orderType == FAST_TASK){
                    request.setOrderType(OrderResponse.TypeOrders.quick_task);
                } else {
                    request.setOrderType(OrderResponse.TypeOrders.long_work);
                }

                if(priceType == FIX_PRICE){
                    request.setPriceType(OrderResponse.TypePayment.fix);
                } else if(priceType == TREATY_PRICE){
                    request.setPriceType(OrderResponse.TypePayment.treaty);
                } else {
                    request.setPriceType(OrderResponse.TypePayment.free);
                }

                for(int i = 0; i < categoriesContainer.getChildCount(); i++){
                    IdCheckBox box = categoriesContainer.getChildAt(i).findViewById(R.id.cb_box);
                    if(box.isChecked()){
                        request.getCategories().add(box.getServerId());
                    }
                }
                request.setAttachedFiles(attachFiles.isChecked());

                listener.filterApply(request);

                getFragmentManager().popBackStack();

                break;
        }
    }

    private void pickPriceType(TextView view){
        view.setBackground(getResources().getDrawable(R.drawable.bg_round_corners_32, App.getAppContext().getTheme()));
        view.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent, App.getAppContext().getTheme()), PorterDuff.Mode.ADD);
        view.setTextColor(getResources().getColor(android.R.color.white, App.getAppContext().getTheme()));
    }

    private void unpickPriceType(TextView view){
        view.setBackground(getResources().getDrawable(R.drawable.bg_round_corners_32, App.getAppContext().getTheme()));
        view.getBackground().setColorFilter(getResources().getColor(R.color.gray_light_bg, App.getAppContext().getTheme()), PorterDuff.Mode.ADD);
        view.setTextColor(getResources().getColor(android.R.color.black, App.getAppContext().getTheme()));
    }

    public interface OnFilterApply{
        void filterApply(OrderFilterRequest filterRequest);
    }


}
