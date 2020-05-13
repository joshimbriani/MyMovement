package com.joshimbriani.mymovement;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.wear.widget.drawer.WearableNavigationDrawerView.WearableNavigationDrawerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class NavigationAdapter extends WearableNavigationDrawerAdapter {
    private List<String> actionList = new ArrayList<>(Arrays.asList("Movements", "New Movement", "Settings"));
    private Context mContext;

    NavigationAdapter(Context context) {
        mContext = context;
    }

    @Override
    public String getItemText(int pos) {
        return actionList.get(pos);
    }

    @Override
    public Drawable getItemDrawable(int pos) {
        String selectedItem = actionList.get(pos);
        if (selectedItem.equals("Movements")) {
            return ContextCompat.getDrawable(mContext, R.drawable.ic_mymovements);
        } else if (selectedItem.equals("New Movement")) {
            return ContextCompat.getDrawable(mContext, R.drawable.ic_newmovement);
        } else if (selectedItem.equals("Settings")) {
            return ContextCompat.getDrawable(mContext, R.drawable.ic_settings);
        }

        return null;
    }

    @Override
    public int getCount() {
        return actionList.size();
    }
}
