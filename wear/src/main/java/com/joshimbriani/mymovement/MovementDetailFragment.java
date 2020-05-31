package com.joshimbriani.mymovement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.wear.widget.SwipeDismissFrameLayout;

import com.joshimbriani.mymovement.data.MovementDetailViewModel;
import com.joshimbriani.mymovement.data.MovementDetailViewModelFactory;
import com.joshimbriani.mymovement.data.MovementViewModel;
import com.joshimbriani.mymovement.data.MovementWithPoints;

public class MovementDetailFragment extends Fragment {
    private TextView movementName;
    private MovementDetailViewModel mMovementDetailViewModel;
    private long id;
    private MovementWithPoints movementWithPoints;

    public MovementDetailFragment(long id) {
        this.id = id;
    }

    private final androidx.wear.widget.SwipeDismissFrameLayout.Callback callback =
            new androidx.wear.widget.SwipeDismissFrameLayout.Callback() {
                @Override
                public void onDismissed(SwipeDismissFrameLayout layout) {
                    getFragmentManager().popBackStackImmediate();
                }
            };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SwipeDismissFrameLayout swipeDismissFrameLayout = new SwipeDismissFrameLayout(getActivity());

        View v = inflater.inflate(R.layout.movement_detail_fragment, container, false);
        movementName = v.findViewById(R.id.movement_detail_name);

        mMovementDetailViewModel = new ViewModelProvider(this, new MovementDetailViewModelFactory(getActivity().getApplication(), id)).get(MovementDetailViewModel.class);
        mMovementDetailViewModel.getMovement().observe(this, (movement) -> {
            if (movement == null) {
                return;
            }
            movementWithPoints = movement;
            movementName.setText(movementWithPoints.movement.getName());
            /*
            if (movement.points.size() > 0) {
                adapter.setMovement(movement);
                if (map != null) {
                    setMapPoints(movement);
                }
                recyclerView.setVisibility(View.VISIBLE);
                movementDetailEmptyText.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                movementDetailEmptyText.setVisibility(View.VISIBLE);
            }*/

        });
        swipeDismissFrameLayout.addView(v);
        swipeDismissFrameLayout.addCallback(callback);
        return swipeDismissFrameLayout;
    }
}
