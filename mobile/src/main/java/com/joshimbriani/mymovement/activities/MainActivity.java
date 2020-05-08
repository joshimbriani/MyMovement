package com.joshimbriani.mymovement.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.snackbar.Snackbar;
import com.joshimbriani.mymovement.R;
import com.joshimbriani.mymovement.db.Movement;

public class MainActivity extends AppCompatActivity {
    public static final int NEW_MOVEMENT_ACTIVITY_REQUEST_CODE = 1;

    private MovementViewModel mMovementViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestLocationPermission();

        RecyclerView recyclerView = findViewById(R.id.movementRecyclerView);
        final MovementListAdapter adapter = new MovementListAdapter(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setRecyclerListener(mRecycleListener);
        mMovementViewModel = new ViewModelProvider(this).get(MovementViewModel.class);
        mMovementViewModel.getAllMovementsWithPoints().observe(this, (movements) -> {
            adapter.setAllMovementsWithPoints(movements);
        });
    }

    private void requestLocationPermission() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Snackbar.make(findViewById(R.id.main_root_layout), "To use the app, you need to provide fine permissions.", Snackbar.LENGTH_INDEFINITE).setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 34);
                }
            });
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 34);
        }
    }

    public void startCreateActivity(View v) {
        Intent intent = new Intent(this, NewMovementActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_MOVEMENT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Movement movement = new Movement(data.getStringExtra(NewMovementActivity.EXTRA_REPLY));
            mMovementViewModel.insert(movement);
        } else {
            Snackbar.make(findViewById(R.id.main_root_layout), "Empty name", Snackbar.LENGTH_SHORT);
        }
    }

    private RecyclerView.RecyclerListener mRecycleListener = holder -> {
        MovementListAdapter.MovementViewHolder mapHolder = (MovementListAdapter.MovementViewHolder) holder;
        if (mapHolder != null && mapHolder.map != null) {
            mapHolder.map.clear();
            mapHolder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    };
}
