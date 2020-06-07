package com.joshimbriani.mymovement;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.wear.widget.drawer.WearableNavigationDrawerView;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.joshimbriani.mymovement.data.GsonWithZonedDateTime;
import com.joshimbriani.mymovement.data.Movement;
import com.joshimbriani.mymovement.data.MovementPoint;
import com.joshimbriani.mymovement.data.MovementRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends FragmentActivity implements MenuItem.OnMenuItemClickListener, WearableNavigationDrawerView.OnItemSelectedListener, DataClient.OnDataChangedListener {

    private TextView mTextView;
    private WearableNavigationDrawerView mWearableNavigationDrawerView;
    private Gson gson;

    private MovementRepository movementRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        movementRepository = new MovementRepository(getApplication());
        gson = GsonWithZonedDateTime.getGson();

        // Enables Always-on
        //setAmbientEnabled();

        mWearableNavigationDrawerView = findViewById(R.id.top_navigation_drawer);
        mWearableNavigationDrawerView.setAdapter(new NavigationAdapter(this));
        mWearableNavigationDrawerView.getController().peekDrawer();
        mWearableNavigationDrawerView.addOnItemSelectedListener(this);

        // Initially set Movement List as the main fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        MainListFragment listFragment = new MainListFragment();
        transaction.replace(R.id.content_frame, listFragment);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onItemSelected(int pos) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (pos) {
            case 0:
                // Movement List
                MainListFragment listFragment = new MainListFragment();
                transaction.replace(R.id.content_frame, listFragment);
                break;
            case 1:
                NewMovementFragment newMovementFragment = new NewMovementFragment();
                transaction.replace(R.id.content_frame, newMovementFragment);
                break;
            case 2:
                SettingsFragment settingsFragment = new SettingsFragment();
                transaction.replace(R.id.content_frame, settingsFragment);
                break;
        }

        transaction.commit();
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
                    if (passedMovement.getOrigin().equals("mobile")) {
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
            if (movementPoint.getOrigin().equals("mobile")) {
                AsyncTask.execute(() -> {
                    movementRepository.insert(movementPoint);
                });
            }
        }
    }
}
