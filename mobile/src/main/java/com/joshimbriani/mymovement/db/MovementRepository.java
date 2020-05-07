package com.joshimbriani.mymovement.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MovementRepository {
    private MovementDao mMovementDao;
    private MovementPointDao mMovementPointDao;
    private LiveData<List<MovementWithPoints>> mAllMovementsWithPoints;

    public MovementRepository(Application application) {
        MovementRoomDatabase db = MovementRoomDatabase.getDatabase(application);
        mMovementDao = db.movementDao();
        mMovementPointDao = db.movementPointDao();
        mAllMovementsWithPoints = mMovementDao.getMovementsWithPoints();
    }

    public LiveData<List<MovementWithPoints>> getAllMovementsWithPoints() {
        return mAllMovementsWithPoints;
    }

    public LiveData<MovementWithPoints> getMovement(long id) {
        return mMovementDao.getMovement(id);
    }

    public void insert(Movement movement) {
        MovementRoomDatabase.databaseWriteExecutor.execute(() -> {
            mMovementDao.insert(movement);
        });
    }

    public void insert(MovementPoint movementPoint) {
        MovementRoomDatabase.databaseWriteExecutor.execute(() -> {
            mMovementPointDao.insert(movementPoint);
        });
    }

    public void update(Movement movement) {
        MovementRoomDatabase.databaseWriteExecutor.execute(() -> {
            mMovementDao.updateMovement(movement);
        });
    }
}
