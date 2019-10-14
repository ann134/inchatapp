package ru.sigmadigital.inchat.adapters;

import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.models.ChatMessageResponse;
import ru.sigmadigital.inchat.utils.SettingsHelper;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageHolder> {

    private final static int MY_MESSAGE = 1;
    private final static int NOT_MY_MESSAGE = 2;

    private List<ChatMessageResponse> itemList = new ArrayList<>();


    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_chat_message, null);

        switch (viewType) {
            case MY_MESSAGE:
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                v.setLayoutParams(params);
                v.setGravity(Gravity.RIGHT);
                break;

            case NOT_MY_MESSAGE:
                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                v.setLayoutParams(params);
                v.setGravity(Gravity.LEFT);
                View messageContainer = v.findViewById(R.id.cl_message_container);
                messageContainer.getBackground().setTint(App.getAppContext().getResources().getColor(android.R.color.white, App.getAppContext().getTheme()));
                break;
        }

        return new MessageHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        holder.setIsRecyclable(false);
        ChatMessageResponse item = itemList.get(position);
        holder.message.setText(item.getMessage());
        holder.time.setText(convertTime(item.getCreatedAt()));
        if (holder.getItemViewType() == MY_MESSAGE) {
            if (item.isReaded()) {
                holder.checkRead.setVisibility(View.VISIBLE);
            } else {
                holder.checkRead.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.checkRead.setVisibility(View.GONE);
            holder.checkSend.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (itemList.get(position).getFrom().getId() == SettingsHelper.getUser()) {
            return MY_MESSAGE;
        } else {
            return NOT_MY_MESSAGE;
        }
    }

    public List<ChatMessageResponse> getItemList() {
        return itemList;
    }

    public void setItemList(List<ChatMessageResponse> itemList) {

        this.itemList = itemList;
    }

    public void addPageOfMessage(List<ChatMessageResponse> pageOfMessage){
        int positionStart = itemList.size();
        itemList.addAll(pageOfMessage);
        notifyItemRangeInserted(positionStart, pageOfMessage.size());
    }

    public class MessageHolder extends RecyclerView.ViewHolder {

        private TextView message;
        private TextView time;
        private ImageView checkSend;
        private ImageView checkRead;

        public MessageHolder(@NonNull View v) {
            super(v);
            message = v.findViewById(R.id.tv_message);
            time = v.findViewById(R.id.tv_time);
            checkSend = v.findViewById(R.id.imv_check_send);
            checkRead = v.findViewById(R.id.imv_check_read);
        }
    }

    private String convertTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        return String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    public void addNewMessage(ChatMessageResponse chatMessageResponse) {
        itemList.add(0, chatMessageResponse);
        notifyDataSetChanged();
    }


}
