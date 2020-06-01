package com.joshimbriani.mymovement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.wear.widget.SwipeDismissFrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joshimbriani.mymovement.data.MovementDetailViewModel;
import com.joshimbriani.mymovement.data.MovementDetailViewModelFactory;
import com.joshimbriani.mymovement.data.MovementPoint;
import com.joshimbriani.mymovement.data.MovementViewModel;
import com.joshimbriani.mymovement.data.MovementWithPoints;
import com.joshimbriani.mymovement.services.LocationService;

public class MovementDetailFragment extends Fragment implements OnMapReadyCallback {
    private TextView movementName;
    private MovementDetailViewModel mMovementDetailViewModel;
    private long id;
    private MovementWithPoints movementWithPoints;
    private MapView movementDetailMapView;
    private GoogleMap map;
    private ImageView startButton;
    private boolean serviceRunning;
    private SharedPreferences mPreferences;

    public MovementDetailFragment(long id) {
        this.id = id;
    }

    private final androidx.wear.widget.SwipeDismissFrameLayout.Callback callback =
            new androidx.wear.widget.SwipeDismissFrameLayout.Callback() {
                @Override
                public void onDismissed(SwipeDismissFrameLayout layout) {
                    getFragmentManager().popBackStackImmediate();
                }
            };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity().getApplicationContext());
        map = googleMap;
        if (movementWithPoints != null) {
            setMapPoints(movementWithPoints);
        } else {
            setMapPoints();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SwipeDismissFrameLayout swipeDismissFrameLayout = new SwipeDismissFrameLayout(getActivity());

        View v = inflater.inflate(R.layout.movement_detail_fragment, container, false);
        serviceRunning = LocationService.serviceRunning && LocationService.serviceId == id;

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        movementName = v.findViewById(R.id.movement_detail_name);
        startButton = v.findViewById(R.id.start_icon);
        startButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), serviceRunning ? R.drawable.ic_stop : R.drawable.ic_start, null));
        startButton.setOnClickListener(v1 -> {
            Log.e("SEEME", "Clicking");
            if (!serviceRunning) {
                Log.e("SEEME", "Starting service");
                Intent serviceIntent = new Intent(getContext(), LocationService.class);
                serviceIntent.putExtra("movementId", id);
                serviceIntent.putExtra("refreshInterval", Integer.parseInt(mPreferences.getString("refresh_value", "60")));

                ContextCompat.startForegroundService(getContext(), serviceIntent);
                serviceRunning = true;
            } else {
                getActivity().getApplicationContext().sendBroadcast(new Intent("StopService"));
                serviceRunning = false;
            }

            startButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), serviceRunning ? R.drawable.ic_stop : R.drawable.ic_start, null));
        });

        mMovementDetailViewModel = new ViewModelProvider(this, new MovementDetailViewModelFactory(getActivity().getApplication(), id)).get(MovementDetailViewModel.class);
        mMovementDetailViewModel.getMovement().observe(this, (movement) -> {
            if (movement == null) {
                return;
            }
            movementWithPoints = movement;
            movementName.setText(movementWithPoints.movement.getName());
            movementDetailMapView = v.findViewById(R.id.detail_map_view);
            if (movementDetailMapView != null) {
                movementDetailMapView.onCreate(null);
                movementDetailMapView.getMapAsync(this);
            }
            if (movement.points.size() > 0 && map != null) {
                setMapPoints(movement);
            }
        });
        swipeDismissFrameLayout.addView(v);
        swipeDismissFrameLayout.addCallback(callback);
        return swipeDismissFrameLayout;
    }

    private void setMapPoints() {
        LatLng loc = new LatLng(37.404310, -121.924650);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13f));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void setMapPoints(MovementWithPoints movement) {
        map.moveCamera(movement.getCameraUpdateForList());
        for (MovementPoint point : movement.points) {
            map.addMarker(new MarkerOptions().position(new LatLng(point.getLat(), point.getLon())));
        }
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
}
