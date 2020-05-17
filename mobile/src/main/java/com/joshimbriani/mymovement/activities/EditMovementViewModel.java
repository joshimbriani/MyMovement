package com.joshimbriani.mymovement.activities;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.joshimbriani.mymovement.data.MovementRepository;
import com.joshimbriani.mymovement.data.MovementWithPoints;

public class EditMovementViewModel extends AndroidViewModel {
    private MovementRepository mRepository;
    private LiveData<MovementWithPoints> mMovementWithPoints;
    private long mMovementId;

    public EditMovementViewModel(Application application, long movementId) {
        super(application);
        mMovementId = movementId;
        mRepository = new MovementRepository(application);
        mMovementWithPoints = mRepository.getMovement(mMovementId);
    }

    LiveData<MovementWithPoints> getMovement() {
        return mMovementWithPoints;
    }

    void setMovementName(String movementName) {
        mRepository.updateName(mMovementId, movementName);
    }
}
