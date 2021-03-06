package com.joshimbriani.mymovement.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.joshimbriani.mymovement.R;
import com.joshimbriani.mymovement.data.MovementPoint;
import com.joshimbriani.mymovement.data.MovementWithPoints;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MovementDetailListAdapter extends RecyclerView.Adapter<MovementDetailListAdapter.MovementDetailViewHolder> {
    class MovementDetailViewHolder extends RecyclerView.ViewHolder {
        private final TextView pointDetailView;
        private final TextView pointDatetimeView;

        private MovementDetailViewHolder(Context context, View itemView) {
            super(itemView);
            pointDetailView = itemView.findViewById(R.id.movement_detail_point);
            pointDatetimeView = itemView.findViewById(R.id.movement_detail_datetime);
        }
    }

    private final LayoutInflater mInflater;
    private MovementWithPoints mMovement;
    private Context mContext;
    private Context activityContext;

    MovementDetailListAdapter(Context context) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void addContext(Context context) {
        activityContext = context;
    }

    @Override
    public MovementDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.movement_detail_recyclerview_item, parent, false);
        return new MovementDetailViewHolder(mContext, itemView);
    }

    @Override
    public void onBindViewHolder(MovementDetailViewHolder holder, int position) {
        if (this.mMovement != null) {
            this.mMovement.sortPoints();
            MovementPoint point = this.mMovement.points.get(position);
            holder.pointDetailView.setText(point.getLat() + " " + point.getLon());
            holder.pointDatetimeView.setText(point.getDateTime().format(DateTimeFormatter.ofPattern("hh:mma")));

            holder.itemView.setOnClickListener(v -> ((MovementDetailActivity) activityContext).setMapPoints(point.getId()));
        }
    }

    void setMovement(MovementWithPoints movement) {
        this.mMovement = movement;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mMovement != null) {
            return mMovement.points.size();
        }
        return 0;
    }
}
