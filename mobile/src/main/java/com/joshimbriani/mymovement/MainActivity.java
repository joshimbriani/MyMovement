package com.joshimbriani.mymovement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.snackbar.Snackbar;
import com.joshimbriani.mymovement.db.Movement;

public class MainActivity extends AppCompatActivity {
    public static final int NEW_MOVEMENT_ACTIVITY_REQUEST_CODE = 1;

    private MovementViewModel mMovementViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public void startCreateActivity(View v) {
        Intent intent = new Intent(this, NewMovementActivity.class);
        startActivityForResult(intent, NEW_MOVEMENT_ACTIVITY_REQUEST_CODE);
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

    private RecyclerView.RecyclerListener mRecycleListener = new RecyclerView.RecyclerListener() {
        @Override
        public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
            MovementListAdapter.MovementViewHolder mapHolder = (MovementListAdapter.MovementViewHolder) holder;
            if (mapHolder != null && mapHolder.map != null) {
                mapHolder.map.clear();
                mapHolder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
            }
        }
    };
}
