package com.joshimbriani.mymovement;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

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

public class NewMovementFragment extends Fragment {
    private EditText movementName;
    private Button createMovement;
    private NewMovementViewModel newMovementViewModel;
    private DataClient dataClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_movement, container, false);

        newMovementViewModel = new NewMovementViewModel(getActivity().getApplication());
        movementName = v.findViewById(R.id.new_movement_name);
        createMovement = v.findViewById(R.id.create_movement_button);
        createMovement.setOnClickListener(v1 -> {
            if (TextUtils.isEmpty(movementName.getText())) {
                movementName.setError("Name cannot be empty");
                return;
            } else {
                AsyncTask.execute(() -> {
                    String movementNameVal = movementName.getText().toString();
                    Movement newMovement = new Movement(movementNameVal, "wear");
                    newMovementViewModel.createMovement(newMovement);
                    syncMovement(newMovement);
                });
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                MainListFragment listFragment = new MainListFragment();
                transaction.replace(R.id.content_frame, listFragment);
                transaction.commit();
            }
        });

        dataClient = Wearable.getDataClient(getContext());

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void syncMovement(Movement movement) {
        Gson gson = GsonWithZonedDateTime.getGson();
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/movement/" + movement.getId());
        putDataMapRequest.getDataMap().putString("com.joshimbriani.mymovement.movement", gson.toJson(movement));
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        putDataMapRequest.setUrgent();
        Task<DataItem> putDataTask = dataClient.putDataItem(putDataRequest);
    }
}
