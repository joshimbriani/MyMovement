package com.joshimbriani.mymovement.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Movement.class, MovementPoint.class}, version = 1, exportSchema = false)
@TypeConverters({RoomConverters.class})
public abstract class MovementRoomDatabase extends RoomDatabase {
    public abstract MovementDao movementDao();
    public abstract MovementPointDao movementPointDao();

    private static volatile MovementRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static MovementRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MovementRoomDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MovementRoomDatabase.class, "movement_database").addCallback(sRoomDatabaseCallback).build();
            }
        }
        return INSTANCE;
    }

    // TODO(joshimbriani): Take this out when it's time to launch app
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                MovementDao dao = INSTANCE.movementDao();
                MovementPointDao mPDao = INSTANCE.movementPointDao();

                dao.deleteAll();
                mPDao.deleteAll();

                Movement movement = new Movement("First trip");
                long m1id = dao.insert(movement);
                MovementPoint mP1 = new MovementPoint(m1id, 41.482265, -82.683510);
                MovementPoint mP2 = new MovementPoint(m1id, 41.482275, -82.683500);
                MovementPoint mP3 = new MovementPoint(m1id, 41.482285, -82.683490);
                mPDao.insert(mP1, mP2, mP3);
                movement = new Movement("Cedar Point - March 23rd 2019");
                long m2id = dao.insert(movement);
                MovementPoint mP4 = new MovementPoint(m2id, 41.482265, -82.683510);
                MovementPoint mP5 = new MovementPoint(m2id, 41.482275, -82.683500);
                MovementPoint mP6 = new MovementPoint(m2id, 41.482285, -82.683490);
                mPDao.insert(mP4, mP5, mP6);
                movement = new Movement("HHN - October 28th 2019");
                long m3id = dao.insert(movement);
                MovementPoint mP7 = new MovementPoint(m2id, 41.482265, -82.683510);
                MovementPoint mP8 = new MovementPoint(m2id, 41.482275, -82.683500);
                MovementPoint mP9 = new MovementPoint(m2id, 41.482285, -82.683490);
                mPDao.insert(mP7, mP8, mP9);
            });
        }
    };
}
