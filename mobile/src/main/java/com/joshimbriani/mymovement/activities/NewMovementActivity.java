package com.joshimbriani.mymovement.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.joshimbriani.mymovement.data.Movement;
import com.joshimbriani.mymovement.data.NewMovementViewModel;
import com.joshimbriani.mymovement.services.LocationService;
import com.joshimbriani.mymovement.R;

public class NewMovementActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.joshimbriani.mymovement.REPLY";
    private EditText mEditMovementNameView;
    private NewMovementViewModel newMovementViewModel;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_movement);
        mEditMovementNameView = findViewById(R.id.movementNameEditText);
        newMovementViewModel = new ViewModelProvider(this).get(NewMovementViewModel.class);

        final Button saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(view -> {
            if (TextUtils.isEmpty(mEditMovementNameView.getText())) {
                mEditMovementNameView.setError("Name cannot be empty");
                return;
            } else {
                AsyncTask.execute(() -> {
                    String movementName = mEditMovementNameView.getText().toString();
                    Movement newMovement = new Movement(movementName);
                    newMovementViewModel.createMovement(newMovement);
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
                    Movement newMovement = new Movement(movementName);
                    long id = newMovementViewModel.createMovement(newMovement);
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
}
