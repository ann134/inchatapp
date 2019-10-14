package ru.sigmadigital.inchat.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.models.ChatResponse;
import ru.sigmadigital.inchat.models.UserResponse;
import ru.sigmadigital.inchat.utils.PicassoUtil;
import ru.sigmadigital.inchat.utils.SettingsHelper;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ChatHolder> {

    private List<ChatResponse> itemList = new ArrayList<>();
    private OnChatItemClick onChatItemClick;


    public interface OnChatItemClick {
        void OnChatClick(ChatResponse orderResponse);
    }

    public ChatsListAdapter(OnChatItemClick onChatItemClick) {
        this.onChatItemClick = onChatItemClick;
    }

    public void setItemList(List<ChatResponse> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_chat, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ChatHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        final ChatResponse chatResponse = itemList.get(position);
        if(chatResponse.getUsers().size() > 2){
            holder.name.setText(chatResponse.getName());
        } else {
            for(UserResponse user : chatResponse.getUsers()){
                if(user.getId() != SettingsHelper.getUser()){
                    holder.name.setText(user.getName());
                    setAvatarForChat(user.getId(), holder.avatar);
                }
            }
        }

        if(chatResponse.getLastMessage() != null){
            holder.time.setText(convertTime(chatResponse.getLastMessage().getCreatedAt()));
            holder.message.setText(chatResponse.getLastMessage().getMessage());
            if(chatResponse.getLastMessage().isReaded()){
                holder.indicator.setVisibility(View.GONE);
            } else {
                holder.indicator.setVisibility(View.VISIBLE);
            }

        } else {
            holder.message.setText("Нет сообщений");
            holder.indicator.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChatItemClick.OnChatClick(chatResponse);
            }
        });

    }

    private void setAvatarForChat(long userId, CircleImageView imageView) {
        Glide.with(App.getAppContext())
                .load(MyRetrofit.BASE_URL + "users/" + userId + "/photo")
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .error(R.drawable.common_google_signin_btn_icon_dark)
                .into(imageView);
    }

    private void getImageForChat(long userId, CircleImageView imageView) {
        //Загружать картинку которую установят на чат (пока нет такого api)
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder {

        CircleImageView avatar;
        View indicator;
        TextView name;
        TextView time;
        TextView message;

        ChatHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imv_avatar);
            indicator = itemView.findViewById(R.id.indicator);
            name = itemView.findViewById(R.id.tv_name);
            time = itemView.findViewById(R.id.tv_time);
            message = itemView.findViewById(R.id.tv_message);

        }
    }

    private String convertTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        return String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

}
