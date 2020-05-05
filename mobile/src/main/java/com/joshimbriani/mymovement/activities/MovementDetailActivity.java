package com.joshimbriani.mymovement.activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class MovementDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MovementDetailViewModel mMovementDetailViewModel;
    private MapView movementDetailMapView;
    private GoogleMap map;
    private long movementId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_detail);

        movementId = getIntent().getLongExtra("movementId", 1);

        movementDetailMapView = findViewById(R.id.detail_map_view);
        if (movementDetailMapView != null) {
            movementDetailMapView.onCreate(null);
            movementDetailMapView.getMapAsync(this);
        }
        RecyclerView recyclerView = findViewById(R.id.movement_detail_recycler_view);
        final MovementDetailListAdapter adapter = new MovementDetailListAdapter(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMovementDetailViewModel = new ViewModelProvider(this, new MovementDetailViewModelFactory(getApplication(), movementId)).get(MovementDetailViewModel.class);
        mMovementDetailViewModel.getMovement().observe(this, (movement) -> {
            getSupportActionBar().setTitle(movement.movement.getName());
            adapter.setMovement(movement);
            if (map != null) {
                setMapPoints(movement);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movement_detail_menu, menu);
        return true;
    }

    @Override
    public void onResume() {
        movementDetailMapView.onResume();
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());
        map = googleMap;
        setMapPoints();
    }

    private void setMapPoints() {
        LatLng loc = new LatLng(37.404310, -121.924650);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13f));
        map.addMarker(new MarkerOptions().position(loc));
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
