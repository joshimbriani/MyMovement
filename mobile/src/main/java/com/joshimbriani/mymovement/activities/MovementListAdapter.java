package com.joshimbriani.mymovement.activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joshimbriani.mymovement.R;
import com.joshimbriani.mymovement.db.MovementPoint;
import com.joshimbriani.mymovement.db.MovementWithPoints;

import java.util.List;

public class MovementListAdapter extends RecyclerView.Adapter<MovementListAdapter.MovementViewHolder>  {

    class MovementViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        private final CardView cardView;
        private final TextView movementItemView;
        private final MapView movementMapView;
        private final TextView movementPointCountView;
        private final TextView movementDateView;
        private final Context mContext;
        private MovementWithPoints movementWithPoints;

        protected GoogleMap map;

        private MovementViewHolder(Context context, View itemView) {
            super(itemView);
            movementItemView = itemView.findViewById(R.id.movementName);
            movementMapView = itemView.findViewById(R.id.mapView);
            movementPointCountView = itemView.findViewById(R.id.movementPointCount);
            movementDateView = itemView.findViewById(R.id.movementDates);
            cardView = itemView.findViewById(R.id.card_view);
            if (movementMapView != null) {
                movementMapView.onCreate(null);
                movementMapView.getMapAsync(this);
            }
            mContext = context;
        }

        public void setMovementWithPoints(MovementWithPoints movementWithPoints) {
            this.movementWithPoints = movementWithPoints;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(mContext);
            map = googleMap;
            if (movementWithPoints == null) {
                setMapLocation();
            } else {
                setMapLocation(movementWithPoints);
            }
        }

        private void setMapLocation() {
            if (map == null) {
                return;
            }

            LatLng loc = new LatLng(37.404310, -121.924650);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13f));
            map.addMarker(new MarkerOptions().position(loc));
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        private void setMapLocation(MovementWithPoints movementWithPoints) {
            if (map == null) {
                return;
            }

            map.moveCamera(movementWithPoints.getCameraUpdateForList());
            for (MovementPoint point : movementWithPoints.points) {
                map.addMarker(new MarkerOptions().position(new LatLng(point.getLat(), point.getLon())));
            }
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private final LayoutInflater mInflater;
    private List<MovementWithPoints> mMovements;
    private Context mContext;

    MovementListAdapter(Context context) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MovementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.movement_recyclerview_item, parent, false);
        return new MovementViewHolder(mContext, itemView);
    }

    @Override
    public void onBindViewHolder(MovementViewHolder holder, int position) {
        if (mMovements != null) {
            MovementWithPoints movementWithPoints = mMovements.get(position);
            holder.setMovementWithPoints(movementWithPoints);
            holder.movementItemView.setText(movementWithPoints.movement.getName());
            holder.movementPointCountView.setText("" + movementWithPoints.points.size());
            holder.movementDateView.setText(movementWithPoints.getDatetimeRange());
            holder.cardView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, MovementDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            });
            holder.setMapLocation(movementWithPoints);
        } else {
            holder.movementItemView.setText("No Movement");
        }
    }

    void setAllMovementsWithPoints(List<MovementWithPoints> movements) {
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