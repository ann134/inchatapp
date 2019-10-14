package ru.sigmadigital.inchat.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;

public class FilePickerAdapter extends RecyclerView.Adapter<FilePickerAdapter.FileHolder> {

    OnFileItemClick onFileItemClick;
    private List<File> files = new ArrayList<>();

    public FilePickerAdapter(OnFileItemClick onFileItemClick) {
        this.onFileItemClick = onFileItemClick;
    }

    public void setFiles(List<File> files) {
        this.files = files;
        notifyDataSetChanged();
    }

    public interface OnFileItemClick {
        void OnFileClick(File file);
    }


    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_file, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new FileHolder(v);
    }

    @Override
    public void onBindViewHolder(FileHolder holder, int position) {
        final File file = files.get(position);

        holder.nameTv.setText(file.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFileItemClick.OnFileClick(file);
            }
        });
    }


    @Override
    public int getItemCount() {
        Log.e("files", files.size()+"");
        return files.size();
    }


    class FileHolder extends RecyclerView.ViewHolder {

        private final TextView nameTv;

        public FileHolder(View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.file_name);
        }

    }

}
