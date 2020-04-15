package com.joshimbriani.mymovement;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MovementRepository {
    private MovementDao mMovementDao;
    private LiveData<List<Movement>> mAllMovements;

    MovementRepository(Application application) {
        MovementRoomDatabase db = MovementRoomDatabase.getDatabase(application);
        mMovementDao = db.movementDao();
        mAllMovements = mMovementDao.getMovements();
    }

    LiveData<List<Movement>> getAllMovements() {
        return mAllMovements;
    }

    void insert(Movement movement) {
        MovementRoomDatabase.databaseWriteExecutor.execute(() -> {
            mMovementDao.insert(movement);
        });
    }
}
