package com.joshimbriani.mymovement;

import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.DefaultOffsettingHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.joshimbriani.mymovement.data.MovementViewModel;
import com.joshimbriani.mymovement.data.MovementWithPoints;

import java.util.ArrayList;
import java.util.List;

public class MainListFragment extends ListFragment {
    private MovementViewModel movementViewModel;
    private MainListAdapter adapter;
    List<MovementWithPoints> movements;

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
        return inflater.inflate(R.layout.main_list, container, false);
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


