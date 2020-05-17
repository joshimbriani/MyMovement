package com.joshimbriani.mymovement;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.wear.widget.drawer.WearableNavigationDrawerView;

public class MainActivity extends FragmentActivity implements MenuItem.OnMenuItemClickListener, WearableNavigationDrawerView.OnItemSelectedListener {

    private TextView mTextView;
    private WearableNavigationDrawerView mWearableNavigationDrawerView;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

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
}
