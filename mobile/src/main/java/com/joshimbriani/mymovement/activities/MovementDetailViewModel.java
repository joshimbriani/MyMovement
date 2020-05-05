package com.joshimbriani.mymovement.activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.joshimbriani.mymovement.db.MovementRepository;
import com.joshimbriani.mymovement.db.MovementWithPoints;

public class MovementDetailViewModel extends AndroidViewModel {

    private MovementRepository mRepository;
    private long mMovementId;
    private LiveData<MovementWithPoints> mMovement;

    public MovementDetailViewModel(Application application, long movementId) {
        super(application);
        mMovementId = movementId;
        mRepository = new MovementRepository(application);
        mMovement = mRepository.getMovement(mMovementId);
    }

    LiveData<MovementWithPoints> getMovement() {
        return mMovement;
    }
}
