package com.joshimbriani.mymovement.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.joshimbriani.mymovement.data.GsonWithZonedDateTime;
import com.joshimbriani.mymovement.data.Movement;
import com.joshimbriani.mymovement.data.NewMovementViewModel;
import com.joshimbriani.mymovement.services.LocationService;
import com.joshimbriani.mymovement.R;

public class NewMovementActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.joshimbriani.mymovement.REPLY";
    private EditText mEditMovementNameView;
    private NewMovementViewModel newMovementViewModel;
    private SharedPreferences mPreferences;
    private DataClient dataClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_movement);
        mEditMovementNameView = findViewById(R.id.movementNameEditText);
        newMovementViewModel = new ViewModelProvider(this).get(NewMovementViewModel.class);

        dataClient = Wearable.getDataClient(getApplicationContext());

        final Button saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(view -> {
            if (TextUtils.isEmpty(mEditMovementNameView.getText())) {
                mEditMovementNameView.setError("Name cannot be empty");
                return;
            } else {
                AsyncTask.execute(() -> {
                    String movementName = mEditMovementNameView.getText().toString();
                    Movement newMovement = new Movement(movementName, "mobile");
                    newMovementViewModel.createMovement(newMovement);
                    syncMovement(newMovement);
                    finish();
                });
            }
            finish();
        });

        final Button saveAndStartButton = findViewById(R.id.button_save_and_start);
        saveAndStartButton.setOnClickListener(view -> {
            if (TextUtils.isEmpty(mEditMovementNameView.getText())) {
                mEditMovementNameView.setError("Name cannot be empty");
                return;
            } else {
                AsyncTask.execute(() -> {
                    String movementName = mEditMovementNameView.getText().toString();
                    Movement newMovement = new Movement(movementName, "mobile");
                    long id = newMovementViewModel.createMovement(newMovement);
                    syncMovement(newMovement);
                    startService(id);
                    finish();
                });
            }
            finish();
        });

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void startService(long movementId) {
        Intent serviceIntent = new Intent(this, LocationService.class);
        serviceIntent.putExtra("movementId", movementId);
        serviceIntent.putExtra("refreshInterval", Integer.parseInt(mPreferences.getString("refresh_value", "60")));

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void syncMovement(Movement movement) {
        Gson gson = GsonWithZonedDateTime.getGson();
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/movement/" + movement.getId());
        putDataMapRequest.getDataMap().putString("com.joshimbriani.mymovement.movement", gson.toJson(movement));
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        Task<DataItem> putDataTask = dataClient.putDataItem(putDataRequest);
    }
}
