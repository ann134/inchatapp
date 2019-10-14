package ru.sigmadigital.inchat.adapters;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.ArrayList;
import java.util.List;

import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.models.IdResponse;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoHolder> {

    public static final int PHOTOS_REVIEW = 1;
    public static final int PHOTOS_EDIT = 2;
    private int type;
    private FragmentManager fm;
    private PhotoUploader listener;

    public PhotosAdapter(int type, FragmentManager fm, PhotoUploader listener) {
        this.type = type;
        this.fm = fm;
        this.listener = listener;
    }

    public PhotosAdapter(int type) {
        this.type = type;
    }

    private List<IdResponse> itemList = new ArrayList<>();

    public void setItemList(List<IdResponse> itemList) {
        this.itemList = itemList;
        //добавление первого пустого айтема
        if(type == PHOTOS_EDIT){
            itemList.add(0,new IdResponse());
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_foto, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new PhotoHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotoHolder holder, final int position) {
        if(type == PHOTOS_EDIT && position == 0){
            holder.image.setImageDrawable(App.getAppContext().getResources().getDrawable(R.drawable.ic_add, App.getAppContext().getTheme()));
            holder.image.setBackground(new ColorDrawable(App.getAppContext().getColor(R.color.blue)));
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PickImageDialog.build(new PickSetup()).setOnPickResult(new IPickResult() {
                        @Override
                        public void onPickResult(PickResult pickResult) {
                            listener.uploadPhoto(pickResult.getPath());
                        }
                    }).show(fm);
                }
            });
            return;
        }

        getStickerPhoto(itemList.get(position).getId(), holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type == PHOTOS_EDIT){
                    AlertDialog.Builder builder = new AlertDialog.Builder(fm.getFragments().get(fm.getFragments().size() - 1).getActivity());
                    builder.setTitle("Вы уверены?")
                            .setMessage("Вы точно хотите удалить фотографию?")
                            .setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    listener.deletePhoto(itemList.get(position).getId());
                                    itemList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(0, itemList.size());
                                }
                            }).setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create().show();
                }
            }
        });
    }

    private void getStickerPhoto(long id, ImageView imageView) {
        Glide.with(App.getAppContext())
                .load(MyRetrofit.BASE_URL + "users/stickerphoto/" + id + "/photo")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.common_google_signin_btn_icon_dark)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class PhotoHolder extends RecyclerView.ViewHolder {

        ImageView image;

        PhotoHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_foto);

        }
    }
    public interface PhotoUploader {
        void uploadPhoto(String path);
        void deletePhoto(long photoId);
    }
}
