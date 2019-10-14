package ru.sigmadigital.inchat.fragments.old;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;

public class CameraKitFragment extends BaseFragment implements View.OnClickListener {

    private CameraView cameraView;
    private ImageView photoHolder;
    private Button makePhoto;
    private Button rePhoto;
    private Button addSticker;

    private Bitmap bgForSticker;

    public static CameraKitFragment newInstance() {
        return new CameraKitFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera_kit, null);
        cameraView = v.findViewById(R.id.camera_view);
        photoHolder = v.findViewById(R.id.imv_photoHolder);
        makePhoto = v.findViewById(R.id.btn_make_photo);
        rePhoto = v.findViewById(R.id.btn_rephoto);
        addSticker = v.findViewById(R.id.btn_add_sticker);
        makePhoto.setOnClickListener(this);
        rePhoto.setOnClickListener(this);
        addSticker.setOnClickListener(this);

        cameraView.setCropOutput(false);


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
        Log.e("Camera", "Width - " + cameraView.getWidth());
        Log.e("Camera", "Height - " + cameraView.getHeight());
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        cameraView.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_photoHolder:
                break;

            case R.id.btn_make_photo:
                cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
                    @Override
                    public void callback(final CameraKitImage cameraKitImage) {
                        cameraView.stop();

                        Bitmap bitmap = cameraKitImage.getBitmap();
                        bgForSticker = Bitmap.createScaledBitmap(bitmap, 960, 720, true);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                photoHolder.setImageBitmap(bgForSticker);
                            }
                        });


                    }
                });

                break;

            case R.id.btn_rephoto:
                bgForSticker = null;
                photoHolder.setImageResource(android.R.color.transparent);
                cameraView.start();
                break;

            case R.id.btn_add_sticker:

                break;
        }
    }
}
