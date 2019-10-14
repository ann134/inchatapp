package ru.sigmadigital.inchat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.sigmadigital.inchat.R;

import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.models.UserResponse;
import ru.sigmadigital.inchat.api.MyRetrofit;

public class SubsRvAdapter extends RecyclerView.Adapter<SubsRvAdapter.SubHolder> {

    private List<UserResponse> usersList;
    private MyRetrofit.ServiceManager service;
    private int type;
    private OnSubsButtonListener listener;

    public SubsRvAdapter(int type, OnSubsButtonListener listener) {
        this.type = type;
        service = MyRetrofit.getInstance();
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(App.getAppContext()).inflate(R.layout.item_sub, null);
        return new SubHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SubHolder holder, int position) {
        holder.setIsRecyclable(false);
        final UserResponse user = usersList.get(position);
        holder.name.setText(user.getName());
        holder.city.setText(user.getCity());

        if(type == 1){
            holder.action.setText("Подписаться");
            holder.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.subscribe(user.getId());
                }
            });
        }

        if(type == 2){
            holder.action.setText("Отписаться");
            holder.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.unsubscribe(user.getId());
                }
            });
        }

        if(type == 3){
            holder.action.setText("Удалить");
            holder.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.deleteSubscriber(user.getId());
                }
            });
        }

        if(type == 4){
            holder.action.setText("Подтвердить");
            holder.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.acceptSubRequest(user.getId());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public class SubHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView city;
        private Button action;
        public SubHolder(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.tv_name);
            city = v.findViewById(R.id.tv_city);
            action = v.findViewById(R.id.btn_action);
        }
    }

    public List<UserResponse> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<UserResponse> usersList) {
        this.usersList = usersList;
    }

    public interface OnSubsButtonListener{
        void subscribe(long userId);
        void unsubscribe(long userId);
        void deleteSubscriber(long userId);
        void acceptSubRequest(long userId);
    }


}
