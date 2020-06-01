package com.joshimbriani.mymovement;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.DefaultOffsettingHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.joshimbriani.mymovement.data.MovementViewModel;
import com.joshimbriani.mymovement.data.MovementWithPoints;

import java.util.ArrayList;
import java.util.List;

public class MainListFragment extends ListFragment {
    private MovementViewModel movementViewModel;
    private MainListAdapter adapter;
    List<MovementWithPoints> movements;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        movementViewModel = new MovementViewModel(getActivity().getApplication());
        movementViewModel.getAllMovementsWithPoints().observe(this, (movements) -> {
            if (adapter != null) {
                adapter.clear();
                adapter.addAll(movements);
                this.movements = movements;
            }
        });

        View v = inflater.inflate(R.layout.main_list, container, false);
        listView = v.findViewById(android.R.id.list);
        listView.setOnTouchListener((view, event) -> {
            view.performClick();
            view.getParent().requestDisallowInterceptTouchEvent(true);
            return view.onTouchEvent(event);
        });

        requestLocationPermission(v);
        return v;
    }

    private void requestLocationPermission(View v) {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Snackbar.make(v, "To use the app, you need to provide fine permissions.", Snackbar.LENGTH_INDEFINITE).setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 34);
                }
            });
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 34);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new MainListAdapter(getContext(), new ArrayList<>());
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        MovementDetailFragment detailFragment = new MovementDetailFragment(movements.get(position).movement.getId());
        transaction.add(R.id.content_frame, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}


