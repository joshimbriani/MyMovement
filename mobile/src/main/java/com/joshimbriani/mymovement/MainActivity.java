package com.joshimbriani.mymovement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    public static final int NEW_MOVEMENT_ACTIVITY_REQUEST_CODE = 1;

    private MovementViewModel mMovementViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.movementRecyclerView);
        final MovementListAdapter adapter = new MovementListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMovementViewModel = new ViewModelProvider(this).get(MovementViewModel.class);
        mMovementViewModel.getAllMovements().observe(this, (movements) -> {
            adapter.setMovements(movements);
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
}
