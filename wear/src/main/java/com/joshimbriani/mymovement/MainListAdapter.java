package com.joshimbriani.mymovement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.joshimbriani.mymovement.data.MovementWithPoints;

import java.util.ArrayList;
import java.util.List;

public class MainListAdapter extends ArrayAdapter<MovementWithPoints> {
    public MainListAdapter(@NonNull Context context, List<MovementWithPoints> movements) {
        super(context, 0, movements);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MovementWithPoints movement = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.main_list_layout, parent, false);
        }

        TextView numPoints = convertView.findViewById(R.id.num_points);
        TextView movementName = convertView.findViewById(R.id.movement_name);
        TextView movementTime = convertView.findViewById(R.id.movement_time);

        numPoints.setText(String.valueOf(movement.points.size()));
        movementName.setText(movement.movement.getName());
        movementTime.setText(movement.getDatetimeRange());
        return convertView;
    }
}
