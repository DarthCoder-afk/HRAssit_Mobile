package com.example.bottomnavbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RequestRecyclerViewAdapter extends RecyclerView.Adapter<RequestRecyclerViewAdapter.ViewHolder> {

    private List<RequestItem> requestList;
    private Context context;

    public RequestRecyclerViewAdapter(List<RequestItem> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
    }


    @NonNull
    @Override
    public RequestRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestRecyclerViewAdapter.ViewHolder holder, int position) {
        RequestItem requestItem = requestList.get(position);

        holder.requesttypeTextView.setText(requestItem.getRequestType());
        holder.userProfileImageView.setImageResource(requestItem.getUserProfileImage());
        holder.usernameTextView.setText(requestItem.getRequest_username());
        holder.positionTextView.setText(requestItem.getPosition());
        holder.dateTextView.setText(requestItem.getRequest_date());

        holder.viewInfoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                // You can navigate to the RequestDetailsFragment

                // Example: Start a new fragment transaction
                FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.hr_frame_layout, new RequestDetails());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });



    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public ImageView userProfileImageView;
        public TextView usernameTextView;
        public TextView positionTextView;
        public TextView requesttypeTextView;

        public ImageView viewInfoImageView; // Add this line

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.RequestDate);
            userProfileImageView = itemView.findViewById(R.id.Requestuserprofile);
            usernameTextView = itemView.findViewById(R.id.Employeenametxt);
            positionTextView = itemView.findViewById(R.id.RequestEmployeeposistion);
            requesttypeTextView = itemView.findViewById(R.id.RequestType);
            viewInfoImageView = itemView.findViewById(R.id.ViewInfo);
        }
    }
}
