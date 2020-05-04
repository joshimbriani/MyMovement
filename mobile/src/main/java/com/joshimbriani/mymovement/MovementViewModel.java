package com.joshimbriani.mymovement;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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
