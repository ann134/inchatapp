package ru.sigmadigital.inchat.fragments.old;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.parameter.camera.CameraParameters;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;

public class FotoapparatFragment extends BaseFragment implements View.OnClickListener {

    private Fotoapparat fotoapparat;

    private CameraView cameraView;
    private ImageView photoHolder;
    private Button makePhoto;
    private Button rePhoto;
    private Button addSticker;

    private Bitmap bgForSticker;
    private Bitmap BgForStickerFullHD;

    public static FotoapparatFragment newInstance(){
        return new FotoapparatFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fotoapparat, null);
        cameraView = v.findViewById(R.id.camera_view);
        photoHolder = v.findViewById(R.id.imv_photoHolder);
        makePhoto = v.findViewById(R.id.btn_make_photo);
        rePhoto = v.findViewById(R.id.btn_rephoto);
        addSticker = v.findViewById(R.id.btn_add_sticker);
        makePhoto.setOnClickListener(this);
        rePhoto.setOnClickListener(this);
        addSticker.setOnClickListener(this);







        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        fotoapparat = Fotoapparat.with(App.getAppContext()).into(cameraView).previewScaleType(ScaleType.CenterCrop).build();
        fotoapparat.start();
        fotoapparat.getCurrentParameters().whenAvailable(new Function1<CameraParameters, Unit>() {
            @Override
            public Unit invoke(CameraParameters cameraParameters) {
                Log.e("AspectRatioPreview",cameraParameters.getPreviewResolution().getAspectRatio() + "");

                return null;
            }
        });
        fotoapparat.getCurrentParameters().whenAvailable(new Function1<CameraParameters, Unit>() {
            @Override
            public Unit invoke(CameraParameters cameraParameters) {
                Log.e("AspectRatioPicture",cameraParameters.getPictureResolution().getAspectRatio() + "");

                return null;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        fotoapparat.stop();
    }

    @Override
    public void onStop() {
        super.onStop();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_photoHolder :
                break;

            case R.id.btn_make_photo :

                try {
                    PhotoResult photoResult = fotoapparat.takePicture();
                    photoResult.toBitmap().whenAvailable(new Function1<BitmapPhoto, Unit>() {
                        @Override
                        public Unit invoke(BitmapPhoto bitmapPhoto) {
                            fotoapparat.stop();
                            Bitmap bitmap = bitmapPhoto.bitmap;
                            Log.e("Bitmap", "Width - " + bitmap.getWidth() + " Height - " + bitmap.getHeight());

                            int diff = photoHolder.getMeasuredHeight() - 4 * photoHolder.getMeasuredWidth() / 3;

                            Matrix matrix = new Matrix();
                            matrix.postRotate(90);
                            bitmap = Bitmap.createBitmap(bitmap, 0 , 0 , bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            bitmap = Bitmap.createBitmap(bitmap, 0 , 0 , bitmap.getWidth(), bitmap.getHeight(), new Matrix(), true);
                            BgForStickerFullHD = Bitmap.createScaledBitmap(bitmap, 1200, 1600, true);
                            bgForSticker = Bitmap.createScaledBitmap(bitmap,photoHolder.getMeasuredWidth(), photoHolder.getMeasuredHeight() - diff , true);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    photoHolder.setImageBitmap(bgForSticker);
                                }
                            });
                            return null;
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.btn_rephoto :
                bgForSticker = null;
                photoHolder.setImageResource(android.R.color.transparent);
                fotoapparat.start();
                break;

            case R.id.btn_add_sticker :
                replaceCurrentFragmentWith(getFragmentManager(), R.id.fragment_container,DragAndDrop2.newInstance(bgForSticker, BgForStickerFullHD), true);
                break;
        }
    }
}
