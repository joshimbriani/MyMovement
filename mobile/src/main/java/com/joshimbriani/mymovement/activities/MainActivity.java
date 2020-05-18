package com.joshimbriani.mymovement.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.snackbar.Snackbar;
import com.joshimbriani.mymovement.R;
import com.joshimbriani.mymovement.data.Movement;
import com.joshimbriani.mymovement.data.MovementViewModel;
import com.joshimbriani.mymovement.data.MovementWithPoints;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int NEW_MOVEMENT_ACTIVITY_REQUEST_CODE = 1;

    private MovementViewModel mMovementViewModel;
    private List<MovementWithPoints> mMovements;

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
            mMovements = movements;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_export) {
            List<Integer> selectedItems = new ArrayList<>();
            String[] movementTitles = new String[mMovements.size()];
            for (int i = 0; i < mMovements.size(); i++) {
                movementTitles[i] = mMovements.get(i).movement.getName();
            }
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Select Movements to export")
                    .setMultiChoiceItems(movementTitles, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (isChecked) {
                                selectedItems.add(which);
                            } else if (selectedItems.contains(which)) {
                                selectedItems.remove(which);
                            }
                        }
                    })
                    .setPositiveButton("Export", (dialog, which) -> {
                        if (selectedItems.isEmpty()) {
                            return;
                        }
                        Log.e("TEST", "Selected items " + selectedItems.toString());
                        List<MovementWithPoints> movementWithPoints = new ArrayList<>();
                        for (int movementIndex : selectedItems) {
                            movementWithPoints.add(mMovements.get(movementIndex));
                        }
                        String fileName = "MyMovementExport_" + System.currentTimeMillis() + ".json";
                        File file = new File(getApplicationContext().getCacheDir(), fileName);

                        try {
                            FileWriter writer = new FileWriter(file);
                            writeMovementJson(writer, movementWithPoints);
                            writer.flush();
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        File filePath = new File(getApplicationContext().getCacheDir(), "");
                        File newFile = new File(filePath, fileName);
                        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.joshimbriani.mymovement.fileprovider", newFile);

                        Intent intent = ShareCompat.IntentBuilder.from(MainActivity.this)
                                .setStream(uri)
                                .setType("text/json")
                                .getIntent()
                                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(intent, "Export To..."));
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // Do nothing
                    }).create().show();
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void writeMovementJson(FileWriter fileWriter, List<MovementWithPoints> movementWithPoints) throws IOException {
        JsonWriter writer = new JsonWriter(fileWriter);
        writer.setIndent("    ");
        writer.beginArray();
        for (MovementWithPoints movement : movementWithPoints) {
            movement.movementToJson(writer);
        }
        writer.endArray();
        writer.close();
    }
}
