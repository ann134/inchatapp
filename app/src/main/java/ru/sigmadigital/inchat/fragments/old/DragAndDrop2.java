package ru.sigmadigital.inchat.fragments.old;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.IdResponse;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class DragAndDrop2 extends BaseFragment implements View.OnClickListener {

    private MyRetrofit.ServiceManager service;

    private RelativeLayout rootLayout;
    private ImageView im_move_zoom_rotate;
    private ImageView bgHolder;
    private Button saveButton;
    private Button sendButton;

    private Bitmap backgroundImage;
    private Bitmap backgroundImageFullHD;
    private Bitmap overlayBmFullHD;

    private float rotationAngle;

    float scalediff;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;

    public static DragAndDrop2 newInstance(Bitmap backgroundImage, Bitmap backgroundImageFullHD) {
        DragAndDrop2 fragment = new DragAndDrop2();
        fragment.setBackgroundImage(backgroundImage);
        fragment.setBackgroundImageFullHD(backgroundImageFullHD);
        return fragment;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_drag_and_drop2, container, false);

        service = MyRetrofit.getInstance();

        rootLayout = v.findViewById(R.id.root);
        im_move_zoom_rotate = v.findViewById(R.id.im_move_zoom_rotate);
        bgHolder = v.findViewById(R.id.imv_bg_holder);
        saveButton = v.findViewById(R.id.btn_save);
        sendButton = v.findViewById(R.id.btn_send);

        im_move_zoom_rotate.setDrawingCacheEnabled(true);
        bgHolder.setImageBitmap(backgroundImage);
        saveButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(250, 250);
        layoutParams.leftMargin = 0;
        layoutParams.topMargin = 0;
        layoutParams.bottomMargin = 0;
        layoutParams.rightMargin = 0;
        im_move_zoom_rotate.setLayoutParams(layoutParams);

        im_move_zoom_rotate.setOnTouchListener(new View.OnTouchListener() {


            int startwidth;
            int startheight;
            float dx = 0, dy = 0, x = 0, y = 0;
            float angle = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final ImageView view = (ImageView) v;
                RelativeLayout.LayoutParams parms;
                parms = (RelativeLayout.LayoutParams) view.getLayoutParams();
                //((BitmapDrawable) view.getDrawable()).setAntiAlias(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:

                        startwidth = parms.width;
                        startheight = parms.height;
                        dx = event.getRawX() - parms.leftMargin;
                        dy = event.getRawY() - parms.topMargin;
                        mode = DRAG;
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            mode = ZOOM;
                        }

                        d = rotation(event);

                        break;
                    case MotionEvent.ACTION_UP:

                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;

                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {

                            x = event.getRawX();
                            y = event.getRawY();

                            parms.leftMargin = (int) (x - dx);
                            parms.topMargin = (int) (y - dy);

                            parms.rightMargin = 0;
                            parms.bottomMargin = 0;
                            parms.rightMargin = parms.leftMargin + (5 * parms.width);
                            parms.bottomMargin = parms.topMargin + (10 * parms.height);

                            //Log.e("")

                            view.setLayoutParams(parms);

                        } else if (mode == ZOOM) {

                            if (event.getPointerCount() == 2) {

                                newRot = rotation(event);
                                float r = newRot - d;
                                angle = r;
                                rotationAngle = rotationAngle + angle;

                                x = event.getRawX();
                                y = event.getRawY();

                                float newDist = spacing(event);
                                if (newDist > 10f) {
                                    float scale = newDist / oldDist * view.getScaleX();
                                    if (scale > 0.6) {
                                        scalediff = scale;
                                        view.setScaleX(scale);
                                        view.setScaleY(scale);

                                    }
                                }

                                view.animate().rotationBy(angle).setDuration(0).setInterpolator(new LinearInterpolator()).start();

                                x = event.getRawX();
                                y = event.getRawY();

                                parms.leftMargin = (int) ((x - dx) + scalediff);
                                parms.topMargin = (int) ((y - dy) + scalediff);

                                parms.rightMargin = 0;
                                parms.bottomMargin = 0;
                                parms.rightMargin = parms.leftMargin + (5 * parms.width);
                                parms.bottomMargin = parms.topMargin + (10 * parms.height);

                                view.setLayoutParams(parms);


                            }
                        }
                        break;
                }

                return true;

            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }


    public void setBackgroundImage(Bitmap backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setBackgroundImageFullHD(Bitmap backgroundImageFullHD) {
        this.backgroundImageFullHD = backgroundImageFullHD;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                Bitmap sticker = im_move_zoom_rotate.getDrawingCache();


                overlayBmFullHD = overlay(backgroundImage, sticker);

                break;

            case R.id.btn_send:
                if (overlayBmFullHD != null) {
                    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/stickerPhoto.jpg");

                    try {
                        Log.e("Stickr", file.createNewFile() + "");
                        Log.e("Stickr", file.exists() + "");

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        overlayBmFullHD.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        byte[] outArray = out.toByteArray();

                        FileUtils.writeByteArrayToFile(file, outArray);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    uploadPhotosticker(file);

                    break;
                }
        }
    }

    private Bitmap overlay(Bitmap bg, Bitmap sticker) {

        int[] location = new int[2];
        im_move_zoom_rotate.animate().rotationBy(-rotationAngle).setDuration(0).start();
        im_move_zoom_rotate.getLocationInWindow(location);
        int x1 = location[0] + (int) (im_move_zoom_rotate.getMeasuredWidth() * im_move_zoom_rotate.getScaleX() / 2);
        int y1 = location[1] + (int) (im_move_zoom_rotate.getMeasuredHeight() * im_move_zoom_rotate.getScaleY() / 2);
        im_move_zoom_rotate.animate().rotationBy(rotationAngle).setDuration(0).start();

        Rect rectf = new Rect();
        im_move_zoom_rotate.getGlobalVisibleRect(rectf);


        TypedValue tv = new TypedValue();
        App.getAppContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);
        int statusBarHeight;
        statusBarHeight = getStatusBarHeight();
        Log.e("StatusBar", "Height - " + statusBarHeight);

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);


        sticker = Bitmap.createScaledBitmap(sticker, rectf.width(), rectf.height(), true);
        sticker = Bitmap.createBitmap(sticker, 0, 0, rectf.width(), rectf.height(), matrix, true);
        sticker = Bitmap.createScaledBitmap(sticker, rectf.width(), rectf.height(), true);
        int x2 = location[0] + sticker.getWidth() / 2;
        int y2 = location[1] + sticker.getHeight() / 2;

        int dx = x2 - x1;
        int dy = y2 - y1;
        int topPadding = (bgHolder.getMeasuredHeight() - bg.getHeight()) / 2;

        Bitmap bmOverlay = Bitmap.createBitmap(bg.getWidth(), bg.getHeight(), bg.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bg, new Matrix(), null);
        canvas.drawBitmap(sticker, location[0] - dx, location[1] - dy - statusBarHeight  - actionBarHeight - topPadding, null);

        bgHolder.setImageBitmap(bmOverlay);

        Bitmap overlayFullHD = overlayFullHD(backgroundImageFullHD, sticker, location[0] - dx, location[1] - dy - statusBarHeight - actionBarHeight - topPadding, (double) backgroundImageFullHD.getWidth() / bg.getWidth());

        return overlayFullHD;
    }

    private Bitmap overlayFullHD(Bitmap bg, Bitmap sticker, int x , int y, double rateOfChange) {

//        int[] location = new int[2];
//        location[0] = (int)(location[0] * rateOfChange);
//        location[1] = (int)(location[1] * rateOfChange);
//        im_move_zoom_rotate.animate().rotationBy(-rotationAngle).setDuration(0).start();
//        im_move_zoom_rotate.getLocationInWindow(location);
//        int x1 = location[0] + (int) (im_move_zoom_rotate.getMeasuredWidth() * im_move_zoom_rotate.getScaleX() * rateOfChange / 2);
//        int y1 = location[1] + (int) (im_move_zoom_rotate.getMeasuredHeight() * im_move_zoom_rotate.getScaleY() * rateOfChange / 2);
//        im_move_zoom_rotate.animate().rotationBy(rotationAngle).setDuration(0).start();
//
//        Rect rectf = new Rect();
//        im_move_zoom_rotate.getGlobalVisibleRect(rectf);
//
//
//        TypedValue tv = new TypedValue();
//        App.getAppContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
//        int actionBarHeight = (int)(getResources().getDimensionPixelSize(tv.resourceId) * rateOfChange);
//        int statusBarHeight;
//        statusBarHeight = (int)(getStatusBarHeight() * rateOfChange);
//        Log.e("StatusBar", "Height - " + statusBarHeight);
//
//        Matrix matrix = new Matrix();
//        matrix.postRotate(rotationAngle);
//
//
//        sticker = Bitmap.createScaledBitmap(sticker, (int)(rectf.width() * rateOfChange), (int)(rectf.height() * rateOfChange), true);
//        sticker = Bitmap.createBitmap(sticker, 0, 0, (int)(rectf.width() * rateOfChange), (int)(rectf.height() * rateOfChange), matrix, true);
//        sticker = Bitmap.createScaledBitmap(sticker, (int)(rectf.height() * rateOfChange), (int)(rectf.height() * rateOfChange), true);
//        int x2 = location[0] + sticker.getWidth() / 2;
//        int y2 = location[1] + sticker.getHeight() / 2;
//
//        int dx = x2 - x1;
//        int dy = y2 - y1;
//        int topPadding = (int)((bgHolder.getMeasuredHeight() - bg.getHeight()) / 2 * rateOfChange);
//
        sticker = Bitmap.createScaledBitmap(sticker, (int)(sticker.getWidth() * rateOfChange), (int)(sticker.getHeight() * rateOfChange), true);

        Bitmap bmOverlay = Bitmap.createBitmap(bg.getWidth(), bg.getHeight(), bg.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bg, new Matrix(), null);
        canvas.drawBitmap(sticker, (int)(x * rateOfChange),(int)(y * rateOfChange), null);

        return bmOverlay;


    }

    private void uploadPhotosticker(final File file) {
        ValidateToken.validate(getActivity(), new ValidateToken.IValid() {
            @Override
            public void OnValid() {

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

                service.uploadStickerPhoto(SettingsHelper.getToken().

                        getAccess(), requestFile).

                        enqueue(new Callback<IdResponse>() {
                            @Override
                            public void onResponse(Call<IdResponse> call, Response<IdResponse> response) {
                                Log.e("onResponseCode", response.code() + " ");

                                file.delete();

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
                                Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = App.getAppContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}

