package com.joshimbriani.mymovement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.joshimbriani.mymovement.data.MovementViewModel;
import com.joshimbriani.mymovement.data.MovementWithPoints;

public class MainListFragment extends ListFragment {
    private MovementViewModel movementViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        movementViewModel = new MovementViewModel(getActivity().getApplication());
        return inflater.inflate(R.layout.main_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //ArrayAdapter adapter = new ArrayAdapter<MovementWithPoints>(getContext(), R.layout.main_list_layout, );
    }
}
