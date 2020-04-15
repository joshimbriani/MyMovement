package com.joshimbriani.mymovement;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MovementViewModel extends AndroidViewModel {
    private MovementRepository mRepository;
    private LiveData<List<Movement>> mAllMovements;

    public MovementViewModel(Application application) {
        super(application);
        mRepository = new MovementRepository(application);
        mAllMovements = mRepository.getAllMovements();
    }

    LiveData<List<Movement>> getAllMovements() {
        return  mAllMovements;
    }

    public void insert(Movement movement) {
        mRepository.insert(movement);
    }
}
