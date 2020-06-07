package com.joshimbriani.mymovement.services;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.joshimbriani.mymovement.data.GsonWithZonedDateTime;
import com.joshimbriani.mymovement.data.Movement;
import com.joshimbriani.mymovement.data.MovementPoint;
import com.joshimbriani.mymovement.data.MovementRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataLayerListenerService extends WearableListenerService {
    private MovementRepository movementRepository;
    private Gson gson;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        movementRepository = new MovementRepository(getApplication());
        gson = GsonWithZonedDateTime.getGson();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        List<String> points = new ArrayList<>();
        String pointPattern = "/movement/\\d+/\\d+";
        String movementPattern = "/movement/\\d+";
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().matches(pointPattern)) {
                    // Syncing a point
                    Matcher m = Pattern.compile(pointPattern).matcher(item.getUri().getPath());
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    points.add(dataMap.getString("com.joshimbriani.mymovement.movement"));
                } else if (item.getUri().getPath().matches(movementPattern)) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    Movement passedMovement = gson.fromJson(dataMap.getString("com.joshimbriani.mymovement.movement"), Movement.class);
                    if (passedMovement.getOrigin().equals("wear")) {
                        AsyncTask.execute(() -> {
                            movementRepository.insertMovementIfDoesntExist(passedMovement);
                        });
                    }
                }
            }
        }

        for (String point : points) {
            // Insert new point at the given movement id
            MovementPoint movementPoint = gson.fromJson(point, MovementPoint.class);
            if (movementPoint.getOrigin().equals("wear")) {
                AsyncTask.execute(() -> {
                    movementRepository.insertPointIfDoesntExist(movementPoint);
                });
            }
        }
    }
}
