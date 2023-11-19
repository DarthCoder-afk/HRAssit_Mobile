package com.example.bottomnavbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryItem> historyItemList;
    private Context context;

    public HistoryAdapter(List<HistoryItem> historyItemList, Context context) {
        this.historyItemList = historyItemList;
        this.context = context;
    }


    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        HistoryItem historyItem = historyItemList.get(position);

        // Set data to views in item_row.xml
        holder.dateTextView.setText(historyItem.getDate());

        holder.contentTextView.setText(historyItem.getContent());
        

    }

    @Override
    public int getItemCount() {
        return historyItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView contentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.HistoryDate);

            contentTextView = itemView.findViewById(R.id.Historycontent);

        }
    }
}
