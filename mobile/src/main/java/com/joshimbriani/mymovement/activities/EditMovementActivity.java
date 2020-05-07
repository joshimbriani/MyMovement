package com.joshimbriani.mymovement.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.joshimbriani.mymovement.R;

public class EditMovementActivity extends AppCompatActivity {
    private EditText editTextMovementName;
    private Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movement);

        editTextMovementName = findViewById(R.id.editTextMovementName);
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
        } else {

        }
    }
}
