package com.joshimbriani.mymovement;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.joshimbriani.mymovement.data.Movement;
import com.joshimbriani.mymovement.data.NewMovementViewModel;

public class NewMovementFragment extends Fragment {
    private EditText movementName;
    private Button createMovement;
    private NewMovementViewModel newMovementViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_movement, container, false);

        newMovementViewModel = new NewMovementViewModel(getActivity().getApplication());
        movementName = v.findViewById(R.id.new_movement_name);
        createMovement = v.findViewById(R.id.create_movement_button);
        createMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(movementName.getText())) {
                    movementName.setError("Name cannot be empty");
                    return;
                } else {
                    AsyncTask.execute(() -> {
                        String movementNameVal = movementName.getText().toString();
                        Movement newMovement = new Movement(movementNameVal);
                        newMovementViewModel.createMovement(newMovement);
                    });
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    MainListFragment listFragment = new MainListFragment();
                    transaction.replace(R.id.content_frame, listFragment);
                    transaction.commit();
                }
            }
        });

        return v;
    }
}
