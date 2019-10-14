package ru.sigmadigital.inchat.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.models.RateResponse;

public class RatesAdapter extends RecyclerView.Adapter<RatesAdapter.RateHolder> {

    private List<RateResponse> itemList = new ArrayList<>();

    public void setItemList(List<RateResponse> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_rate, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new RateHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RateHolder holder, int position) {
       /* final ChatResponse chatResponse = itemList.get(position);

        if(chatResponse.getLastMessage() != null){
            holder.name.setText(chatResponse.getLastMessage().getFrom().getName());
            holder.time.setText(chatResponse.getLastMessage().getCreatedAt());
            holder.message.setText(chatResponse.getLastMessage().getMessage());

             if readed
        }


        avatar

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChatItemClick.OnChatClick(chatResponse);
            }
        });*/


        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.message.setMaxLines(Integer.MAX_VALUE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class RateHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView date;
        TextView order;
        TextView message;

        RateHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_date_value);
            name = itemView.findViewById(R.id.tv_name);
            order = itemView.findViewById(R.id.tv_order_id);
            message = itemView.findViewById(R.id.tv_message);
        }
    }

}
