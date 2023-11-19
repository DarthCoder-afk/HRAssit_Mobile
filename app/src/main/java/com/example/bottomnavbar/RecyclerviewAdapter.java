package com.example.bottomnavbar;

import android.app.Notification;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder> {

    private List<EmployeeItem> employeeList;
    private Context context;

    public RecyclerviewAdapter(List<EmployeeItem> employeeList, Context context) {
        this.employeeList = employeeList;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerviewAdapter.ViewHolder holder, int position) {
        EmployeeItem employeeItem = employeeList.get(position);

        // Set data to views in item_row.xml
        holder.dateTextView.setText(employeeItem.getDate());
        holder.userProfileImageView.setImageResource(employeeItem.getUserProfileImage());
        holder.usernameTextView.setText(employeeItem.getUsername());
        holder.positionTextView.setText(employeeItem.getPosition());
        holder.contentTextView.setText(employeeItem.getContent());
        holder.statusTextView.setText(employeeItem.getStatus());

        // Check if the status is "Declined" and set the text color to red if true
        if ("Declined!".equals(employeeItem.getStatus())) {
            holder.statusTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        } else {
            // Reset the text color to the default color for other cases
            holder.statusTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        }

    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public ImageView userProfileImageView;
        public TextView usernameTextView;
        public TextView positionTextView;
        public TextView contentTextView;
        public TextView statusTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.DateTxt);
            userProfileImageView = itemView.findViewById(R.id.Userprofile);
            usernameTextView = itemView.findViewById(R.id.UsernameTxt);
            positionTextView = itemView.findViewById(R.id.PositionTxt);
            contentTextView = itemView.findViewById(R.id.ContentTxt);
            statusTextView = itemView.findViewById(R.id.StatusTxt);
        }
    }
}
