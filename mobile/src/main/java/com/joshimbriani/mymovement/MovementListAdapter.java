package com.joshimbriani.mymovement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MovementListAdapter extends RecyclerView.Adapter<MovementListAdapter.MovementViewHolder> {
    class MovementViewHolder extends RecyclerView.ViewHolder {
        private final TextView movementItemView;

        private MovementViewHolder(View itemView) {
            super(itemView);
            movementItemView = itemView.findViewById(R.id.movementName);
        }
    }

    private final LayoutInflater mInflater;
    private List<Movement> mMovements;

    MovementListAdapter(Context context) {mInflater = LayoutInflater.from(context);}

    @Override
    public MovementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.movement_recyclerview_item, parent, false);
        return new MovementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovementViewHolder holder, int position) {
        if (mMovements != null) {
            Movement current = mMovements.get(position);
            holder.movementItemView.setText(current.getName());
        } else {
            holder.movementItemView.setText("No Movement");
        }
    }

    void setMovements(List<Movement> movements) {
        mMovements = movements;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mMovements != null) {
            return mMovements.size();
        }
        return 0;
    }
}
