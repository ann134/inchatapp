package ru.sigmadigital.inchat.fragments.old.chat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.activity.MainActivity;
import ru.sigmadigital.inchat.adapters.FilePickerAdapter;
import ru.sigmadigital.inchat.fragments.BaseFragment;
import ru.sigmadigital.inchat.utils.FileManager;

public class FilePickerFragment extends BaseFragment implements FilePickerAdapter.OnFileItemClick, MainActivity.OnBackPressedClick {

    private static final String TAG = "FilePickerActivity";

    private static final int PERMISSION_REQUEST_CODE = 78;

    private FileManager fileManager;

    private FilePickerAdapter filePickerAdapter;

    OnFileSelected onFileSelected;

    public static FilePickerFragment newInstance(OnFileSelected onFileSelected) {
        FilePickerFragment fragment = new FilePickerFragment();
        fragment.setOnFileSelected(onFileSelected);
        return fragment;
    }

    private void setOnFileSelected(OnFileSelected onFileSelected){
        this.onFileSelected = onFileSelected;
    }


    public interface OnFileSelected {
        void onFileSelected(File file);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_file_picker, container, false);


        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getAppContext()));

        filePickerAdapter = new FilePickerAdapter(this);
        recyclerView.setAdapter(filePickerAdapter);

        initFileManager();

        return v;
    }

    private void requestPermissions() {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted!");

                initFileManager();
            } else {
                Log.i(TAG, "Permission denied");

                requestPermissions(); // Запрашиваем ещё раз
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setOnBackPressedClick(this);
            ((MainActivity) getActivity()).hideBottomNavigationBar();

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).deleteOnBackPressedClick();
            ((MainActivity) getActivity()).showBottomNavigationBar();
        }
    }

    private void initFileManager() {
        if (ContextCompat.checkSelfPermission(App.getAppContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            fileManager = new FileManager(App.getAppContext());
            updateFileList();
        } else {
            requestPermissions();
        }
    }

    private void updateFileList() {
        List<File> files = fileManager.getFiles();
        filePickerAdapter.setFiles(files);
    }


    @Override
    public void OnFileClick(File file) {
        Log.e("file", file.getAbsolutePath());
        if (file.isDirectory()) {
            Log.e("navigateTo", fileManager.navigateTo(file) + "");
            updateFileList();
        } else {
            onFileSelected.onFileSelected(file);
            if (getActivity() != null && getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).deleteOnBackPressedClick();
                getActivity().onBackPressed();
            }
        }
    }




    @Override
    public void OnBackPressClick() {
        if (fileManager.navigateUp()) {
            updateFileList();
        } else if (getActivity() != null && getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).deleteOnBackPressedClick();
                getActivity().onBackPressed();
            }

    }
}
