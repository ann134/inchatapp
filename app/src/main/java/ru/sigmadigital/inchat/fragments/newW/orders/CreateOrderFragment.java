package ru.sigmadigital.inchat.fragments.newW.orders;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.fragments.old.chat.FilePickerFragment;
import ru.sigmadigital.inchat.models.Category;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.IdResponse;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.OrderFileDescriptionRequest;
import ru.sigmadigital.inchat.models.OrderRequest;
import ru.sigmadigital.inchat.models.OrderResponse;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class CreateOrderFragment extends BaseFragment implements FilePickerFragment.OnFileSelected {
    private static final int FIX_PRICE = 10;
    private static final int TREATY_PRICE = 11;
    private int priceType = FIX_PRICE;

    private long categoryId = 1;

    private List<File> filesList = new ArrayList<>();
    private int uploadFilesCounter = 0;


    private FrameLayout waitContainer;
    private ProgressBar progressBar;
    private TextView waitFilesCount;

    private LinearLayout filesContainer;
    private RadioButton payedOrder;
    private RadioButton notPayedOrder;

    private TextView fixPrice;
    private TextView treatyPrice;
    private TextView uploadFileButton;
    private TextView createOrderButton;
    private TextView category;

    private Date deadline;
    private EditText deadlineDay;
    private EditText deadlineTime;
    private EditText price;
    private EditText orderTittle;
    private EditText orderDesc;

    private View chooseCategory;

    public static CreateOrderFragment newInstance() {
        return new CreateOrderFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_create, null);
        filesContainer = v.findViewById(R.id.ll_files_container);
        deadlineDay = v.findViewById(R.id.et_deadline_day);
        deadlineTime = v.findViewById(R.id.et_deadline_time);
        payedOrder = v.findViewById(R.id.rb_payed_order);
        notPayedOrder = v.findViewById(R.id.rb_not_payed_order);
        fixPrice = v.findViewById(R.id.tv_fix_price);
        treatyPrice = v.findViewById(R.id.tv_treaty_price);
        uploadFileButton = v.findViewById(R.id.tv_upload_files_button);
        createOrderButton = v.findViewById(R.id.tv_create_order_button);
        price = v.findViewById(R.id.et_price);
        orderTittle = v.findViewById(R.id.et_order_title);
        orderDesc = v.findViewById(R.id.et_order_desc);
        chooseCategory = v.findViewById(R.id.ll_category);
        category = v.findViewById(R.id.tv_category);

        waitContainer = v.findViewById(R.id.fl_wait_container);
        progressBar = v.findViewById(R.id.progress_bar);
        waitFilesCount = v.findViewById(R.id.tv_wait_files_count);


        initListeners();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (priceType == FIX_PRICE) {
            treatyPrice.setBackground(new ColorDrawable(getResources().getColor(android.R.color.transparent, App.getAppContext().getTheme())));
            fixPrice.setBackground(getResources().getDrawable(R.drawable.bg_round_corners_32, App.getAppContext().getTheme()));
            fixPrice.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent, App.getAppContext().getTheme()), PorterDuff.Mode.ADD);
            fixPrice.setTextColor(getResources().getColor(android.R.color.white, App.getAppContext().getTheme()));
            treatyPrice.setTextColor(getResources().getColor(android.R.color.black, App.getAppContext().getTheme()));
        }
        if (priceType == TREATY_PRICE) {
            fixPrice.setBackground(new ColorDrawable(getResources().getColor(android.R.color.transparent, App.getAppContext().getTheme())));
            treatyPrice.setBackground(getResources().getDrawable(R.drawable.bg_round_corners_32, App.getAppContext().getTheme()));
            treatyPrice.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent, App.getAppContext().getTheme()), PorterDuff.Mode.ADD);
            treatyPrice.setTextColor(getResources().getColor(android.R.color.white, App.getAppContext().getTheme()));
            fixPrice.setTextColor(getResources().getColor(android.R.color.black, App.getAppContext().getTheme()));
        }
        for (File file : filesList) {
            addFile(file);
        }
    }

    private void initListeners() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.et_deadline_day:
                        final Calendar calendar = Calendar.getInstance();
                        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                calendar.set(i,i1,i2);
                                setDeadlineDay(calendar);
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

                        break;
                    case R.id.et_deadline_time:
                        final Calendar calendar2 = Calendar.getInstance();
                        TimePickerDialog dialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                calendar2.set(Calendar.HOUR_OF_DAY, i);
                                calendar2.set(Calendar.MINUTE, i1);
                                setDeadlineTime(calendar2);
                            }
                        }, 12, 0, true);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.show();

                        break;
                    case R.id.rb_payed_order:
                        payedOrder.setChecked(true);
                        notPayedOrder.setChecked(false);

                        price.setClickable(true);
                        price.setEnabled(true);
                        ((TextView)v.getRootView().findViewById(R.id.tv_price_title)).setTextColor(getResources().getColor(R.color.black, App.getAppContext().getTheme()));
                        treatyPrice.setBackground(new ColorDrawable(getResources().getColor(android.R.color.transparent, App.getAppContext().getTheme())));
                        treatyPrice.setTextColor(getResources().getColor(android.R.color.black, App.getAppContext().getTheme()));
                        treatyPrice.setClickable(true);
                        treatyPrice.setEnabled(true);
                        fixPrice.setBackground(getResources().getDrawable(R.drawable.bg_round_corners_32, App.getAppContext().getTheme()));
                        fixPrice.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent, App.getAppContext().getTheme()), PorterDuff.Mode.ADD);
                        fixPrice.setTextColor(getResources().getColor(android.R.color.white, App.getAppContext().getTheme()));
                        fixPrice.setClickable(true);
                        fixPrice.setEnabled(true);
                        break;
                    case R.id.rb_not_payed_order:
                        notPayedOrder.setChecked(true);
                        payedOrder.setChecked(false);

                        price.setText("");
                        price.setClickable(false);
                        price.setEnabled(false);
                        ((TextView)v.getRootView().findViewById(R.id.tv_price_title)).setTextColor(getResources().getColor(R.color.text_gray, App.getAppContext().getTheme()));
                        treatyPrice.setBackground(new ColorDrawable(getResources().getColor(android.R.color.transparent, App.getAppContext().getTheme())));
                        treatyPrice.setTextColor(getResources().getColor(R.color.colorPrimaryDark, App.getAppContext().getTheme()));
                        treatyPrice.setClickable(false);
                        treatyPrice.setEnabled(false);
                        fixPrice.setBackground(new ColorDrawable(getResources().getColor(android.R.color.transparent, App.getAppContext().getTheme())));
                        fixPrice.setTextColor(getResources().getColor(R.color.colorPrimaryDark, App.getAppContext().getTheme()));
                        fixPrice.setClickable(false);
                        fixPrice.setEnabled(false);
                        break;
                    case R.id.tv_fix_price:
                        treatyPrice.setBackground(new ColorDrawable(getResources().getColor(android.R.color.transparent, App.getAppContext().getTheme())));
                        fixPrice.setBackground(getResources().getDrawable(R.drawable.bg_round_corners_32, App.getAppContext().getTheme()));
                        fixPrice.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent, App.getAppContext().getTheme()), PorterDuff.Mode.ADD);
                        fixPrice.setTextColor(getResources().getColor(android.R.color.white, App.getAppContext().getTheme()));
                        treatyPrice.setTextColor(getResources().getColor(android.R.color.black, App.getAppContext().getTheme()));
                        priceType = FIX_PRICE;
                        break;
                    case R.id.tv_treaty_price:
                        fixPrice.setBackground(new ColorDrawable(getResources().getColor(android.R.color.transparent, App.getAppContext().getTheme())));
                        treatyPrice.setBackground(getResources().getDrawable(R.drawable.bg_round_corners_32, App.getAppContext().getTheme()));
                        treatyPrice.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent, App.getAppContext().getTheme()), PorterDuff.Mode.ADD);
                        treatyPrice.setTextColor(getResources().getColor(android.R.color.white, App.getAppContext().getTheme()));
                        fixPrice.setTextColor(getResources().getColor(android.R.color.black, App.getAppContext().getTheme()));
                        priceType = TREATY_PRICE;
                        break;
                    case R.id.tv_upload_files_button:
                        replaceCurrentFragmentWith(getFragmentManager(), getParentContainer(CreateOrderFragment.this.getView()), FilePickerFragment.newInstance(CreateOrderFragment.this), true);
                        break;
                    case R.id.tv_create_order_button:
                        addOrder();
                        break;

                    case R.id.ll_category:
                        ChooseCategoryDialog.newInstance(new ChooseCategoryDialog.OnCategoryClick() {
                            @Override
                            public void onCategoryClick(Category c) {
                                category.setText(c.getName());
                                categoryId = c.getId();
                            }
                        }).show(getFragmentManager(), "dialog");
                        break;

                }


            }
        };

        deadlineDay.setOnClickListener(listener);
        deadlineTime.setOnClickListener(listener);
        payedOrder.setOnClickListener(listener);
        notPayedOrder.setOnClickListener(listener);
        fixPrice.setOnClickListener(listener);
        treatyPrice.setOnClickListener(listener);
        uploadFileButton.setOnClickListener(listener);
        createOrderButton.setOnClickListener(listener);
        chooseCategory.setOnClickListener(listener);

    }

    private void addFile(File file) {
        final View inflate = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_file_in_create, null);
        final TextView fileName = inflate.findViewById(R.id.tv_file_name);
        TextView date = inflate.findViewById(R.id.tv_upload_date);
        TextView kbCount = inflate.findViewById(R.id.tv_kb_count);
        TextView deleteButton = inflate.findViewById(R.id.tv_delete_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflate.setVisibility(View.GONE);
                for (int i = 0; i < filesList.size(); i++) {
                    if (filesList.get(i).getName().equals(fileName.getText().toString())) {
                        filesList.remove(filesList.get(i));
                        break;
                    }
                }
            }
        });

        fileName.setText(file.getName());
        date.setText("13 дек 2016 в 12:32");
        kbCount.setText(file.length() / 1024 + " КБ");
        filesContainer.addView(inflate);

    }


    private void addOrder() {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                OrderRequest orderRequest = new OrderRequest();
                orderRequest.setCategory(categoryId);
                orderRequest.setTitle(orderTittle.getText().toString());
                orderRequest.setDescription(orderDesc.getText().toString());
                long twoDays = 1000 * 60 * 60 * 24 * 2;
                if(deadline == null){
                    Toast.makeText(App.getAppContext(), "Укажите дедлайн", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (deadline.getTime() < System.currentTimeMillis() + twoDays) {
                    orderRequest.setOrderType(OrderResponse.TypeOrders.quick_task);
                } else {
                    orderRequest.setOrderType(OrderResponse.TypeOrders.long_work);
                }
                orderRequest.setActuality(deadline.getTime());

                if (payedOrder.isChecked()) {
                    if (priceType == FIX_PRICE) {
                        orderRequest.setPriceType(OrderResponse.TypePayment.fix);
                    }
                    if (priceType == TREATY_PRICE) {
                        orderRequest.setPriceType(OrderResponse.TypePayment.treaty);
                    }

                } else {
                    orderRequest.setPriceType(OrderResponse.TypePayment.free);
                }
                if(!price.getText().toString().equals("")) {
                    orderRequest.setPrice(Integer.parseInt(price.getText().toString()));
                } else {
                    orderRequest.setPrice(0);
                }
                Log.e("send", orderRequest.toJson());

                App.getRetrofitService().addOrder(SettingsHelper.getToken().getAccess(), orderRequest).enqueue(new Callback<IdResponse>() {

                    @Override
                    public void onResponse(Call<IdResponse> call, Response<IdResponse> response) {
                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());

                            int height = CreateOrderFragment.this.getView().getHeight();
                            ((NestedScrollView) CreateOrderFragment.this.getView().findViewById(R.id.ns_root)).smoothScrollTo(0, height);
                            if (!filesList.isEmpty()) {
                                for (File file : filesList) {
                                    addFileOrder(response.body().getId(), file);
                                }
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getFragmentManager().popBackStack();
                                    }
                                }, 1000);
                            }

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


    private void scrollToView(final NestedScrollView scrollViewParent, final View view) {
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        WindowManager wm = (WindowManager) App.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        scrollViewParent.smoothScrollTo(childOffset.x, 0);
    }

    private void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }


    private void addFileOrder(final long orderId, final File file) {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                OrderFileDescriptionRequest d = new OrderFileDescriptionRequest(file.getName());
                RequestBody description = RequestBody.create(MediaType.parse("text/plain"), d.getName());

                waitContainer.setVisibility(View.VISIBLE);
                waitFilesCount.setText(String.format("Загрузка фалов 0 из %d", filesList.size()));

                Log.e("body", body.headers().toString());
                Log.e("body", body.body().contentType().toString());

                App.getRetrofitService().addFileOrder(SettingsHelper.getToken().getAccess(), orderId, description, body).enqueue(new Callback<IdResponse>() {
                    @Override
                    public void onResponse(Call<IdResponse> call, Response<IdResponse> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());

                            uploadFilesCounter++;
                            waitFilesCount.setText(String.format("Загрузка фалов %d из %d", uploadFilesCounter, filesList.size()));

                            if (uploadFilesCounter == filesList.size()) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getFragmentManager().popBackStack();
                                    }
                                }, 1000);
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
                    public void onFailure(Call<IdResponse> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(App.getAppContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public void onFileSelected(File file) {
        filesList.add(file);
    }

    private void setDeadlineDay(Calendar calendar){
        if(deadline != null){
            Calendar currTime = Calendar.getInstance();
            currTime.setTime(deadline);
            currTime.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            if(currTime.getTime().getTime() > System.currentTimeMillis()){
                deadline = currTime.getTime();
                deadlineDay.setText(String.format("%02d.%02d.%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)));
            } else {
                Toast.makeText(App.getAppContext(), "Некорректная дата", Toast.LENGTH_SHORT).show();
            }
        } else {
            if(calendar.getTime().getTime() > System.currentTimeMillis()){
                deadline = calendar.getTime();
                deadlineDay.setText(String.format("%02d.%02d.%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)));
            } else {
                Toast.makeText(App.getAppContext(), "Некорректная дата", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void setDeadlineTime(Calendar calendar){
        deadlineTime.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        if(deadline != null){
            Calendar currDay = Calendar.getInstance();
            currDay.setTime(deadline);
            currDay.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            currDay.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            deadline = currDay.getTime();
            if(currDay.getTime().getTime() > System.currentTimeMillis()){
                deadline = currDay.getTime();
                deadlineTime.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
            } else {
                Toast.makeText(App.getAppContext(), "Некорректное время", Toast.LENGTH_SHORT).show();
            }
        } else {
            deadline = calendar.getTime();
            if(calendar.getTime().getTime() > System.currentTimeMillis()){
                deadline = calendar.getTime();
                deadlineTime.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
            } else {
                Toast.makeText(App.getAppContext(), "Некорректная время", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
