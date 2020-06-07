package com.joshimbriani.mymovement.data;

import android.app.Application;
import android.os.Build;
import android.util.Pair;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;

import java.util.List;

public class MovementRepository {
    private MovementDao mMovementDao;
    private MovementPointDao mMovementPointDao;
    private LiveData<List<MovementWithPoints>> mAllMovementsWithPoints;
    private Gson gson;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MovementRepository(Application application) {
        MovementRoomDatabase db = MovementRoomDatabase.getDatabase(application);
        mMovementDao = db.movementDao();
        mMovementPointDao = db.movementPointDao();
        mAllMovementsWithPoints = mMovementDao.getRecentMovementWithRecentPoint();
        gson = GsonWithZonedDateTime.getGson();
    }

    public LiveData<List<MovementWithPoints>> getAllMovementsWithPoints() {
        return mAllMovementsWithPoints;
    }

    public List<MovementWithPoints> getRawAllMovementsWithPoints() {
        return mMovementDao.getRawMovementWithPoints();
    }

    public LiveData<MovementWithPoints> getMovement(long id) {
        return mMovementDao.getMovement(id);
    }

    public void insert(Movement movement) {
        MovementRoomDatabase.databaseWriteExecutor.execute(() -> {
            mMovementDao.insert(movement);
        });
    }

    public long rawInsert(Movement movement) {
        return mMovementDao.insert(movement);
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

    public void updateName(long id, String name) {
        MovementRoomDatabase.databaseWriteExecutor.execute(() -> {
            Movement movement = mMovementDao.getRawMovement(id);
            movement.setName(name);
            update(movement);
        });
    }

    public void deleteMovement(long id) {
        MovementRoomDatabase.databaseWriteExecutor.execute(() -> {
            mMovementDao.delete(id);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertMovementIfDoesntExist(Movement passedMovement) {
        Movement movement = mMovementDao.getRawMovementByDatetime(passedMovement.getDateTime().toEpochSecond());
        if (movement != null) {
            return;
        }

        insert(passedMovement);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertPointIfDoesntExist(MovementPoint point) {
        Movement movement = mMovementDao.getRawMovement(point.getMovementId());
        if (movement == null) {
            return;
        }

        MovementPoint p = mMovementPointDao.getRawPointByDatetime(point.getDateTime().toEpochSecond());
        if (p != null) {
            return;
        }

        insert(point);
    }

    public Movement getRawMovementByDatetime(long dateTime) {
        return mMovementDao.getRawMovementByDatetime(dateTime);
    }

    public MovementPoint getRawPointByDatetime(long dateTime) {
        return mMovementPointDao.getRawPointByDatetime(dateTime);
    }
}
