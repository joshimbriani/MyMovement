package com.joshimbriani.mymovement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.joshimbriani.mymovement.data.MovementViewModel;
import com.joshimbriani.mymovement.data.MovementWithPoints;

import java.util.ArrayList;

public class MainListFragment extends ListFragment {
    private MovementViewModel movementViewModel;
    private MainListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        movementViewModel = new MovementViewModel(getActivity().getApplication());
        movementViewModel.getAllMovementsWithPoints().observe(this, (movements) -> {
            if (adapter != null) {
                adapter.clear();
                adapter.addAll(movements);
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
}
