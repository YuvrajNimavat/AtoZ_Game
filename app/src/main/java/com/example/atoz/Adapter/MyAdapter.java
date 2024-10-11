package com.example.atoz.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atoz.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<String> mData;
    private OnButtonClickListener onButtonClickListener;
    private boolean buttonsEnabled = true;

    // Data and click listener are passed into the constructor
    public MyAdapter(List<String> data, OnButtonClickListener onButtonClickListener) {
        this.mData = data;
        this.onButtonClickListener = onButtonClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the button
        String buttonText = mData.get(position);
        holder.itemButton.setText(buttonText);

        // Set click listener for the button
        holder.itemButton.setOnClickListener(v -> {
            if (buttonsEnabled) {
                onButtonClickListener.onButtonClick(holder.getAdapterPosition(), buttonText);
            }
        });

        // Set the button's enabled state
        holder.itemButton.setEnabled(buttonsEnabled);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(List<String> newData) {
        this.mData = newData;
        notifyDataSetChanged();
    }

    public void setButtonsEnabled(boolean enabled) {
        this.buttonsEnabled = enabled;
        notifyDataSetChanged(); // This will refresh the button states
    }

    public interface OnButtonClickListener {
        void onButtonClick(int position, String buttonText);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Button itemButton;

        ViewHolder(View itemView) {
            super(itemView);
            itemButton = itemView.findViewById(R.id.item_button);
        }
    }
}
