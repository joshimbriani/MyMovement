package com.joshimbriani.mymovement.data;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.joshimbriani.mymovement.data.Movement;
import com.joshimbriani.mymovement.data.MovementRepository;

public class NewMovementViewModel extends AndroidViewModel {
    private MovementRepository mRepository;

    public NewMovementViewModel(Application application) {
        super(application);
        mRepository = new MovementRepository(application);
    }

    public long createMovement(Movement movement) {
        return mRepository.rawInsert(movement);
    }
}
