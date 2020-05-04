package com.joshimbriani.mymovement;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MovementRepository {
    private MovementDao mMovementDao;
    private MovementPointDao mMovementPointDao;
    private LiveData<List<MovementWithPoints>> mAllMovementsWithPoints;

    MovementRepository(Application application) {
        MovementRoomDatabase db = MovementRoomDatabase.getDatabase(application);
        mMovementDao = db.movementDao();
        mMovementPointDao = db.movementPointDao();
        mAllMovementsWithPoints = mMovementDao.getMovementsWithPoints();
    }

    LiveData<List<MovementWithPoints>> getAllMovementsWithPoints() {
        return mAllMovementsWithPoints;
    }

    void insert(Movement movement) {
        MovementRoomDatabase.databaseWriteExecutor.execute(() -> {
            mMovementDao.insert(movement);
        });
    }

    void insert(MovementPoint movementPoint) {
        MovementRoomDatabase.databaseWriteExecutor.execute(() -> {
            mMovementPointDao.insert(movementPoint);
        });
    }
}
