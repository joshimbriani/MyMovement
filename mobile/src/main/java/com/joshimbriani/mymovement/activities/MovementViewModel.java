package com.joshimbriani.mymovement.activities;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.joshimbriani.mymovement.data.Movement;
import com.joshimbriani.mymovement.data.MovementRepository;
import com.joshimbriani.mymovement.data.MovementWithPoints;

import java.util.List;

public class MovementViewModel extends AndroidViewModel {
    private MovementRepository mRepository;
    private LiveData<List<MovementWithPoints>> mAllMovementsWithPoints;

    public MovementViewModel(Application application) {
        super(application);
        mRepository = new MovementRepository(application);
        mAllMovementsWithPoints = mRepository.getAllMovementsWithPoints();
    }

    LiveData<List<MovementWithPoints>> getAllMovementsWithPoints() {
        return mAllMovementsWithPoints;
    }

    public void insert(Movement movement) {
        mRepository.insert(movement);
    }
}
