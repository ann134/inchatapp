package ru.sigmadigital.inchat.fragments.newW.orders;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.Done;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.FileResponse;
import ru.sigmadigital.inchat.models.IdResponse;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.OrderFileDescriptionRequest;
import ru.sigmadigital.inchat.models.OrderResponse;
import ru.sigmadigital.inchat.utils.DpPxUtil;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.utils.FileManager;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class OrderInfoFragment extends BaseFragment {

    private OrderResponse currOrder;
    private boolean isOwner;

    private TextView subjects;
    private TextView price;
    private TextView actionButton;
    private TextView taskType;
    private TextView deaedline;

    private ImageView taskTypeIcon;
    private ImageView actualityIcon;

    private View pastTime;
    private View futureTime;

    private TextView createOrderDate;
    private TextView createOrderTime;
    private TextView deadlineOrderDate;
    private TextView deadlineOrderTime;

    private LinearLayout orderDescTitleContainer;
    private TextView orderDesc;

    private LinearLayout orderFilesTitleContainer;
    private LinearLayout orderFilesItemsContainer;

    private TextView saveAsArchiveButton;


    public static OrderInfoFragment newInstance(OrderResponse orderResponse, boolean isOwner) {
        OrderInfoFragment fragment = new OrderInfoFragment();
        fragment.currOrder = orderResponse;
        fragment.isOwner = isOwner;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_info, null);
        subjects = v.findViewById(R.id.tv_subjects);
        price = v.findViewById(R.id.tv_price);
        actionButton = v.findViewById(R.id.tv_action_button);
        taskType = v.findViewById(R.id.tv_task_type);
        deaedline = v.findViewById(R.id.tv_deadline);
        taskTypeIcon = v.findViewById(R.id.imv_task_type);
        actualityIcon = v.findViewById(R.id.imv_actuality);
        pastTime = v.findViewById(R.id.past_time);
        futureTime = v.findViewById(R.id.future_time);
        createOrderDate = v.findViewById(R.id.tv_create_date);
        createOrderTime = v.findViewById(R.id.tv_create_time);
        deadlineOrderDate = v.findViewById(R.id.tv_deadline_date);
        deadlineOrderTime = v.findViewById(R.id.tv_deadline_time);
        orderDescTitleContainer = v.findViewById(R.id.ll_order_desc_title_container);
        orderDesc = v.findViewById(R.id.tv_order_desc);
        orderFilesTitleContainer = v.findViewById(R.id.ll_order_files_title_container);
        orderFilesItemsContainer = v.findViewById(R.id.ll_orders_files_container);
        saveAsArchiveButton = v.findViewById(R.id.tv_save_as_archive);

        setInfo(currOrder);
        return v;

    }

    private void setInfo(final OrderResponse order) {
        subjects.setText(order.getCategory().getName());
        price.setText(order.getPrice() + " \u20BD");

        if(isOwner){
            actionButton.setText("ИСПОЛНИТЕЛИ");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        } else {
            actionButton.setText("ПРИНЯТЬ");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        if (order.getOrderType() == OrderResponse.TypeOrders.quick_task) {
            taskType.setText("Быстрая задача");
            taskTypeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_quick_task, App.getAppContext().getTheme()));
        } else {
            taskType.setText("Подработка");
            taskTypeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_watch, App.getAppContext().getTheme()));
        }

        if (order.getPriceType() == OrderResponse.TypePayment.free) {
            actualityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flag_task_in_work, App.getAppContext().getTheme()));
        } else {
            actualityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle, App.getAppContext().getTheme()));
        }
        if (System.currentTimeMillis() < order.getActuality()) {
            float[] weights = measureTimeLineWeights(order.getCreatedAt(), order.getActuality());
            LinearLayout.LayoutParams paramsForPast = new LinearLayout.LayoutParams(0, DpPxUtil.getPixelsFromDp(2), weights[0]);
            LinearLayout.LayoutParams paramsForFuture = new LinearLayout.LayoutParams(0, DpPxUtil.getPixelsFromDp(2), weights[1]);
            pastTime.setLayoutParams(paramsForPast);
            futureTime.setLayoutParams(paramsForFuture);
        } else {
            LinearLayout.LayoutParams paramsForPast = new LinearLayout.LayoutParams(0, DpPxUtil.getPixelsFromDp(2), 1f);
            LinearLayout.LayoutParams paramsForFuture = new LinearLayout.LayoutParams(0, DpPxUtil.getPixelsFromDp(2), 0f);
            pastTime.setLayoutParams(paramsForPast);
            futureTime.setLayoutParams(paramsForFuture);
        }
        if (System.currentTimeMillis() < order.getActuality()) {
            deaedline.setText("До дедлайна осталось " + measureTimeToDeadline(order.getActuality()));
        } else {
            deaedline.setText("Заказ уже не актуален");
        }

        createOrderDate.setText(convertMillisToStringDate(order.getCreatedAt()));
        createOrderTime.setText(convertMillisToStringTime(order.getCreatedAt()));
        deadlineOrderDate.setText(convertMillisToStringDate(order.getActuality()));
        deadlineOrderTime.setText(convertMillisToStringTime(order.getActuality()));

        if (!order.getDescription().equals("")) {
            orderDesc.setText(order.getDescription());
        } else {
            orderDescTitleContainer.setVisibility(View.GONE);
            orderDesc.setVisibility(View.GONE);
        }

        if (order.getFiles() != null && !order.getFiles().isEmpty()) {
            for (FileResponse fileResponse : order.getFiles()) {
                addFileItem(fileResponse);
            }
            saveAsArchiveButton.setVisibility(View.VISIBLE);
        } else {
            orderFilesTitleContainer.setVisibility(View.GONE);
            orderFilesItemsContainer.setVisibility(View.GONE);
            saveAsArchiveButton.setVisibility(View.GONE);
        }

        if(isOwner){
            saveAsArchiveButton.setText("ДОБАВИТЬ ФАЙЛ");
            saveAsArchiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                //TODO сделать логику добавление файла
                }
            });
        } else {
            saveAsArchiveButton.setText("СКАЧАТЬ ВСЕ АРХИВОМ");
            saveAsArchiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadFilesAsArchive(order.getId(), "ArchiveFiles_" + order.getId() + ".zip");
                }
            });

        }



    }

    private String convertMillisToStringDate(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(millis));
        return String.format("%02d.%02d.%02d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    }

    private String convertMillisToStringTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(millis));
        return String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    private String measureTimeToDeadline(long deadlineMillis) {
        long currTime = System.currentTimeMillis();
        long diff = deadlineMillis - currTime;
        long days = diff / 1000 / 60 / 60 / 24;
        long hours = (diff / 1000 / 60 / 60) % 24;
        if (days > 0) {
            return String.format("%d дн. %d ч.", days, hours);

        } else {
            return String.format("%d ч.", hours);
        }
    }

    private float[] measureTimeLineWeights(long createMillis, long deadlineMillis) {
        float[] weights = new float[2];
        long currTime = System.currentTimeMillis();
        long diffToCreate = currTime - createMillis;
        long diffToDeadline = deadlineMillis - createMillis;
        weights[0] = diffToCreate;
        weights[1] = diffToDeadline;
        return weights;
    }

    private void addFileItem(final FileResponse fileResponse) {
        final View inflate = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_file_in_create, null);
        final TextView fileName = inflate.findViewById(R.id.tv_file_name);
        TextView date = inflate.findViewById(R.id.tv_upload_date);
        TextView kbCount = inflate.findViewById(R.id.tv_kb_count);
        TextView rightButton = inflate.findViewById(R.id.tv_delete_button);
        TextView leftButton = inflate.findViewById(R.id.tv_preview);

        fileName.setText(fileResponse.getName());
        if(isOwner){
            rightButton.setText("УДАЛИТЬ ФАЙЛ");
            rightButton.setTextColor(getResources().getColor(R.color.red, App.getAppContext().getTheme()));
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteFile(currOrder.getId(), fileResponse.getId(), inflate);
                }
            });
            leftButton.setVisibility(View.VISIBLE);
            leftButton.setText("СКАЧАТЬ");
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadFile(currOrder.getId(), fileResponse.getId(), fileResponse.getName());
                }
            });
        } else {
            rightButton.setText("СКАЧАТЬ ФАЙЛ");
            rightButton.setTextColor(getResources().getColor(R.color.colorAccent, App.getAppContext().getTheme()));
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadFile(currOrder.getId(), fileResponse.getId(), fileResponse.getName());
                }
            });
            leftButton.setVisibility(View.VISIBLE);
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    previewFile(currOrder.getId(), fileResponse.getId(), fileResponse.getName());
                }
            });
        }

        kbCount.setText("------ КБ");
        date.setText("--.--.----");

        orderFilesItemsContainer.addView(inflate);
    }

    private void downloadFile(final long orderId, final long fileId, final String fileName) {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                App.getRetrofitService().getFileOrder(SettingsHelper.getToken().getAccess(), orderId, fileId).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            if (Environment.getExternalStorageDirectory().canWrite()) {
                                final File file = new File(Environment.getExternalStorageDirectory().getPath().concat("/ComeOn/").concat(fileName));
                                String f = file.getAbsolutePath().replace(fileName, "");
                                File folder = new File(f);
                                if(!folder.exists()){
                                    folder.mkdirs();
                                }
                                if(file.exists()){
                                    file.delete();
                                }
                                final HandlerThread handlerThread = new HandlerThread("NetworkThread");
                                handlerThread.start();
                                new Handler(handlerThread.getLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            file.createNewFile();
                                            FileUtils.writeByteArrayToFile(file, response.body().bytes());
                                            Toast.makeText(App.getAppContext(), "Файл загружен", Toast.LENGTH_SHORT).show();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},11);
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
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(App.getAppContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void previewFile(final long orderId, final long fileId, final String fileName) {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                App.getRetrofitService().getFileOrder(SettingsHelper.getToken().getAccess(), orderId, fileId).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            if (Environment.getExternalStorageDirectory().canWrite()) {
                                final File file = new File(Environment.getExternalStorageDirectory().getPath().concat("/ComeOn/cache/").concat(fileName));
                                String f = file.getAbsolutePath().replace(fileName, "");
                                File folder = new File(f);
                                if(!folder.exists()){
                                    folder.mkdirs();
                                }
                                if(file.exists()){
                                    FileManager.previewFile(file,getActivity(),getActivity().getPackageManager());
                                    return;
                                }
                                final HandlerThread handlerThread = new HandlerThread("NetworkThread");
                                handlerThread.start();
                                new Handler(handlerThread.getLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            file.createNewFile();
                                            FileUtils.writeByteArrayToFile(file, response.body().bytes());
                                            FileManager.previewFile(file,getActivity(),getActivity().getPackageManager());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},11);
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
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        Toast.makeText(App.getAppContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void addFileOrder(final long orderId, final File file) {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                OrderFileDescriptionRequest d = new OrderFileDescriptionRequest(file.getName());
                RequestBody description = RequestBody.create(MediaType.parse("text/plain"), d.getName());

                Log.e("body", body.headers().toString());
                Log.e("body", body.body().contentType().toString());

                App.getRetrofitService().addFileOrder(SettingsHelper.getToken().getAccess(), orderId, description, body).enqueue(new Callback<IdResponse>() {
                    @Override
                    public void onResponse(Call<IdResponse> call, Response<IdResponse> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {
                            Log.e("onResponse", " " + response.body().toJson());


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


    private void deleteFile(final long orderId, final long fileId, final View fileItem){
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                App.getRetrofitService().deleteFileOrder(SettingsHelper.getToken().getAccess(),orderId,fileId).enqueue(new Callback<Done>() {
                    @Override
                    public void onResponse(Call<Done> call, Response<Done> response) {
                        if(response.body().isDone()){
                            fileItem.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<Done> call, Throwable t) {

                    }
                });
            }
        });
    }

    private void downloadFilesAsArchive(final long orderId, final String fileName){
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                App.getRetrofitService().getAllOrderFilesAsArchive(SettingsHelper.getToken().getAccess(), orderId).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                        final File zipFile = new File(Environment.getExternalStorageDirectory().getPath().concat("/ComeOn/").concat(fileName));
                        File folder = new File(zipFile.getAbsolutePath().replace(zipFile.getName(), ""));
                        if(!folder.exists()){
                            folder.mkdirs();
                        }
                            HandlerThread handlerThread = new HandlerThread("NetworkTread");
                            handlerThread.start();
                            new Handler(handlerThread.getLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        zipFile.createNewFile();
                                        FileUtils.writeByteArrayToFile(zipFile, response.body().bytes());
                                        Toast.makeText(App.getAppContext(), "Aрхив загружен", Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 11 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(App.getAppContext(), "Нажмите скачать еще раз", Toast.LENGTH_SHORT).show();
        }
    }
}
