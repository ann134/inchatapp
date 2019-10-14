package ru.sigmadigital.inchat.adapters;

import android.annotation.SuppressLint;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.realm.ChatMessageRealm;
import ru.sigmadigital.inchat.models.realm.FileRealm;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.PicassoUtil;
import ru.sigmadigital.inchat.utils.SettingsHelper;
import ru.sigmadigital.inchat.utils.WriteFileTask;

public class ChatAdapterOld extends RecyclerView.Adapter<ChatAdapterOld.ChatHolder> {

    private List<ChatMessageRealm> itemList = new ArrayList<>();

    long chatId;
    //File dir;

    OnFileChatClick onFileChatClick;

    public ChatAdapterOld(long chatId, OnFileChatClick onFileChatClick) {
        this.chatId = chatId;
        this.onFileChatClick = onFileChatClick;
    }

    public void addItemList(List<ChatMessageRealm> itemList, boolean previous) {
        if (previous){
            this.itemList.addAll(0, itemList);
        } else {
            this.itemList.addAll(itemList);
        }

        //this.itemList.addAll(itemList);
        notifyDataSetChanged();
    }

    public void addItemList(ChatMessageRealm item) {
        itemList.add(item);
        notifyDataSetChanged();
    }


    public interface OnFileChatClick {
        void OnFileClick(File file);
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_chat_message, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ChatHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        holder.setIsRecyclable(false);
        final ChatMessageRealm message = itemList.get(position);

        holder.message.setText("message: " + message.getMessage());
        holder.from.setText("from: " + message.getFrom().getName() + " " + message.getFrom().getId()+"     "+ message.getDate());

        for (FileRealm file : message.getFiles()){
            addChildFile(holder.filesContainer, file);
        }

    }


    private void addChildFile(final LinearLayout container, final FileRealm file) {
        @SuppressLint("InflateParams") View childView = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_selected_file, null);
        TextView name = childView.findViewById(R.id.file_name);
        name.setText(file.getName());
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(file, container);
            }
        });
        container.addView(childView);
    }


    private void getFile (final FileRealm file, final LinearLayout container) {
        MyRetrofit.ServiceManager service = MyRetrofit.getInstance();

        Log.e("getFile", " " + chatId + " " + file.getId());

                service.getFileChat(SettingsHelper.getToken().getAccess(), chatId, file.getId()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.e("onResponseCode", response.code() + " ");

                        if (response.body() != null) {

                            new WriteFileTask(new WriteFileTask.OnWriteFile() {
                                @Override
                                public void onWrite(File file) {
                                    Log.e("onWrite",  file.getAbsolutePath());
                                    if (onFileChatClick != null) {
                                        onFileChatClick.OnFileClick( file);
                                    }



//                                    addChildImage(container, file);

                                }
                            }, response.body(), file).execute();

                           /* if (onFileChatClick != null) {
                                onFileChatClick.OnFileClick(response.body(), file);
                            }*/

                            //writeFile(response.body(), file);

                        } else if (response.errorBody() != null) {
                            try {
                                String responseError = response.errorBody().string();
                                Log.e("onResponseError", " " + responseError);
                                Error error = JsonParser.fromJson(responseError, Error.class);
                                if (error != null) {
                                    Log.e("Error", " " + error.getDescription() + " " + error.getCode());
                                    //ErrorHandler.catchError(error, getActivity());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("onFailure", t + "");
                        //Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });

    }

   /* private void loadImage(File file) {

    }*/

    private void addChildImage(LinearLayout container, File file) {
        @SuppressLint("InflateParams") View childView = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_selected_file, null);
        ImageView imageView = childView.findViewById(R.id.image_file);
        imageView.setVisibility(View.VISIBLE);
        PicassoUtil.getPicasso()
                .load(file)
                .error(R.drawable.common_google_signin_btn_icon_dark)
                .into(imageView);
        container.addView(childView);
    }


    @Override
    public int getItemCount() {
        //Log.e("frtrfg", itemList.size() + "");
        return itemList.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder {

        TextView message;
        TextView from;
        LinearLayout filesContainer;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            filesContainer = itemView.findViewById(R.id.files_container);
            message = itemView.findViewById(R.id.message);

        }
    }


    public long getLastMessageDown(){
        return itemList.get(itemList.size()-1).getId();
    }

    public long getLastMessageUp(){
        return itemList.get(0).getId();
    }

}
