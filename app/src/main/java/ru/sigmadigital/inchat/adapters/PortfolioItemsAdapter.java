package ru.sigmadigital.inchat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.models.PortfolioItemResponse;

public class PortfolioItemsAdapter extends RecyclerView.Adapter<PortfolioItemsAdapter.PortfolioItem> {

    private List<PortfolioItemResponse> itemList;


    @NonNull
    @Override
    public PortfolioItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_portfolio, null);

        return new PortfolioItem(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PortfolioItem holder, int position) {
        holder.setIsRecyclable(false);
        PortfolioItemResponse item = itemList.get(position);
        holder.title.setText(item.getTitle());
        holder.desc.setText(item.getDescription());
        holder.icon.setImageDrawable(App.getAppContext().getResources().getDrawable(R.drawable.ic_file));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class PortfolioItem extends RecyclerView.ViewHolder{
        private ImageView icon;
        private TextView title;
        private TextView desc;

        public PortfolioItem(@NonNull View v) {
            super(v);
            icon = v.findViewById(R.id.imv_icon);
            title = v.findViewById(R.id.tv_title);
            desc = v.findViewById(R.id.tv_desc);
        }
    }

    public List<PortfolioItemResponse> getItemList() {
        return itemList;
    }

    public void setItemList(List<PortfolioItemResponse> itemList) {
        this.itemList = itemList;
    }
}
