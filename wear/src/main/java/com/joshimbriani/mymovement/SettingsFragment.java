package com.joshimbriani.mymovement;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

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
import com.joshimbriani.mymovement.data.MovementPoint;
import com.joshimbriani.mymovement.data.MovementRepository;
import com.joshimbriani.mymovement.data.MovementWithPoints;

import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat {
    private Gson gson;
    private DataClient dataClient;
    private MovementRepository movementRepository;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        gson = GsonWithZonedDateTime.getGson();
        movementRepository = new MovementRepository(getActivity().getApplication());
        dataClient = Wearable.getDataClient(getActivity().getApplicationContext());

        Preference syncButton = findPreference("syncButton");
        syncButton.setOnPreferenceClickListener(preference -> {
            AsyncTask.execute(() -> syncAllData());
            return true;
        });
    }

    private void syncAllData() {
        List<MovementWithPoints> allMovements = movementRepository.getRawAllMovementsWithPoints();
        for (MovementWithPoints movement : allMovements) {
            syncMovement(movement.movement);
            for (MovementPoint point : movement.points) {
                syncMovementPoint(point);
            }
        }
    }

    private void syncMovementPoint(MovementPoint movementPoint) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/movement/" + movementPoint.getMovementId() + "/" + movementPoint.getId());
        putDataMapRequest.getDataMap().putString("com.joshimbriani.mymovement.movement", gson.toJson(movementPoint));
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        Task<DataItem> putDataTask = dataClient.putDataItem(putDataRequest);
    }

    private void syncMovement(Movement movement) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/movement/" + movement.getId());
        putDataMapRequest.getDataMap().putString("com.joshimbriani.mymovement.movement", gson.toJson(movement));
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        Task<DataItem> putDataTask = dataClient.putDataItem(putDataRequest);
    }
}
