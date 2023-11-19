package com.example.bottomnavbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotifItem> notifItemList;
    private Context context;

    public NotificationAdapter(List<NotifItem> notifItemList, Context context) {
        this.notifItemList = notifItemList;
        this.context = context;
    }


    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        NotifItem notifItem = notifItemList.get(position);

        // Set data to views in item_row.xml
        holder.dateTextView.setText(notifItem.getDate());

        holder.contentTextView.setText(notifItem.getContent());


    }

    @Override
    public int getItemCount() {
        return notifItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView contentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.NotifDate);

            contentTextView = itemView.findViewById(R.id.Notifcontent);

        }
    }
}
