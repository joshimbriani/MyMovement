package com.joshimbriani.mymovement;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.wear.widget.drawer.WearableNavigationDrawerView;

public class MainActivity extends WearableActivity implements MenuItem.OnMenuItemClickListener, WearableNavigationDrawerView.OnItemSelectedListener {

    private TextView mTextView;
    private WearableNavigationDrawerView mWearableNavigationDrawerView;

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
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onItemSelected(int pos) {

    }
}
