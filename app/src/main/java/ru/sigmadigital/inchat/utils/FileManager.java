package ru.sigmadigital.inchat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.sigmadigital.inchat.App;

public class FileManager {

    private static final String TAG = "FileManager";

    private File currentDirectory;
    private final File rootDirectory;

    public FileManager(Context context) {
        File directory;

        directory = Environment.getExternalStorageDirectory();
        Log.e("directory", directory.getAbsolutePath());

        /*if (Environment.MEDIA_MOUNTED.equals(Environment.getRootDirectory())) {

        } else {
            directory = ContextCompat.getDataDir(context);
            Log.e("directory", directory.getAbsolutePath());
        }*/

        rootDirectory = directory;

        navigateTo(directory);
    }


    public boolean navigateTo(File directory) {
        // Проверим, является ли файл директорией
        if (!directory.isDirectory()) {
            Log.e(TAG, directory.getAbsolutePath() + " is not a directory!");

            return false;
        }

        // Проверим, не поднялись ли мы выше rootDirectory
        if (!directory.equals(rootDirectory) && rootDirectory.getAbsolutePath().contains(directory.getAbsolutePath())) {
            Log.w(TAG, "Trying to navigate upper than root directory to " + directory.getAbsolutePath());
            return false;
        }

        currentDirectory = directory;
        Log.e("directory", directory.getAbsolutePath());

        return true;
    }


    public boolean navigateUp() {
        if (currentDirectory != null && currentDirectory.getParentFile() != null){
            return navigateTo(currentDirectory.getParentFile());
        }
        return false;
    }


    public List<File> getFiles() {
        List<File> files = new ArrayList<>();

        if (currentDirectory != null && currentDirectory.listFiles() != null){
            files.addAll(Arrays.asList(currentDirectory.listFiles()));
        }

        for (File f: files){
            Log.e("file", f.getAbsolutePath());
        }

        return files;
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public static void previewFile(File file, Activity activity, PackageManager pm){
        Intent intent = new Intent(Intent.ACTION_VIEW);

        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Uri uri = FileProvider.getUriForFile(App.getAppContext(), App.getAppContext().getPackageName() + ".provider", file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, mimetype);
        if (intent.resolveActivity(pm) != null) {
            activity.startActivity(intent);
        } else {
            Log.d(mimetype, "Не получается обработать намерение!");
        }
    }
}
