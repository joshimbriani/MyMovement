package com.joshimbriani.mymovement.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.joshimbriani.mymovement.R;
import com.joshimbriani.mymovement.data.EditMovementViewModel;
import com.joshimbriani.mymovement.data.EditMovementViewModelFactory;

public class EditMovementActivity extends AppCompatActivity {
    private EditMovementViewModel mEditMovementViewModel;
    private EditText editTextMovementName;
    private Menu menu;
    private long movementId;
    private String mMovementName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movement);

        movementId = getIntent().getLongExtra("movementId", 1);
        editTextMovementName = findViewById(R.id.editTextMovementName);

        mEditMovementViewModel = new ViewModelProvider(this, new EditMovementViewModelFactory(getApplication(), movementId)).get(EditMovementViewModel.class);
        mEditMovementViewModel.getMovement().observe(this, (movementWithPoints -> {
            mMovementName = movementWithPoints.movement.getName();
            editTextMovementName.setText(mMovementName);
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_movement_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveMovement();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void saveMovement() {
        if (editTextMovementName.getText().toString().isEmpty()) {
            // Show a toast
            editTextMovementName.setError("Name cannot be empty");
        } else {
            mEditMovementViewModel.setMovementName(editTextMovementName.getText().toString());
            finish();
        }
    }
}
