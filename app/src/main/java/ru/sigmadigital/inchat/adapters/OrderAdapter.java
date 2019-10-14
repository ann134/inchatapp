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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.models.OrderResponse;
import ru.sigmadigital.inchat.utils.DpPxUtil;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {

    private List<OrderResponse> itemList = new ArrayList<>();
    private OnOrderItemClick onOrderItemClick;


    public interface OnOrderItemClick {
        void OnOrderClick(OrderResponse orderResponse);
    }


    public OrderAdapter(OnOrderItemClick onOrderItemClick) {
        this.onOrderItemClick = onOrderItemClick;
    }

    public void addItems(List<OrderResponse> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_order, null);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = DpPxUtil.getPixelsFromDp(16);
        v.setLayoutParams(layoutParams);
        return new OrderHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {

        final OrderResponse orderResponse = itemList.get(position);
        holder.title.setText(orderResponse.getTitle());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderItemClick.OnOrderClick(orderResponse);
            }
        });
        holder.categories.setText(orderResponse.getCategory().getName());
        holder.price.setText(orderResponse.getPrice() + " \u20BD");

        holder.usersAccepted.setImageDrawable(App.getAppContext().getResources().getDrawable(R.drawable.ic_user, App.getAppContext().getTheme()));
        holder.messages.setImageDrawable(App.getAppContext().getResources().getDrawable(R.drawable.ic_message, App.getAppContext().getTheme()));

        if (orderResponse.getOrderType() == OrderResponse.TypeOrders.quick_task) {
            holder.orderType.setImageDrawable(App.getAppContext().getResources().getDrawable(R.drawable.ic_quick_task, App.getAppContext().getTheme()));
        } else {
            holder.orderType.setImageDrawable(App.getAppContext().getResources().getDrawable(R.drawable.ic_watch, App.getAppContext().getTheme()));
        }

        if (orderResponse.getPriceType() == OrderResponse.TypePayment.fix || orderResponse.getPriceType() == OrderResponse.TypePayment.treaty) {
            //если заказ не завершен
            holder.status.setImageDrawable(App.getAppContext().getResources().getDrawable(R.drawable.ic_play_circle, App.getAppContext().getTheme()));

            //если заказ завершен то надо сетить ic_check_circle
        } else {
            //если заказ не завершен
            holder.status.setImageDrawable(App.getAppContext().getResources().getDrawable(R.drawable.ic_flag_task_in_work, App.getAppContext().getTheme()));

            //если заказ завершен то надо сетить ic_flag_task_is_done

        }

        if (orderResponse.getFiles() != null && !orderResponse.getFiles().isEmpty()) {
            holder.files.setVisibility(View.VISIBLE);
        } else {
            holder.files.setVisibility(View.INVISIBLE);
        }

        if (orderResponse.getDescription() != null && !orderResponse.getDescription().equals("")) {
            holder.text.setImageDrawable(App.getAppContext().getResources().getDrawable(R.drawable.ic_text, App.getAppContext().getTheme()));
        } else {
            holder.text.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class OrderHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView categories;
        TextView price;
        TextView actionButton;
        TextView userCount;
        TextView messageCount;

        ImageView orderType;
        ImageView usersAccepted;
        ImageView messages;
        ImageView status;
        ImageView text;
        ImageView files;

        OrderHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            categories = itemView.findViewById(R.id.tv_categories);
            price = itemView.findViewById(R.id.tv_price);
            actionButton = itemView.findViewById(R.id.tv_action_button);
            userCount = itemView.findViewById(R.id.tv_users_count);
            messageCount = itemView.findViewById(R.id.tv_message_count);

            orderType = itemView.findViewById(R.id.imv_1);
            usersAccepted = itemView.findViewById(R.id.imv_3);
            messages = itemView.findViewById(R.id.imv_5);
            status = itemView.findViewById(R.id.imv_2);
            text = itemView.findViewById(R.id.imv_4);
            files = itemView.findViewById(R.id.imv_6);


        }
    }

}
