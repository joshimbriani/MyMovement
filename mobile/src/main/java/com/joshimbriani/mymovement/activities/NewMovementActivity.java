package com.joshimbriani.mymovement.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.joshimbriani.mymovement.services.LocationService;
import com.joshimbriani.mymovement.R;

public class NewMovementActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.joshimbriani.mymovement.REPLY";
    private EditText mEditMovementNameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_movement);
        mEditMovementNameView = findViewById(R.id.movementNameEditText);

        final Button saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(mEditMovementNameView.getText())) {
                mEditMovementNameView.setError("Name cannot be empty");
                return;
            } else {
                String movement = mEditMovementNameView.getText().toString();
                replyIntent.putExtra(EXTRA_REPLY, movement);
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });

        final Button saveAndStartButton = findViewById(R.id.button_save_and_start);
        saveAndStartButton.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(mEditMovementNameView.getText())) {
                mEditMovementNameView.setError("Name cannot be empty");
                return;
            } else {
                requestLocationPermission();
                String movement = mEditMovementNameView.getText().toString();
                replyIntent.putExtra(EXTRA_REPLY, movement);
                setResult(RESULT_OK, replyIntent);
                startService(1);
            }
            finish();
        });
    }

    private void requestLocationPermission() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Snackbar.make(findViewById(R.id.main_root_layout), "To use the app, you need to provide fine permissions.", Snackbar.LENGTH_INDEFINITE).setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(NewMovementActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 34);
                }
            });
        } else {
            ActivityCompat.requestPermissions(NewMovementActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 34);
        }
    }

    private void startService(long movementId) {
        Intent serviceIntent = new Intent(this, LocationService.class);
        serviceIntent.putExtra("movementId", movementId);

        ContextCompat.startForegroundService(this, serviceIntent);
    }
}
