package ru.sigmadigital.inchat.utils;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

import okhttp3.ResponseBody;
import ru.sigmadigital.inchat.models.FileResponse;
import ru.sigmadigital.inchat.models.realm.FileRealm;

public class WriteFileTask extends AsyncTask<Void, Void, File> {

    OnWriteFile onWriteFile;

    ResponseBody body;
    FileRealm fileResponse;

    public interface OnWriteFile {
        void onWrite(File file);
    }

    public WriteFileTask(OnWriteFile onWriteFile, ResponseBody body, FileRealm fileResponse) {
        this.onWriteFile = onWriteFile;
        this.body = body;
        this.fileResponse = fileResponse;
    }

    @Override
    protected File doInBackground(Void... voids) {

        try {

            File myFile = new File(Environment.getDownloadCacheDirectory().getPath() + "/" + fileResponse.getName() );
            if (myFile.exists()){
                myFile.delete();
            }

            myFile.createNewFile();                                         // Создается файл, если он не был создан
            FileOutputStream outputStream = new FileOutputStream(myFile);   // После чего создаем поток для записи
            outputStream.write(body.bytes());                            // и производим непосредственно запись
            outputStream.close();
            return myFile;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if (onWriteFile != null) {
            onWriteFile.onWrite(file);
        }
    }


}
