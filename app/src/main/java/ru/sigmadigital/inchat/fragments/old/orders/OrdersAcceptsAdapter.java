package ru.sigmadigital.inchat.fragments.old.orders;

import android.annotation.SuppressLint;
import android.util.Log;
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
import ru.sigmadigital.inchat.models.OrderAcceptResponse;

public class OrdersAcceptsAdapter extends RecyclerView.Adapter<OrdersAcceptsAdapter.AcceptHolder> {


    private List<OrderAcceptResponse> itemList = new ArrayList<>();
    OnAcceptItemClick onAcceptItemClick;


    public void setItemList(List<OrderAcceptResponse> itemList, OnAcceptItemClick onAcceptItemClick) {
        this.itemList = itemList;
        this.onAcceptItemClick = onAcceptItemClick;
    }

    public interface OnAcceptItemClick {
        void OnAcceptClick(OrderAcceptResponse acceptResponse);
    }

    @NonNull
    @Override
    public AcceptHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_order_accept, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new AcceptHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AcceptHolder holder, int position) {
        final OrderAcceptResponse acceptResponse = itemList.get(position);
        Log.e("order", position + "");
        holder.title.setText("Message: " + acceptResponse.getMessage());
        holder.description.setText("Days: " + acceptResponse.getDays());
        holder.price.setText("price: " + acceptResponse.getPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAcceptItemClick.OnAcceptClick(acceptResponse);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.e("frtrfg", itemList.size() + "");
        return itemList.size();
    }

    public class AcceptHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        TextView price;

        public AcceptHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            description = itemView.findViewById(R.id.tv_description);
            price = itemView.findViewById(R.id.tv_price);

        }
    }


}





