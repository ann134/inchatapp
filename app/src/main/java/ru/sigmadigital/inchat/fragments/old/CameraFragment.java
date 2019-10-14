package ru.sigmadigital.inchat.fragments.old;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;

public class CameraFragment extends BaseFragment implements View.OnClickListener {

    private static final int MY_CAMERA_REQUEST_CODE = 100;

    private TextView permissionDenied;
    private TextView permissionSettings;





    CameraManager mCameraManager;
    String cameraId;
    TextureView textureView;
    SurfaceTexture surfaceTexture;


    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);


        permissionDenied = v.findViewById(R.id.permission_denied);
        permissionDenied.setOnClickListener(this);
        permissionSettings = v.findViewById(R.id.settings);
        permissionSettings.setOnClickListener(this);
        textureView = v.findViewById(R.id.texture_view);


        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                surfaceTexture = surface;

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });




        mCameraManager = (CameraManager) App.getAppContext().getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraId = getCameraWithFacing(mCameraManager, CameraCharacteristics.LENS_FACING_FRONT);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


        getPermissions();

        return v;
    }





    @Nullable
    private static String getCameraWithFacing(@NonNull CameraManager manager, int lensFacing) throws CameraAccessException {
        String possibleCandidate = null;
        String[] cameraIdList = manager.getCameraIdList();
        if (cameraIdList.length == 0) {
            return null;
        }
        for (String cameraId : cameraIdList) {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map == null) {
                continue;
            }

            Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
            if (facing != null && facing == lensFacing) {
                return cameraId;
            }

            //just in case device don't have any camera with given facing
            possibleCandidate = cameraId;
        }
        if (possibleCandidate != null) {
            return possibleCandidate;
        }
        return cameraIdList[0];
    }





    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(App.getAppContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        } else {
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE && grantResults.length != 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                showPermissionDenied(true);
            }
        }
    }

    private void startCamera() {
        if (ContextCompat.checkSelfPermission(App.getAppContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

        }
        try {
            mCameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull final CameraDevice camera) {
                    List<Surface> list = new ArrayList<>();
                    list.add(new Surface(surfaceTexture));
                    try {
                        camera.createCaptureSession(list, new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session) {


                               /* session.setRepeatingRequest( , new CameraCaptureSession.CaptureCallback(){
                                    @Override
                                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                                        super.onCaptureCompleted(session, request, result);
                                    }
                                }, null);*/

                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                            }


                        }, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {

                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {

                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(App.getAppContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        }




        //glView.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();


        //glView.onPause();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.permission_denied:
                goToSettings();
                break;

            case R.id.settings:
                goToSettings();
                break;
        }
    }

    private void showPermissionDenied(boolean show) {
        if (show){
            permissionDenied.setVisibility(View.VISIBLE);
            permissionSettings.setVisibility(View.VISIBLE);

        } else {
            permissionDenied.setVisibility(View.GONE);
            permissionSettings.setVisibility(View.GONE);

        }
    }

    private void goToSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + App.getAppContext().getPackageName()));
        startActivity(intent);
    }






















}
