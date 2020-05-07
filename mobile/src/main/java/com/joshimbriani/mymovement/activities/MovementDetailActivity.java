package com.joshimbriani.mymovement.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
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
import com.joshimbriani.mymovement.services.LocationService;

import java.util.Collections;
import java.util.Comparator;

public class MovementDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MovementDetailViewModel mMovementDetailViewModel;
    private MapView movementDetailMapView;
    private GoogleMap map;
    private long movementId;
    private boolean serviceRunning;
    private Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_detail);

        movementId = getIntent().getLongExtra("movementId", 1);
        serviceRunning = LocationService.serviceRunning && LocationService.serviceId == movementId;

        if (serviceRunning) {
        }

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
        this.menu = menu;
        setStartStopButton();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_start:
                if (serviceRunning) {
                    stopService();
                    serviceRunning = false;
                } else {
                    startService();
                    serviceRunning = true;
                }
                setStartStopButton();
                return true;

            case R.id.action_edit:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void setStartStopButton() {
        menu.getItem(0).setIcon(ResourcesCompat.getDrawable(getResources(), serviceRunning ? R.drawable.ic_stop : R.drawable.ic_start, null));
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        serviceIntent.putExtra("movementId", movementId);

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void stopService() {
        getApplicationContext().sendBroadcast(new Intent("StopService"));
    }
}
