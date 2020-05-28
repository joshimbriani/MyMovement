package com.joshimbriani.mymovement;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainListViewHolder extends RecyclerView.ViewHolder {
    protected TextView count;
    protected TextView name;
    protected TextView date;

    public MainListViewHolder(@NonNull View itemView) {
        super(itemView);
        Log.e("SEEME", "Creating holder");
        count = itemView.findViewById(R.id.num_points);
        name = itemView.findViewById(R.id.movement_name);
        date = itemView.findViewById(R.id.movement_time);
    }
}
