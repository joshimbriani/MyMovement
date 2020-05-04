package com.joshimbriani.mymovement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joshimbriani.mymovement.db.MovementWithPoints;

import java.util.List;

public class MovementListAdapter extends RecyclerView.Adapter<MovementListAdapter.MovementViewHolder>  {

    class MovementViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        private final TextView movementItemView;
        private final MapView movementMapView;
        private final TextView movementPointCountView;
        private final TextView movementDateView;
        private final Context mContext;

        protected GoogleMap map;

        private MovementViewHolder(Context context, View itemView) {
            super(itemView);
            movementItemView = itemView.findViewById(R.id.movementName);
            movementMapView = itemView.findViewById(R.id.mapView);
            movementPointCountView = itemView.findViewById(R.id.movementPointCount);
            movementDateView = itemView.findViewById(R.id.movementDates);
            if (movementMapView != null) {
                movementMapView.onCreate(null);
                movementMapView.getMapAsync(this);
            }
            mContext = context;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(mContext);
            map = googleMap;
            setMapLocation();
        }

        private void setMapLocation() {
            if (map == null) {
                return;
            }

            LatLng loc = new LatLng(-33.920455, 18.466941);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13f));
            map.addMarker(new MarkerOptions().position(loc));
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
            holder.movementItemView.setText(movementWithPoints.movement.getName());
            holder.movementPointCountView.setText("" + movementWithPoints.points.size());
            holder.movementDateView.setText(movementWithPoints.getDatetimeRange());
            holder.setMapLocation();
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